package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by King on 24-May-15.
 */
public class DialogDeleteMember extends DialogFragment {
    Boolean[] checkList;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        final String[] memberList = ((TaskMember)getActivity()).loadMemberString();
        if (memberList.length == 0)
            this.dismiss();
        checkList = new Boolean[memberList.length];
        for (int i=0; i<checkList.length; i++) {
            checkList[i] = false;
        }
        builder.setTitle("Choose members to delete");
        builder.setMultiChoiceItems(memberList, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkList[which] = isChecked;
            }
        });
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((TaskMember)getActivity()).deleteMember(memberList, checkList);
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
