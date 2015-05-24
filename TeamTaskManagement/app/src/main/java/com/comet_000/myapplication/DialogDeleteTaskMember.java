package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by King on 24-May-15.
 */
public class DialogDeleteTaskMember extends DialogFragment {
    String loadProjectName, loadOwner, loadAccount, loadProjectDes;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        builder.setTitle("Choose an option");
        String[] options = new String[2];
        options[0] = "Delete task";
        options[1] = "Delete member";
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0) {
                    ((TaskMember)getActivity()).showDialogDeleteTask();
                } else {
                    ((TaskMember)getActivity()).showDialogDeleteMember();
                }
            }
        });
        builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog dialog = builder.create();
        return dialog;
    }
}
