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

    public TableTask(String taskName, String memberName, String taskDescriptions, String projectName) {
        TaskName = taskName;
        MemberName = memberName;
        TaskDescriptions = taskDescriptions;
        ProjectName = projectName;
    }

    public TableTask(){}

    public String getTaskName() {
        return TaskName;
    }
}
