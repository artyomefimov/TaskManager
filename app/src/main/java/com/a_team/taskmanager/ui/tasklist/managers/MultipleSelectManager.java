package com.a_team.taskmanager.ui.tasklist.managers;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.fragment.TaskListFragment;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SelectableHolder;

import java.util.ArrayList;
import java.util.List;

import static com.a_team.taskmanager.utils.RequestCodeStorage.SELECT_TASK_REQUEST_CODE;

public class MultipleSelectManager {

    public interface MultipleSelectActionModeFinishedCallback {
        void onActionModeFinished();
    }

    private MultiSelector mMultiSelector;
    private ModalMultiSelectorCallback mActionModeCallback;
    private InitializationManager mInitializationManager;

    private List<Task> mSelectedTasks;
    private List<Task> mTasks;

    private ActionMode mActionMode;

    private MultipleSelectActionModeFinishedCallback mCallback;

    public MultipleSelectManager(InitializationManager initializationManager, MultipleSelectActionModeFinishedCallback callback) {
        mMultiSelector = new MultiSelector();
        mInitializationManager = initializationManager;
        mSelectedTasks = new ArrayList<>();

        mCallback = callback;
    }

    public void configureActionModeCallback(TaskListFragment fragment) {
        mActionModeCallback = new ModalMultiSelectorCallback(mMultiSelector) {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                super.onCreateActionMode(actionMode, menu);
                fragment.getActivity().getMenuInflater().inflate(R.menu.menu_task_list_select_mode, menu);
                mActionMode = actionMode;
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.task_list_delete) {
                    finishActionMode();

                    deleteSelectedTasks(fragment);

                    mMultiSelector.clearSelections();
                    return true;
                }
                return false;
            }
        };
    }

    private void finishActionMode() {
        mActionMode.finish();
        mCallback.onActionModeFinished();
    }

    private void deleteSelectedTasks(TaskListFragment fragment) {
        TaskListFragment.TaskListAdapter adapter = ((TaskListFragment.TaskListAdapter) fragment.getRecyclerView().getAdapter());
        Task[] tasksToDelete = mSelectedTasks.toArray(new Task[mSelectedTasks.size()]);
        mInitializationManager.getViewModel().deleteTasks(tasksToDelete);
        adapter.notifyDataSetChanged();
    }

    public void performClick(SelectableHolder holder, TaskListFragment fragment, Task task, int position) {
        if (!mMultiSelector.tapSelection(holder)) {
            Intent intent = SingleTaskActivity.newIntent(fragment.getActivity(), task);
            fragment.startActivityForResult(intent, SELECT_TASK_REQUEST_CODE);
        } else {
            if (isCurrentTaskAlreadySelected(position)) {
                removeSelection(holder, position);
                finishActionModeIfLastTaskUnselected();
            } else {
                selectCurrentTask(holder, position);
            }
        }
    }

    private boolean isCurrentTaskAlreadySelected(int position) {
        return !mMultiSelector.isSelected(position, 0);
    }

    private void removeSelection(SelectableHolder holder, int position) {
        mMultiSelector.setSelected(holder, false);
        removeSelectedTask(position);
    }

    private void selectCurrentTask(SelectableHolder holder, int position) {
        mMultiSelector.setSelected(holder, true);
        addSelectedTask(position);
    }

    private void addSelectedTask(int position) {
        Task task = mTasks.get(position);
        mSelectedTasks.add(task);
    }

    private void finishActionModeIfLastTaskUnselected() {
        if (mSelectedTasks.size() == 0)
            finishActionMode();
    }

    private void removeSelectedTask(int position) {
        Task task = mTasks.get(position);
        mSelectedTasks.remove(task);
        if (mSelectedTasks.size() == 0) {
            mMultiSelector.setSelectable(false);
        }
    }

    public boolean performLongClick(SelectableHolder holder, TaskListFragment fragment, int position) {
        if (!mMultiSelector.isSelectable()) {
            ((AppCompatActivity) fragment.getActivity()).startSupportActionMode(mActionModeCallback);
            mMultiSelector.setSelectable(true);

            selectCurrentTask(holder, position);
            return true;
        }
        return false;
    }

    public MultiSelector getMultiSelector() {
        return mMultiSelector;
    }

    public void setTasks(List<Task> tasks) {
        mTasks = tasks;
    }
}