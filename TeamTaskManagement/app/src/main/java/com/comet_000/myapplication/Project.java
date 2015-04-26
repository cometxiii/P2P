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
    CheckingMails mailChecker;
    String[] message = null;
    String loadAccount = null;
    String loadPassword = null;
    String loadCallingActivity = null;
    Button add;
    Button btnLoad;
    EditText eName, eDes;
    ListView listView;
    TextView display;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    public static String ACCOUNT = null;
    public static String PASSWORD = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        final Intent intentToTaskMember = new Intent(this, TaskMember.class);
        Intent intent = getIntent();
        loadAccount = intent.getStringExtra("account");
        loadPassword = intent.getStringExtra("password");
        mailChecker = new CheckingMails(loadAccount, loadPassword);
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
                    message = mailChecker.check();
                    alertMessage(message);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
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
                intentToTaskMember.putExtra("projectName", item);
                startActivity(intentToTaskMember);
            }
        });
    }
    public void alertMessage(String[] message) throws IOException, MessagingException {
        String body = message[0];
        int firstPN = body.indexOf("<ProjectName>") + 13;
        int lastPN =  body.lastIndexOf("<ProjectName>");
        int firstPD = body.indexOf("<ProjectDes>") + 12;
        int lastPD =  body.lastIndexOf("<ProjectDes>");
        String sentDate = message[2];
        final String projectName = body.substring(firstPN, lastPN);
        final String projectDes = body.substring(firstPD, lastPD);
        String address = message[1];
        int firstFR = address.indexOf("<") + 1;
        int lastFR = address.indexOf(">");
        final String projectOwner = address.substring(firstFR,lastFR);
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
//                        Toast.makeText(Project.this, "Yes Clicked",Toast.LENGTH_LONG).show();
                        addProject(projectName, projectDes, projectOwner);
                        String message = "<zfgHsj6Uyk><AcceptInvitation><ProjectName>" + projectName + "<ProjectName>";
                        MailSender myMailSender = new MailSender(projectOwner, "P2P accept", message, Project.ACCOUNT, Project.PASSWORD);
                        try {
                            String result = myMailSender.send();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
//                        Toast.makeText(Project.this, "No Clicked",Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(projectOwner)
//                .setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener).show();
        builder.setMessage("You have been invited to the project " + projectName +", do you want to join?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
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
        dataProvider.addProject(new TableProject(name, des, user));
        dataProvider.addProjectMember(new TableProjectMember(name, user));
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