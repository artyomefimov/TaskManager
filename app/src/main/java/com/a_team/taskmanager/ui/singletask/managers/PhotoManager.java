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

import java.io.File;
import java.util.List;

import static com.a_team.taskmanager.ui.singletask.Constants.FILE_PROVIDER;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_PHOTO;

public class PhotoManager {
    private static PhotoManager ourInstance;

    private boolean isShouldDeletePhoto;
    private File mPhotoFile;
    private File mTempPhotoFile;

    public static PhotoManager getInstance() {
        if (ourInstance == null) {
            ourInstance = new PhotoManager();
        }
        return ourInstance;
    }

    private PhotoManager() {
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

                fragment.startActivityForResult(makePhotoIntent, REQUEST_PHOTO);
            });
        }
    }

    public void markPhotoForDelete(ImageView imageView) {
        isShouldDeletePhoto = true;
        imageView.setImageDrawable(null);
    }

    public void setPhotoFile(TaskViewModel viewModel, Task task) {
        mPhotoFile = viewModel.getPhotoFile(task);
        task.setPhotoFile(mPhotoFile);
    }

    public void updatePhotoView(Activity activity, ImageView imageView) {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            imageView.setImageDrawable(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), activity);
            imageView.setImageBitmap(scaledBitmap);
        }
    }

    public void getPhotoFromCamera(Activity activity, ImageView imageView) {
        Uri uri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mPhotoFile);
        activity.revokeUriPermission(uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        updatePhotoView(activity, imageView);
    }

    public void removePhotoIfNecessary(Activity activity, TaskViewModel viewModel) {
        if (isShouldDeletePhoto) {
            Uri fileUri = FileProvider.getUriForFile(activity, FILE_PROVIDER, mPhotoFile);
            viewModel.removePhotoFile(fileUri);
        }
    }

    public File getPhotoFile() {
        return mPhotoFile;
    }
}
