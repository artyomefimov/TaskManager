package com.a_team.taskmanager.ui.singletask;

import android.app.Activity;
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
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.a_team.taskmanager.Constants;
import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.TaskViewModel;
import com.a_team.taskmanager.controller.utils.PictureUtils;
import com.a_team.taskmanager.entity.Task;

import java.io.File;
import java.util.List;

public abstract class AbstractTaskFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_TIMESTAMP = "timestamp";
    public static final String FILE_PROVIDER = "com.artyom.criminalintent.fileprovider";
    private static final int REQUEST_PHOTO = 2;
    private static final int REQUEST_REAL_PHOTO = 3;
    private static final String DIALOG_IMAGE = "DialogImage";
    private static final String CREATE_TASK_TITLE = "Create task";

    protected FloatingActionButton mMakePhotoButton;
    protected FloatingActionButton mSetNotificationButton;
    protected TextView mNotificationTimestamp;
    protected EditText mTitleField;
    protected EditText mDescriptionField;
    protected ImageView mPhoto;

    protected Task mTask;
    protected TaskViewModel mViewModel;
    protected File mPhotoFile;

    public static AbstractTaskFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ARG_CURRENT_TASK, task);

        AbstractTaskFragment fragment;
        fragment = task.equals(Task.emptyTask()) ? new NewTaskFragment() : new TaskEditFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        //outState.putString(ARG_TIMESTAMP, mNotificationTimestamp.getText().toString());
        outState.putString(ARG_TITLE, mTitleField.getText().toString());
        outState.putString(ARG_DESCRIPTION, mDescriptionField.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task_fragment, container, false);

        mMakePhotoButton = view.findViewById(R.id.task_edit_make_photo);

        mSetNotificationButton = view.findViewById(R.id.task_edit_add_notification);
        configureSetNotificationButton();

//        mNotificationTimestamp = view.findViewById(R.id.task_edit_notification_timestamp);
//        configureNotificationTimestampButton();

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

        boolean canTakePhoto = makePhotoIntent.resolveActivity(packageManager) != null;
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

    private void setPhotoFile() {
        mPhotoFile = mViewModel.getPhotoFile(mTask);
        mTask.setPhotoFile(mPhotoFile);
    }

    private void setPhotoFileForFragment() {
        mPhotoFile = mViewModel.getPhotoFile(mTask);
    }

    private void configurePhotoView() {
        mPhoto.setOnClickListener((view) -> {
            if (getFragmentManager() != null) {
                RealImageFragment fragment = RealImageFragment.newInstance(mPhotoFile);
                fragment.setTargetFragment(AbstractTaskFragment.this, REQUEST_REAL_PHOTO);
                fragment.show(getFragmentManager(), DIALOG_IMAGE);
            }
        });

        mPhoto.setOnLongClickListener((view) -> {
            configurePopupMenu();
            return true;
        });
    }

    private void configurePopupMenu() {
        PopupMenu popup = new PopupMenu(getActivity(), mPhoto);
        popup.getMenuInflater().inflate(R.menu.popup_remove_photo, popup.getMenu());

        popup.setOnMenuItemClickListener((item -> {
            Uri fileUri = FileProvider.getUriForFile(getActivity(), FILE_PROVIDER, mPhotoFile);
            mViewModel.removePhotoFile(fileUri);
            setPhotoFileForFragment();
            updatePhotoView();
            return true;
        }));
        popup.show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiveTaskFromBundle();
        if (isReceivedTaskNotNew()) {
            createViewModelAndSubscribeUi();
            setActionBarTitle(mTask.getTitle());
        } else {
            createViewModel();
            setActionBarTitle(CREATE_TASK_TITLE);
        }
        configureMakePhotoButton();
    }

    private void receiveTaskFromBundle() {
        mTask = getArguments().getParcelable(Constants.ARG_CURRENT_TASK);
    }

    private boolean isReceivedTaskNotNew() {
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

    private void createViewModel() {
        TaskViewModel.Factory factory = new TaskViewModel.Factory(
                getActivity().getApplication(),
                Constants.BAD_TASK_ID);
        mViewModel = ViewModelProviders.of(this, factory).get(TaskViewModel.class);
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

    private void setActionBarTitle(String title) {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setTitle(title);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhoto.setImageDrawable(null);
        } else {
            Bitmap scaledBitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhoto.setImageBitmap(scaledBitmap);
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

    protected void performPhotoUpdating() {
        setPhotoFile();
        updatePhotoView();
    }

    protected void performSave() {
        updateTask();
        finishActivity();
    }

    protected void performDelete() {
        deleteTask();
        finishActivity();
    }
}
