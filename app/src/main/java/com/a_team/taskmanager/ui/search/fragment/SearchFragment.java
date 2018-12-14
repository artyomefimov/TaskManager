package com.a_team.taskmanager.ui.search.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.search.managers.InitializationManager;
import com.a_team.taskmanager.ui.singletask.activity.SingleTaskActivity;
import com.a_team.taskmanager.utils.TaskSearchUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.a_team.taskmanager.utils.RequestCodeStorage.SELECT_TASK_FROM_SEARCH_REQUEST_CODE;

public class SearchFragment extends Fragment {
    private static final String QUERY = "query";
    private static final String IDS_OF_FOUND_TASKS = "idsOfFoundTasks";

    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;
    private TextView mNoResultsTextView;
    private InitializationManager mInitializationManager;

    private String query;

    private ArrayList<Long> mIdsOfFoundTasks;

    public static SearchFragment newInstance(String query) {
        Bundle args = new Bundle();

        args.putString(QUERY, query);
        SearchFragment fragment = new SearchFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        query = getArguments().getString(QUERY);

        Serializable ids = getArguments().getSerializable(IDS_OF_FOUND_TASKS);
        mIdsOfFoundTasks = ids == null ? new ArrayList<>() : (ArrayList<Long>) ids;

        // todo подумать, в какой момент запускать асинхронный поиск и как обрабатывать результаты. Сейчас выкидывается npe, когда поиск обращается к полям фрагмента

        setActionBarSubtitle();
    }

    private void setActionBarSubtitle() {
        String subtitle = "Search results for: \"" +
                query +
                "\"";
        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void addIdsOfFoundTasks(List<Task> foundTasks) {
        for (Task task : foundTasks) {
            mIdsOfFoundTasks.add(task.getId());
        }
    }

    public void updateRecyclerViewAdapter(List<Task> tasks) {
        if (isAdded()) {
            ((SearchAdapter) mRecyclerView.getAdapter()).setTasks(tasks);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(QUERY, query);
        outState.putSerializable(IDS_OF_FOUND_TASKS, mIdsOfFoundTasks);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_search);
        configureRecyclerView();

        mProgressBar = view.findViewById(R.id.search_fragment_progress_bar);
        mNoResultsTextView = view.findViewById(R.id.search_fragment_no_results);

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SearchAdapter(Collections.emptyList()));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mIdsOfFoundTasks.size() == 0)
            new AsyncSearchTask().execute();
    }

    private void initManager() {
        mInitializationManager = new InitializationManager();
        mInitializationManager.createViewModelAndSubscribeUI(this, mIdsOfFoundTasks);
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Task mTask;
        private TextView mTitle;
        private TextView mDescription;

        private SearchViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_task, parent, false));

            mTitle = itemView.findViewById(R.id.task_title);
            mDescription = itemView.findViewById(R.id.task_description);

            itemView.setOnClickListener(this);
        }

        private void bind(Task task) {
            mTask = task;
            mTitle.setText(task.getTitle());
            mDescription.setText(task.getDescription());
        }

        @Override
        public void onClick(View v) {
            Intent intent = SingleTaskActivity.newIntent(getActivity(), mTask);
            startActivityForResult(intent, SELECT_TASK_FROM_SEARCH_REQUEST_CODE);
        }
    }

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<Task> mTasks;

        private SearchAdapter(List<Task> tasks) {
            mTasks = tasks;
        }

        @NonNull
        @Override
        public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new SearchViewHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount() {
            return mTasks.size();
        }

        public void setTasks(List<Task> tasks) {
            mTasks = tasks;
            notifyDataSetChanged();
        }
    }

    private class AsyncSearchTask extends AsyncTask<Void, Void, List<Task>> {
        private List<Task> mTasksFromSearch;

        @Override
        protected void onPreExecute() {
            showProgressBar();
        }

        private void showProgressBar() {
            if (isActivityRunning()) {
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Task> doInBackground(Void... voids) {
            mTasksFromSearch = TaskSearchUtil.getInstance().performSearch(query);
            return mTasksFromSearch;
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            if (isNoResults(mTasksFromSearch)) {
                showNoResultsText();
            } else {
                addIdsOfFoundTasks(mTasksFromSearch);
                hideProgressBar();
            }
            initManager();
        }

        private boolean isNoResults(List<Task> tasksFromSearch) {
            return tasksFromSearch.equals(Collections.emptyList());
        }

        private void showNoResultsText() {
            if (isActivityRunning()) {
                mRecyclerView.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.GONE);
                mNoResultsTextView.setVisibility(View.VISIBLE);
            }
        }

        private void hideProgressBar() {
            if (isActivityRunning()) {
                mRecyclerView.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
            }
        }

        private boolean isActivityRunning() {
            Activity activity = SearchFragment.this.getActivity();
            return activity != null && !(activity.isFinishing() || activity.isDestroyed());
        }
    }
}
