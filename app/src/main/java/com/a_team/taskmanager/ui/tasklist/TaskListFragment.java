package com.a_team.taskmanager.ui.tasklist;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.TaskListViewModel;
import com.a_team.taskmanager.controller.utils.PictureUtils;
import com.a_team.taskmanager.controller.utils.TaskSearchUtil;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.SingleTaskActivity;
import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    private static final int REQUEST_CODE = 1;
    private static final String SEARCH_FRAGMENT = "searchFragment";

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private TaskListViewModel mViewModel;

    private MultiSelector mMultiSelector;
    private ModalMultiSelectorCallback mActionModeCallback;

    private TaskSearchUtil mSearchUtil;
    private List<Task> mTasks;

    public static TaskListFragment newInstance() {
        Bundle args = new Bundle();

        TaskListFragment fragment = new TaskListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createViewModel();
        subscribeUi();
    }

    private void createViewModel() {
        TaskListViewModel.Factory factory =
                new TaskListViewModel.Factory(getActivity().getApplication());
        mViewModel = ViewModelProviders.of(this, factory).get(TaskListViewModel.class);
    }

    private void subscribeUi() {
        mViewModel.getTasks().observe(this, tasks -> {
            if (tasks != null) {
                mTasks = tasks;
                mSearchUtil.setStringTaskData(tasks);
                updateRecyclerViewAdapter(mTasks);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        clearActionBarSubtitle();
    }

    private void clearActionBarSubtitle() {
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setSubtitle(null);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        mTasks = new ArrayList<>();
        mSearchUtil = TaskSearchUtil.getInstance();

        mRecyclerView = view.findViewById(R.id.recycler_view_task_list);
        configureRecyclerView();

        mFloatingActionButton = view.findViewById(R.id.fab);
        configureFloatingActionButton();

        mMultiSelector = new MultiSelector();
        configureActionModeCallback();

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TaskListAdapter(mTasks));
    }

    private void configureFloatingActionButton() {
        mFloatingActionButton.setOnClickListener(view -> {
            Intent intent = SingleTaskActivity.newIntent(getActivity(), Task.emptyTask());
            startActivityForResult(intent, REQUEST_CODE);
        });
    }

    private void configureActionModeCallback() {
        mActionModeCallback = new ModalMultiSelectorCallback(mMultiSelector) {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                super.onCreateActionMode(actionMode, menu);
                getActivity().getMenuInflater().inflate(R.menu.menu_task_list_select_mode, menu);
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (item.getItemId() == R.id.task_list_delete) {
                    mode.finish();

                    deleteSelectedTasks();

                    mMultiSelector.clearSelections();
                    return true;
                }
                return false;
            }
        };
    }

    private void deleteSelectedTasks() {
        TaskListAdapter adapter = ((TaskListAdapter) mRecyclerView.getAdapter());
        Task[] tasksToDelete = adapter.mSelectedTasksIds.toArray(new Task[adapter.mSelectedTasksIds.size()]);
        mViewModel.deleteTasks(tasksToDelete);
        adapter.notifyDataSetChanged();
    }

    private void updateRecyclerViewAdapter(List<Task> tasks) {
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

        MenuItem syncItem = menu.findItem(R.id.menu_sync);
        configureSyncItem(syncItem);
    }

    private void configureSearchView(final SearchView searchView) {
        searchView.setSubmitButtonEnabled(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                hideKeyboard(getActivity(), getView());
                startSearchFragment(query);
                return true;
            }

            private void startSearchFragment(String query) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    SearchFragment searchFragment = SearchFragment.newInstance(query);
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, searchFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            private void hideKeyboard(Context context, View view) {
                InputMethodManager imm = ((InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE));
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: ");
                return false;
            }
        });
    }

    private void configureSyncItem(MenuItem syncItem) {
        syncItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Snackbar.make(getView(), "Synchronize", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case R.id.menu_sync:
                Snackbar.make(getView(), "Synchronize", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * View holder to show single action in recycler view
     */

    private class TaskListViewHolder extends SwappingHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private Task mTask;
        private TextView mTitle;
        private TextView mDescription;
        private ImageView mImage;

        private TaskListViewHolder(View view, MultiSelector selector) {
            super(view, selector);

            mTitle = itemView.findViewById(R.id.task_title);
            mDescription = itemView.findViewById(R.id.task_description);
            mImage = itemView.findViewById(R.id.task_image);

            itemView.setOnClickListener(this);

            itemView.setLongClickable(true);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Task task) {
            mTask = task;
            mTitle.setText(task.getTitle());
            mDescription.setText(task.getDescription());
            if (hasPhoto(task)) {
                Bitmap scaledBitmap = PictureUtils.getScaledBitmap(task.getPhotoFile().getPath(), getActivity());
                mImage.setImageBitmap(scaledBitmap);
            }
        }

        private boolean hasPhoto(Task task) {
            return task.getPhotoFile() != null;
        }

        @Override
        public void onClick(View v) {
            if (!mMultiSelector.tapSelection(TaskListViewHolder.this)) {
                Intent intent = SingleTaskActivity.newIntent(getActivity(), mTask);
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                if (isCurrentTaskAlreadySelected()) {
                    removeSelection();
                } else {
                    selectCurrentTask();
                }
            }
        }

        private boolean isCurrentTaskAlreadySelected() {
            return !mMultiSelector.isSelected(getAdapterPosition(), 0);
        }

        private void removeSelection() {
            mMultiSelector.setSelected(TaskListViewHolder.this, false);
            TaskListAdapter adapter = ((TaskListAdapter) mRecyclerView.getAdapter());
            adapter.removeSelectedTask(getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            Log.i(TAG, "onLongClick method is running");
            if (!mMultiSelector.isSelectable()) {
                ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
                mMultiSelector.setSelectable(true);

                selectCurrentTask();
                return true;
            }
            return false;
        }

        private void selectCurrentTask() {
            mMultiSelector.setSelected(TaskListViewHolder.this, true);
            TaskListAdapter adapter = ((TaskListAdapter) mRecyclerView.getAdapter());
            adapter.addSelectedTask(getAdapterPosition());
        }
    }

    /**
     * Adapter for recycler view that holds all tasks from database
     */

    private class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {
        private List<Task> mTasks;
        private List<Task> mSelectedTasksIds;

        private TaskListAdapter(List<Task> tasks) {
            mTasks = tasks;
            mSelectedTasksIds = new ArrayList<>();
        }

        @NonNull
        @Override
        public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_task, parent, false);
            return new TaskListViewHolder(view, mMultiSelector);
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
            notifyDataSetChanged();
        }

        private void addSelectedTask(int position) {
            Task task = mTasks.get(position);
            mSelectedTasksIds.add(task);
        }

        private void removeSelectedTask(int position) {
            Task task = mTasks.get(position);
            mSelectedTasksIds.remove(task);
            if (mSelectedTasksIds.size() == 0) {
                mMultiSelector.setSelectable(false);
                mActionModeCallback.onDestroyActionMode(null);
            }
        }
    }
}
