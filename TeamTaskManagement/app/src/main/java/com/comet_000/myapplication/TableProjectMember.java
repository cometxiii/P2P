package com.comet_000.myapplication;

import android.provider.Browser;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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
    @DatabaseField
    String Owner;



    public TableProjectMember(String projectName, String owner, String memberName) {
        ProjectName = projectName;
        MemberName = memberName;
        Owner = owner;
    }

    public TableProjectMember(){}

    public String getMemberName() {
        return MemberName;
    }
}
