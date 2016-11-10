package com.rakesh.mukherjee.teskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.rakesh.mukherjee.teskapp.fragments.DoneFragment;
import com.rakesh.mukherjee.teskapp.fragments.PendingFragment;
import com.rakesh.mukherjee.teskapp.utils.DBHelper;
import com.rakesh.mukherjee.teskapp.utils.Tasks;
import com.rakesh.mukherjee.teskapp.utils.TinyDB;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ProgressDialog dialog;
    private DBHelper dbh;
    public static List<Tasks> doneTaskList = new ArrayList<>();
    public static List<Tasks> pendingTaskList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        dbh = new DBHelper(getApplicationContext());

        dialog = new ProgressDialog(this);
        dialog.setMessage("PLease wait....");
        dialog.setCancelable(false);

        if(dbh.getTaskCount() == 0) {
            dialog.show();
            requestUrl();
        } else {
            loadLists();
        }

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }

    private void loadLists() {
        doneTaskList.clear();
        pendingTaskList.clear();

        List<Tasks> tasksList = dbh.getAllTasks();

        for(int i = 0; i < tasksList.size(); i++){
            Tasks tasks = tasksList.get(i);
            if(tasks.getState() == 1){
                doneTaskList.add(tasks);
            } else {
                pendingTaskList.add(tasks);
            }
            Log.e("====", tasks.getName());
        }
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void requestUrl() {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://dl.dropboxusercontent.com/u/6890301/tasks.json";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        if(dialog != null && dialog.isShowing()){
                            dialog.dismiss();
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for(int i = 0; i < jsonArray.length(); i++){
                                int id = Integer.parseInt(jsonArray.getJSONObject(i).getString("id"));
                                String name = jsonArray.getJSONObject(i).getString("name");
                                int state = Integer.parseInt(jsonArray.getJSONObject(i).getString("state"));
                                Log.e("====", name);
                                Tasks tasks = new Tasks(id, name, state);
                                dbh.addTask(tasks);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(MainActivity.this, "JSON format error!", Toast.LENGTH_LONG).show();
                        } finally {
                            if(dbh.getTaskCount() > 0){
                                loadLists();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Server error!", Toast.LENGTH_LONG).show();
                if(dialog != null && dialog.isShowing()){
                    dialog.dismiss();
                }
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new DoneFragment(), "DONE");
        adapter.addFragment(new PendingFragment(), "PENDING");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            startActivity(new Intent(MainActivity.this, AddActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLists();
    }
}
