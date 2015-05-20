package com.comet_000.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;

public class Project extends ActionBarActivity {
    private Toolbar toolbar;
    MailManager mailManager = new MailManager();
    CheckMail checkMail;
    String[] listMessage;
    public String loadAccount = null;
    public String loadPassword = null;
    String loadCallingActivity = null;
    Button add;
    EditText eName, eDes;
    ListView listView;
    TextView display;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    ProgressDialog progressDialog;
    TableAccount myAccount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        final Intent intentToTaskMember = new Intent(this, TaskMember.class);
        Intent intent = getIntent();
        progressDialog = new ProgressDialog(Project.this);
        loadAccount = intent.getStringExtra("intentAccount");
        checkMail = new CheckMail(loadAccount, loadPassword, Project.this);
        loadCallingActivity = intent.getStringExtra("CallingActivity");
        eName = (EditText) findViewById(R.id.txtTitle);
        eDes = (EditText) findViewById(R.id.txtDes);
        //connect to database using ORMLite
        dbHelper = OpenHelperManager.getHelper(Project.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.setTableTask(myTableTask);
        dataProvider.setTableProject(myTableProject);
        dataProvider.setTableProjectMember(myTableProjectMember);
        myAccount = dataProvider.getAccountById(1);
        loadPassword = myAccount.Password;
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
                    if (dataProvider.checkProject((eName.getText()).toString(), loadAccount)) {
                        String name = eName.getText().toString();
                        String des = eDes.getText().toString();
                        String owner = loadAccount;
                        addProject(name, des, owner);
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "This project has already been created by you!", Toast.LENGTH_SHORT).show();
                    }
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
                int fistOwner = item.indexOf("-");
                String projectName = item.substring(0, fistOwner - 1);
                String owner = item.substring(fistOwner + 2);
                intentToTaskMember.putExtra("intentProjectName", projectName);
                intentToTaskMember.putExtra("intentAccount", loadAccount);
                intentToTaskMember.putExtra("intentOwner", owner);
                startActivity(intentToTaskMember);
            }
        });
    }

    private class MailChecker extends AsyncTask<Void, Void, String[]> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Checking mails...");
            progressDialog.show();
        }
        @Override
        protected String[] doInBackground(Void... params) {
            Properties props = System.getProperties();
            props.setProperty("mail.store.protocol", "imaps");
            try {
                Session session = Session.getDefaultInstance(props, null);
                //GMail
                System.out.println("GMail logging in..");
                Store store = session.getStore("imaps");
                store.connect("imap.gmail.com", loadAccount, loadPassword);
                System.out.println("Connected to = " + store);
                Folder inbox = store.getFolder("Inbox");
                inbox.open(Folder.READ_WRITE);
                //Enter term to search here
                BodyTerm bodyTerm = new BodyTerm("<zfgHsj6Uyk>");
                FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
                AndTerm andTerm = new AndTerm(bodyTerm, flagTerm);
                //Search
                Message[] foundMessages = inbox.search(andTerm);
                String[] listMessage = new String[foundMessages.length];
                System.out.println("Total P2P mails are = " + listMessage.length);
                for (int i = foundMessages.length - 1; i >= 0; i--) {
                    listMessage[i] = foundMessages[i].getContent().toString();
                }
                inbox.setFlags(foundMessages, new Flags(Flags.Flag.SEEN), true);
                store.close();
                return listMessage;
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String[] result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            for (String message : result)
                alertMessage(message);
        }
    }

    public void alertMessage(String message) {
        String mailType = mailManager.classifyMail(message);
        switch (mailType) {
            case MailManager.invitationTag:
                final String[] result = mailManager.readInvitation(message);
                final String projectName = result[0];
                final String projectDes = result[1];
                final String projectOwner = result[2];
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    if (dataProvider.checkProject(projectName, projectOwner)) {
                                        dataProvider.addProject(new TableProject(projectName, projectDes, projectOwner));
                                        dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, projectOwner));
                                        dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, loadAccount));
                                        loadProjects();
                                        Toast.makeText(getApplicationContext(), "Add new project successfully!", Toast.LENGTH_SHORT).show();
                                        String message1 = mailManager.makeAcceptInvitation(projectName, loadAccount);
                                        MailSender myMailSender = new MailSender(projectOwner, "P2P invitation acceptance", message1, loadAccount, loadPassword, Project.this);
                                        myMailSender.send();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "This project already exist!", Toast.LENGTH_SHORT).show();
                                    }
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
                if (dataProvider.checkProjectMember(result1[0], result1[1], loadAccount)) {
                    dataProvider.addProjectMember(new TableProjectMember(result1[0], loadAccount, result1[1]));
                    DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("User " + result1[1] + " has accepted your invitation to project " + result1[0] + ".")
                            .setPositiveButton("Ok", dialogClickListener1).show();
                }
                break;
            case MailManager.assignTaskTag:
                final String[] result2 = mailManager.readAssignment(message);
                final String projectName1 = result2[0];
                final String owner = result2[1];
                final String taskName = result2[2];
                final String taskDes = result2[3];
                if (dataProvider.checkTask(taskName, projectName1, owner)) {
                    DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    dataProvider.addTask(new TableTask(projectName1, owner, taskName, taskDes, loadAccount, "accepted"));
                                    String message = mailManager.makeAccetpTask(projectName1, taskName, loadAccount);
                                    MailSender myMailSender = new MailSender(owner, "P2P assignment acceptance", message, loadAccount, loadPassword, Project.this);
                                    myMailSender.send();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    String messageDeny = mailManager.makeDenyTask(projectName1, taskName, loadAccount);
                                    myMailSender = new MailSender(owner, "P2P assignment deny", messageDeny, loadAccount, loadPassword, Project.this);
                                    myMailSender.send();
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                    builder2.setMessage("You have been assigned to task " + taskName + " from project " + projectName1 + ", do you want to join?")
                            .setPositiveButton("Yes", dialogClickListener2)
                            .setNegativeButton("No", dialogClickListener2).show();
                }
                break;
            case MailManager.acceptTaskTag:
                String[] result3 = mailManager.readAcceptTask(message);
                if (!dataProvider.checkTaskAssignment(result3[1], result3[0], loadAccount, result3[2], "waiting")) {
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
                    builder3.setMessage("User " + result3[2] + " has accepted your assignment to task " + result3[1] + " of project " + result3[0] + ".")
                            .setPositiveButton("Ok", dialogClickListener3).show();
                }
                break;
            case MailManager.changeStaTag:
                String[] result4 = mailManager.readChangeStatus(message);
                if (!dataProvider.checkTaskMember(result4[1], result4[0], loadAccount, result4[2])) {
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
                    builder4.setMessage("User " + result4[2] + " has changed status of task " + result4[1] + " of project " + result4[0] + " to " + result4[3] + ".")
                            .setPositiveButton("Ok", dialogClickListener4).show();
                }
                break;
            case MailManager.excludeTaskTag:
                String[] result5 = mailManager.readExcludeTask(message);
                if (!dataProvider.checkTaskMember(result5[1], result5[0], result5[2], loadAccount)) {
                    dataProvider.deleteTask(result5[0], result5[1], result5[2]);
                    DialogInterface.OnClickListener dialogClickListener5 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
                    builder5.setMessage("You have been excluded from task " + result5[1] + " of project " + result5[0] + ".")
                            .setPositiveButton("Ok", dialogClickListener5).show();
                }
                break;
            case MailManager.changeDesTag:
                String[] result6 = mailManager.readChangeDes(message);
                dataProvider.updateTaskDes(result6[0], result6[1], result6[2], result6[3]);
                DialogInterface.OnClickListener dialogClickListener6 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder6 = new AlertDialog.Builder(this);
                builder6.setMessage("Description of task " + result6[1] + " from project " + result6[0] + " has been changed to " + result6[3] + ".")
                        .setPositiveButton("Ok", dialogClickListener6).show();
                break;
            case MailManager.denyTaskTag:
                final String[] result7 = mailManager.readDenyTask(message);
                DialogInterface.OnClickListener dialogClickListener7 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if(!dataProvider.checkTaskMember(result7[1], result7[0], loadAccount, result7[2])){
                                    dataProvider.updateTaskDeny(result7[0], result7[1], loadAccount, "new", result7[2]);
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "This task does not exist!", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder7 = new AlertDialog.Builder(this);
                builder7.setMessage("User " + result7[2] + " has denied request from task " + result7[1]+ ".")
                        .setPositiveButton("Ok", dialogClickListener7).show();
                break;

        }
    }

    private Void addProjectFromInvitation(String projectName, String projectDes, String projectOwner) {
        if (!dataProvider.checkProject(projectName, projectOwner)) {
            Toast.makeText(getApplicationContext(), "This project is already exist!", Toast.LENGTH_SHORT).show();
            return null;
        }
        dataProvider.addProject(new TableProject(projectName, projectDes, projectOwner));
        dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, projectOwner));
        dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, loadAccount));
        loadProjects();
        Toast.makeText(getApplicationContext(), "Add new project successfully!", Toast.LENGTH_SHORT).show();
        String message = mailManager.makeAcceptInvitation(projectName, loadAccount);
        MailSender myMailSender = new MailSender(projectOwner, "P2P invitation acceptance", message, loadAccount, loadPassword, Project.this);
        myMailSender.send();
        return null;
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
            Intent intentToChangePass=new Intent(Project.this, ChangePassword.class);
            intentToChangePass.putExtra("accountID", loadAccount);
            startActivity(intentToChangePass);
        }

        //add new project
        if(id==R.id.addNew){
            DialogAddProject dialogAddProject = new DialogAddProject();
            dialogAddProject.show(getFragmentManager(), "ProjectFragment");
        }

        /////////////////////////////////////////////////////////////////
        //REFRESH here
        if(id==R.id.synchronize){
            MailChecker checkMailTask = new MailChecker();
            checkMailTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }

    //Load project names to ListView
    private void loadProjects() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataProvider.getAllProjectString());
        listView.setAdapter(adapter);
    }

    protected Void addProject(String name, String des, String user) {
        if (!dataProvider.checkProject(name, user)) {
            Toast.makeText(getApplicationContext(), "This project is already exist!", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (loadAccount.equals(user)) {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, user));
        } else {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, loadAccount));
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
}