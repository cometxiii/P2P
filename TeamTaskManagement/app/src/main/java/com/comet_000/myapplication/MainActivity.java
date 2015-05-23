package com.comet_000.myapplication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import android.widget.TableRow.LayoutParams;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class MainActivity extends ActionBarActivity {
    private Toolbar toolbar;
    TextView tUser, tAlert;
    EditText ePass;
    Button reg;
    DatabaseHelper dbHelper;
    ProgressDialog PD;
    DataProvider dataProvider = new DataProvider();
    String account, password;
    ToastMaker toastMaker;
    Session emailSession = null;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);
        intent = new Intent(this, Home.class);
        toastMaker = new ToastMaker(getApplicationContext());
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
                if (ePass.getText().toString().trim().isEmpty()) {
                    toastMaker.makeToast("Please enter password");
                    return;
                } else {
                    CheckPassword task = new CheckPassword();
                    account = tUser.getText().toString();
                    password = ePass.getText().toString();
                    task.execute(account, password);
                }
            }
        });
    }

    class CheckPassword extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(MainActivity.this);
            dialog.setMessage("Loading...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            Store store = null;
            try {
                Properties properties = new Properties();
                properties.put("mail.pop3.host", "pop.gmail.com");
                properties.put("mail.pop3.port", "995");
                properties.put("mail.pop3.starttls.enable", "true");
                emailSession = Session.getDefaultInstance(properties);
                store = emailSession.getStore("pop3s");
                store.connect("pop.gmail.com", params[0], params[1]);
                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);
                Message[] messages = emailFolder.getMessages();
                emailFolder.close(false);
                store.close();
            } catch (AuthenticationFailedException e) {
                e.printStackTrace();
                return "Wrong password";
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return "Ok";
        }
        @Override
        protected void onPostExecute(String result) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (result.equals("Ok")) {
                toastMaker.makeToast("Sign-in successfully!");
                addUser();
                intent.putExtra("account", account);
                intent.putExtra("password", password);
                intent.putExtra("CallingActivity", "Main");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(ePass.getWindowToken(), 0);
                startActivity(intent);
            }
            if (result.equals("foo")) {
                toastMaker.makeToast("foo");
                return;
            }
            if (result.equals("Wrong password")) {
                toastMaker.makeToast("Wrong password");
                return;
            }
        }
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
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.addAccount(new TableAccount(account, password));
        return null;
    }
}
