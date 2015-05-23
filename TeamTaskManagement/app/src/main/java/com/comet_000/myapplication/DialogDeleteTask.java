package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.List;

/**
 * Created by King on 23-May-15.
 */
public class DialogDeleteTask extends DialogFragment {
    Boolean[] checkList;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        final String[] taskList = ((TaskMember)getActivity()).loadTaskString();
        checkList = new Boolean[taskList.length];
        for (int i=0; i<checkList.length; i++) {
            checkList[i] = false;
        }
        builder.setTitle("Choose tasks to delete");
        builder.setMultiChoiceItems(taskList, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkList[which] = isChecked;
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((TaskMember)getActivity()).deleteTask(taskList, checkList);
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
