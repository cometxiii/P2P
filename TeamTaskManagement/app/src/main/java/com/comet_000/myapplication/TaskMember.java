package com.comet_000.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TaskMember extends Activity {
    SQLController sqlController;
    ListView listViewTask, listViewMember;
    TextView tPName1, tPName2, tDes1, tDes2;
    Button addTask, addMember;
    public static final String PROJECT_INTENT="com.comet_000.myapplication.PROJECT";
    String loadProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_member);

        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        Intent intent=getIntent();
//        loadProjectName=intent.getStringExtra(Project.PROJECT_INTENT);
        sqlController=new SQLController(this);

        String loadProjectNameFromProject=intent.getStringExtra(Project.PROJECT_INTENT);
        String loadProjectNameFromUpdateTask=intent.getStringExtra(UpdateTask.PROJECT_INTENT);

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

//        tPName1.setText(loadProjectName);
//        tPName2.setText(loadProjectName);

        tPName1.setText(loadProjectNameFromProject);
        tPName2.setText(loadProjectNameFromProject);
        tPName1.setText(loadProjectNameFromUpdateTask);
        tPName2.setText(loadProjectNameFromUpdateTask);

        loadProjectName=tPName1.getText().toString();

        //Load descriptions of a project
        loadProjectDescriptions();

        addTask=(Button)findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTask=new Intent(TaskMember.this, Task.class);
                intentTask.putExtra(PROJECT_INTENT, loadProjectName);
                startActivity(intentTask);
            }
        });

        addMember=(Button)findViewById(R.id.btnAddMember);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMember=new Intent(TaskMember.this, Member.class);
                intentMember.putExtra(PROJECT_INTENT, loadProjectName);
                startActivity(intentMember);
            }
        });

        listViewTask=(ListView)findViewById(R.id.listViewTask);
        listViewMember=(ListView)findViewById(R.id.listViewMember);
        loadTasks();
        loadMembers();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tPName1=(TextView)findViewById(R.id.txtProjectName);
        tPName2=(TextView)findViewById(R.id.txtProjectName2);
        tDes1=(TextView)findViewById(R.id.txtDes);
        tDes2=(TextView)findViewById(R.id.txtDes2);

        tPName1.setText(loadProjectName);
        tPName2.setText(loadProjectName);

        addTask=(Button)findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTask=new Intent(TaskMember.this, Task.class);
                intentTask.putExtra(PROJECT_INTENT, loadProjectName);
                startActivity(intentTask);
            }
        });

        addMember=(Button)findViewById(R.id.btnAddMember);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMember=new Intent(TaskMember.this, Member.class);
                intentMember.putExtra(PROJECT_INTENT, loadProjectName);
                startActivity(intentMember);
            }
        });

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
                List<String> tItems=getTaskName();
                String item=tItems.get(position);
                myBundle.putString("projectName", loadProjectName);
                myBundle.putString("taskName", item);
                intentToUpdateTask.putExtras(myBundle);
                startActivity(intentToUpdateTask);
            }
        });
    }

    //Load project information

    //Load list of task names in ListView
    private void loadTasks(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, getTaskName());
        listViewTask.setAdapter(adapter);
    }

    //Load list of members in ListView
    private void loadMembers(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, getMemberName());
        listViewMember.setAdapter(adapter);
    }

    //Get project description
    private List<String> getDescriptions(){
        sqlController.open();
        Cursor DescriptionsCursor=sqlController.readSelectedProject(loadProjectName);
        List<String> items=new ArrayList<String>();
        String result="";
        int pDes=DescriptionsCursor.getColumnIndex(MyDbHelper.PROJECT_DESCRIPTION);
        for (DescriptionsCursor.moveToFirst(); !DescriptionsCursor.isAfterLast(); DescriptionsCursor.moveToNext()){
            result=DescriptionsCursor.getString(pDes);
            items.add(result);
        }
        DescriptionsCursor.close();
        return items;
    }

    //Get list of task names in a project
    private List<String> getTaskName(){
        sqlController.open();
        Cursor TaskCursor=sqlController.readTaskEntry(loadProjectName);
        List<String> items=new ArrayList<String>();
        String result="";
        int tName=TaskCursor.getColumnIndex(MyDbHelper.TASK_NAME);
        for (TaskCursor.moveToFirst(); !TaskCursor.isAfterLast(); TaskCursor.moveToNext()){
            result=TaskCursor.getString(tName);
            items.add(result);
        }
        TaskCursor.close();
        return items;
    }

    //Get list of task names in a project
    private List<String> getTaskMember(){
        sqlController.open();
        Cursor TaskCursor=sqlController.readTaskEntry(loadProjectName);
        List<String> items=new ArrayList<String>();
        String result="";
        int tMember=TaskCursor.getColumnIndex(MyDbHelper.TASK_PROJECT_MEMBER_TASK_MEMBER_NAME);
        for (TaskCursor.moveToFirst(); !TaskCursor.isAfterLast(); TaskCursor.moveToNext()){
            result=TaskCursor.getString(tMember);
            items.add(result);
        }
        TaskCursor.close();
        return items;
    }

    //Get list of task descriptions in a project
    private List<String> getTaskDescriptions(){
        sqlController.open();
        Cursor TaskCursor=sqlController.readTaskEntry(loadProjectName);
        List<String> items=new ArrayList<String>();
        String result="";
        int tDes=TaskCursor.getColumnIndex(MyDbHelper.TASK_DESCRIPTION);
        for (TaskCursor.moveToFirst(); !TaskCursor.isAfterLast(); TaskCursor.moveToNext()){
            result=TaskCursor.getString(tDes);
            items.add(result);
        }
        TaskCursor.close();
        return items;
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

    //Load project descriptions
    private void loadProjectDescriptions(){
        sqlController.open();
        Cursor c=sqlController.readSelectedProject(loadProjectName);
        c.moveToFirst();
        int rows=c.getCount();
        for (int i=0; i<rows; i++){
            String projectDescriptions=c.getString(1);
            tDes1.setText(projectDescriptions);
            tDes2.setText(projectDescriptions);
            c.moveToNext();
        }
        sqlController.close();
    }
}
