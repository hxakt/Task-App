package com.rakesh.mukherjee.teskapp.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bignerdranch.android.multiselector.ModalMultiSelectorCallback;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.rakesh.mukherjee.teskapp.R;
import com.rakesh.mukherjee.teskapp.fragments.DoneFragment;

import java.util.List;

import static com.rakesh.mukherjee.teskapp.MainActivity.doneTaskList;
import static com.rakesh.mukherjee.teskapp.MainActivity.pendingTaskList;

/**
 * Created by 100384 on 11/8/2016.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.MyViewHolder> {

    private List<Tasks> tasksList;
    private Activity activity;
    private RecyclerView mRecyclerView;
    private DBHelper dbh;
    private AlertDialog alert;
    private Handler handler;
    private Runnable myRunnable;

    public TaskAdapter(Activity activity, List<Tasks> tasksList, RecyclerView recyclerView) {
        this.tasksList = tasksList;
        this.activity = activity;
        this.mRecyclerView = recyclerView;
        dbh = new DBHelper(activity.getApplicationContext());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Tasks tasks = tasksList.get(position);
        holder.name.setText(tasks.getName());
    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    private MultiSelector mMultiSelector = new MultiSelector();

    private ModalMultiSelectorCallback mActionModeCallback
            = new ModalMultiSelectorCallback(mMultiSelector) {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            super.onCreateActionMode(actionMode, menu);
            activity.getMenuInflater().inflate(R.menu.list_context_menu, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            if (menuItem.getItemId() == R.id.menu_item_delete) {
                actionMode.finish();

                for (int i = tasksList.size(); i >= 0; i--) {
                    if (mMultiSelector.isSelected(i, 0)) { // (1)
                        // remove item from list
                        dbh.deleteTasks(tasksList.get(i));
                        tasksList.remove(i);
                        mRecyclerView.getAdapter().notifyItemRemoved(i);
                    }
                }
                mMultiSelector.clearSelections(); // (2)
                return true;

            }
            return false;
        }
    };

    public class MyViewHolder extends SwappingHolder implements View.OnLongClickListener,View.OnClickListener {
        public TextView name;

        public MyViewHolder(View view) {
            super(view, mMultiSelector);
            name = (TextView) view.findViewById(R.id.name);
            view.setOnLongClickListener(this);
            view.setLongClickable(true);
            view.setOnClickListener(this);
            view.setClickable(true);
        }

        @Override
        public boolean onLongClick(View view) {
            if (!mMultiSelector.isSelectable()) {
                ((AppCompatActivity) activity).startSupportActionMode(mActionModeCallback); // (2)
                mMultiSelector.setSelectable(true);
                mMultiSelector.setSelected(MyViewHolder.this, true);
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            if (mMultiSelector.tapSelection(MyViewHolder.this)){
                // do whatever we want to do when not in selection mode
                // perhaps navigate to a detail screen
                mMultiSelector.setSelected(MyViewHolder.this, true);
            } else {
                final int itemPosition = mRecyclerView.getChildLayoutPosition(v);
                final Tasks tasks = tasksList.get(itemPosition);
                if(tasks.getState() == 0){
                    showDialog();
                    handler =  new Handler();
                    myRunnable = new Runnable() {
                        public void run() {
                            //handler.removeCallbacks(myRunnable);
                            closeDialog(tasks, itemPosition);
                        }
                    };
                    handler.postDelayed(myRunnable, 5000);
                }
            }
        }
    }

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("Click CANCEL if you want to cancel!")
                .setCancelable(false)
                .setPositiveButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        handler.removeCallbacks(myRunnable);
                    }
                });
        alert = builder.create();
        alert.show();
    }

    private void closeDialog(Tasks tasks, int itemPosition){
        alert.dismiss();
        Tasks newTask = new Tasks(tasks.getID(), tasks.getName(), 1);
        dbh.updateTasks(newTask);
        doneTaskList.add(newTask);
        pendingTaskList.remove(tasks);
        mRecyclerView.getAdapter().notifyItemRemoved(itemPosition);
        DoneFragment.doneRecycle.getAdapter().notifyDataSetChanged();
    }
}
