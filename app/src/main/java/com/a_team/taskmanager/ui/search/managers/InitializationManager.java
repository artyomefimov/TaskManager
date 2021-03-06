package com.a_team.taskmanager.ui.search.managers;

import android.arch.lifecycle.ViewModelProviders;

import com.a_team.taskmanager.ui.search.fragment.SearchFragment;
import com.a_team.taskmanager.ui.search.viewmodel.SearchViewModel;

import java.util.List;

public class InitializationManager {
    private SearchViewModel mViewModel;

    public void createViewModelAndSubscribeUI(SearchFragment fragment, List<Long> idsOfTasksForSearch) {
        SearchViewModel.Factory factory =
                new SearchViewModel.Factory(fragment.getActivity().getApplication(), idsOfTasksForSearch);
        mViewModel = ViewModelProviders.of(fragment, factory).get(SearchViewModel.class);
        subscribeUi(fragment);
    }

    private void subscribeUi(SearchFragment fragment) {
        mViewModel.getTasks().observe(fragment, tasks -> {
            if (tasks != null) {
                fragment.updateRecyclerViewAdapter(tasks);
            }
        });
    }
}
