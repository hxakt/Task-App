package com.rakesh.mukherjee.teskapp.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rakesh.mukherjee.teskapp.MainActivity;
import com.rakesh.mukherjee.teskapp.R;
import com.rakesh.mukherjee.teskapp.utils.DBHelper;
import com.rakesh.mukherjee.teskapp.utils.TaskAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingFragment extends Fragment {
    SwipeRefreshLayout swipeRefreshLayout;

    public PendingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        TaskAdapter mAdapter = new TaskAdapter(getActivity(), MainActivity.pendingTaskList, recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshItems();
            }
        });

        return view;
    }

    private void refreshItems() {
        DBHelper dbh = new DBHelper(getActivity().getApplicationContext());
        dbh.deleteTableItems();
        swipeRefreshLayout.setRefreshing(false);
        getActivity().recreate();
    }
}
