package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;

/**
 * Created by King on 21-May-15.
 */
public class DialogAddProject extends DialogFragment{
    DatabaseHelper dbHelper;
    DataProvider dataProvider = new DataProvider();
    EditText eName, eDes;
    TableAccount myAccount;
    String loadAccount;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dbHelper = OpenHelperManager.getHelper(getActivity(), DatabaseHelper.class);
        RuntimeExceptionDao<TableProject, Integer> myTableProject = dbHelper.getTableProject();
        RuntimeExceptionDao<TableAccount, Integer> myTableAccount = dbHelper.getTableAccount();
        RuntimeExceptionDao<TableProjectMember, Integer> myTableProjectMember = dbHelper.getTableProjectMember();
        dataProvider.setTableProject(myTableProject);
        dataProvider.setTableAccount(myTableAccount);
        dataProvider.setTableProjectMember(myTableProjectMember);
        myAccount = dataProvider.getAccountById(1);
        loadAccount = myAccount.Account;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        LayoutInflater inflate = getActivity().getLayoutInflater();
        View view = inflate.inflate(R.layout.dialog_newproject, null);
        eName = (EditText) view.findViewById(R.id.txtProjectName);
        eDes = (EditText) view.findViewById(R.id.txtDescriptions);
        builder.setTitle("Add new project");
        builder.setView(view);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }

    @Override
    public void onStart()
    {
        super.onStart();    //super.onStart() is where dialog.show() is actually called on the underlying dialog, so we have to do it after this point
        final AlertDialog d = (AlertDialog)getDialog();
        if(d != null)
        {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Boolean wantToCloseDialog = false;
                    //Do stuff, possibly set wantToCloseDialog to true then...
                    if (eName.getText().toString().trim().isEmpty() && eDes.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(),"Please enter project name and descriptions", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else if (eName.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(),"Please enter project name", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else if (eDes.getText().toString().trim().isEmpty()) {
                        Toast toast = Toast.makeText(getActivity(),"Please enter project descriptions", Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        if(eName.getText().toString().length()>20 && eDes.getText().toString().length()>200){
                            Toast toast = Toast.makeText(getActivity(),"Project name can not be longer than 20 characters. Descriptions can not be longer than 200 characters.", Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                        else if(eName.getText().toString().length()>20 || eDes.getText().toString().length()>200){
                            if(eName.getText().toString().length()>20){
                                Toast toast = Toast.makeText(getActivity(),"Project name can not be longer than 20 characters.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                            else{
                                Toast toast = Toast.makeText(getActivity(),"Descriptions can not be longer than 200 characters.", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                        else{
                            if (dataProvider.checkProject((eName.getText()).toString(), loadAccount)) {
                                String name = eName.getText().toString();
                                String des = eDes.getText().toString();
                                String owner = loadAccount;
                                addProject(name, des, owner);
                                wantToCloseDialog = true;
                            }
                            else {
                                Toast toast = Toast.makeText(getActivity(),"This project has already been created by you!", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }
                        }
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                    //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
                }
            });
        }
    }

    protected Void addProject(String name, String des, String user) {
        if (!dataProvider.checkProject(name, user)) {
            Toast.makeText(getActivity(), "This project is already exist!", Toast.LENGTH_SHORT).show();
            return null;
        }
        if (loadAccount.equals(user)) {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, user));
        } else {
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, loadAccount));
        }
        ((Project)getActivity()).loadProjects();
        Toast.makeText(getActivity(), "Add new project successfully!", Toast.LENGTH_SHORT).show();
        return null;
    }
}
