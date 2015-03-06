package com.comet_000.myapplication;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.ArrayList;
import java.util.List;


public class MainActivity1 extends ActionBarActivity {
    DatabaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        doNoteDataStuff();
    }

    private void doNoteDataStuff()
    {
        dbHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        RuntimeExceptionDao<TableAccount, Integer> accountDao = dbHelper.getTableAccount();
        RuntimeExceptionDao<TableProject, Integer> myTableProjects = dbHelper.getTableProject();
        DataProvider Dp = new DataProvider();
        Dp.myProjectTable = myTableProjects;
        Dp.setTableProject(myTableProjects);
        TextView t1 = (TextView)findViewById(R.id.text);
        //List<TableProject> myProjectList = Dp.getProjectByFieldName("ProjectName", "asdfasdf");
        boolean empty = Dp.checkProjectByFieldName("ProjectName","sjs");

        t1.setText(String.valueOf(empty));

        if(Dp.checkProjectByFieldName("ProjectName","sjs"))
            Toast.makeText(getApplicationContext(), "This project has already been created by you!", Toast.LENGTH_SHORT).show();
        else
        {
            Toast.makeText(getApplicationContext(),"Add new project successfully!", Toast.LENGTH_SHORT).show();
        }



//        List<String> listString = new ArrayList<String>();
//        for (TableProject n : myProjectList) listString.add(n.toString());
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, listString);
//        ListView list = (ListView) findViewById(R.id.listView);
//        list.setAdapter(adapter);
        OpenHelperManager.releaseHelper();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
