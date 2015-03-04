package com.comet_000.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.lang.ref.SoftReference;

/**
 * Created by comet_000 on 22/01/2015.
 */
public class MyDbHelper extends SQLiteOpenHelper{
    //Account table
    public static final String ACCOUNT_TABLE="Account";
    public static final String ACCOUNT_ID="_id";
    public static final String ACCOUNT_USER="Account";
    public static final String ACCOUNT_NAME="DisplayName";

    //Project table
    public static final String PROJECT_TABLE="PROJECT";
    public static final String PROJECT_ID="_id";
    public static final String PROJECT_NAME="ProjectName";
    public static final String PROJECT_DESCRIPTION="ProjectDescriptions";
    public static final String PROJECT_OWNER="Owner";

    //Task table
    public static final String TASK_TABLE="TASK";
    public static final String TASK_ID="_id";
    public static final String TASK_NAME="TaskName";
    public static final String TASK_DESCRIPTION="TaskDescriptions";
    public static final String TASK_PROJECT_TASK_NAME="ProjectName";
    public static final String TASK_PROJECT_MEMBER_TASK_MEMBER_NAME="MemberName";

    //Project-Member table
    public static final String PROJECT_MEMBER_TABLE="PROJECT_MEMBER";
    public static final String PROJECT_MEMBER_ID="_id";
    public static final String PROJECT_MEMBER_NAME="ProjectName";
    public static final String PROJECT_MEMBER_MEMBER_NAME="MemberName";

    //Old database
//    //Account table
//    public static final String ACCOUNT_TABLE="ACCOUNT";
//    public static final String ACCOUNT_USER="_id";
//    public static final String ACCOUNT_NAME="Name";
//
//    //Project table
//    public static final String PROJECT_TABLE="PROJECT";
//    public static final String PROJECT_NAME="_id";
//    public static final String PROJECT_DESCRIPTION="Descriptions";
//    public static final String PROJECT_OWNER="Owner";
//
//    //Task table
//    public static final String TASK_TABLE="TASK";
//    public static final String TASK_NAME="_id";
//    public static final String TASK_DESCRIPTION="Descriptions";
//    public static final String TASK_PROJECT_TASK_NAME="ProjectName";
//    public static final String TASK_PROJECT_MEMBER_TASK_MEMBER_NAME="Member";
//
//    //Project-Member table
//    public static final String PROJECT_MEMBER_TABLE="PROJECT_MEMBER";
//    public static final String PROJECT_MEMBER_NAME="_id";
//    public static final String PROJECT_MEMBER_MEMBER_NAME="Member";

    //Database information
    static final String DB_NAME="R&D.DB";
    static final int DB_VERSION=1;

    //Account table creation
    private static final String CREATE_ACCOUNT_TABLE="create table "+ACCOUNT_TABLE
            + "(" + ACCOUNT_ID + " integer primary key autoincrement, "
            + ACCOUNT_USER + " text unique, "
            + ACCOUNT_NAME + " text);";

    //Project table creation
    private static final String CREATE_PROJECT_TABLE="create table "+ PROJECT_TABLE
            + "(" + PROJECT_ID + " integer primary key autoincrement, "
            + PROJECT_NAME + " text, "
            + PROJECT_DESCRIPTION + " text, "
            + PROJECT_OWNER + " text);";

    //Task table creation
    private static final String CREATE_TASK_TABLE="create table " + TASK_TABLE
            + "(" + TASK_ID + " integer primary key autoincrement, "
            + TASK_NAME + " text, "
            + TASK_DESCRIPTION + " text, "
            + TASK_PROJECT_TASK_NAME + " text, "
            + TASK_PROJECT_MEMBER_TASK_MEMBER_NAME + " text);";

    //Project_Member table creation
    private static final String CREATE_PROJECT_MEMBER_TABLE="create table " + PROJECT_MEMBER_TABLE
            + "("+ PROJECT_MEMBER_ID + " integer primary key autoincrement, "
            + PROJECT_MEMBER_NAME + " text, "
            + PROJECT_MEMBER_MEMBER_NAME + " text);";

    public MyDbHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_ACCOUNT_TABLE);
        db.execSQL(CREATE_PROJECT_TABLE);
        db.execSQL(CREATE_TASK_TABLE);
        db.execSQL(CREATE_PROJECT_MEMBER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + ACCOUNT_TABLE);
        db.execSQL("drop table if exists " + PROJECT_TABLE);
        db.execSQL("drop table if exists " + CREATE_TASK_TABLE);
        db.execSQL("drop table if exists " + CREATE_PROJECT_MEMBER_TABLE);
        onCreate(db);
    }
}
