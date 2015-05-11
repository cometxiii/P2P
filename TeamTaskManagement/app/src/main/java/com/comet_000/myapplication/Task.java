package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Task extends ActionBarActivity {
    private Toolbar toolbar;
    TextView txtMsg;
    Button add;
    EditText eName, eDes;
    Spinner spinner;
    ProgressDialog PD;
    String loadProjectName, loadPassword, loadAccount, loadOwner;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    MailSender mailSender;
    MailManager mailManager = new MailManager();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        dbHelper = OpenHelperManager.getHelper(Task.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        dataProvider.setTableTask(myTableTask);
        dataProvider.setTableProjectMember(myTableProjectMember);
        Intent intent = getIntent();
        loadProjectName = intent.getStringExtra("intentProjectName");
        loadAccount = intent.getStringExtra("intentAccount");
        loadPassword = intent.getStringExtra("intentPassword");
        loadOwner = intent.getStringExtra("intentOwner");
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        txtMsg.setText("Your selected project: " + loadProjectName);
        eName = (EditText) findViewById(R.id.txtName);
        eDes = (EditText) findViewById(R.id.txtDes);
        spinner = (Spinner) findViewById(R.id.spinnerMember);
        loadSpinner();
        add = (Button) findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eName.getText().toString().trim().isEmpty() && eDes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter task name and descriptions", Toast.LENGTH_SHORT).show();
                } else if (eName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter task name", Toast.LENGTH_SHORT).show();
                } else if (eDes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter task descriptions", Toast.LENGTH_SHORT).show();
                } else {
                    if (dataProvider.checkTask((eName.getText()).toString(), loadProjectName, loadOwner)) {
                        Toast.makeText(getApplicationContext(), "This task has already been existing in project!", Toast.LENGTH_SHORT).show();
                    } else {
                        addTask();
                        Toast.makeText(getApplicationContext(), "Add new task successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public Void addTask() {
        String name = eName.getText().toString();
        String des = eDes.getText().toString();
        String project = loadProjectName;
        String member = spinner.getSelectedItem().toString();
        if (member.equals(loadAccount)) {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, member, "new"));
        }else if (member.length() > 1) {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, "", "new"));
            String message = mailManager.makeAssignment(project, loadAccount, name,des);
            mailSender = new MailSender(member, "P2P task assignment", message, loadAccount, loadPassword, Task.this);
            mailSender.send();
            Toast.makeText(getApplicationContext(), "Assignment has been sent.", Toast.LENGTH_SHORT).show();
        } else {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, member, "new"));
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
        eName.setText("");
        eDes.setText("");
        return null;
    }
    private void loadSpinner() {
        List<String> members = dataProvider.getProjectMember(loadProjectName, loadOwner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, members) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView) v.findViewById(android.R.id.text1)).setText("");
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount() - 1; // you don't display last item. It is used as hint.
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.add("");
        adapter.add("");
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }
}