package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by King on 05-Jun-15.
 */
public class DialogDeleteProject extends DialogFragment {
    Boolean[] checkList;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        final String[] projectList = ((Project)getActivity()).loadProjectString();
        checkList = new Boolean[projectList.length];
        for (int i=0; i<checkList.length; i++) {
            checkList[i] = false;
        }
        builder.setTitle("Choose project to delete");
        builder.setMultiChoiceItems(projectList, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkList[which] = isChecked;
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((Project)getActivity()).deleteProject(projectList, checkList);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder.create();
    }
}
