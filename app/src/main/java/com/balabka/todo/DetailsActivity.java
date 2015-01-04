package com.balabka.todo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;

public class DetailsActivity extends ActionBarActivity {

    private TaskEntity task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ActiveAndroid.initialize(this);

        Intent intent = getIntent();
        if(intent.hasExtra("task_id")) {
            Long taskId = intent.getLongExtra("task_id", 0);
            task = TaskEntity.load(TaskEntity.class, taskId);
        } else {
            task = new TaskEntity();
        }

        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText(task.text);


        Button deleteButton = (Button) findViewById(R.id.delete_btn);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteClickHandle(task);
            }
        });

        Button shareButton = (Button) findViewById(R.id.share_btn);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareTaskText();
            }
        });
    }

    public void deleteClickHandle(TaskEntity task) {
        task.delete();
        Toast.makeText(getApplicationContext(), R.string.task_was_deleted, Toast.LENGTH_LONG).show();
//        Intent intent = new Intent(this, MainActivity.class);
//        //intent.putExtra("someData", "Here is some data");
//        startActivityForResult(intent, 1);
        finish();
    }

    private void shareTaskText() {
        if (!task.hasText()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, task.text);
        startActivity(Intent.createChooser(intent, "Choose one"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
