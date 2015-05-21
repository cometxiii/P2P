package com.comet_000.myapplication;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Created by King on 21-May-15.
 */
public class ToastMaker {
    Context context;
    public ToastMaker(Context context) {
        this.context = context;
    }

    public void makeToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public void  makeToastMiddle(String message) {
        Toast toast = Toast.makeText(context,message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
