package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;


public class TaskMember extends ActionBarActivity {
    private Toolbar toolbar;
    ListView listViewTask, listViewMember;
    TextView tPName1, tPName2, tDes1, tDes2, note1, note2;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    public String loadProjectName, loadAccount, loadPassword, loadOwner, loadProjectDes;
    MailManager mailManager = new MailManager();
    ProgressDialog progressDialog;
    TableAccount myAccount;
    ToastMaker toastMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(TaskMember.this);
        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        Intent intent=getIntent();

        dbHelper = OpenHelperManager.getHelper(TaskMember.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.setTableProjectMember(myTableProjectMember);
        dataProvider.setTableProject(myTableProject);
        dataProvider.setTableTask(myTableTask);

        loadProjectName = intent.getStringExtra("intentProjectName");
        loadAccount = intent.getStringExtra("intentAccount");
        myAccount = dataProvider.getAccountById(1);
        loadPassword = myAccount.Password;
        loadOwner = intent.getStringExtra("intentOwner");

        toastMaker = new ToastMaker(getApplicationContext());

        tabHost.setup();
        TabHost.TabSpec tabSpec=tabHost.newTabSpec("tab task");
        tabSpec.setContent(R.id.tabTask);
        tabSpec.setIndicator("Task");
        tabHost.addTab(tabSpec);

        tabSpec=tabHost.newTabSpec("tab member");
        tabSpec.setContent(R.id.tabMember);
        tabSpec.setIndicator("Member");
        tabHost.addTab(tabSpec);

        tPName1=(TextView)findViewById(R.id.txtProjectName);
        tPName2=(TextView)findViewById(R.id.txtProjectName2);
        tDes1=(TextView)findViewById(R.id.txtDes);
        tDes2=(TextView)findViewById(R.id.txtDes2);
        note1=(TextView)findViewById(R.id.txtNote1);
        note2=(TextView)findViewById(R.id.txtNote2);

        tPName1.setText("Project name: "+loadProjectName);
        tPName2.setText("Project name: "+loadProjectName);

        //Load descriptions of a project
        loadProjectDescriptions();

        //check if user is project owner or not
        if(!loadAccount.equals(loadOwner)){

            note1.setVisibility(View.VISIBLE);
            note2.setVisibility(View.VISIBLE);
        }

        listViewTask=(ListView)findViewById(R.id.listViewTask);
        listViewMember=(ListView)findViewById(R.id.listViewMember);
        loadTasks();
        loadMembers();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(loadAccount.equals(loadOwner)){
            getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        }
        else{
            getMenuInflater().inflate(R.menu.menu_task_member, menu);
        }
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
            Intent intentToChangePass=new Intent(TaskMember.this, ChangePassword.class);
            intentToChangePass.putExtra("accountID", loadAccount);
            startActivity(intentToChangePass);
        }

        //add new task and member here
        if(id==R.id.addNew){
            DialogAddTaskMember dialogAddTaskMember = new DialogAddTaskMember();
            dialogAddTaskMember.show(getFragmentManager(), "AddTaskMemberFragment");
        }

        //delete task
        if(id==R.id.delete){
            if (listViewTask.getAdapter().getCount()>0) {
                DialogDeleteTask dialogDeleteTask = new DialogDeleteTask();
                dialogDeleteTask.show(getFragmentManager(), "DeleteTaskFragment");
            } else {
                toastMaker.makeToast("There are no tasks in this project.");
            }
        }

        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        /////////////////////////////////////////////////////////////////
        //REFRESH here
        if(id==R.id.synchronize){
            MailChecker checkMailTask = new MailChecker();
            checkMailTask.execute();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();

        tPName1=(TextView)findViewById(R.id.txtProjectName);
        tPName2=(TextView)findViewById(R.id.txtProjectName2);
        tDes1=(TextView)findViewById(R.id.txtDes);
        tDes2=(TextView)findViewById(R.id.txtDes2);

        tPName1.setText("Project name: "+loadProjectName);
        tPName2.setText("Project name: "+loadProjectName);

        if(!loadAccount.equals(loadOwner)){
            note1.setVisibility(View.VISIBLE);
            note1.setText("Note: Since you are not project owner then you are unable to create task.");
            note2.setVisibility(View.VISIBLE);
            note2.setText("Note: Since you are not project owner then you are unable to invite member to project.");
        }

        listViewTask=(ListView)findViewById(R.id.listViewTask);
        listViewMember=(ListView)findViewById(R.id.listViewMember);
        loadTasks();
        loadMembers();

        final Intent intentToUpdateTask=new Intent(TaskMember.this, UpdateTask.class);
        final Bundle myBundle=new Bundle();

        //Select a task to update information
        listViewTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> tItems = dataProvider.getTaskProject(loadProjectName, loadOwner);
                String item = tItems.get(position);
                intentToUpdateTask.putExtra("intentProjectName", loadProjectName);
                intentToUpdateTask.putExtra("intentTaskName", item);
                intentToUpdateTask.putExtra("intentOwner", loadOwner);
                intentToUpdateTask.putExtra("intentAccount", loadAccount);
                intentToUpdateTask.putExtra("intentPassword", loadPassword);
                startActivity(intentToUpdateTask);
            }
        });
    }

    //Load project information

    //Load list of task names in ListView
    private void loadTasks(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataProvider.getTaskProject(loadProjectName, loadOwner));
        listViewTask.setAdapter(adapter);
    }
    //Load string list of task
    public String[] loadTaskString() {
        return dataProvider.getTaskString(loadProjectName, loadOwner);
    }
    //Load list of members in ListView
    private void loadMembers(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataProvider.getAllProjectMember(loadProjectName, loadOwner));
        listViewMember.setAdapter(adapter);
    }

    //Load project descriptions
    private void loadProjectDescriptions(){
        TableProject myProject = dataProvider.getProject(loadProjectName, loadOwner);
        loadProjectDes = myProject.getProjectDescriptions();
        tDes1.setText("Project description: "+myProject.getProjectDescriptions());
        tDes2.setText("Project description: "+myProject.getProjectDescriptions());
    }
    private String getProjectDescriptions(){
        TableProject myProject = dataProvider.getProject(loadProjectName, loadOwner);
        return myProject.getProjectDescriptions();
    }

    //Check mail

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
            loadMembers();
            loadTasks();
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
                                    dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, projectOwner, "Accepted"));
                                    dataProvider.addProjectMember(new TableProjectMember(projectName, projectOwner, loadAccount, "Accepted"));
                                    toastMaker.makeToast("Add new project successfully!");
                                    String message1 = mailManager.makeAcceptInvitation(projectName, loadAccount);
                                    MailSender myMailSender = new MailSender(projectOwner, "P2P invitation acceptance", message1, loadAccount, loadPassword, TaskMember.this);
                                    myMailSender.send();
                                }
                                else {
                                    toastMaker.makeToast("This project already exist!");
                                }
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                String message1 = mailManager.makeDenyInvitation(projectName, loadAccount);
                                MailSender myMailSender = new MailSender(projectOwner, "P2P invitation deny", message1, loadAccount, loadPassword, TaskMember.this);
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
                String[] result1 = mailManager.readAcceptInvitation(message);
                if (dataProvider.checkProjectMember(result1[0], result1[1], loadAccount)) {
                    dataProvider.updateProjectMember(result1[0], result1[1], loadAccount, "Accepted");
                    loadMembers();
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
            case MailManager.denyInviTag:
                String[] resultDenyInvi = mailManager.readAcceptInvitation(message);
                if (dataProvider.checkProjectMember(resultDenyInvi[0], resultDenyInvi[1], loadAccount)) {
                    dataProvider.deleteProjectMember(resultDenyInvi[0], resultDenyInvi[1], loadAccount);
                    DialogInterface.OnClickListener dialogClickListener1 = new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                    builder1.setMessage("User " + resultDenyInvi[1] + " has denied your invitation to project " + resultDenyInvi[0] + ".")
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
                                    dataProvider.addTask(new TableTask(projectName1, owner, taskName, taskDes, loadAccount, "Accepted"));
                                    loadTasks();
                                    String message = mailManager.makeAccetpTask(projectName1, taskName, loadAccount);
                                    MailSender myMailSender = new MailSender(owner, "P2P assignment acceptance", message, loadAccount, loadPassword, TaskMember.this);
                                    myMailSender.send();
                                    break;
                                case DialogInterface.BUTTON_NEGATIVE:
                                    String messageDeny = mailManager.makeDenyTask(projectName1, taskName, loadAccount);
                                    myMailSender = new MailSender(owner, "P2P assignment deny", messageDeny, loadAccount, loadPassword, TaskMember.this);
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
                if (!dataProvider.checkTaskAssignment(result3[1], result3[0], loadAccount, result3[2], "Waiting")) {
                    dataProvider.updateTaskAssignment(result3[0], result3[1], result3[2], "Accepted");
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
                    loadTasks();
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

        }
    }
    //Delete tasks
    public void deleteTask(String[] taskList, Boolean[] checkList) {
        for (int i=0; i<taskList.length; i++) {
            String member = dataProvider.get1Task(taskList[i], loadProjectName, loadOwner).MemberName;
            if (checkList[i]) {
                if (member.equals("") || member.equals(loadOwner)) {
                    dataProvider.deleteTask(loadProjectName, taskList[i], loadOwner);
                } else {
                    String message = mailManager.makeExcludeTask(loadProjectName, taskList[i], loadOwner);
                    dataProvider.deleteTask(loadProjectName, taskList[i], loadOwner);
                    MailSender mailSender = new MailSender(member, "P2P exclude task", message, loadAccount, loadPassword, TaskMember.this);
                    mailSender.send();
                }
            }
        }
        loadTasks();
    }
}
