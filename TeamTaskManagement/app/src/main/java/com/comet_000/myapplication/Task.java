package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

public class Task extends Activity implements AdapterView.OnItemSelectedListener{
    TextView txtMsg;
    Button add;
    EditText eName, eDes;
    Spinner spinner;
    SQLController sqlController;
    ProgressDialog PD;
    String loadProjectName, memberName;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();

//    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);


        dbHelper = OpenHelperManager.getHelper(Task.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        dataProvider.setTableTask(myTableTask);

        sqlController=new SQLController(this);
        Intent intent=getIntent();
        loadProjectName=intent.getStringExtra(TaskMember.PROJECT_INTENT);
        txtMsg=(TextView)findViewById(R.id.txtMsg);
        txtMsg.setText("Your selected project: "+loadProjectName);

        eName=(EditText)findViewById(R.id.txtName);
        eDes=(EditText)findViewById(R.id.txtDes);
        spinner=(Spinner)findViewById(R.id.spinnerMember);

        loadSpinner();
//        tableLayout=(TableLayout)findViewById(R.id.tableLayoutTask);
//        BuildTable();

        add=(Button)findViewById(R.id.btnAdd);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eName.getText().toString().trim().isEmpty() && eDes.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please enter task name and descriptions", Toast.LENGTH_SHORT).show();
                }
                else if(eName.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter task name", Toast.LENGTH_SHORT).show();
                }
                else if(eDes.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please enter task descriptions", Toast.LENGTH_SHORT).show();
                }
                else{
                        if(dataProvider.checkTaskByFieldName("TaskName", eName.toString())){
                            Toast.makeText(getApplicationContext(), "This task has already been existing in project!", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            MyAsync ma=new MyAsync();
                            ma.execute();
                            Toast.makeText(getApplicationContext(),"Add new task successfully!", Toast.LENGTH_SHORT).show();
                        }
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        memberName=parent.getItemAtPosition(position).toString();
//        Toast.makeText(parent.getContext(),"Selected member: "+memberName, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    //Save new task to database
    private class MyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            PD = new ProgressDialog(Task.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
//            tableLayout.removeAllViews();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String name=eName.getText().toString();
            String des=eDes.getText().toString();
            String project=loadProjectName;
            String member=spinner.getSelectedItem().toString();
            dataProvider.addTask(new TableTask(name, member, des, project));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            PD.dismiss();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
            eName.setText("");
            eDes.setText("");
//            BuildTable();
        }
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
        adapter.add("");
        adapter.add("");
        spinner.setAdapter(adapter);
        spinner.setSelection(adapter.getCount());
    }

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

    //Check task
//    private List<String> checkTask(){
//        String task=eName.getText().toString();
//        String project=loadProjectName;
//
//        sqlController.open();
//        Cursor TaskCursor=sqlController.checkTaskEntry(task, project);
//        List<String> items=new ArrayList<String>();
//        String result="";
//        int tName=TaskCursor.getColumnIndex(MyDbHelper.TASK_NAME);
//        for (TaskCursor.moveToFirst(); !TaskCursor.isAfterLast(); TaskCursor.moveToNext()){
//            result=TaskCursor.getString(tName);
//            items.add(result);
//        }
//        TaskCursor.close();
//        return items;
//    }

//    private void BuildTable(){
//        sqlController.open();
//        Cursor c=sqlController.readTaskEntry(loadProjectName);
//        int rows=c.getCount();
//        int cols=c.getColumnCount();
//        c.moveToFirst();
//        for(int i=0; i<rows; i++){
//            TableRow row=new TableRow(this);
//            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
//            for (int j = 0; j < cols; j++) {
//                TextView tv = new TextView(this);
//                tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,
//                        TableRow.LayoutParams.WRAP_CONTENT));
//                tv.setGravity(Gravity.CENTER);
//                tv.setTextSize(18);
//                tv.setPadding(0, 5, 0, 5);
//                tv.setText(c.getString(j));
//                row.addView(tv);
//            }
//            tableLayout.addView(row);
//            c.moveToNext();
//        }
//        sqlController.close();
//    }
}
