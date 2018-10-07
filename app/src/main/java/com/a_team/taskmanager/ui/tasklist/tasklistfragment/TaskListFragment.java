package com.a_team.taskmanager.ui.tasklist.tasklistfragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.utils.FilenameGenerator;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers.PhotoNameContainer;
import com.a_team.taskmanager.ui.tasklist.searchfragment.SearchFragment;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers.InitializationManager;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers.MultipleSelectManager;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    public static final int REQUEST_CODE = 1;
    private static final String SEARCH_FRAGMENT = "searchFragment";

    private RecyclerView mRecyclerView;
    private FloatingActionButton mNewTaskButton;

    private InitializationManager mInitializationManager;
    private MultipleSelectManager mMultipleSelectManager;

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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mInitializationManager = InitializationManager.getInstance();
        mInitializationManager.createViewModelAndSubscribeUI(this);
        mMultipleSelectManager = MultipleSelectManager.getInstance();
        mMultipleSelectManager.configureActionModeCallback(this);
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

        mRecyclerView = view.findViewById(R.id.recycler_view_task_list);
        configureRecyclerView();

        mNewTaskButton = view.findViewById(R.id.fab);
        configureNewTaskButton();

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TaskListAdapter(mTasks));
    }

    private void configureNewTaskButton() {
        mNewTaskButton.setOnClickListener(view -> {
            Task emptyTask = Task.emptyTask();
            String tempPhotoName = FilenameGenerator.getTempName();
            PhotoNameContainer.getInstance().putName(emptyTask.getId(), tempPhotoName);
            Intent intent = SingleTaskActivity.newIntent(getActivity(), emptyTask);
            startActivityForResult(intent, REQUEST_CODE);
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

        MenuItem syncItem = menu.findItem(R.id.menu_sync);
        configureSyncItem(syncItem);
    }

    private void configureSearchView(final SearchView searchView) {
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
                return false;
            }
        });
    }

    private void configureSyncItem(MenuItem syncItem) {
        syncItem.setOnMenuItemClickListener(item -> {
            Snackbar.make(getView(), "Synchronize", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return false;
        });
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * View holder to show single action in recycler view
     */

    public class TaskListViewHolder extends SwappingHolder
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
            return mMultipleSelectManager.performLongClick(this, TaskListFragment.this, getAdapterPosition());
        }
    }

    /**
     * Adapter for recycler view that holds all tasks from database
     */

    public class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {
        private List<Task> mTasks;

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
    }
}
