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
    TableTask myTask;
    ProgressDialog PD;
    String loadProjectName, loadTaskName, loadOwner, loadAccount, loadPassword;
    String taskDesBefore, taskMemberBefore, taskStatusBefore;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    MailSender mailSender;
    MailManager mailManager = new MailManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        txtMsg = (TextView) findViewById(R.id.txtMsg);
        tName = (TextView) findViewById(R.id.txtName);
        eDes = (EditText) findViewById(R.id.txtDescriptions);
        spinnerStatus = (Spinner) findViewById(R.id.status);
        spinner = (Spinner) findViewById(R.id.spinner);
        update = (Button) findViewById(R.id.btnUpdate);
        final Intent intent = getIntent();
        loadAccount = intent.getStringExtra("intentAccount");
        loadPassword = intent.getStringExtra("intentPassword");
        loadProjectName = intent.getStringExtra("intentProjectName");
        loadTaskName = intent.getStringExtra("intentTaskName");
        loadOwner = intent.getStringExtra("intentOwner");
        txtMsg.setText("Project: " + loadProjectName);
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
                if (eDes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter task descriptions", Toast.LENGTH_SHORT).show();
                } else {
                    updateTask();
                    //Reload information after updating task
                    loadTaskDescriptions();
                    loadSpinnerStatus();
                    loadSpinner();
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
    private void loadTaskDescriptions() {
        myTask = dataProvider.get1Task(loadTaskName, loadProjectName, loadOwner);
        taskDesBefore = myTask.getTaskDescriptions();
        eDes.setText(myTask.getTaskDescriptions());
        if (!myTask.Owner.equals(loadAccount) || myTask.Status.equals("waiting")) {
            eDes.setEnabled(false);
        }
    }

    //Load status of the task
    private void loadSpinnerStatus() {
        TableTask myTask = dataProvider.get1Task(loadTaskName, loadProjectName, loadOwner);
        List<String> statuses = new ArrayList<>();
        statuses.add("new");
        statuses.add("accepted");
        statuses.add("done");
        if (myTask.Owner.equals(loadAccount) && !myTask.MemberName.equals(loadAccount)) {
            statuses.add("waiting");
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, statuses);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(dataAdapter);
        if (!myTask.Status.equals(null)) {
            taskStatusBefore = myTask.Status;
            int currentStatus = dataAdapter.getPosition(myTask.Status);
            spinnerStatus.setSelection(currentStatus);
        }
        if (myTask.Status.equals("waiting") || !myTask.MemberName.equals(loadAccount)) {
            spinnerStatus.setEnabled(false);
            return;
        }
    }

    //Load member of a project to spinner
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
        TableTask myTask = dataProvider.get1Task(loadTaskName, loadProjectName, loadOwner);
        taskMemberBefore = myTask.MemberName;
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        adapter.add("");
        adapter.add(taskMemberBefore);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());

        if (myTask.Status.equals("waiting")) {
            spinner.setEnabled(false);
        }
    }

    //Update new task
    //Save new task to database
    protected Void updateTask() {
        String taskName = tName.getText().toString();
        String taskDes = eDes.getText().toString();
        String taskMember = spinner.getSelectedItem().toString();
        String taskStatus = spinnerStatus.getSelectedItem().toString();
        if (loadAccount.equals(loadOwner)) {
            if (!taskDes.equals(taskDesBefore)) {
                if (!taskMember.equals(taskMemberBefore)) {
                    if (taskMemberBefore.equals("")) {
                        if (taskMember.equals(loadAccount)) {
                            dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "accepted");
                        } else {
                            dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "waiting");
                            String message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                            mailSender = new MailSender(taskMember, "P2P assign task.", message, loadAccount, loadPassword, UpdateTask.this);
                            mailSender.send();
                        }
                    } else {
                        if (taskMemberBefore.equals(loadAccount)) {
                            if (taskMember.equals("")) {
                                dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "new");
                            } else {
                                dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "waiting");
                                String message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                                mailSender = new MailSender(taskMember, "P2P change status.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            }
                        } else {
                            if (taskMember.equals(loadAccount)) {
                                dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "accepted");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            } else if (taskMember.equals("")) {
                                dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "new");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            }else {
                                dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "waiting");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                                message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                                mailSender = new MailSender(taskMember, "P2P change status.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            }
                        }
                    }
                } else {
                    if (taskMemberBefore.equals("")) {
                        dataProvider.updateTaskDes(loadProjectName, loadTaskName, loadOwner, taskDes);
                    }
                    if (taskMemberBefore.equals(loadAccount)) {
                        dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, taskStatus);
                    } else if (!taskMemberBefore.equals("")) {
                        dataProvider.updateTaskDes(loadProjectName, loadTaskName, loadOwner, taskDes);
                        String message = mailManager.makeChangeDes(loadProjectName, loadTaskName, loadOwner, taskDes);
                        mailSender = new MailSender(taskMember, "P2P change description.", message, loadAccount, loadPassword, UpdateTask.this);
                        mailSender.send();
                    }
                }
            } else {
                if (!taskMember.equals(taskMemberBefore)) {
                    if (taskMemberBefore.equals("")) {
                        if (taskMember.equals(loadAccount)) {
                            dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "accepted");
                        } else {
                            dataProvider.updateTask(loadProjectName, loadTaskName, taskDes, taskMember, "waiting");
                            String message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                            mailSender = new MailSender(taskMember, "P2P assign task.", message, loadAccount, loadPassword, UpdateTask.this);
                            mailSender.send();
                        }

                    } else {
                        if (taskMemberBefore.equals(loadAccount)) {
                            if (taskMember.equals("")) {
                                dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "new");
                            } else {
                                dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "waiting");
                                String message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                                mailSender = new MailSender(taskMember, "P2P assign task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            }
                        } else {
                            if (taskMember.equals(loadAccount)) {
                                dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "accepted");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            } else if (!taskMember.equals("")){
                                dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "waiting");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                                message = mailManager.makeAssignment(loadProjectName, loadOwner, loadTaskName, taskDes);
                                mailSender = new MailSender(taskMember, "P2P assign task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            } else {
                                dataProvider.updateTaskMemSta(loadProjectName, loadTaskName, taskMember, "new");
                                String message = mailManager.makeExcludeTask(loadProjectName, loadTaskName, loadOwner);
                                mailSender = new MailSender(taskMemberBefore, "P2P exclude task.", message, loadAccount, loadPassword, UpdateTask.this);
                                mailSender.send();
                            }
                        }
                    }
                } else if (!taskStatus.equals(taskStatusBefore)) {
                    dataProvider.updateTaskStatus(loadProjectName, loadTaskName, loadOwner, taskStatus);
                }
            }
        } else {
            if (!taskStatus.equals(taskStatusBefore)) {
                dataProvider.updateTaskStatus(loadProjectName, loadTaskName, loadOwner, taskStatus);
                String message = mailManager.makeChangeStatus(loadProjectName, loadTaskName, loadAccount, taskStatus);
                mailSender = new MailSender(loadOwner, "P2P change status.", message, loadAccount, loadPassword, UpdateTask.this);
                mailSender.send();
            }
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
        return null;
    }
}
