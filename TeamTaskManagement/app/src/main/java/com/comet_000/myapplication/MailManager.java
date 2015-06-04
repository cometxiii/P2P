package com.comet_000.myapplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 27-Apr-15.
 */
public class MailManager {
    final String keyTag = "<zfgHsj6Uyk>";
    static final String invitationTag = "<Invitation>";
    static final String acceptIviTag = "<AcceptInvitation>";
    static final String denyInviTag = "<DenyInvitation>";
    static final String assignTaskTag = "<AssignTask>";
    static final String priorityTag = "<Priority>";
    static final String acceptTaskTag = "<AcceptTask>";
    static final String changeStaTag = "<ChangeStatus>";
    static final String excludeTaskTag = "<ExcludeTask>";
    static final String changeDesTag = "<ChangeDes>";
    static final String denyTaskTag = "<DenyTask>";
    static final String excludeProTag = "<ExcludeProject>";

    final String projectTag = "<ProjectName>";
    final String pDesTag = "<ProjectDes>";
    final String ownerTag = "<ProjectOwner>";
    final String senderTag = "<Sender>";
    final String taskTag = "<TaskName>";
    final String taskDesTag = "<TaskDes>";
    final String statusTag = "<StatusTag>";
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
        return message.substring(0,firstID + 1);
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
    public String makeDenyInvitation(String projectName, String sender) {
        String message = addHeader(denyInviTag);
        message += addTag(projectName, projectTag);
        message += addTag(sender, senderTag);
        return message;
    }
    public String[] readDenyInvitation(String message) {
        String[] result = new String[2];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, senderTag);
        return result;
    }
    public String makeAssignment(String projectName, String projectOwner, String taskName, String taskDes, String priority) {
        String message = addHeader(assignTaskTag);
        message += addTag(projectName, projectTag);
        message += addTag(projectOwner, ownerTag);
        message += addTag(taskName, taskTag);
        message += addTag(taskDes, taskDesTag);
        message += addTag(priority, priorityTag);
        return message;
    }
    public String[] readAssignment(String message) {
        String[] result = new String[5];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, ownerTag);
        result[2] = readTag(message, taskTag);
        result[3] = readTag(message, taskDesTag);
        result[4] = readTag(message, priorityTag);
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
    public String makeChangeStatus(String projectName, String taskName, String sender, String status) {
        String message = addHeader(changeStaTag);
        message += addTag(projectName, projectTag);
        message += addTag(taskName, taskTag);
        message += addTag(sender, senderTag);
        message += addTag(status, statusTag);
        return message;
    }
    public String[] readChangeStatus(String message) {
        String[] result = new String[4];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, taskTag);
        result[2] = readTag(message, senderTag);
        result[3] = readTag(message, statusTag);
        return result;
    }
    public String makeChangeDes(String projectName, String taskName, String owner, String des) {
        String message = addHeader(changeDesTag);
        message += addTag(projectName, projectTag);
        message += addTag(taskName, taskTag);
        message += addTag(owner, ownerTag);
        message += addTag(des, taskDesTag);
        return message;
    }
    public String[] readChangeDes(String message) {
        String[] result = new String[4];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, taskTag);
        result[2] = readTag(message, ownerTag);
        result[3] = readTag(message, taskDesTag);
        return result;
    }
    public String makeExcludeTask(String projectName, String taskName, String owner) {
        String message = addHeader(excludeTaskTag);
        message += addTag(projectName, projectTag);
        message += addTag(taskName, taskTag);
        message += addTag(owner, ownerTag);
        return message;
    }
    public String[] readExcludeTask(String message) {
        String[] result = new String[3];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, taskTag);
        result[2] = readTag(message, ownerTag);
        return result;
    }

    public String makeDenyTask(String projectName, String taskName, String sender){
        String message=addHeader(denyTaskTag);
        message+=addTag(projectName, projectTag);
        message+=addTag(taskName, taskTag);
        message+=addTag(sender, senderTag);
        return message;
    }

    public String[] readDenyTask(String message){
        String[] result=new String[3];
        result[0]=readTag(message, projectTag);
        result[1]=readTag(message, taskTag);
        result[2]=readTag(message, senderTag);
        return result;
    }

    public String makeExcludeProject(String projectName, String owner) {
        String message = addHeader(excludeProTag);
        message += addTag(projectName, projectTag);
        message += addTag(owner, ownerTag);
        return message;
    }

    public String[] readExcludeProject(String message) {
        String[] result = new String[2];
        result[0] = readTag(message, projectTag);
        result[1] = readTag(message, ownerTag);
        return result;
    }
}
