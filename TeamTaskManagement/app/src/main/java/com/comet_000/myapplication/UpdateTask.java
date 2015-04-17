package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.w3c.dom.Text;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class UpdateTask extends ActionBarActivity {
    private Toolbar toolbar;
    TextView txtMsg, tName;
    Button update;
    EditText eDes;
    Spinner spinner, spinnerStatus;
    ProgressDialog PD;
    String loadProjectName, loadTaskName;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    public static final String PROJECT_INTENT="com.comet_000.myapplication.PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        txtMsg=(TextView)findViewById(R.id.txtMsg);
        tName=(TextView)findViewById(R.id.txtName);
        eDes=(EditText)findViewById(R.id.txtDescriptions);
        spinnerStatus = (Spinner)findViewById(R.id.status);
        spinner=(Spinner)findViewById(R.id.spinner);
        update=(Button)findViewById(R.id.btnUpdate);
        final Intent intent=getIntent();
        Bundle loadBundle=intent.getExtras();
        loadProjectName=loadBundle.getString("projectName");
        loadTaskName=loadBundle.getString("taskName");
        txtMsg.setText("Project: "+loadProjectName);
        tName.setText(loadTaskName);
        dbHelper = OpenHelperManager.getHelper(UpdateTask.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProjectMember(myTableProjectMember);
        dataProvider.setTableTask(myTableTask);
        loadTaskDescriptions();
        //Load members of project
        loadSpinnerStatus();
        loadSpinner();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eDes.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter task descriptions", Toast.LENGTH_SHORT).show();
                }
                else{
                    updateTask();
                    Toast.makeText(getApplicationContext(),"Update task successfully!", Toast.LENGTH_SHORT).show();
                    Intent intentToTaskMember=new Intent(UpdateTask.this, TaskMember.class);
                    intentToTaskMember.putExtra("projectName", loadProjectName);
                    startActivity(intentToTaskMember);
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
    //Load task descriptions
    private void loadTaskDescriptions(){
        TableTask myTask = dataProvider.get1TaskByFieldName(loadTaskName,loadProjectName);
        eDes.setText(myTask.getTaskDescriptions());
    }
    //Load status of the task
    private void loadSpinnerStatus(){
        TableTask myTask = dataProvider.get1TaskByFieldName(loadTaskName, loadProjectName);
        List<String> statuses = new ArrayList<>();
        statuses.add("new");
        statuses.add("accepted");
        statuses.add("fix");
        statuses.add("pending");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statuses);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(dataAdapter);
        if (!myTask.Status.equals(null)) {
            int spinnerPostion = dataAdapter.getPosition(myTask.Status);
            spinnerStatus.setSelection(spinnerPostion);
        }
//        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statuses){
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                View v = super.getView(position, convertView, parent);
//                if (position == getCount()) {
//                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
//                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
//                }
//                return v;
//            }
//
//            @Override
//            public int getCount() {
//                return super.getCount()-1; // you don't display last item. It is used as hint.
//            }
//        };
//        TableTask myTask = dataProvider.get1TaskByFieldName(loadTaskName, loadProjectName);
//        String myStatus = myTask.Status;
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
//        adapter.add("");
//        adapter.add(myStatus);
//        spinnerStatus.setAdapter(adapter);
//        spinnerStatus.setSelection(adapter.getCount());
    }
    //Load member of a project to spinner
    private void loadSpinner(){
        List<String> members = dataProvider.getProjectMemberByFieldNameString("ProjectName", loadProjectName);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, members){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you don't display last item. It is used as hint.
            }
        };
        TableTask myTask = dataProvider.get1TaskByFieldName(loadTaskName, loadProjectName);
        String myMember = myTask.MemberName;
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.add("");
        adapter.add(myMember);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }
    //Update new task
    //Save new task to database
    protected Void updateTask() {
        String name=tName.getText().toString();
        String des=eDes.getText().toString();
        String member = spinner.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();
        dataProvider.updateTask(loadProjectName, name, des, member, status);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
        return null;
    }
}
