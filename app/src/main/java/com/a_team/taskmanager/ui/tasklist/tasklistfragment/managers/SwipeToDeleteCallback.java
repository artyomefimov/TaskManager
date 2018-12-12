package com.a_team.taskmanager.ui.tasklist.tasklistfragment.managers;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.a_team.taskmanager.R;
import com.a_team.taskmanager.ui.tasklist.tasklistfragment.TaskListFragment;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private TaskListFragment.TaskListAdapter mAdapter;
    private Drawable mDeleteIcon;
    private ColorDrawable mBackgroundColorOnSwipe;

    public SwipeToDeleteCallback(TaskListFragment.TaskListAdapter adapter, Activity activity) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;

        mDeleteIcon = ContextCompat.getDrawable(activity, R.drawable.ic_action_delete);
        mBackgroundColorOnSwipe = new ColorDrawable(Color.RED);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position);
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        View itemView = viewHolder.itemView;
        int backgroundOffsetFromEdges = 20; // depends on corner radius of a card view (check list_item_task.xml)

        int iconMargin = (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2;
        int iconTop = itemView.getTop() + (itemView.getHeight() - mDeleteIcon.getIntrinsicHeight()) / 2;
        int iconBottom = iconTop + mDeleteIcon.getIntrinsicHeight();

        int iconLeft, iconRight;

        if (isRightSwiping(dX)) {
            iconLeft = itemView.getLeft() + iconMargin + mDeleteIcon.getIntrinsicWidth();
            iconRight = itemView.getLeft() + iconMargin;
            mDeleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            mBackgroundColorOnSwipe.setBounds(
                    itemView.getLeft(),
                    itemView.getTop(),
                    itemView.getLeft() + ((int)dX) + backgroundOffsetFromEdges,
                    itemView.getBottom());
        } else if (isLeftSwiping(dX)) {
            iconLeft = itemView.getRight() - iconMargin - mDeleteIcon.getIntrinsicWidth();
            iconRight = itemView.getRight() - iconMargin;
            mDeleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom);

            mBackgroundColorOnSwipe.setBounds(
                    itemView.getRight() + ((int) dX) - backgroundOffsetFromEdges,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());
        } else if (isUnswiped(dX)) {
            mBackgroundColorOnSwipe.setBounds(0,0,0,0);
        }

        mBackgroundColorOnSwipe.draw(c);
        mDeleteIcon.draw(c);
    }

    private boolean isRightSwiping(float dx) {
        return dx > 0;
    }

    private boolean isLeftSwiping(float dx) {
        return dx < 0;
    }

    private boolean isUnswiped(float dx) {
        return dx == 0;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // used for up and down movements
        return false;
    }
}
