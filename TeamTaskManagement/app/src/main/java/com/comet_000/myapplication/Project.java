package com.comet_000.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.SimpleCursorAdapter;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;

public class Project extends ActionBarActivity {
    private Toolbar toolbar;
    MailChecker mailChecker;
    MailManager mailManager = new MailManager();
    String[] listMessage;
    public String loadAccount = null;
    public String loadPassword = null;
    String loadCallingActivity = null;
    Button add;
    Button btnLoad;
    EditText eName, eDes;
    ListView listView;
    TextView display;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        final Intent intentToTaskMember = new Intent(this, TaskMember.class);
        Intent intent = getIntent();
        loadAccount = intent.getStringExtra("intentAccount");
        loadPassword = intent.getStringExtra("intentPassword");
        mailChecker = new MailChecker(loadAccount, loadPassword);
        loadCallingActivity = intent.getStringExtra("CallingActivity");
        eName = (EditText) findViewById(R.id.txtTitle);
        eDes = (EditText) findViewById(R.id.txtDes);
        //connect to database using ORMLite
        dbHelper = OpenHelperManager.getHelper(Project.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProject(myTableProject);
        dataProvider.setTableProjectMember(myTableProjectMember);
        //Add new project
        add = (Button) findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eName.getText().toString().trim().isEmpty() && eDes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter project name and descriptions", Toast.LENGTH_SHORT).show();
                } else if (eName.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter project name", Toast.LENGTH_SHORT).show();
                } else if (eDes.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter project descriptions", Toast.LENGTH_SHORT).show();
                } else {
                    if (dataProvider.checkProjectByFieldName("ProjectName", (eName.getText()).toString()))
                        Toast.makeText(getApplicationContext(), "This project has already been created by you!", Toast.LENGTH_SHORT).show();
                    else {
                        String name = eName.getText().toString();
                        String des = eDes.getText().toString();
                        String owner = loadAccount;
                        addProject(name, des, owner);
                    }
                }
            }
        });

        btnLoad = (Button) findViewById(R.id.btnLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    listMessage = mailChecker.check();
                    for (String message : listMessage)
                        alertMessage(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


        //Select a project
        listView = (ListView) findViewById(R.id.listView);
        loadProjects();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> lItems = dataProvider.getAllProjectString();
                String item = lItems.get(position);
                intentToTaskMember.putExtra("intentProjectName", item);
                intentToTaskMember.putExtra("intentAccount", loadAccount);
                intentToTaskMember.putExtra("intentPassword", loadPassword);
                startActivity(intentToTaskMember);
            }
        });
    }

    public void alertMessage(String message) {
        String mailType = mailManager.classifyMail(message);
        switch (mailType) {
            case MailManager.invitationTag:
                String[] result = mailManager.readInvitation(message);
                final String projectName = result[0];
                final String projectDes = result[1];
                final String projectOwner = result[2];
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes button clicked
                                addProject(projectName, projectDes, projectOwner);
                                String message = mailManager.makeAcceptInvitation(projectName, loadAccount);
                                MailSender myMailSender = new MailSender(projectOwner, "P2P invitation acceptance", message, loadAccount, loadPassword);
                                myMailSender.send();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("You have been invited to the project " + projectName + ", do you want to join?")
                        .setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
                break;
            case MailManager.acceptIviTag:
                String[] result1 = mailManager.readAcceptInvitation(message);
                dataProvider.addProjectMember(new TableProjectMember(result1[0], result1[1]));
                DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("User " + result1[1] + " has accepted your invitation to project " + result1[0] + ".")
                        .setPositiveButton("Ok", dialogClickListener1).show();
                break;
            case MailManager.assignTaskTag:
                final String[] result2 = mailManager.readAssignment(message);
                final String projectName1 = result2[0];
                final String owner = result2[1];
                final String taskName = result2[2];
                final String taskDes = result2[3];
                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                // Yes button clicked
                                dataProvider.addTask(new TableTask(projectName1, owner, taskName, taskDes, loadAccount, "new"));
                                String message = mailManager.makeAccetpTask(projectName1, taskName, loadAccount);
                                MailSender myMailSender = new MailSender(owner, "P2P assignment acceptance", message, loadAccount, loadPassword);
                                myMailSender.send();
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("You have been assigned to task " + taskName + " from project" + projectName1 +", do you want to join?")
                        .setPositiveButton("Yes", dialogClickListener2)
                        .setNegativeButton("No", dialogClickListener2).show();
                break;
            case MailManager.acceptTaskTag:
                String[] result3 = mailManager.readAcceptTask(message);
                dataProvider.updateTaskAssignment(result3[0], result3[1], result3[2], "accepted");
                DialogInterface.OnClickListener dialogClickListener3 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setMessage("User " + result3[2] + " has accepted your assignment to task " + result3[1] + "of project" + result3[0] + ".")
                        .setPositiveButton("Ok", dialogClickListener3).show();
                break;
            case MailManager.changeStaTag:
                String[] result4 = mailManager.readChangeStatus(message);
                dataProvider.updateTaskAssignment(result4[0], result4[1], result4[2], result4[3]);
                DialogInterface.OnClickListener dialogClickListener4 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setMessage("User " + result4[2] + " has changed status of task " + result4[1] + " of project " + result4[0] + " to " + result4[3] +".")
                        .setPositiveButton("Ok", dialogClickListener4).show();
                break;
        }
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

    //Load project names to ListView
    private void loadProjects() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataProvider.getAllProjectString());
        listView.setAdapter(adapter);
    }

    protected Void addProject(String name, String des, String user) {
//        String name = eName.getText().toString();
//        String des = eDes.getText().toString();
//        String user = loadAccount;
        if(loadAccount.equals(name)) {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user));
        } else {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user));
            dataProvider.addProjectMember(new TableProjectMember(loadAccount, user));
        }
        loadProjects();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(eName.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
        eName.setText("");
        eDes.setText("");
        Toast.makeText(getApplicationContext(), "Add new project successfully!", Toast.LENGTH_SHORT).show();
        return null;
    }


//    public void onLoadButtonClicked(View v) {
//        Toast.makeText(getApplicationContext(), "Please enter project name", Toast.LENGTH_SHORT).show();
//
//        //mailChecker.check();
//    }

}