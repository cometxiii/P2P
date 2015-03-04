package com.comet_000.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.SQLException;

/**
 * Created by comet_000 on 22/01/2015.
 */
public class SQLController {
    private MyDbHelper dbHelper;
    private Context ourContext;
    public SQLiteDatabase database;

    public SQLController(Context c){
        ourContext=c;
    }

    public SQLController open() throws SQLException{
        dbHelper=new MyDbHelper(ourContext);
        database=dbHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbHelper.close();
    }

    //Add new account
    public void insertAccountData(String user, String name){
        ContentValues cv=new ContentValues();
        cv.put(MyDbHelper.ACCOUNT_USER, user);
        cv.put(MyDbHelper.ACCOUNT_NAME, name);
        database.insert(MyDbHelper.ACCOUNT_TABLE, null, cv);
    }

    //Load account to UI
    public Cursor readAccountEntry(){
        String[] allColumns=new String[]{MyDbHelper.ACCOUNT_USER,MyDbHelper.ACCOUNT_NAME};
        Cursor c=database.query(MyDbHelper.ACCOUNT_TABLE, allColumns, null,null, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Check if account is existing or not
    public String[] checkAccount(SQLiteDatabase db){
        Cursor c =  database.query(MyDbHelper.ACCOUNT_TABLE,new String[]{MyDbHelper.ACCOUNT_USER}, null,null, null, null, null);
        if(c!=null)
            c.moveToFirst();
        else
            return null;
        int rows=c.getCount();
        String[] re = new String[rows];
        c.moveToFirst();
        for(int i=0; i<rows; i++){
                re[i] = c.getString(0);
            c.moveToNext();
        }
        return re;
    }

    //Add new project
    public void insertProjectData(String name, String description, String owner){
        ContentValues cv=new ContentValues();
        cv.put(MyDbHelper.PROJECT_NAME, name);
        cv.put(MyDbHelper.PROJECT_DESCRIPTION, description);
        cv.put(MyDbHelper.PROJECT_OWNER, owner);
        database.insert(MyDbHelper.PROJECT_TABLE, null, cv);
    }

    //Load Project to UI
    public Cursor readProjectEntry(){
        String[] allColumns=new String[]{MyDbHelper.PROJECT_NAME,MyDbHelper.PROJECT_DESCRIPTION,MyDbHelper.PROJECT_OWNER};
        Cursor c=database.query(MyDbHelper.PROJECT_TABLE, allColumns, null,null, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Load information from selected project
    public Cursor readSelectedProject(String pName){
        String[] allColumns=new String[]{MyDbHelper.PROJECT_NAME,MyDbHelper.PROJECT_DESCRIPTION,MyDbHelper.PROJECT_OWNER};
        Cursor c=database.query(MyDbHelper.PROJECT_TABLE, allColumns, MyDbHelper.PROJECT_NAME+"=?", new String[]{pName}, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Add new task to a project
    public void insertTaskData(String name, String description, String project, String member){
        ContentValues cv=new ContentValues();
        cv.put(MyDbHelper.TASK_NAME, name);
        cv.put(MyDbHelper.TASK_DESCRIPTION, description);
        cv.put(MyDbHelper.TASK_PROJECT_TASK_NAME, project);
        cv.put(MyDbHelper.TASK_PROJECT_MEMBER_TASK_MEMBER_NAME, member);
        database.insert(MyDbHelper.TASK_TABLE, null, cv);
    }

    //Load tasks of a project to UI
    public Cursor readTaskEntry(String pName){
        String[] allColumns=new String[]{MyDbHelper.TASK_NAME,MyDbHelper.TASK_DESCRIPTION,MyDbHelper.TASK_PROJECT_TASK_NAME,MyDbHelper.TASK_PROJECT_MEMBER_TASK_MEMBER_NAME};
        Cursor c=database.query(MyDbHelper.TASK_TABLE, allColumns,MyDbHelper.TASK_PROJECT_TASK_NAME+"=?", new String[]{pName}, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Load members of a project to UI
    public Cursor readMemberEntry(String pName){
        String[] allColumns=new String[]{MyDbHelper.PROJECT_MEMBER_NAME,MyDbHelper.PROJECT_MEMBER_MEMBER_NAME};
        Cursor c=database.query(MyDbHelper.PROJECT_MEMBER_TABLE, allColumns, MyDbHelper.PROJECT_MEMBER_NAME+"=?", new String[]{pName}, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Add new member to SQLite
    public void insertMemberData(String pName, String member){
        ContentValues cv=new ContentValues();
        cv.put(MyDbHelper.PROJECT_MEMBER_NAME, pName);
        cv.put(MyDbHelper.PROJECT_MEMBER_MEMBER_NAME, member);
        database.insert(MyDbHelper.PROJECT_MEMBER_TABLE, null, cv);
    }

    //Check if project exist or not
    public boolean checkProjectEntry(String pName, String owner){
        String[] allColumns=new String[]{MyDbHelper.PROJECT_NAME,MyDbHelper.PROJECT_OWNER};
        Cursor cAll = database.query(MyDbHelper.PROJECT_TABLE, allColumns, null, null, null, null, null);
        if(cAll!=null){
            cAll.moveToFirst();
        }
        for (int i = 0; i<=cAll.getCount()-1;i++)
        {
            String prjNameLowerCase = cAll.getString(cAll.getColumnIndex(MyDbHelper.PROJECT_NAME));
            String prjOwnerLowerCase = cAll.getString(cAll.getColumnIndex(MyDbHelper.PROJECT_OWNER));
            if (Extra.checkDuplicatedStrings(pName,prjNameLowerCase) && Extra.checkDuplicatedStrings(owner,prjOwnerLowerCase))
                return true;
            cAll.moveToNext();
        }
        return false;
    }

    //Check if task in project exist or not
    public boolean checkTaskEntry(String task, String pName){
        String[] allColumns=new String[]{MyDbHelper.TASK_NAME,MyDbHelper.TASK_PROJECT_TASK_NAME};
        Cursor cAll = database.query(MyDbHelper.TASK_TABLE, allColumns, null, null, null, null, null);
        if(cAll!=null){
            cAll.moveToFirst();
        }
        for (int i = 0; i<=cAll.getCount()-1;i++)
        {
            if (Extra.checkDuplicatedStrings(task,cAll.getString(cAll.getColumnIndex(MyDbHelper.TASK_NAME))) &&
                    Extra.checkDuplicatedStrings(pName,cAll.getString(cAll.getColumnIndex(MyDbHelper.TASK_PROJECT_TASK_NAME))))
                return true;
            cAll.moveToNext();
        }
        return false;
    }

    //Check if member in project existing or not
//    public boolean checkMemberEntry(String pName, String member){
//        String[] allColumns=new String[]{MyDbHelper.PROJECT_MEMBER_NAME,MyDbHelper.PROJECT_MEMBER_MEMBER_NAME};
//        Cursor cAll = database.query(MyDbHelper.PROJECT_MEMBER_TABLE, allColumns, null, null, null, null, null);
//        if(cAll!=null){
//            cAll.moveToFirst();
//        }
//        for (int i = 0; i<=cAll.getCount()-1;i++)
//        {
//            if (Extra.checkDuplicatedStrings(pName,cAll.getString(cAll.getColumnIndex(MyDbHelper.PROJECT_MEMBER_NAME))) &&
//                    Extra.checkDuplicatedStrings(member,cAll.getString(cAll.getColumnIndex(MyDbHelper.PROJECT_MEMBER_MEMBER_NAME))))
//                return true;
//            cAll.moveToNext();
//        }
//        return false;
//    }

    //Check if member in project existing or not
    public Cursor checkMemberEntry(String pName, String member){
        String[] allColumns=new String[]{MyDbHelper.PROJECT_MEMBER_NAME,MyDbHelper.PROJECT_MEMBER_MEMBER_NAME};
        Cursor c=database.query(MyDbHelper.PROJECT_MEMBER_TABLE, allColumns, MyDbHelper.PROJECT_MEMBER_NAME+ "=?" + " and " + MyDbHelper.PROJECT_MEMBER_MEMBER_NAME + "=?", new String[]{pName, member}, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Load information of selected task to UI
    public Cursor readSelectedTaskEntry(String tName, String pName){
        String[] allColumns=new String[]{MyDbHelper.TASK_NAME,MyDbHelper.TASK_DESCRIPTION,MyDbHelper.TASK_PROJECT_TASK_NAME,MyDbHelper.TASK_PROJECT_MEMBER_TASK_MEMBER_NAME};
        Cursor c=database.query(MyDbHelper.TASK_TABLE, allColumns,MyDbHelper.TASK_NAME+"=?" + " and " + MyDbHelper.TASK_PROJECT_TASK_NAME + "=?", new String[]{tName,pName}, null, null, null);
        if(c!=null){
            c.moveToFirst();
        }
        return c;
    }

    //Update task information
    public void updateTaskData(String name, String description, String project, String member){
        ContentValues cv=new ContentValues();
        cv.put(MyDbHelper.TASK_DESCRIPTION, description);
        cv.put(MyDbHelper.TASK_PROJECT_MEMBER_TASK_MEMBER_NAME, member);
        database.update(MyDbHelper.TASK_TABLE, cv, MyDbHelper.TASK_NAME + "=?" + " and " + MyDbHelper.TASK_PROJECT_TASK_NAME + "=?", new String[]{name, project});
    }
}
