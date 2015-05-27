package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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
    Spinner spinnerMember, spinnerPriority;
    String loadProjectName, loadPassword, loadAccount, loadOwner;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    MailSender mailSender;
    MailManager mailManager = new MailManager();
    TableAccount myAccount;
    ToastMaker toastMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = OpenHelperManager.getHelper(Task.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.setTableTask(myTableTask);
        dataProvider.setTableProjectMember(myTableProjectMember);
        Intent intent = getIntent();
        loadProjectName = intent.getStringExtra("intentProjectName");
        loadAccount = intent.getStringExtra("intentAccount");
        myAccount = dataProvider.getAccountById(1);
        loadPassword = myAccount.Password;
        loadOwner = intent.getStringExtra("intentOwner");
        toastMaker = new ToastMaker(getApplicationContext());
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        txtMsg.setText("Your selected project: " + loadProjectName);
        eName = (EditText) findViewById(R.id.txtName);
        eDes = (EditText) findViewById(R.id.txtDes);
        spinnerMember = (Spinner) findViewById(R.id.spinnerMember);
        spinnerPriority = (Spinner) findViewById(R.id.spinnerPriority);
        loadSpinner();
        loadSpinnerPriority();
        add = (Button) findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eName.getText().toString().trim().isEmpty() && eDes.getText().toString().trim().isEmpty()) {
                    toastMaker.makeToastMiddle("Please enter task name and descriptions");
                } else if (eName.getText().toString().trim().isEmpty()) {
                    toastMaker.makeToastMiddle("Please enter task name");
                } else if (eDes.getText().toString().trim().isEmpty()) {
                    toastMaker.makeToastMiddle("Please enter task descriptions");
                } else {
                    if(eName.getText().toString().contains("'")){
                        toastMaker.makeToastMiddle("Task name can not have ' symbol.");
                    }
                    else{
                        if(eName.getText().toString().length()>20 && eDes.getText().toString().length()>100){
                            toastMaker.makeToastMiddle("Project name can not be longer than 20 characters. Descriptions can not be longer than 100 characters.");
                        }
                        else if(eName.getText().toString().length()>20 || eDes.getText().toString().length()>100){
                            if(eName.getText().toString().length()>20){
                                toastMaker.makeToastMiddle("Project name can not be longer than 20 characters.");
                            }
                            else{
                                toastMaker.makeToastMiddle("Descriptions can not be longer than 100 characters.");
                            }
                        }
                        else{
                            if (dataProvider.checkTask((eName.getText()).toString(), loadProjectName, loadOwner)) {
                                addTask();
                                toastMaker.makeToast("Add new task successfully!");
                            } else {
                                toastMaker.makeToastMiddle("This task has already been created in project!");
                            }
                        }
                    }
                }
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_task, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intentToChangePass=new Intent(Task.this, ChangePassword.class);
            intentToChangePass.putExtra("accountID", loadAccount);
            startActivity(intentToChangePass);
        }
        return super.onOptionsItemSelected(item);
    }

    public Void addTask() {
        String name = eName.getText().toString();
        String des = eDes.getText().toString();
        String project = loadProjectName;
        String member = spinnerMember.getSelectedItem().toString();
        String priority = spinnerPriority.getSelectedItem().toString();
        if (member.equals(loadAccount)) {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, member, "Accepted", priority));
        }else if (!member.equals("")) {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, member, "Waiting", priority));
            String message = mailManager.makeAssignment(project, loadAccount, name, des, priority);
            mailSender = new MailSender(member, "P2P task assignment", message, loadAccount, loadPassword, Task.this);
            mailSender.send();
            toastMaker.makeToast("Assignment has been sent.");
        } else {
            dataProvider.addTask(new TableTask(project, loadAccount, name, des, member, "New", priority));
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
        eName.setText("");
        eDes.setText("");
        loadSpinner();
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
                    ((TextView) v.findViewById(android.R.id.text1)).setHint(getItem(getCount()));
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount() - 1;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.add("");
        adapter.add("");
        spinnerMember.setAdapter(adapter);
        spinnerMember.setSelection(adapter.getCount());
    }

    private void loadSpinnerPriority() {
        List<String> priorities = new ArrayList<>();
        priorities.add("1");
        priorities.add("2");
        priorities.add("3");
        priorities.add("4");
        priorities.add("5");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, priorities);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(dataAdapter);
    }
}