package com.comet_000.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;


public class Member extends ActionBarActivity
{
    private Toolbar toolbar;
    String loadProjectName, loadProjectDes, loadAccount, loadPassword;
    String result = null;
    TextView msg;
    EditText eMail;
    Button add;
    SQLController sqlController;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    MailManager mailManager = new MailManager();
    TableAccount myAccount;
    ToastMaker toastMaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbHelper = OpenHelperManager.getHelper(Member.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.setTableProjectMember(myTableProjectMember);
        Intent intent = getIntent();
        loadProjectName = intent.getStringExtra("intentProjectName");
        loadProjectDes = intent.getStringExtra("intentProjectDes");
        myAccount = dataProvider.getAccountById(1);
        loadAccount = myAccount.Account;
        loadPassword = myAccount.Password;
        msg=(TextView)findViewById(R.id.txtMsg);
        msg.setText("Invite new member to project: "+loadProjectName);
        toastMaker = new ToastMaker(getApplicationContext());

        eMail=(EditText)findViewById(R.id.txtUser);
        add=(Button)findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String member = eMail.getText().toString();

                //Validate email input
                if(member.contains("@")){
                    if(member.contains("@gmail.com"))
                            inviteMember(member);
                    else{
                        toastMaker.makeToast("Application supports only Google Mail account.");
                    }
                }
                else{
                    member += "@gmail.com";
                    if(eMail.getText().toString().trim().isEmpty()){
                        toastMaker.makeToast("Please enter Google account to invite");
                    }
                    else{
                        inviteMember(member);
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_member, menu);
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
            Intent intentToChangePass=new Intent(Member.this, ChangePassword.class);
            intentToChangePass.putExtra("accountID", loadAccount);
            startActivity(intentToChangePass);
        }
        return super.onOptionsItemSelected(item);
    }
    //invite member
    private void inviteMember(String member) {
        if(!dataProvider.checkProjectMember(loadProjectName, member, loadAccount)){
            dataProvider.addProjectMember(new TableProjectMember(loadProjectName, loadAccount, member, "Waiting"));
            String message = mailManager.makeInvitation(loadProjectName, loadProjectDes, loadAccount);
            MailSender myMailSender = new MailSender(member, "P2P invitation", message, loadAccount, loadPassword, Member.this);
            myMailSender.send();
        }
        else {
            toastMaker.makeToast("This user has already been invited to project!");
        }
    }
}
