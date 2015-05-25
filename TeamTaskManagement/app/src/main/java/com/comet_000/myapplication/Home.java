package com.comet_000.myapplication;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import java.util.Properties;
import java.util.TooManyListenersException;
import java.util.concurrent.ExecutionException;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class Home extends ActionBarActivity {
    private Toolbar toolbar;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    Session emailSession = null;
    ToastMaker toastMaker;
    String account, password;
    Intent intent, intent2, intent3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        dbHelper = OpenHelperManager.getHelper(Home.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);

        intent = new Intent(this, MainActivity.class);
        intent2 = new Intent(this, Project.class);
        intent3 = new Intent(this, ChangePassword.class);

        if (dataProvider.getNumOfAccount() == 0) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(1000);
                        startActivity(intent);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } else {
            TableAccount myAccount = dataProvider.getAccountById(1);
            CheckPassword task = new CheckPassword();
            account = myAccount.Account;
            password = myAccount.Password;
            task.execute(account, password);
        }
    }

    class CheckPassword extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(Home.this);
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
                intent2.putExtra("intentAccount", account);
                intent2.putExtra("CallingActivity", "Home");
                startActivity(intent2);
            }
            if (result.equals("foo")) {
                toastMaker.makeToast("foo");
                return;
            }
            if (result.equals("Wrong password")) {
                toastMaker.makeToast("Please enter your new gmail password!");
                intent3.putExtra("AccountID", account);
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
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
