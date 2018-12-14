package com.a_team.taskmanager.ui.search;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStore;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.support.annotation.NonNull;
import android.test.mock.MockApplication;

import com.a_team.taskmanager.BasicApp;
import com.a_team.taskmanager.entity.Task;
import com.a_team.taskmanager.repository.TaskManagerRepository;
import com.a_team.taskmanager.ui.tasklist.viewmodel.TaskListViewModel;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class SearchViewModel extends AndroidViewModel {
    private TaskManagerRepository mRepository;
    private MediatorLiveData<List<Task>> mTasks;

    private Executor mExecutor;

    public SearchViewModel(@NonNull Application application, List<Long> idsOfTasksForSearch) {
        super(application);

        mRepository = ((BasicApp) application).getRepository();
        mExecutor = Executors.newSingleThreadExecutor();

        mTasks = new MediatorLiveData<>();
        mTasks.setValue(null);

        Long[] ids = idsOfTasksForSearch.toArray(new Long[0]);

        LiveData<List<Task>> tasksForSearch = mRepository.getTasksByIds(ids);
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
