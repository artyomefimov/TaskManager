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
import com.a_team.taskmanager.alarm.AlarmManager;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.managers.InitializationManager;
import com.a_team.taskmanager.ui.singletask.managers.PhotoManager;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManager;
import com.a_team.taskmanager.ui.singletask.managers.TaskOperationsManagerKeeper;
import com.a_team.taskmanager.ui.singletask.managers.UIUpdateManager;
import com.a_team.taskmanager.ui.singletask.managers.alarms.AlarmDateTimePicker;
import com.a_team.taskmanager.utils.ToastMaker;

import java.util.Date;

import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_CURRENT_TASK;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_DESCRIPTION;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_IS_ALARM_REMOVED;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_IS_ALARM_SET;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_TIMESTAMP;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.ARG_TITLE;
import static com.a_team.taskmanager.ui.singletask.SingleTaskConstants.DIALOG_IMAGE;
import static com.a_team.taskmanager.utils.RequestCodeStorage.MAKE_PHOTO_REQUEST_CODE;
import static com.a_team.taskmanager.utils.RequestCodeStorage.REAL_PHOTO_REQUEST_CODE;

public abstract class AbstractTaskFragment extends Fragment implements AlarmDateTimePicker.OnChangedNotificationDateCallback {
    protected FloatingActionButton mSetNotificationButton;
    protected FloatingActionButton mDeleteNotificationButton;
    protected TextView mNotificationTimestamp;
    private FloatingActionButton mMakePhotoButton;
    private EditText mTitleField;
    private EditText mDescriptionField;
    private ImageView mPhoto;

    private PhotoManager mPhotoManager;
    private InitializationManager mInitializationManager;
    private TaskOperationsManager mTaskOperationsManager;

    protected OnChangedCallback mCallback;

    protected boolean isAlarmSet;
    protected boolean isAlarmRemoved;

    protected Task mTask;

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
        if (savedInstanceState != null) {
            isAlarmSet = savedInstanceState.getBoolean(ARG_IS_ALARM_SET);
            isAlarmRemoved = savedInstanceState.getBoolean(ARG_IS_ALARM_REMOVED);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(ARG_TITLE, mTitleField.getText().toString());
        outState.putString(ARG_DESCRIPTION, mDescriptionField.getText().toString());
        outState.putString(ARG_TIMESTAMP, mNotificationTimestamp.getText().toString());
        outState.putBoolean(ARG_IS_ALARM_SET, isAlarmSet);
        outState.putBoolean(ARG_IS_ALARM_REMOVED, isAlarmRemoved);
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
        Bundle args = getArguments();
        if (args == null)
            ToastMaker.show(getActivity(), R.string.incorrect_task, ToastMaker.ToastPeriod.SHORT);
        else
            mTask = getArguments().getParcelable(ARG_CURRENT_TASK);
    }

    private void initManagers() {
        mInitializationManager = new InitializationManager(mTask);
        mInitializationManager.initViewModel(this);
        mPhotoManager = new PhotoManager(mInitializationManager.getViewModel(), mTask);
        mTaskOperationsManager = new TaskOperationsManager(mInitializationManager.getViewModel(), mTask);
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

    protected abstract void configureSetNotificationButton();

    protected abstract void configureDeleteNotificationButton();

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
                fragment.setTargetFragment(AbstractTaskFragment.this, REAL_PHOTO_REQUEST_CODE);
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
            case MAKE_PHOTO_REQUEST_CODE:
                mPhotoManager.getPhotoFromCamera(this, mPhoto);
        }
    }

    private void finishActivity() {
        Activity activity = getActivity();
        if (activity != null) {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
    }

    protected void performPhotoUpdating() {
        mPhotoManager.setPhotoFile(mInitializationManager.getViewModel());
        mPhotoManager.updatePhotoView(getActivity(), mPhoto);
    }

    protected void performSave() {
        if (isAlarmSet) {
            AlarmManager.addNotification(getActivity(), mTask);
        }
        if (isAlarmRemoved)
            AlarmManager.removeNotification(getActivity(), mTask);
        mTaskOperationsManager.updateOrInsertTask(getActivity());
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
        isAlarmSet = true;
        isAlarmRemoved = false;
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