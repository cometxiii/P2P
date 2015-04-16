package com.comet_000.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class TaskMember extends ActionBarActivity {
    private Toolbar toolbar;
    ListView listViewTask, listViewMember;
    TextView tPName1, tPName2, tDes1, tDes2;
    Button addTask, addMember;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    public static final String PROJECT_INTENT="com.comet_000.myapplication.PROJECT";
    String loadProjectName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_appbar);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        TabHost tabHost=(TabHost)findViewById(R.id.tabHost);
        Intent intent=getIntent();

        dbHelper = OpenHelperManager.getHelper(TaskMember.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableTask, Integer> myTableTask = dbHelper.getTableTask();
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProjectMember(myTableProjectMember);
        dataProvider.setTableProject(myTableProject);
        dataProvider.setTableTask(myTableTask);

        loadProjectName = intent.getStringExtra("projectName");

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

        tPName1.setText(loadProjectName);
        tPName2.setText(loadProjectName);


        //Load descriptions of a project
        loadProjectDescriptions();

        addTask=(Button)findViewById(R.id.btnAddTask);
        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentTask=new Intent(TaskMember.this, Task.class);
                intentTask.putExtra("projectName", loadProjectName);
                startActivity(intentTask);
            }
        });

        addMember=(Button)findViewById(R.id.btnAddMember);
        addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentMember=new Intent(TaskMember.this, Member.class);
                intentMember.putExtra("projectName", loadProjectName);
                startActivity(intentMember);
            }
        });

        listViewTask=(ListView)findViewById(R.id.listViewTask);
        listViewMember=(ListView)findViewById(R.id.listViewMember);
        loadTasks();
        loadMembers();
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
            return true;
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
                List<String> tItems = dataProvider.getTaskByFieldNameString("ProjectName", loadProjectName);
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
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataProvider.getTaskByFieldNameString("ProjectName", loadProjectName));
        listViewTask.setAdapter(adapter);
    }

    //Load list of members in ListView
    private void loadMembers(){
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, dataProvider.getProjectMemberByFieldNameString("ProjectName", loadProjectName));
        listViewMember.setAdapter(adapter);
    }

    //Load project descriptions
    private void loadProjectDescriptions(){
        TableProject myProject = dataProvider.get1ProjectByFieldName("ProjectName", loadProjectName);
        tDes1.setText(myProject.getProjectDescriptions());
        tDes2.setText(myProject.getProjectDescriptions());

    }
}
