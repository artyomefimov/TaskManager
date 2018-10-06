package com.a_team.taskmanager.ui.singletask.managers;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.ImageView;

import com.a_team.taskmanager.controller.TaskViewModel;
import com.a_team.taskmanager.controller.utils.PictureUtils;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.fragments.AbstractTaskFragment;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import static com.a_team.taskmanager.ui.singletask.Constants.FILE_PROVIDER;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_PHOTO;

public class PhotoManager implements AbstractTaskFragment.OnChangedCallback{
    private static PhotoManager ourInstance;

    private boolean isShouldDeletePhoto;
    private boolean isHasNoPhoto;

    private File mPhotoFile;
    private File mTempPhotoFile;

    private TaskViewModel mViewModel;
    private Task mTask;

    private String tempPhotoFileName;

    public static PhotoManager getInstance(TaskViewModel viewModel, Task task) {
        if (ourInstance == null) {
            ourInstance = new PhotoManager(viewModel, task);
        }
        return ourInstance;
    }

    @Nullable
    public static PhotoManager getInstance() {
        return ourInstance;
    }

    private PhotoManager(TaskViewModel viewModel, Task task) {
        mViewModel = viewModel;
        mTask = task;
    }

    public void configurePhotoButton(Fragment fragment, View photoButton) {
        final Intent makePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = fragment.getActivity().getPackageManager();

        boolean canTakePhoto = makePhotoIntent.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            photoButton.setOnClickListener((view) -> {
                Uri uri = FileProvider.getUriForFile(fragment.getActivity(), FILE_PROVIDER, mTempPhotoFile);
                makePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = packageManager
                        .queryIntentActivities(makePhotoIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo info : cameraActivities) {
                    fragment.getActivity().grantUriPermission(info.activityInfo.packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                fragment.startActivityForResult(makePhotoIntent, REQUEST_PHOTO);
            });
        }
    }

    public void markPhotoForDelete(AbstractTaskFragment fragment, ImageView imageView) {
        isShouldDeletePhoto = true;
        imageView.setImageDrawable(null);
        fragment.getCallback().onDataChanged(true);
    }

    public void setPhotoFile(TaskViewModel viewModel, Task task) {
        mTask = task;
        mPhotoFile = viewModel.getPhotoFile(task.getPhotoFilename());
        task.setPhotoFile(mPhotoFile);
        mTempPhotoFile = viewModel.getPhotoFile(tempPhotoFileName);
    }

    public void updatePhotoView(Activity activity, ImageView imageView) {
        if (isPhotoFileNotExists(mPhotoFile))
            isHasNoPhoto = true;
        tempUpdatePhotoView(activity, imageView, mPhotoFile);
    }

    private void tempUpdatePhotoView(Activity activity, ImageView imageView, File photoFile) {
        if (isPhotoFileNotExists(photoFile)) {
            imageView.setImageDrawable(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), activity);
            imageView.setImageBitmap(scaledBitmap);
        }
    }

    private boolean isPhotoFileNotExists(File photoFile) {
        return photoFile == null || !photoFile.exists();
    }

    public void getPhotoFromCamera(AbstractTaskFragment fragment, ImageView imageView) {
        Uri uri = FileProvider.getUriForFile(fragment.getActivity(), FILE_PROVIDER, mTempPhotoFile);
        fragment.getActivity().revokeUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        tempUpdatePhotoView(fragment.getActivity(), imageView, mTempPhotoFile);
        mTask.setPhotoFile(mTempPhotoFile);
        fragment.getCallback().onDataChanged(true);
    }

    void removePhotoIfNecessary(Activity activity) {
        if (isShouldDeletePhoto) {
            if (mTempPhotoFile.exists()) {
                removePhoto(activity, mTempPhotoFile);
            } else {
                removePhoto(activity, mPhotoFile);
            }
        }
    }

    private void removePhoto(Activity activity, File file) {
        Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, file);
        mViewModel.removePhotoFile(fileUri);
    }

    public void removeTempPhoto(Activity activity) {
        Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mTempPhotoFile);
        mViewModel.removePhotoFile(fileUri);
    }

    void updatePhotoFileForTask(Activity activity) {
        if (mTempPhotoFile.exists()) {
            mTask.setFileUUID(trimmedName());
            Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mPhotoFile);
            mViewModel.removePhotoFile(fileUri);
        }
    }

    private String trimmedName() {
        int point = tempPhotoFileName.indexOf('.');
        return tempPhotoFileName.substring(0, point);
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public boolean isTaskHasNoPhoto() {
        return isHasNoPhoto;
    }

    public void setTempPhotoFileName(String tempPhotoFileName) {
        this.tempPhotoFileName = tempPhotoFileName;
    }

    Task getTask() {
        return mTask;
    }

    @Override
    public void onDataChanged(boolean isChanged) {
    }

    @Override
    public void taskChanged(Task task) {
        mTask = task;
    }
}