package com.rakesh.mukherjee.teskapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.rakesh.mukherjee.teskapp.utils.DBHelper;
import com.rakesh.mukherjee.teskapp.utils.Tasks;

public class AddActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText task_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        task_name = (EditText) findViewById(R.id.input_task_name);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_done) {
            if(!TextUtils.isEmpty(task_name.getText())) {
                DBHelper dbh = new DBHelper(getApplicationContext());
                int task_id = dbh.getTaskCount() + 1;
                Tasks tasks = new Tasks(task_id, task_name.getText().toString(), 0);
                dbh.addTask(tasks);
                finish();
            } else {
                Toast.makeText(AddActivity.this, "Enter a Task Name", Toast.LENGTH_LONG).show();
            }
            return true;
        } else if (id == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
