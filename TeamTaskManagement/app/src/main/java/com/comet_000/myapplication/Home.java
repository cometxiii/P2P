package com.comet_000.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.TooManyListenersException;

public class Home extends ActionBarActivity {
    private Toolbar toolbar;
    public static final String ACCOUNT_INTENT="com.comet_000.myapplication.MESSAGE";
    public static final String NAME_INTENT="com.comet_000.myapplication.MESSAGE";
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        Button btnGo=(Button)findViewById(R.id.btnGo);
        final Intent intent = new Intent(this, MainActivity.class);
        final Intent intent2 = new Intent(this, Project.class);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper = OpenHelperManager.getHelper(Home.this, DatabaseHelper.class);
                RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
                dataProvider.setTableAccount(myTableAccount);
                if (dataProvider.getNumOfAccount() == 0)
                {
                    startActivity(intent);
                }
                else
                {
                    TableAccount myAccount = dataProvider.getAccountById(1);
                    intent2.putExtra(ACCOUNT_INTENT, myAccount.Account);
                    intent2.putExtra(NAME_INTENT, myAccount.DisplayName);
                    startActivity(intent2);
                }
            }
        });
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
}
