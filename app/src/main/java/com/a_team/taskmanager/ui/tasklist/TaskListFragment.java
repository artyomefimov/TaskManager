package com.a_team.taskmanager.ui.tasklist;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.TaskListViewModel;
import com.a_team.taskmanager.model.Task;
import com.a_team.taskmanager.ui.taskedit.TaskEditActivity;;

import java.util.ArrayList;
import java.util.List;

public class TaskListFragment extends Fragment {

    private static final String TAG = "TaskListFragment";
    private static final int REQUEST_CODE = 1;

    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private List<Task> mTasks;

    private TaskListViewModel mViewModel;

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
        mViewModel = ViewModelProviders.of(this).get(TaskListViewModel.class);
    }

    private void subscribeUi() {
        mViewModel.getTasks().observe(this, new Observer<List<Task>>() {
            @Override
            public void onChanged(@Nullable List<Task> tasks) {
                mTasks = tasks != null ? tasks : new ArrayList<Task>();
                updateRecyclerViewAdapter(mTasks);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_list_fragment, container, false);

        mTasks = new ArrayList<>();

        mRecyclerView = view.findViewById(R.id.recycler_view_task_list);
        configureRecyclerView();

        fab = view.findViewById(R.id.fab);
        configureFloatingActionButton();

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new TaskListAdapter(mTasks));
    }

    private void configureFloatingActionButton() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = TaskEditActivity.newIntent(getActivity(), Task.emptyTask());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
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
                Log.d(TAG, "onQueryTextSubmit: ");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(TAG, "onQueryTextChange: ");
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                Snackbar.make(getView(), "Search", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case R.id.menu_sync:
                Snackbar.make(getView(), "Synchronize", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateRecyclerViewAdapter(List<Task> tasks) {
        if (isAdded()) {
            ((TaskListAdapter) mRecyclerView.getAdapter()).setData(tasks);
        }
    }

    private class TaskListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Task mTask;
        private TextView mTitle;
        private TextView mDescription;
        private CardView mCardView;
        private ImageView mImage;

        public TaskListViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));

            mTitle = itemView.findViewById(R.id.task_title);
            mDescription = itemView.findViewById(R.id.task_description);
            mCardView = itemView.findViewById(R.id.card_view_task);
            mImage = itemView.findViewById(R.id.task_image);

            itemView.setOnClickListener(this);
        }

        public void bind(Task task) {
            mTask = task;
            mTitle.setText(task.getTitle());
            mDescription.setText(task.getDescription());
        }

        @Override
        public void onClick(View v) {
            Intent intent = TaskEditActivity.newIntent(getActivity(), mTask);
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    private class TaskListAdapter extends RecyclerView.Adapter<TaskListViewHolder> {
        private List<Task> mTasks;

        public TaskListAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @NonNull
        @Override
        public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new TaskListViewHolder(inflater, parent);
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
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
