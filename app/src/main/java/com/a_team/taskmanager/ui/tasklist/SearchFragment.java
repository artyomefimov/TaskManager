package com.a_team.taskmanager.ui.tasklist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.controller.utils.TaskSearchUtil;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.ui.taskedit.TaskEditActivity;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchFragment extends Fragment {
    private static final String QUERY = "query";
    private static final int REQUEST_CODE = 2;

    private RecyclerView mRecyclerView;
    private TaskSearchUtil mSearchUtil;

    private String query;

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
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(QUERY, query);
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_fragment, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view_search);
        configureRecyclerView();

        mSearchUtil = TaskSearchUtil.getInstance();
        List<Task> tasksFromSearch = mSearchUtil.performSearch(query);
        updateAdapter(tasksFromSearch);

        return view;
    }

    private void configureRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(new SearchAdapter(null));
    }

    private void updateAdapter(List<Task> tasks) {
        if (isAdded()) {
            ((SearchAdapter) mRecyclerView.getAdapter()).setTasks(tasks);
        }
    }

    private class SearchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Task mTask;
        private TextView mTitle;
        private TextView mDescription;
        private CardView mCardView;
        private ImageView mImage;

        public SearchViewHolder(LayoutInflater inflater, ViewGroup parent) {
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

    private class SearchAdapter extends RecyclerView.Adapter<SearchViewHolder> {
        private List<Task> mTasks;

        public SearchAdapter(List<Task> tasks) {
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
}
