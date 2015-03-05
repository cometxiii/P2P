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
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;


public class UpdateTask extends Activity {
    TextView txtMsg, tName;
    Button update;
    EditText eDes;
    Spinner spinner;
    SQLController sqlController;
    ProgressDialog PD;
    String loadProjectName, loadTaskName;
    public static final String PROJECT_INTENT="com.comet_000.myapplication.PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        sqlController=new SQLController(this);

        txtMsg=(TextView)findViewById(R.id.txtMsg);
        tName=(TextView)findViewById(R.id.txtName);
        eDes=(EditText)findViewById(R.id.txtDescriptions);
        spinner=(Spinner)findViewById(R.id.spinner);
        update=(Button)findViewById(R.id.btnUpdate);

        final Intent intent=getIntent();
        Bundle loadBundle=intent.getExtras();
        loadProjectName=loadBundle.getString("projectName");
        loadTaskName=loadBundle.getString("taskName");
        txtMsg.setText("Project: "+loadProjectName);
        tName.setText(loadTaskName);

        loadTaskDescriptions();
        //Load members of project
        loadSpinner();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eDes.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter task descriptions", Toast.LENGTH_SHORT).show();
                }
                else{
                    MyAsync ma=new MyAsync();
                    ma.execute();
                    Toast.makeText(getApplicationContext(),"Update task successfully!", Toast.LENGTH_SHORT).show();
                    Intent intentToTaskMember=new Intent(UpdateTask.this, TaskMember.class);
                    intentToTaskMember.putExtra(PROJECT_INTENT, loadProjectName);
                    startActivity(intentToTaskMember);
                }
            }
        });
    }

    //Load task descriptions
    private void loadTaskDescriptions(){
        sqlController.open();
        Cursor c=sqlController.readSelectedTaskEntry(loadTaskName, loadProjectName);
        c.moveToFirst();
        int rows=c.getCount();
        for (int i=0; i<rows; i++){
            String taskDescriptions=c.getString(1);
            eDes.setText(taskDescriptions);
            c.moveToNext();
        }
        sqlController.close();
    }

    //Get list of members in a project
    private List<String> getMemberName(){
        sqlController.open();
        Cursor TaskCursor=sqlController.readMemberEntry(loadProjectName);
        List<String> items=new ArrayList<String>();
        String result="";
        int mName=TaskCursor.getColumnIndex(MyDbHelper.PROJECT_MEMBER_MEMBER_NAME);
        for (TaskCursor.moveToFirst(); !TaskCursor.isAfterLast(); TaskCursor.moveToNext()){
            result=TaskCursor.getString(mName);
            items.add(result);
        }
        TaskCursor.close();
        return items;
    }

    //Load member of a project to spinner
    private void loadSpinner(){
        sqlController.open();
        List<String> members=getMemberName();
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, members){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (position == getCount()) {
                    ((TextView)v.findViewById(android.R.id.text1)).setText("");
                    ((TextView)v.findViewById(android.R.id.text1)).setHint(getItem(getCount())); //"Hint to be displayed"
                }
                return v;
            }

            @Override
            public int getCount() {
                return super.getCount()-1; // you don't display last item. It is used as hint.
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        Cursor AssigneeCursor=sqlController.readSelectedTaskEntry(loadTaskName, loadProjectName);
        AssigneeCursor.moveToFirst();
        String taskAssignee=AssigneeCursor.getString(3);
        adapter.add("");
        adapter.add(taskAssignee);
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }

    //Update new task
    //Save new task to database
    private class MyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            PD = new ProgressDialog(UpdateTask.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
//            tableLayout.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String name=tName.getText().toString();
            String des=eDes.getText().toString();
            String project=loadProjectName;
            String member=spinner.getSelectedItem().toString();
            sqlController.open();
            sqlController.updateTaskData(name, des, project, member);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            PD.dismiss();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
//            BuildTable();
        }
    }
}
