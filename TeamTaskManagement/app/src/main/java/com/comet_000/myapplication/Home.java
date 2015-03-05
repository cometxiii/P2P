package com.comet_000.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

public class Home extends Activity {
    public static final String ACCOUNT_INTENT="com.comet_000.myapplication.MESSAGE";
    SQLController sqlController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button btnGo=(Button)findViewById(R.id.btnGo);

        sqlController=new SQLController(this);
        sqlController.open();
        final Intent intent = new Intent(this, MainActivity.class);
        final Intent intent2 = new Intent(this, Project.class);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] accounts = sqlController.checkAccount(sqlController.database);

                if (accounts.length == 0)
                {
                    startActivity(intent);
                }
                else
                {
                    intent2.putExtra(ACCOUNT_INTENT, accounts[0]);
                    startActivity(intent2);
                }
            }
        });
    }
}
