package com.comet_000.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by comet_000 on 21/05/2015.
 */
public class DialogAddTaskMember extends DialogFragment {
    String loadProjectName, loadOwner, loadAccount, loadProjectDes;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        setCancelable(false);
        builder.setTitle("Choose an option");
        String[] options = new String[2];
        options[0] = "Add new task";
        options[1] = "Invite member";
        getStrings();
        builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0) {
                            Intent intentTask=new Intent(getActivity(), Task.class);
                            intentTask.putExtra("intentProjectName", loadProjectName);
                            intentTask.putExtra("intentOwner", loadOwner);
                            intentTask.putExtra("intentAccount", loadAccount);
                            startActivity(intentTask);
                        } else {
                            Intent intentMember=new Intent(getActivity(), Member.class);
                            intentMember.putExtra("intentProjectName", loadProjectName);
                            intentMember.putExtra("intentProjectDes", loadProjectDes);
                            startActivity(intentMember);
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
    private void getStrings() {
        loadAccount = ((TaskMember)getActivity()).loadAccount;
        loadOwner = ((TaskMember)getActivity()).loadOwner;
        loadProjectName = ((TaskMember)getActivity()).loadProjectName;
        loadProjectDes = ((TaskMember)getActivity()).loadProjectDes;
    }
}
