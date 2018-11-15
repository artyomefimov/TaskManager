package com.a_team.taskmanager.ui.search;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.a_team.taskmanager.ui.FragmentActivity;

public class SearchActivity extends FragmentActivity {
    private static final String QUERY = "query";
    @Override
    protected Fragment createFragment() {
        Intent intent = getIntent();
        String query = intent.getStringExtra(QUERY);
        return SearchFragment.newInstance(query);
    }

    @NonNull
    public static Intent newIntent(Context context, String query) {
        return new Intent(context, SearchActivity.class)
                .putExtra(QUERY, query);
    }
}
