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
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.notification.NotificationManager;
import com.a_team.taskmanager.ui.singletask.managers.InitializationManager;
import com.a_team.taskmanager.ui.singletask.managers.UIUpdateManager;
import com.a_team.taskmanager.ui.singletask.managers.notifications.NotificationDateTimePicker;
import com.a_team.taskmanager.ui.singletask.managers.PhotoManager;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManager;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManagerKeeper;
import com.a_team.taskmanager.utils.DateFormatter;

import java.util.Date;

import static com.a_team.taskmanager.ui.singletask.Constants.ARG_CURRENT_TASK;
import static com.a_team.taskmanager.ui.singletask.Constants.ARG_DESCRIPTION;
import static com.a_team.taskmanager.ui.singletask.Constants.ARG_TIMESTAMP;
import static com.a_team.taskmanager.ui.singletask.Constants.ARG_TITLE;
import static com.a_team.taskmanager.ui.singletask.Constants.DIALOG_IMAGE;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_PHOTO;
import static com.a_team.taskmanager.ui.singletask.Constants.REQUEST_REAL_PHOTO;

public abstract class AbstractTaskFragment extends Fragment implements NotificationDateTimePicker.OnChangedNotificationDateCallback{
    protected FloatingActionButton mMakePhotoButton;
    protected FloatingActionButton mSetNotificationButton;
    protected FloatingActionButton mDeleteNotificationButton;
    protected TextView mNotificationTimestamp;
    protected EditText mTitleField;
    protected EditText mDescriptionField;
    protected ImageView mPhoto;

    protected Task mTask;

    private PhotoManager mPhotoManager;
    private InitializationManager mInitializationManager;
    private TaskOperationsManager mTaskOperationsManager;

    private OnChangedCallback mCallback;

    public static AbstractTaskFragment newInstance(Task task) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_TASK, task);

        AbstractTaskFragment fragment;
        boolean isEmptyTask = task.equals(Task.emptyTask());
        fragment = isEmptyTask ? new NewTaskFragment() : new TaskEditFragment();
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
        outState.putString(ARG_TITLE, mTitleField.getText().toString());
        outState.putString(ARG_DESCRIPTION, mDescriptionField.getText().toString());
        outState.putString(ARG_TIMESTAMP, mNotificationTimestamp.getText().toString());
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_task_fragment, container, false);

        mMakePhotoButton = view.findViewById(R.id.task_edit_make_photo);
        mSetNotificationButton = view.findViewById(R.id.task_edit_add_notification);
        mDeleteNotificationButton = view.findViewById(R.id.task_edit_delete_notification);
        mNotificationTimestamp = view.findViewById(R.id.task_edit_notification_timestamp);
        mTitleField = view.findViewById(R.id.task_edit_title);
        mDescriptionField = view.findViewById(R.id.task_edit_description);
        mPhoto = view.findViewById(R.id.task_edit_photo);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initManagers();
        configureButtons();
        performPhotoUpdating();
    }

    protected void receiveArgsFromBundle() {
        mTask = getArguments().getParcelable(ARG_CURRENT_TASK);
    }

    private void initManagers() {
        mInitializationManager = new InitializationManager(mTask);
        mInitializationManager.initViewModel(this);
        mPhotoManager = new PhotoManager(mInitializationManager.getViewModel(), mTask);
        mTaskOperationsManager = new TaskOperationsManager(mInitializationManager.getViewModel(), mTask, mPhotoManager);
        TaskOperationsManagerKeeper.getInstance().setTaskOperationsManager(mTaskOperationsManager);
    }

    private void configureButtons() {
        configureSetNotificationButton();
        configureDeleteNotificationButton();
        configureNotificationTimestampButton();
        configureTitleField();
        configureDescriptionField();
        configureMakePhotoButton();
        configurePhotoView();
    }

    private void configureSetNotificationButton() {
        mSetNotificationButton.setOnClickListener((view) -> {
            NotificationDateTimePicker notificationDateTimePicker = new NotificationDateTimePicker(this);
            notificationDateTimePicker.showDateTimePicker(this);
        });
    }

    private void configureDeleteNotificationButton() {
        mDeleteNotificationButton.setOnClickListener((view) -> {
            mTask.setNotificationDate(null);
            new NotificationManager().removeNotification();
            UIUpdateManager.removeNotificationText(mNotificationTimestamp);
            mCallback.onDataChanged(true);
        });
    }

    private void configureNotificationTimestampButton() {
        UIUpdateManager.setNotificationText(mNotificationTimestamp, mTask.getNotificationDate());
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
        mPhotoManager.setPhotoFile(mInitializationManager.getViewModel());
        mPhotoManager.updatePhotoView(getActivity(), mPhoto);
    }

    protected void performSave() {
        mTaskOperationsManager.updateTask(getActivity());
        finishActivity();
    }

    protected void performDelete() {
        mTaskOperationsManager.deleteTask(getActivity());
        finishActivity();
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

    @Override
    public void onNotificationDateChanged(Date newDate) {
        mTask.setNotificationDate(newDate.getTime());
        UIUpdateManager.setNotificationText(mNotificationTimestamp, mTask.getNotificationDate());
        mCallback.onDataChanged(true);
    }

    public interface OnChangedCallback {
        void onDataChanged(boolean isChanged);
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