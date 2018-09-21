package com.a_team.taskmanager.ui.taskedit;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Scroller;
import android.widget.TextView;

import com.a_team.taskmanager.Constants;
import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.TaskViewModel;
import com.a_team.taskmanager.controller.utils.PictureUtils;
import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.util.List;

public class TaskEditFragment extends Fragment {

    private static final String ARG_TIMESTAMP = "timestamp";
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";

    private static final String FILE_PROVIDER = "com.artyom.criminalintent.fileprovider";
    public static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_REAL_PHOTO = 3;
    private static final String DIALOG_IMAGE = "DialogImage";

    private FloatingActionButton mMakePhotoButton;
    private FloatingActionButton mSetNotificationButton;
    private TextView mNotificationTimestamp;
    private EditText mTitleField;
    private EditText mDescriptionField;
    private ImageView mPhoto;

    private Task mTask;
    private TaskViewModel mViewModel;
    private File mPhotoFile;

    public static TaskEditFragment newInstance(Task task) {
        Bundle args = new Bundle();

        args.putParcelable(Constants.ARG_CURRENT_TASK, task);
        TaskEditFragment fragment = new TaskEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiveTaskFromBundle();
        if (isReceivedTaskCorrect()) {
            createViewModelAndSubscribeUi();
        } else {
            createViewModelAndEmptyTask();
        }
        setActionBarSubtitle();

        // todo мб в другое место перенести
        setPhotoFile();
        configureMakePhotoButton();
        updatePhotoView();
    }

    private void receiveTaskFromBundle() {
        mTask = getArguments().getParcelable(Constants.ARG_CURRENT_TASK);
    }

    private boolean isReceivedTaskCorrect() {
        return mTask != null && !mTask.equals(Task.emptyTask());
    }

    private void createViewModelAndSubscribeUi() {
        TaskViewModel.Factory factory = new TaskViewModel.Factory(
                getActivity().getApplication(),
                mTask.getId());
        mViewModel = ViewModelProviders.of(this, factory).get(TaskViewModel.class);
        subscribeUi();
        updateUI(mTask);
    }

    private void createViewModelAndEmptyTask() {
        TaskViewModel.Factory factory = new TaskViewModel.Factory(
                getActivity().getApplication(),
                Constants.BAD_TASK_ID);
        mViewModel = ViewModelProviders.of(this, factory).get(TaskViewModel.class);
        mTask = Task.emptyTask();
    }

    private void subscribeUi() {
        mViewModel.getTask().observe(this, task -> updateUI(task));
    }

    private void updateUI(Task task) {
        if (task != null) {
            mTitleField.setText(task.getTitle());
            mDescriptionField.setText(task.getDescription());
            //mNotificationTimestamp.setText(task.getNotificationDate().toString()); // todo add notification feature
        }
    }

    private void setActionBarSubtitle() {
        String subtitle = mTask.getTitle() != null ?
                mTask.getTitle() :
                "Create task";
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(subtitle);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.task_edit_menu_save:
                updateTask();
                finishActivity();
                return true;
            case R.id.task_edit_menu_delete:
                deleteTask();
                finishActivity();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateTask() {
        mViewModel.updateOrInsertTask(mTask);
    }

    private void deleteTask() {
        mViewModel.deleteTask(mTask);
    }

    private void finishActivity() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(ARG_TIMESTAMP, mNotificationTimestamp.getText().toString());
        outState.putString(ARG_TITLE, mTitleField.getText().toString());
        outState.putString(ARG_DESCRIPTION, mDescriptionField.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_edit_fragment, container, false);

        mMakePhotoButton = view.findViewById(R.id.task_edit_make_photo);

        mSetNotificationButton = view.findViewById(R.id.task_edit_add_notification);
        configureSetNotificationButton();

        mNotificationTimestamp = view.findViewById(R.id.task_edit_notification_timestamp);
        configureNotificationTimestampButton();

        mTitleField = view.findViewById(R.id.task_edit_title);
        configureTitleField();

        mDescriptionField = view.findViewById(R.id.task_edit_description);
        configureDescriptionField();

        mPhoto = view.findViewById(R.id.task_edit_photo);
        configurePhotoView();

        return view;
    }

    private void configureMakePhotoButton() {
        final Intent makePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        PackageManager packageManager = getActivity().getPackageManager();

        boolean canTakePhoto = mPhotoFile != null &&
                makePhotoIntent.resolveActivity(packageManager) != null;
        if (canTakePhoto) {
            mMakePhotoButton.setOnClickListener((view) -> {
                Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
                makePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = packageManager
                        .queryIntentActivities(makePhotoIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo info : cameraActivities) {
                    getActivity().grantUriPermission(info.activityInfo.packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                startActivityForResult(makePhotoIntent, REQUEST_PHOTO);
            });
        }
    }

    private void configureSetNotificationButton() {
        // adding a notification for the specific time in future
    }

    private void configureNotificationTimestampButton() {
        mNotificationTimestamp.setText("No notification yet");
    }

    private void configureTitleField() {
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void configureDescriptionField() {
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTask.setDescription(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void configurePhotoView() {
        mPhoto.setOnClickListener((view) -> {
            if (getFragmentManager() != null) {
                RealImageFragment fragment = RealImageFragment.newInstance(mPhotoFile);
                fragment.setTargetFragment(TaskEditFragment.this, REQUEST_REAL_PHOTO);
                fragment.show(getFragmentManager(), DIALOG_IMAGE);
            }
        });
    }

    private void setPhotoFile() {
        mPhotoFile = mViewModel.getPhotoFile(mTask);
        mTask.setPhotoFile(mPhotoFile);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhoto.setImageDrawable(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhoto.setImageBitmap(scaledBitmap);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_PHOTO:
                Uri uri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
                getActivity().revokeUriPermission(uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                updatePhotoView();
        }
    }
}
