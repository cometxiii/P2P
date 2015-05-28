package com.comet_000.myapplication;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.mail.Address;
import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.auth.AuthenticationException;

/**
 * Created by King on 30-Mar-15.
 */
public class MailSender {
    String author = null;
    String receiver = null;
    String subject = null;
    String password = null;
    String textMessage = null;
    Session session = null;
    Session emailSession = null;
    Context context = null;
    public MailSender(Context context) {
        this.context = context;
    }

    public MailSender(String receiver, String subject, String textMessage, String author, String password, Context context) {
        this.receiver = receiver;
        this.subject = subject;
        this.textMessage = textMessage;
        this.password = password;
        this.author = author;
        this.context = context;
    }

    public String check(String host, String user, String password) throws ExecutionException, InterruptedException {
        Properties properties = new Properties();
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        emailSession = Session.getDefaultInstance(properties);
        String myResult = new CheckPassword().execute(host, user, password).get();
        return myResult;
    }

    class CheckPassword extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Loading...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            Store store = null;
            try {
                store = emailSession.getStore("pop3s");
                store.connect(params[0], params[1], params[2]);
                Folder emailFolder = store.getFolder("INBOX");
                emailFolder.open(Folder.READ_ONLY);
                Message[] messages = emailFolder.getMessages();
                emailFolder.close(false);
                store.close();
            } catch (AuthenticationFailedException e) {
                e.printStackTrace();
                return "Wrong password";
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return "Ok";
        }
        @Override
        protected void onPostExecute(String unused) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

    public void send() {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(author, password);
            }
        });
            SendMail task = new SendMail();
            task.execute();
    }

    class SendMail extends AsyncTask<String, Void, String> {
        ProgressDialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(context);
            dialog.setMessage("Sending...");
            dialog.show();
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(author));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
                message.setSubject(subject);
                message.setContent(textMessage, "text/plain; charset=utf-8");
                Transport.send(message);
            } catch (AuthenticationFailedException e) {
                e.printStackTrace();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return "Ok";
        }
        @Override
        protected void onPostExecute(String unused) {
            super.onPostExecute(unused);
            dialog.dismiss();
            Toast.makeText(context, "Email has been sent to " + receiver + "!", Toast.LENGTH_SHORT).show();
            if(subject.equals("P2P invitation")) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Invitation has been sent to " + receiver + ".")
                        .setPositiveButton("Ok", dialogClickListener).show();
            }
        }
    }
}
