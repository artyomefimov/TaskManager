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

import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.fragments.AbstractTaskFragment;
import com.a_team.taskmanager.utils.PictureUtils;
import com.a_team.taskmanager.ui.singletask.viewmodel.TaskViewModel;

import java.io.File;
import java.util.List;

import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.FILE_PROVIDER;
import static com.a_team.taskmanager.utils.RequestCodeStorage.MAKE_PHOTO_REQUEST_CODE;

public class PhotoManager {
    private TaskViewModel mViewModel;
    private Task mTask;

    private File mPhotoFile;

    public PhotoManager(TaskViewModel viewModel, Task task) {
        mViewModel = viewModel;
        mTask = task;
    }

    public void configurePhotoButton(Fragment fragment, View photoButton) {
        final Intent makePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = fragment.getActivity().getPackageManager();

        boolean canTakePhoto = makePhotoIntent.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            photoButton.setOnClickListener((view) -> {
                Uri uri = FileProvider.getUriForFile(fragment.getActivity(), FILE_PROVIDER, mPhotoFile);
                makePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = packageManager
                        .queryIntentActivities(makePhotoIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo info : cameraActivities) {
                    fragment.getActivity().grantUriPermission(info.activityInfo.packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                fragment.startActivityForResult(makePhotoIntent, MAKE_PHOTO_REQUEST_CODE);
            });
        }
    }

    public void setPhotoFile(TaskViewModel viewModel) {
        mPhotoFile = viewModel.getPhotoFile(mTask.getPhotoFilename());
    }

    public void updatePhotoView(Activity activity, ImageView imageView) {
        if (isPhotoFileNotExists(mPhotoFile)) {
            imageView.setImageBitmap(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), activity);
            imageView.setImageBitmap(scaledBitmap);
        }
    }

    private boolean isPhotoFileNotExists(File photoFile) {
        return photoFile == null || !photoFile.exists();
    }

    public void getPhotoFromCamera(AbstractTaskFragment fragment, ImageView imageView) {
        Uri uri = FileProvider.getUriForFile(fragment.getActivity(), FILE_PROVIDER, mPhotoFile);
        fragment.getActivity().revokeUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        updatePhotoView(fragment.getActivity(), imageView);
        fragment.getCallback().onDataChanged(true);
    }

    public void markPhotoForDelete(AbstractTaskFragment fragment, ImageView imageView) {
        removePhoto(fragment.getActivity(), mPhotoFile);
        imageView.setImageDrawable(null);
        fragment.getCallback().onDataChanged(true);
    }

    private void removePhoto(Activity activity, File file) {
        if (file != null) {
            Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, file);
            mViewModel.removePhotoFile(fileUri);
        }
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }
}