package com.comet_000.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 27-Apr-15.
 */
public class MailManager {
    MailManager() {

    }
    public String classifyMail(String message) {
        message = message.replace("<zfgHsj6Uyk>","");
        int firstID = message.indexOf(">");
        String mailType = message.substring(1,firstID);
        return mailType;
    }
    public String makeInvitation(String projectName, String projectDes, String projectOwner) {
        String message = "<zfgHsj6Uyk><Invitation><ProjectName>" + projectName + "<ProjectName>";
        message += "<ProjectDes>" + projectDes + "<ProjectDes>";
        message += "<ProjectOwner>" + projectOwner + "<ProjectOwner>";
        return message;
    }
    public String[] readInvitation(String message) {
        String[] result = new String[3];
        int firstPN = message.indexOf("<ProjectName>") + 13;
        int lastPN =  message.lastIndexOf("<ProjectName>");
        String projectName = message.substring(firstPN, lastPN);
        int firstPD = message.indexOf("<ProjectDes>") + 12;
        int lastPD =  message.lastIndexOf("<ProjectDes>");
        String projectDes = message.substring(firstPD, lastPD);
        int firstPO = message.indexOf("<ProjectOwner>") + 14;
        int lastPO =  message.lastIndexOf("<ProjectOwner>");
        String projectOwner = message.substring(firstPO, lastPO);
        result[0] = projectName;
        result[1] = projectDes;
        result[2] = projectOwner;
        return result;
    }
    public String makeAcceptInvitation(String projectName, String sender) {
        String message = "<zfgHsj6Uyk><AcceptInvitation><ProjectName>" + projectName + "<ProjectName>";
        message += "<Sender>" + sender + "<Sender>";
        return message;
    }
    public String[] readAcceptInvitation(String message) {
        String[] result = new String[2];
        int firstPN = message.indexOf("<ProjectName>") + 13;
        int lastPN =  message.lastIndexOf("<ProjectName>");
        String projectName = message.substring(firstPN, lastPN);
        int firstSD = message.indexOf("<Sender>") + 8;
        int lastSD =  message.lastIndexOf("<Sender>");
        String sender = message.substring(firstSD, lastSD);
        result[0] = projectName;
        result[1] = sender;
        return result;
    }
//    public static void main(String[] args) {
//        String message = "<zfgHsj6Uyk><AcceptInvitation><ProjectName>";
//        System.out.println(classifyMail(message));
//    }
}
