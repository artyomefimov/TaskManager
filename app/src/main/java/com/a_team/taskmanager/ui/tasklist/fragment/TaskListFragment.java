package com.a_team.taskmanager.ui.tasklist.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.search.activity.SearchActivity;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.managers.InitializationManager;
import com.a_team.taskmanager.ui.tasklist.managers.MultipleSelectManager;
import com.a_team.taskmanager.ui.tasklist.managers.SwipeDeleteAsyncTask;
import com.a_team.taskmanager.ui.tasklist.managers.SwipeToDeleteCallback;
import com.a_team.taskmanager.ui.tasklist.viewmodel.TaskListViewModel;
import com.a_team.taskmanager.utils.IntentBuilder;
import com.a_team.taskmanager.utils.SnackbarMaker;
import com.a_team.taskmanager.utils.ToastMaker;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.a_team.taskmanager.ui.tasklist.TaskListConstants.REQUEST_PERMISSIONS;
import static com.a_team.taskmanager.utils.RequestCodeStorage.CHOOSE_BACKUP_REQUEST_CODE;
import static com.a_team.taskmanager.utils.RequestCodeStorage.SELECT_TASK_REQUEST_CODE;

public class TaskListFragment extends Fragment implements MultipleSelectManager.MultipleSelectActionModeFinishedCallback {
    private RecyclerView mRecyclerView;
    private FloatingActionButton mNewTaskButton;

    private InitializationManager mInitializationManager;
    private MultipleSelectManager mMultipleSelectManager;

    private List<Task> mTasks;

    private ItemTouchHelper mSwipeDeleteTouch;

    public static TaskListFragment newInstance() {
        Bundle args = new Bundle();

        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInitializationManager = new InitializationManager();
        mInitializationManager.createViewModelAndSubscribeUI(this);

        mMultipleSelectManager = new MultipleSelectManager(mInitializationManager, this);
        mMultipleSelectManager.configureActionModeCallback(this);

        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearActionBarSubtitle();
    }

    private void clearActionBarSubtitle() {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        if (activity != null && activity.getSupportActionBar() != null)
            activity.getSupportActionBar().setSubtitle(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        mTasks = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.recycler_view_task_list);
        configureRecyclerView();

        mNewTaskButton = view.findViewById(R.id.fab);
        configureNewTaskButton();

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TaskListAdapter(mTasks));
        mSwipeDeleteTouch = new ItemTouchHelper(new SwipeToDeleteCallback((TaskListAdapter) mRecyclerView.getAdapter(), getActivity()));
        mSwipeDeleteTouch.attachToRecyclerView(mRecyclerView);
    }

    private void configureNewTaskButton() {
        mNewTaskButton.setOnClickListener(view -> {
            Task emptyTask = Task.emptyTask();
            Intent intent = SingleTaskActivity.newIntent(getActivity(), emptyTask);
            startActivityForResult(intent, SELECT_TASK_REQUEST_CODE);
        });
    }

    public void updateRecyclerViewAdapter(List<Task> tasks) {
        if (isAdded()) {
            ((TaskListAdapter) mRecyclerView.getAdapter()).setData(tasks);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_task_list, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = ((SearchView) searchItem.getActionView());
        configureSearchView(searchView);

        MenuItem storeItem = menu.findItem(R.id.menu_store);
        configureStoreItem(storeItem);

        MenuItem restoreItem = menu.findItem(R.id.menu_restore);
        configureRestoreItem(restoreItem);
    }

    private void configureSearchView(final SearchView searchView) {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard(getActivity(), getView());
                startSearchActivity(query);
                return true;
            }

            private void startSearchActivity(String query) {
                TaskListFragment taskListFragment = TaskListFragment.this;
                Intent intent = SearchActivity.newIntent(taskListFragment.getActivity(), query);
                taskListFragment.startActivity(intent);
            }

            private void hideKeyboard(Context context, View view) {
                if (context != null) {
                    InputMethodManager imm = ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE));
                    if (imm != null)
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void configureStoreItem(MenuItem storeItem) {
        storeItem.setOnMenuItemClickListener(item -> {
            Activity activity = TaskListFragment.this.getActivity();
            TaskListViewModel viewModel = mInitializationManager.getViewModel();
            List<Task> actualTasks = mInitializationManager.getTasks();
            try {
                requestPermissionsIfNecessary(activity);
                viewModel.storeTasksToBackup(activity, actualTasks);
            } catch (IOException e) {
                ToastMaker.show(activity, e.getMessage(), ToastMaker.ToastPeriod.LONG);
            }
            return true;
        });
    }

    private void requestPermissionsIfNecessary(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionWrite = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int permissionRead = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);

            if (permissionRead != PackageManager.PERMISSION_GRANTED && permissionWrite != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                activity.requestPermissions(permissions, REQUEST_PERMISSIONS.hashCode());
            }
        }
    }

    private void configureRestoreItem(MenuItem restoreItem) {
        restoreItem.setOnMenuItemClickListener(item -> {
            Activity activity = TaskListFragment.this.getActivity();
            Intent intent = IntentBuilder.buildIntentForChoosingBackupFile();
            if (activity != null)
                activity.startActivityForResult(intent, CHOOSE_BACKUP_REQUEST_CODE);
            return true;
        });
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK)
            return;
        switch (requestCode) {
            case CHOOSE_BACKUP_REQUEST_CODE:
                Activity activity = TaskListFragment.this.getActivity();
                Uri backupUri = data.getData();
                if (backupUri != null) {
                    try {
                        mInitializationManager.getViewModel().addTasksFromBackupToDatabase(activity, backupUri);
                    } catch (IOException e) {
                        ToastMaker.show(activity, e.getMessage(), ToastMaker.ToastPeriod.LONG);
                    }
                } else
                    ToastMaker.show(activity, R.string.incorrect_file_to_restore, ToastMaker.ToastPeriod.SHORT);
        }
    }

    @Override
    public void onActionModeFinished() {
        mSwipeDeleteTouch.attachToRecyclerView(mRecyclerView);
    }

    /**
     * View holder to show single action in recycler view
     */

    public class TaskListViewHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {
        private Task mTask;
        private TextView mTitle;
        private TextView mDescription;

        private TaskListViewHolder(View view, MultiSelector selector) {
            super(view, selector);

            mTitle = itemView.findViewById(R.id.task_title);
            mDescription = itemView.findViewById(R.id.task_description);

            itemView.setOnClickListener(this);

            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        private void bind(Task task) {
            mTask = task;
            mTitle.setText(task.getTitle());
            mDescription.setText(task.getDescription());
        }

        @Override
        public void onClick(View v) {
            mMultipleSelectManager.performClick(this, TaskListFragment.this, mTask, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            mSwipeDeleteTouch.attachToRecyclerView(null);
            return mMultipleSelectManager.performLongClick(this, TaskListFragment.this, getAdapterPosition());
        }
    }

    /**
     * Adapter for recycler view that holds all tasks from database
     */

    public class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {
        private List<Task> mTasks;
        private Task mRecentlyDeletedTask;
        private int mRecentlyDeletedItemPosition;
        private SwipeDeleteAsyncTask mAsyncSwipeDelete;

        private TaskListAdapter(List<Task> tasks) {
            mTasks = tasks;
            mMultipleSelectManager.setTasks(tasks);
        }

        @NonNull
        @Override
        public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_task, parent, false);
            return new TaskListViewHolder(view, mMultipleSelectManager.getMultiSelector());
        }

        @Override
        public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        private void setData(List<Task> tasks) {
            mTasks = tasks;
            mMultipleSelectManager.setTasks(tasks);
            notifyDataSetChanged();
        }

        public void deleteItem(int position) {
            mRecentlyDeletedTask = mTasks.get(position);
            mRecentlyDeletedItemPosition = position;
            mTasks.remove(position);
            notifyItemRemoved(position);

            mAsyncSwipeDelete = new SwipeDeleteAsyncTask(mInitializationManager.getViewModel(), mRecentlyDeletedTask);
            mAsyncSwipeDelete.execute();

            showUndoSnackbar();
        }

        private void showUndoSnackbar() {
            Snackbar undoSnackbar = SnackbarMaker.makeUndoDeleteSnackbar(getActivity());
            if (undoSnackbar != null) {
                undoSnackbar.setAction(R.string.undo_remove, (view) -> undoDelete());
                undoSnackbar.show();
            }
        }

        private void undoDelete() {
            mAsyncSwipeDelete.cancel(true);
            mTasks.add(mRecentlyDeletedItemPosition, mRecentlyDeletedTask);
            notifyItemInserted(mRecentlyDeletedItemPosition);
        }
    }
}
