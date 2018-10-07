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

import com.a_team.taskmanager.viewmodel.TaskViewModel;
import com.a_team.taskmanager.utils.PictureUtils;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.fragments.AbstractTaskFragment;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers.PhotoNameContainer;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.a_team.taskmanager.ui.singletask.Constants.FILE_PROVIDER;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_PHOTO;

public class PhotoManager implements AbstractTaskFragment.OnChangedCallback {

    private TaskViewModel mViewModel;
    private Task mTask;

    private File mPhotoFile;
    private File mTempPhotoFile;

    private boolean mIsShouldDeletePhoto;
    private boolean mIsHasNoPhoto;

    private String mTempPhotoFileName;

    public PhotoManager(TaskViewModel viewModel, Task task) {
        mViewModel = viewModel;
        mTask = task;
        mTempPhotoFileName = PhotoNameContainer.getInstance().getName(mTask.getId());
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

    public void setPhotoFile(TaskViewModel viewModel) {
        mPhotoFile = viewModel.getPhotoFile(mTask.getPhotoFilename());
        mTempPhotoFile = viewModel.getPhotoFile(mTempPhotoFileName);
    }

    public void updatePhotoView(Activity activity, ImageView imageView) {
        if (isPhotoFileNotExists(mPhotoFile))
            mIsHasNoPhoto = true;
        tempUpdatePhotoView(activity, imageView, mPhotoFile);
    }

    private boolean isPhotoFileNotExists(File photoFile) {
        return photoFile == null || !photoFile.exists();
    }

    private void tempUpdatePhotoView(Activity activity, ImageView imageView, File photoFile) {
        if (isPhotoFileNotExists(photoFile)) {
            imageView.setImageBitmap(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), activity);
            imageView.setImageBitmap(scaledBitmap);
        }
    }

    public void getPhotoFromCamera(AbstractTaskFragment fragment, ImageView imageView) {
        Uri uri = FileProvider.getUriForFile(fragment.getActivity(), FILE_PROVIDER, mTempPhotoFile);
        fragment.getActivity().revokeUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        tempUpdatePhotoView(fragment.getActivity(), imageView, mTempPhotoFile);
        fragment.getCallback().onDataChanged(true);
    }

    void removePhotoIfNecessary(Activity activity) {
        if (mIsShouldDeletePhoto) {
            if (mTempPhotoFile.exists()) {
                removePhoto(activity, mTempPhotoFile);
            } else {
                removePhoto(activity, mPhotoFile);
            }
        }
    }

    public void markPhotoForDelete(AbstractTaskFragment fragment, ImageView imageView) {
        mIsShouldDeletePhoto = true;
        imageView.setImageDrawable(null);
        fragment.getCallback().onDataChanged(true);
    }

    private void removePhoto(Activity activity, File file) {
        if (file != null) {
            Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, file);
            mViewModel.removePhotoFile(fileUri);
        }
    }

    public void removeTempPhoto(Activity activity) {
        if (mTempPhotoFile != null) {
            Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mTempPhotoFile);
            mViewModel.removePhotoFile(fileUri);
        }
    }

    void updatePhotoFileForTask(Activity activity) {
        if (mTempPhotoFile.exists()) {
            mTask.setFileUUID(mTempPhotoFileName);
            if (mPhotoFile != null && !mTempPhotoFile.equals(mPhotoFile)) {
                Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mPhotoFile);
                mViewModel.removePhotoFile(fileUri);
            }
        }
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }

    public boolean isTaskHasNoPhoto() {
        return mIsHasNoPhoto;
    }

    @Override
    public void onDataChanged(boolean isChanged) {
    }

    @Override
    public void taskChanged(Task task) {
        mTask = task;
    }
}