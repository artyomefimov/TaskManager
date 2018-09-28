package com.a_team.taskmanager.ui.singletask.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.a_team.taskmanager.R;

import java.io.File;

public class RealImageFragment extends DialogFragment {
    private static final String ARG_IMAGE = "image";

    private ImageView mImageView;

    public static RealImageFragment newInstance(File photoFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_IMAGE, photoFile);

        RealImageFragment fragment = new RealImageFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File image = ((File) getArguments().getSerializable(ARG_IMAGE));
        View imageView = LayoutInflater.from(getActivity()).inflate(R.layout.real_image, null);

        mImageView = imageView.findViewById(R.id.real_image_view);

        Bitmap realImage = BitmapFactory.decodeFile(image.getPath());
        mImageView.setImageBitmap(realImage);

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.task_image)
                .setPositiveButton(R.string.ok, (dialog, which) -> sendResult(Activity.RESULT_OK))
                .setView(mImageView)
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() != null) {
            getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, new Intent());
        }
    }
}
