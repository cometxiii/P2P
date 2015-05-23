package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    ToastMaker toastMaker;

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
        toastMaker = new ToastMaker(getActivity());
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
        super.onStart();
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
                        toastMaker.makeToastMiddle("Please enter project name and descriptions");
                    } else if (eName.getText().toString().trim().isEmpty()) {
                        toastMaker.makeToastMiddle("Please enter project name");
                    } else if (eDes.getText().toString().trim().isEmpty()) {
                        toastMaker.makeToastMiddle("Please enter project descriptions");
                    } else {
                        if(eName.getText().toString().length()>20 && eDes.getText().toString().length()>200){
                            toastMaker.makeToastMiddle("Project name can not be longer than 20 characters. Descriptions can not be longer than 200 characters.");
                        }
                        else if(eName.getText().toString().length()>20 || eDes.getText().toString().length()>200){
                            if(eName.getText().toString().length()>20){
                                toastMaker.makeToastMiddle("Project name can not be longer than 20 characters.");
                            }
                            else{
                                toastMaker.makeToastMiddle("Descriptions can not be longer than 200 characters.");
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
                                toastMaker.makeToastMiddle("This project has already been created!");
                            }
                        }
                    }
                    if(wantToCloseDialog)
                        d.dismiss();
                }
            });
        }
    }

    protected Void addProject(String name, String des, String user) {
        if (!dataProvider.checkProject(name, user)) {
            toastMaker.makeToast("This project is already exist!");
            return null;
        }
            dataProvider.addProject(new TableProject(name, des, user));
            dataProvider.addProjectMember(new TableProjectMember(name, user, loadAccount, "Accepted"));
        ((Project)getActivity()).loadProjects();
        toastMaker.makeToast("Add new project successfully!");
        return null;
    }
}
