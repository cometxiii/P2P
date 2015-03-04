package com.comet_000.myapplication;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by King on 2/28/2015.
 */
public class TableProject {
    @DatabaseField (generatedId = true)
    int id;
    @DatabaseField
    String ProjectName;
    @DatabaseField
    String ProjectDescriptions;
    @DatabaseField
    String Owner;
    public TableProject(){}
    public TableProject(String projectName, String projectDescriptions, String owner) {
        ProjectName = projectName;
        ProjectDescriptions = projectDescriptions;
        Owner = owner;
    }

    public String getProjectName() {
        return ProjectName;
    }
}

