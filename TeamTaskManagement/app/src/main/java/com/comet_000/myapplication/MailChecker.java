package com.comet_000.myapplication;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FlagTerm;

public class MailChecker {
    String user, password;

    public MailChecker(String user, String password) {
        this.user = user;
        this.password = password;
    }

    public String[] check() throws ExecutionException, InterruptedException {
        String[] task = new checkMail().execute().get();
        return task;
    }

    class checkMail extends AsyncTask<String, Void, String[]> {
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
    }
}
