package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;

public class Project extends Activity {
    Button add;
    EditText eName, eDes;
    SQLController sqlController;
    TableLayout tableLayout;
    ListView listView;
    TextView display;
    ProgressDialog PD;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    public static final String PROJECT_INTENT = "com.comet_000.myapplication.PROJECT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        final Intent intentToTaskMember = new Intent(this, TaskMember.class);

        Intent intent = getIntent();
        String loadAccountFromHome = intent.getStringExtra(Home.ACCOUNT_INTENT);
        String loadAccountFromMain = intent.getStringExtra(MainActivity.ACCOUNT_INTENT);
        display = (TextView) findViewById(R.id.txtDisplay);
        display.setText(loadAccountFromHome);
        display.setText(loadAccountFromMain);

        eName = (EditText) findViewById(R.id.txtTitle);
        eDes = (EditText) findViewById(R.id.txtDes);

        //connect to database using ORMLite
        dbHelper = OpenHelperManager.getHelper(Project.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        dataProvider.setTableProject(myTableProject);

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
                        MyAsync ma = new MyAsync();
                        ma.execute();
                        Toast.makeText(getApplicationContext(), "Add new project successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Select a project
//        tableLayout=(TableLayout)findViewById(R.id.tableLayoutProject);
        listView = (ListView) findViewById(R.id.listView);
//        populateListView();

        loadProjects();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                List<String> lItems=getProjectName();
                List<String> lItems = dataProvider.getAllProjectString();
                String item = lItems.get(position);
//                TextView msg=(TextView)findViewById(R.id.txtMsg);
//                msg.setText(item);
                intentToTaskMember.putExtra(PROJECT_INTENT, item);
                startActivity(intentToTaskMember);
            }
        });
    }

    //Load project names to ListView
    private void loadProjects() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataProvider.getAllProjectString());
        listView.setAdapter(adapter);
    }

    //Get List<String> from SQLite
    private List<String> getProjectName() {
        sqlController.open();
        Cursor ProjectCursor = sqlController.readProjectEntry();
        List<String> items = new ArrayList<String>();
        String result = "";
        int pName = ProjectCursor.getColumnIndex(MyDbHelper.PROJECT_NAME);
        for (ProjectCursor.moveToFirst(); !ProjectCursor.isAfterLast(); ProjectCursor.moveToNext()) {
            result = ProjectCursor.getString(pName);
            items.add(result);
        }
        ProjectCursor.close();
        return items;
    }

    //Save new project to database
    private class MyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            tableLayout.removeAllViews();
            PD = new ProgressDialog(Project.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        //add new project to database
        protected Void doInBackground(Void... params) {
            String name = eName.getText().toString();
            String des = eDes.getText().toString();
            String user = display.getText().toString();
            dataProvider.addProject(new TableProject(name, des, user));
//            sqlController.open();
//            sqlController.insertProjectData(name,des,user);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            populateListView();
            loadProjects();
            PD.dismiss();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(eName.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(eDes.getWindowToken(), 0);
            eName.setText("");
            eDes.setText("");
        }
    }

    //Load project information to ListView
//    private void populateListView(){
//        sqlController.open();
//        Cursor cursor=sqlController.readProjectEntry();
//        String[] fromFieldNames=new String[] {MyDbHelper.PROJECT_NAME};
//        int[] toViewIDs=new int[]{R.id.txtProjectName};
//        SimpleCursorAdapter myCursorAdapter;
//        myCursorAdapter=new SimpleCursorAdapter(getBaseContext(),R.layout.project_items_layout, cursor, fromFieldNames, toViewIDs, 0);
////        myCursorAdapter=new SimpleCursorAdapter(this,R.layout.project_items_layout, cursor, fromFieldNames, toViewIDs);
//        listView.setAdapter(myCursorAdapter);
//    }


//    //Load project descriptions
//    private void loadProjectName(){
//        sqlController.open();
//        Cursor c=sqlController.checkProjectEntry(eName.getText().toString(),display.getText().toString());
//        c.moveToFirst();
//        int rows=c.getCount();
//            String projectName=c.getString(0);
//        sqlController.close();
//    }
}
