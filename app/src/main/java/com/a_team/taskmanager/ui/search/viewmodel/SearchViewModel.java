package com.a_team.taskmanager.ui.search.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;

import java.util.List;

public class SearchViewModel extends AndroidViewModel {
    private MediatorLiveData<List<Task>> mTasks;

    public SearchViewModel(@NonNull Application application, List<Long> idsOfTasksForSearch) {
        super(application);

        mTasks = new MediatorLiveData<>();
        mTasks.setValue(null);

        TaskManagerRepository repository = ((BasicApp) application).getRepository();
        Long[] ids = idsOfTasksForSearch.toArray(new Long[0]);

        LiveData<List<Task>> tasksForSearch = repository.getTasksByIds(ids);
        mTasks.addSource(tasksForSearch, foundTasks -> mTasks.setValue(foundTasks));
    }

    public MutableLiveData<List<Task>> getTasks() {
        return mTasks;
    }

    public static class Factory extends ViewModelProvider.AndroidViewModelFactory {
        private Application mApplication;
        private List<Long> mIdsForSearch;

        public Factory(@NonNull Application application, List<Long> idsOfTasksForSearch) {
            super(application);
            mApplication = application;
            mIdsForSearch = idsOfTasksForSearch;
        }

        @SuppressWarnings("noinspection unchecked")
        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SearchViewModel(mApplication, mIdsForSearch);
        }
    }
}
