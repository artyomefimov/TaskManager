package com.a_team.taskmanager.ui.singletask.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.utils.FilenameGenerator;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.managers.InitializationManager;
import com.a_team.taskmanager.ui.singletask.managers.PhotoManager;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManager;

import static com.a_team.taskmanager.ui.singletask.Constants.ARG_CURRENT_TASK;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_PHOTO;

public abstract class AbstractTaskFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_DESCRIPTION = "description";
    private static final String ARG_TIMESTAMP = "timestamp";
    private static final String ARG_TEMP_FILENAME = "tempfilename";
    private static final int REQUEST_REAL_PHOTO = 3;
    private static final String DIALOG_IMAGE = "DialogImage";

    protected FloatingActionButton mMakePhotoButton;
    protected FloatingActionButton mSetNotificationButton;
    protected TextView mNotificationTimestamp;
    protected EditText mTitleField;
    protected EditText mDescriptionField;
    protected ImageView mPhoto;

    protected Task mTask;

    private PhotoManager mPhotoManager;
    private InitializationManager mInitializationManager;
    private TaskOperationsManager mTaskOperationsManager;

    private OnChangedCallback mCallback;

    private String mTempFileName;

    public static AbstractTaskFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_TASK, task);

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
        outState.putString(ARG_TEMP_FILENAME, mTempFileName);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task_fragment, container, false);

        mMakePhotoButton = view.findViewById(R.id.task_edit_make_photo);
        mSetNotificationButton = view.findViewById(R.id.task_edit_add_notification);
//      mNotificationTimestamp = view.findViewById(R.id.task_edit_notification_timestamp);
        mTitleField = view.findViewById(R.id.task_edit_title);
        mDescriptionField = view.findViewById(R.id.task_edit_description);
        mPhoto = view.findViewById(R.id.task_edit_photo);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        receiveArgsFromBundle();
        initManagers();
        configureButtons();
    }

    private void receiveArgsFromBundle() {
        mTempFileName = getArguments().getString(ARG_TEMP_FILENAME);
        mTask = getArguments().getParcelable(ARG_CURRENT_TASK);
    }

    private void initManagers() {
        mInitializationManager = InitializationManager.getInstance();
        mInitializationManager.setTask(mTask);
        mInitializationManager.initViewModel(this);
        mPhotoManager = PhotoManager.getInstance(mInitializationManager.getViewModel(), mTask);
        mTaskOperationsManager = TaskOperationsManager.getInstance(mInitializationManager.getViewModel());
        mTaskOperationsManager.setTask(mTask);
    }

    private void configureButtons() {
        configureSetNotificationButton();
        //configureNotificationTimestampButton();
        configureTitleField();
        configureDescriptionField();
        configureMakePhotoButton();
        configurePhotoView();
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
                mCallback.onDataChanged(true);
                mCallback.taskChanged(mTask);
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
                mCallback.onDataChanged(true);
                mCallback.taskChanged(mTask);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void configureMakePhotoButton() {
        mPhotoManager.configurePhotoButton(this, mMakePhotoButton);
    }

    private void configurePhotoView() {
        mPhoto.setOnClickListener((view) -> {
            if (getFragmentManager() != null) {
                RealImageFragment fragment = RealImageFragment.newInstance(mPhotoManager.getPhotoFile());
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
            mPhotoManager.markPhotoForDelete(this, mPhoto);
            return true;
        }));
        popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case REQUEST_PHOTO:
                mPhotoManager.getPhotoFromCamera(this, mPhoto);
        }
    }

    private void finishActivity() {
        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    protected void performPhotoUpdating() {
        if (mTempFileName == null)
            mTempFileName = FilenameGenerator.getTempName();
        mPhotoManager.setTempPhotoFileName(mTempFileName);
        mPhotoManager.setPhotoFile(mInitializationManager.getViewModel(), mTask);
        mPhotoManager.updatePhotoView(getActivity(), mPhoto);
    }

    protected void performSave() {
        mTaskOperationsManager.updateTask(getActivity());
        finishActivity();
    }

    protected void performDelete() {
        mTaskOperationsManager.deleteTask();
        finishActivity();
    }

    public TextView getNotificationTimestamp() {
        return mNotificationTimestamp;
    }

    public EditText getTitleField() {
        return mTitleField;
    }

    public EditText getDescriptionField() {
        return mDescriptionField;
    }

    public OnChangedCallback getCallback() {
        return mCallback;
    }

    public interface OnChangedCallback {
        void onDataChanged(boolean isChanged);
        void taskChanged(Task task);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = ((OnChangedCallback) context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
