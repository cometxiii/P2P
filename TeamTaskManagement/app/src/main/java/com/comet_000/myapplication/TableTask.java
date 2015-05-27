package com.comet_000.myapplication;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by King on 04-03-2015.
 */
public class TableTask {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String TaskName;
    @DatabaseField
    String TaskDescriptions;
    @DatabaseField
    String ProjectName;
    @DatabaseField
    String MemberName;
    @DatabaseField
    String Owner;
    @DatabaseField
    String Status;
    @DatabaseField
    String Priority;

    public TableTask(String projectName , String owner, String taskName, String taskDescriptions, String memberName, String status, String priority) {
        TaskName = taskName;
        Owner = owner;
        MemberName = memberName;
        TaskDescriptions = taskDescriptions;
        ProjectName = projectName;
        Status = status;
        Priority = priority;
    }

    public TableTask(){}

    public String getTaskName() {
        return TaskName;
    }

    public String getTaskDescriptions() {
        return TaskDescriptions;
    }
}
