package com.comet_000.myapplication;
import android.os.AsyncTask;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.BodyTerm;

public class CheckingMails {
    String host = "pop.gmail.com", key = "p2pteamtaskmanagement", user, password;

    public CheckingMails(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public void check() {
        AsyncTask<String, Void, String> task = new checkMail().execute();
    }

    class checkMail extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
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
                inbox.open(Folder.READ_ONLY);
                System.out.println("Total mails are = "+inbox.getMessageCount());

                //Enter term to search here
                final String keyword = "p2pteamtaskmanagement";

                //Search term
                BodyTerm bodyTerm = new BodyTerm(keyword);

                System.out.println("Searching for BodyTerm with keyword = "+keyword);

                Message[] foundMessages = inbox.search(bodyTerm);

                System.out.println("Searching done...");
                System.out.println("Total mails found for searched term are = "+foundMessages.length);

                for(Message message: foundMessages){
                    System.out.println("found message: "+ message.getSubject());
                    System.out.println("found message: "+ message.toString());
                    System.out.println("=====");
                }

            }catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
