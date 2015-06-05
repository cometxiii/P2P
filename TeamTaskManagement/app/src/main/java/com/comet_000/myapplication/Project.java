package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
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
    ListView listView;
    TextView display;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    ProgressDialog progressDialog;
    TableAccount myAccount;
    ToastMaker toastMaker;
    Intent intent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        intent2 = new Intent(this, ChangePassword.class);
        final Intent intentToTaskMember = new Intent(this, TaskMember.class);
        Intent intent = getIntent();
        progressDialog = new ProgressDialog(Project.this);
        loadAccount = intent.getStringExtra("intentAccount");
        checkMail = new CheckMail(loadAccount, loadPassword, Project.this);
        loadCallingActivity = intent.getStringExtra("CallingActivity");
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
        toastMaker = new ToastMaker(getApplicationContext());
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
        callAsynchronousTask();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjects();
    }

    public void callAsynchronousTask() {
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            MailChecker performBackgroundTask = new MailChecker();
                            performBackgroundTask.execute();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 1000 * 60 * 5, 1000 * 60 * 5);
    }

    public String[] loadProjectString() {
        List<String> projecList = dataProvider.getAllProjectStringDelete(loadAccount);
        String[] projectArr = new String[projecList.size()];
        projecList.toArray(projectArr);
        return projectArr;
    }

    public void deleteProject(String[] projectList, Boolean[] checkList) {
        for (int i=0; i < projectList.length; i++) {
            if (checkList[i]) {
                dataProvider.deleteProject(projectList[i], loadAccount);
            }
        }
        loadProjects();
    }

    //check mail
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
                for (int i = 0; i < foundMessages.length; i++) {
                    listMessage[i] = foundMessages[foundMessages.length-i-1].getContent().toString();
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
                readMessage(message);
        }
    }

    //read mail

    public void readMessage(String message) {
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
                                    dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, projectOwner, "Accepted"));
                                    dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, loadAccount, "Accepted"));
                                    toastMaker.makeToast("Add new project successfully!");
                                    String message1 = mailManager.makeAcceptInvitation(projectName, loadAccount);
                                    MailSender myMailSender = new MailSender(projectOwner, "P2P invitation acceptance", message1, loadAccount, loadPassword, Project.this);
                                    loadProjects();
                                    myMailSender.send();
                                }
                                else {
                                    toastMaker.makeToast("This project already exists!");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                String message1 = mailManager.makeDenyInvitation(projectName, loadAccount);
                                MailSender myMailSender = new MailSender(projectOwner, "P2P invitation deny", message1, loadAccount, loadPassword, Project.this);
                                myMailSender.send();
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
                final String[] result1 = mailManager.readAcceptInvitation(message);
                DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (dataProvider.checkProjectMember(result1[0], result1[1], loadAccount)) {
                                    dataProvider.updateProjectMember(result1[0], result1[1], loadAccount, "Accepted");
                                } else {
                                    toastMaker.makeToast("This member does not exist.");
                                }
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setMessage("User " + result1[1] + " has accepted your invitation to project " + result1[0] + ".")
                        .setPositiveButton("Ok", dialogClickListener1).show();
                break;
            case MailManager.denyInviTag:
                final String[] resultDenyInvi = mailManager.readAcceptInvitation(message);
                DialogInterface.OnClickListener dialogClickListenerDenyInvi = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (dataProvider.checkProjectMember(resultDenyInvi[0], resultDenyInvi[1], loadAccount)) {
                                    dataProvider.deleteProjectMember(resultDenyInvi[0], resultDenyInvi[1], loadAccount);
                                } else {
                                    toastMaker.makeToast("This member does not exist.");
                                }
                                break;
                        }
                    }
                };
                AlertDialog.Builder builderDenyInvi = new AlertDialog.Builder(this);
                builderDenyInvi.setMessage("User " + resultDenyInvi[1] + " has denied your invitation to project " + resultDenyInvi[0] + ".")
                        .setPositiveButton("Ok", dialogClickListenerDenyInvi).show();
                break;
            case MailManager.assignTaskTag:
                final String[] result2 = mailManager.readAssignment(message);
                final String projectName1 = result2[0];
                final String owner = result2[1];
                final String taskName = result2[2];
                final String taskDes = result2[3];
                final String taskPriority = result2[4];
                DialogInterface.OnClickListener dialogClickListener2 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (dataProvider.checkTask(taskName, projectName1, owner)) {
                                    dataProvider.addTask(new TableTask(projectName1, owner, taskName, taskDes, loadAccount, "Accepted", taskPriority));
                                    String message = mailManager.makeAccetpTask(projectName1, taskName, loadAccount);
                                    MailSender myMailSender = new MailSender(owner, "P2P assignment acceptance", message, loadAccount, loadPassword, Project.this);
                                    myMailSender.send();
                                } else {
                                    toastMaker.makeToast("This task already exists.");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                if (dataProvider.checkTask(taskName, projectName1, owner)) {
                                    String messageDeny = mailManager.makeDenyTask(projectName1, taskName, loadAccount);
                                    MailSender myMailSender = new MailSender(owner, "P2P assignment deny", messageDeny, loadAccount, loadPassword, Project.this);
                                    myMailSender.send();
                                } else {
                                    toastMaker.makeToast("This task already exists.");
                                }
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setMessage("You have been assigned to task " + taskName + " from project " + projectName1 + ", do you want to join?")
                        .setPositiveButton("Yes", dialogClickListener2)
                        .setNegativeButton("No", dialogClickListener2).show();
                break;
            case MailManager.acceptTaskTag:
                final String[] result3 = mailManager.readAcceptTask(message);
                DialogInterface.OnClickListener dialogClickListener3 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!dataProvider.checkTaskAssignment(result3[1], result3[0], loadAccount, result3[2], "Waiting")) {
                                    dataProvider.updateTaskAssignment(result3[0], result3[1], result3[2], "Accepted");
                                } else {
                                    toastMaker.makeToast("This task does not exist.");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
                builder3.setMessage("User " + result3[2] + " has accepted your assignment to task " + result3[1] + " of project " + result3[0] + ".")
                        .setPositiveButton("Ok", dialogClickListener3).show();
                break;
            case MailManager.changeStaTag:
                final String[] result4 = mailManager.readChangeStatus(message);
                DialogInterface.OnClickListener dialogClickListener4 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!dataProvider.checkTaskMember(result4[1], result4[0], loadAccount, result4[2])) {
                                    dataProvider.updateTaskAssignment(result4[0], result4[1], result4[2], result4[3]);
                                } else {
                                    toastMaker.makeToast("This task does not exist.");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder4 = new AlertDialog.Builder(this);
                builder4.setMessage("User " + result4[2] + " has changed status of task " + result4[1] + " of project " + result4[0] + " to " + result4[3] + ".")
                        .setPositiveButton("Ok", dialogClickListener4).show();
                break;
            case MailManager.excludeTaskTag:
                final String[] result5 = mailManager.readExcludeTask(message);
                DialogInterface.OnClickListener dialogClickListener5 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (!dataProvider.checkTaskMember(result5[1], result5[0], result5[2], loadAccount)) {
                                    dataProvider.deleteTask(result5[0], result5[1], result5[2]);
                                } else {
                                    toastMaker.makeToast("This task does not exist.");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder5 = new AlertDialog.Builder(this);
                builder5.setMessage("You have been excluded from task " + result5[1] + " of project " + result5[0] + ".")
                        .setPositiveButton("Ok", dialogClickListener5).show();
                break;
            case MailManager.changeDesTag:
                final String[] result6 = mailManager.readChangeDes(message);
                DialogInterface.OnClickListener dialogClickListener6 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (dataProvider.checkTask(result6[1],result6[0],result6[2])) {
                                    dataProvider.updateTaskDes(result6[0], result6[1], result6[2], result6[3]);
                                } else {
                                    toastMaker.makeToast("This task does not exist.");
                                }
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
                                    dataProvider.updateTaskDeny(result7[0], result7[1], loadAccount, "New", result7[2]);
                                }
                                else{
                                    toastMaker.makeToast("This task does not exist!");
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
            case MailManager.excludeProTag:
                final String[] resultExcludePro = mailManager.readExcludeProject(message);
                DialogInterface.OnClickListener dialogClickListener8 = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if(!dataProvider.checkProject(resultExcludePro[0], resultExcludePro[1])){
                                    dataProvider.deleteProject(resultExcludePro[0], resultExcludePro[1]);
                                    loadProjects();
                                } else {
                                    toastMaker.makeToast("This project does not exist.");
                                }
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder8 = new AlertDialog.Builder(this);
                builder8.setMessage("You have been excluded from project" + resultExcludePro[0]+ ".")
                        .setPositiveButton("Ok", dialogClickListener8).show();
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

        if(id==R.id.delete) {
            toastMaker.makeToast("You have to remove all members before delete a project.");
            DialogDeleteProject dialogDeleteProject = new DialogDeleteProject();
            dialogDeleteProject.show(getFragmentManager(), "DeleteProjectFragment");
        }
        return super.onOptionsItemSelected(item);
    }

    //Load project names to ListView
    public void loadProjects() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataProvider.getAllProjectString());
        listView.setAdapter(adapter);
    }
}