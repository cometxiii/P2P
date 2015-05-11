package com.comet_000.myapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.widget.TableRow.LayoutParams;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    TextView tUser, tAlert;
    EditText ePass;
    Button reg;
    DatabaseHelper dbHelper;
    ProgressDialog PD;
    DataProvider dataProvider = new DataProvider();
    String result = null;
    public static final String ACCOUNT_INTENT = "com.comet_000.myapplication.MESSAGE";
    public static final String NAME_INTENT = "com.comet_000.myapplication.MESSAGE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        final Intent intent = new Intent(this, Home.class);
        dbHelper = OpenHelperManager.getHelper(MainActivity.this, DatabaseHelper.class);
        ListView lv = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getUsername());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> items = getUsername();
                String item = items.get(position);
                tUser = (TextView) findViewById(R.id.txtMsg);
                tUser.setText(item);
                tAlert.setVisibility(View.VISIBLE);
                ePass.setEnabled(true);
            }
        });

        ePass = (EditText) findViewById(R.id.txtPass);
        reg = (Button) findViewById(R.id.btnAdd);
        tAlert = (TextView) findViewById(R.id.txtAlert);

        ePass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reg.setEnabled(!ePass.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                BackgroundTask task = new BackgroundTask(MainActivity.this);
//                task.execute();
                if (ePass.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();


                    return;
                } else {
                    MailSender myMailSender = new MailSender(MainActivity.this);
                    try {
                        result = myMailSender.check("pop.gmail.com", tUser.getText().toString(), ePass.getText().toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (result.equals("Ok")) {
                        Toast.makeText(getApplicationContext(), "Ok", Toast.LENGTH_SHORT).show();
                        addUser();
                        intent.putExtra("account", tUser.getText().toString());
                        intent.putExtra("password", ePass.getText().toString());
                        intent.putExtra("CallingActivity", "Main");
                        startActivity(intent);
                    }
                    if (result.equals("foo")) {
                        Toast.makeText(getApplicationContext(), "foo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals("Wrong password")) {
                        Toast.makeText(getApplicationContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }
        });
    }

    public List<String> getUsername() {
        AccountManager manager = AccountManager.get(this);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();
        for (Account account : accounts) {
            // TODO: Check possibleEmail against an email regex or treat
            // account.name as an email address only for certain account.type
            // values.

            if (account.name.contains("@gmail.com"))
            {
                possibleEmails.add(account.name);
            }
        }

        return possibleEmails;
    }
    public Void addUser() {
        String user = tUser.getText().toString();
        String name = ePass.getText().toString();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.addAccount(new TableAccount(user, name));
        return null;
    }
}
