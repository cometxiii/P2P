package com.comet_000.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.concurrent.ExecutionException;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        dbHelper = OpenHelperManager.getHelper(Member.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProjectMember(myTableProjectMember);
        Intent intent = getIntent();
        loadProjectName = intent.getStringExtra("intentProjectName");
        loadProjectDes = intent.getStringExtra("intentProjectDes");
        loadAccount = intent.getStringExtra("intentAccount");
        loadPassword = intent.getStringExtra("intentPassword");
        msg=(TextView)findViewById(R.id.txtMsg);
        msg.setText("Invite new member to project: "+loadProjectName);

        eMail=(EditText)findViewById(R.id.txtUser);
        add=(Button)findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String member = eMail.getText().toString();
                member += "@gmail.com";
                if(eMail.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter Google account to invite", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(dataProvider.checkProjectMember(loadProjectName, member, loadAccount)){
                        String message = mailManager.makeInvitation(loadProjectName, loadProjectDes, loadAccount);
                        MailSender myMailSender = new MailSender(member, "P2P invitation", message, loadAccount, loadPassword, Member.this);
                        myMailSender.send();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "This user has already been invited to project!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
