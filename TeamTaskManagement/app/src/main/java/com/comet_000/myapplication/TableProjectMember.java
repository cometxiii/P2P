package com.comet_000.myapplication;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by King on 04-03-2015.
 */
public class TableProjectMember {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String ProjectName;
    @DatabaseField
    String MemberName;


    public TableProjectMember(String projectName, String memberName) {
        ProjectName = projectName;
        MemberName = memberName;
    }

    public TableProjectMember(){}

    public String getMemberName() {
        return MemberName;
    }
}
