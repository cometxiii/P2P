package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Member extends Activity {
    String loadProjectName;
    TextView msg;
    EditText eMail;
    Button add;
    SQLController sqlController;
    ProgressDialog PD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        sqlController=new SQLController(this);
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
                    if(checkMember().size()>0){
                        Toast.makeText(getApplicationContext(), "This user has already been invited to project!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        MyAsync ma = new MyAsync();
                        ma.execute();
                        Toast.makeText(getApplicationContext(), "Invite new member successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //Save member to a project
    private class MyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            PD = new ProgressDialog(Member.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String project=loadProjectName;
            String mail=eMail.getText().toString().toLowerCase();
            sqlController.open();
            sqlController.insertMemberData(project, mail);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            PD.dismiss();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eMail.getWindowToken(), 0);
            eMail.setText("");
        }
    }

    //Check member
    private List<String> checkMember(){
        String project=loadProjectName;
        String mail=eMail.getText().toString().toLowerCase();

        sqlController.open();
        Cursor ProjectMemberCursor=sqlController.checkMemberEntry(project, mail);
        List<String> items=new ArrayList<String>();
        String result="";
        int mName=ProjectMemberCursor.getColumnIndex(MyDbHelper.PROJECT_MEMBER_MEMBER_NAME);
        for (ProjectMemberCursor.moveToFirst(); !ProjectMemberCursor.isAfterLast(); ProjectMemberCursor.moveToNext()){
            result=ProjectMemberCursor.getString(mName);
            items.add(result);
        }
        ProjectMemberCursor.close();
        return items;
    }
}
