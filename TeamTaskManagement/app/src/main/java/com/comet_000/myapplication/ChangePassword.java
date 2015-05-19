package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import org.w3c.dom.Text;

import java.util.concurrent.ExecutionException;


public class ChangePassword extends ActionBarActivity {
//    private Toolbar toolbar;
    TextView txtAccount;
    Button change;
    EditText ePass;
    String result;
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Toolbar toolBar=(Toolbar)findViewById(R.id.app_bar);
        setSupportActionBar(toolBar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Setup ormlite
        dbHelper = OpenHelperManager.getHelper(ChangePassword.this, DatabaseHelper.class);
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        dataProvider.setTableAccount(myTableAccount);

        Intent intent=getIntent();
        final String loadAccount=intent.getStringExtra("accountID");
        txtAccount=(TextView)findViewById(R.id.txtAccount);
        txtAccount.setText("ID: "+loadAccount);

        ePass=(EditText)findViewById(R.id.txtPass);
        change=(Button)findViewById(R.id.btnChange);
        change.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (ePass.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    MailSender myMailSender = new MailSender(ChangePassword.this);
                    try {
                        result = myMailSender.check("pop.gmail.com", loadAccount, ePass.getText().toString());
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (result.equals("Ok")) {
                        dataProvider.updatePass(ePass.getText().toString());
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setMessage("Change password successfully!")
                                .setPositiveButton("Ok", dialogClickListener).show();
                        ePass.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(ePass.getWindowToken(), 0);
                    }
                    if (result.equals("foo")) {
                        Toast.makeText(getApplicationContext(), "foo", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (result.equals("Wrong password")) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChangePassword.this);
                        builder.setMessage("Wrong password, please try again!")
                                .setPositiveButton("Ok", dialogClickListener).show();
                        return;
                    }
                }
            }
        });
    }


//    private void getSupportActionBar(Toolbar toolbar) {
//    }


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

        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
