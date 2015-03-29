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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.comet_000.myapplication.SlidingTabLayout.MailSender;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class Member extends ActionBarActivity
{
    private Toolbar toolbar;
    String loadProjectName;
    TextView msg;
    EditText eMail;
    Button add;
    SQLController sqlController;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        sqlController=new SQLController(this);
        dbHelper = OpenHelperManager.getHelper(Member.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProjectMember(myTableProjectMember);
        Intent intent=getIntent();
        loadProjectName=intent.getStringExtra(TaskMember.PROJECT_INTENT);
        msg=(TextView)findViewById(R.id.txtMsg);
        msg.setText("Invite new member to project: "+loadProjectName);

        eMail=(EditText)findViewById(R.id.txtUser);
        add=(Button)findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eMail.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter Google account to invite", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(dataProvider.checkProjectMemberByFieldName("ProjectName", eMail.getText().toString().toLowerCase())){
                        Toast.makeText(getApplicationContext(), "This user has already been invited to project!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        MailSender myMailSender = new MailSender(eMail.getText().toString(), "test invite member", "message");
                        myMailSender.send();
                        eMail.setText("");
                        Toast.makeText(getApplicationContext(), "Invitation has been sent!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
