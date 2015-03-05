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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
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
import android.widget.TableRow.LayoutParams;

public class MainActivity extends Activity {
//    TableLayout tableLayout;
    TextView tUser,tAlert;
    EditText eName;
    Button reg;
    SQLController sqlController;
    ProgressDialog PD;
    public static final String ACCOUNT_INTENT="com.comet_000.myapplication.MESSAGE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Intent intent = new Intent(this, Project.class);

        ListView lv = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, getUsername());
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<String> items=getUsername();
                String item = items.get(position);
                tUser=(TextView)findViewById(R.id.txtMsg);
                tUser.setText(item);
                tAlert.setVisibility(View.VISIBLE);
                eName.setEnabled(true);
            }
        });

        sqlController=new SQLController(this);
        eName=(EditText)findViewById(R.id.txtName);
        reg=(Button)findViewById(R.id.btnAdd);
        tAlert=(TextView)findViewById(R.id.txtAlert);

        eName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reg.setEnabled(!eName.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        tableLayout=(TableLayout)findViewById(R.id.tableLayout);
//        BuildTable();
        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eName.getText().toString().trim().isEmpty())
                {
                    Toast.makeText(getApplicationContext(), "Please enter display name", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    MyAsync ma = new MyAsync();
                    ma.execute();
                    intent.putExtra(ACCOUNT_INTENT, tUser.getText().toString());
                    startActivity(intent);
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
            possibleEmails.add(account.name);
        }
    return possibleEmails;
    }

//    private void BuildTable(){
//        sqlController.open();
//        Cursor c=sqlController.readAccountEntry();
//        int rows=c.getCount();
//        int cols=c.getColumnCount();
//        c.moveToFirst();
//        for(int i=0; i<rows; i++){
//            TableRow row=new TableRow(this);
//            row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
//            for (int j = 0; j < cols; j++) {
//                TextView tv = new TextView(this);
//                tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
//                        LayoutParams.WRAP_CONTENT));
//                tv.setGravity(Gravity.CENTER);
//                tv.setTextSize(18);
//                tv.setPadding(0, 5, 0, 5);
//                tv.setText(c.getString(j));
//                row.addView(tv);
//            }
//            tableLayout.addView(row);
//            c.moveToNext();
//        }
//        sqlController.close();
//    }

    private class MyAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            tableLayout.removeAllViews();
            PD = new ProgressDialog(MainActivity.this);
            PD.setTitle("Please Wait..");
            PD.setMessage("Loading...");
            PD.setCancelable(false);
            PD.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String user = tUser.getText().toString();
            String name = eName.getText().toString();
            // inserting data
            sqlController.open();
            sqlController.insertAccountData(user, name);
//            BuildTable();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
//            BuildTable();
            PD.dismiss();
        }
    }
}
