package com.comet_000.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 27-Apr-15.
 */
public class MailManager {
    final String keyTag = "<zfgHsj6Uyk>";
    final String invitationTag = "<Invitation>";
    final String acceptIviTag = "<AcceptInvitation>";
    final String assignTaskTag = "<AssignTask>";
    final String acceptTaskTag = "<AcceptTask>";
    final String projectTag = "<ProjectName>";
    final String pDesTag = "<ProjectDes>";
    final String ownerTag = "<ProjectOwner>";
    final String senderTag = "<Sender>";
    final String taskTag = "TaskName>";
    final String taskDesTag = "<TaskDes>";
    MailManager() {
    }
    private String addHeader(String tag) {
        return keyTag + tag;
    }
    private String addTag(String word, String tag) {
        return tag + word + tag;
    }
    private String readTag(String message, String tag) {
        int firstIndex = message.indexOf(tag) + tag.length();
        int lastIndex = message.lastIndexOf(tag);
        return message.substring(firstIndex, lastIndex);
    }
    public String classifyMail(String message) {
        message = message.replace(keyTag,"");
        int firstID = message.indexOf(">");
        return message.substring(1,firstID);
    }
    public String makeInvitation(String projectName, String projectDes, String projectOwner) {
        String message = addHeader(invitationTag);
        message += addTag(projectName, projectTag);
        message += addTag(projectDes, pDesTag);
        message += addTag(projectOwner, ownerTag);
        return message;
    }
    public String[] readInvitation(String message) {
        String[] result = new String[3];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, pDesTag);
        result[2] = readTag(message, ownerTag);
        return result;
    }
    public String makeAcceptInvitation(String projectName, String sender) {
        String message = addHeader(acceptIviTag);
        message += addTag(projectName, projectTag);
        message += addTag(sender, senderTag);
        return message;
    }
    public String[] readAcceptInvitation(String message) {
        String[] result = new String[2];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, senderTag);
        return result;
    }
    public String makeAssignment(String projectName, String projectOwner, String taskName, String taskDes) {
        String message = addHeader(assignTaskTag);
        message += addTag(projectName, projectTag);
        message += addTag(projectOwner, ownerTag);
        message += addTag(taskName, taskTag);
        message += addTag(taskDes, taskDesTag);
        return message;
    }
    public String[] readAssignment(String message) {
        String[] result = new String[4];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, ownerTag);
        result[2] = readTag(message, taskTag);
        result[3] = readTag(message, taskDesTag);
        return result;
    }
    public String makeAccetpTask(String projectName, String taskName, String sender) {
        String message = addHeader(acceptTaskTag);
        message += addTag(projectName, projectTag);
        message += addTag(taskName, taskTag);
        message += addTag(sender, senderTag);
        return message;
    }
    public String[] readAcceptTask(String message) {
        String[] result = new String[3];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, taskTag);
        result[2] = readTag(message, senderTag);
        return  result;
    }
//    public static void main(String[] args) {
//        String message = "<zfgHsj6Uyk><AcceptInvitation><ProjectName>";
//        System.out.println(classifyMail(message));
//    }
}
