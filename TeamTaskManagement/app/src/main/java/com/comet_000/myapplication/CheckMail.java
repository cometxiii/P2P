package com.comet_000.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;

/**
 * Created by King on 11-May-15.
 */
public class CheckMail extends AsyncTask<String, Void, String[]> {
    private Context context;
    ProgressDialog dialog;
    String user, password;
    public CheckMail(String user, String password, Context cxt) {
        this.user = user;
        this.password = password;
        dialog = new ProgressDialog(cxt);
    }
    @Override
    protected void onPreExecute() {
        dialog.setMessage("Loading...");
        dialog.show();
    }
    @Override
    protected String[] doInBackground(String... params) {
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", "imaps");
        try {
            Session session = Session.getDefaultInstance(props, null);
            //GMail
            System.out.println("GMail logging in..");
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", user, password);
            System.out.println("Connected to = "+store);
            Folder inbox = store.getFolder("Inbox");
            inbox.open(Folder.READ_WRITE);
            System.out.println("Total mails are = "+inbox.getMessageCount());

            //Enter term to search here

            BodyTerm bodyTerm = new BodyTerm("<zfgHsj6Uyk>");
            FlagTerm flagTerm = new FlagTerm(new Flags(Flags.Flag.SEEN), false);
            AndTerm andTerm = new AndTerm(bodyTerm,flagTerm);
            //Search

            Message[] foundMessages = inbox.search(andTerm);
            String[] listMessage = new String[foundMessages.length];
            for(int i = 0 ; i < foundMessages.length ; i++) {
                listMessage[i] = foundMessages[i].getContent().toString();
            }
            inbox.setFlags(foundMessages,new Flags(Flags.Flag.SEEN),true);
            store.close();
            return listMessage;
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    protected void onPostExecute(String[] unused) {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
