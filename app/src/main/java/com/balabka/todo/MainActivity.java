package com.balabka.todo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.activeandroid.ActiveAndroid;


public class MainActivity extends ActionBarActivity {

    private static final int RESULT_SETTINGS = 1;

    private ListAdapter listAdapter;
    private EditText editText;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.edit_text);
        addBtn = (Button) findViewById(R.id.btn_add);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewTask(editText.getText().toString());
            }
        });

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

            @Override
            public void afterTextChanged(Editable editable) {
                if (editText.getText().toString().equals("")) {
                    addBtn.setText(R.string.btn_cancel);
                } else {
                    addBtn.setText(R.string.btn_add);
                }
            }
        });

        ActiveAndroid.initialize(this);
        initSettings();
        listAdapter = new ListAdapter(this);

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(listAdapter);

    }

    private void addNewTask(String s) {
        if (s.equals("")) {
            addBtn.setText(R.string.btn_add);
            hideSoftKeyboard();
            return;
        }
        TaskEntity task = new TaskEntity();
        task.text = s;
        task.save();
        editText.setText("");
        addBtn.setText(R.string.btn_add);
        editText.setSelected(false);
        hideSoftKeyboard();
        listAdapter.notifyDataSetChanged();
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View focusedView = this.getCurrentFocus();
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    private void initSettings() {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(!sharedPrefs.contains("is_first_launch")) {
            TaskEntity task = new TaskEntity();
            task.isDone = true;
            task.text = getResources().getString(R.string.first_launch_task_text);
            task.save();
            sharedPrefs.edit().putBoolean("is_first_launch", false).commit();
        }

        String bgColor = sharedPrefs.getString("bg_color", getResources().getString(R.string.default_bg_color));
        LinearLayout rootLayout = (LinearLayout) findViewById(R.id.main_layout);
        rootLayout.setBackgroundColor(Color.parseColor(bgColor));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        listAdapter.notifyDataSetChanged();
        switch (requestCode) {
            case RESULT_SETTINGS:
                initSettings();
                break;
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // settings button pressed
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, RESULT_SETTINGS);
        }

        return super.onOptionsItemSelected(item);
    }

}
