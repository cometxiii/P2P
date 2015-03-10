package com.comet_000.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class Home extends Activity {
    public static final String ACCOUNT_INTENT="com.comet_000.myapplication.MESSAGE";
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
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
                    startActivity(intent2);
                }
            }
        });
    }
}
