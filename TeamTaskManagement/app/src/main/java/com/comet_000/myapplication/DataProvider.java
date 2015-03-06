package com.comet_000.myapplication;


import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2/1/2015.
 */
public class DataProvider {
    public RuntimeExceptionDao<Note, Integer> myNoteDao = null;
    public RuntimeExceptionDao<TableAccount, Integer> myAccountTable = null;
    public RuntimeExceptionDao<TableProject, Integer> myProjectTable = null;
    public RuntimeExceptionDao<TableTask, Integer> myTaskTable = null;
    public RuntimeExceptionDao<TableProjectMember, Integer> myProjectMemberTable = null;

    public DataProvider() {
    }

    public void SetNoteDao(RuntimeExceptionDao<Note, Integer> noteDao) {
        myNoteDao = noteDao;
    }

    public void AddNote(Note myNote) {
        myNoteDao.create(myNote);
    }

    public List<Note> GetAllNote() {
        return myNoteDao.queryForAll();
    }

    public List<String> GetAllNoteString() {
        List<Note> listNote = this.GetAllNote();
        List<String> listString = new ArrayList<String>();
        for (Note n : listNote) listString.add(n.toString());
        return listString;
    }

    public void UpdateNoteById(Integer id, String fieldName, String arg) {
        UpdateBuilder<Note, Integer> updateBuilder = myNoteDao.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue(fieldName, arg);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Note GetNoteById(int id) {
        return myNoteDao.queryForId(id);
    }

    public List<Note> GetNodeByFieldName(String fieldName, String arg) {
        return myNoteDao.queryForEq(fieldName, arg);
    }

    public void DeleteNoteById(int id) {
        myNoteDao.deleteById(id);
    }

    public void DeleteNoteByFieldName(String fieldName, String arg) {
        DeleteBuilder<Note, Integer> deleteBuilder = myNoteDao.deleteBuilder();
        try {
            deleteBuilder.where().eq(fieldName, arg);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int GetNumOfNote() {
        return (int) myNoteDao.countOf();
    }

    /////////////////
    /////////////////
    /////Account/////
    /////////////////
    /////////////////
    public void setTableAccount(RuntimeExceptionDao<TableAccount, Integer> accountDao) {
        myAccountTable = accountDao;
    }

    public void addAccount(TableAccount myAccount) {
        myAccountTable.create(myAccount);
    }

    public List<TableAccount> getAllAccount() {
        return myAccountTable.queryForAll();
    }

    public List<String> getAllAccountString() {
        List<TableAccount> listAccount = this.getAllAccount();
        List<String> listString = new ArrayList<String>();
        for (TableAccount n : listAccount) listString.add(n.toString());
        return listString;
    }

    public void updateAccountById(Integer id, String fieldName, String arg) {
        UpdateBuilder<TableAccount, Integer> updateBuilder = myAccountTable.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue(fieldName, arg);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TableAccount getAccountById(int id) {
        return myAccountTable.queryForId(id);
    }

    public List<TableAccount> getAccountByFieldName(String fieldName, String arg) {
        return myAccountTable.queryForEq(fieldName, arg);
    }

    public void deleteAccountById(int id) {
        myAccountTable.deleteById(id);
    }

    public void deleteAccountByFieldName(String fieldName, String arg) {
        DeleteBuilder<TableAccount, Integer> deleteBuilder = myAccountTable.deleteBuilder();
        try {
            deleteBuilder.where().eq(fieldName, arg);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfAccount() {
        return (int) myAccountTable.countOf();
    }

    /////////////////
    /////////////////
    /////Project/////
    /////////////////
    /////////////////

    public void setTableProject(RuntimeExceptionDao<TableProject, Integer> ProjectDao) {
        myProjectTable = ProjectDao;
    }

    public void addProject(TableProject myProject) {
        myProjectTable.create(myProject);
    }

    public List<TableProject> getAllProject() {
        return myProjectTable.queryForAll();
    }

    public List<String> getAllProjectString() {
        List<TableProject> listProject = this.getAllProject();
        List<String> listString = new ArrayList<String>();
        for (TableProject n : listProject) listString.add(n.getProjectName());
        return listString;
    }

    public void updateProjectById(Integer id, String fieldName, String arg) {
        UpdateBuilder<TableProject, Integer> updateBuilder = myProjectTable.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue(fieldName, arg);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TableProject getProjectById(int id) {
        return myProjectTable.queryForId(id);
    }

    public List<TableProject> getProjectByFieldName(String fieldName, String arg) {
        return myProjectTable.queryForEq(fieldName, arg);
    }

    public void deleteProjectById(int id) {
        myProjectTable.deleteById(id);
    }

    public void deleteProjectByFieldName(String fieldName, String arg) {
        DeleteBuilder<TableProject, Integer> deleteBuilder = myProjectTable.deleteBuilder();
        try {
            deleteBuilder.where().eq(fieldName, arg);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfProject() {
        return (int) myProjectTable.countOf();
    }

    public boolean checkProjectByFieldName(String fieldName, String arg) {
        List<TableProject> myList = this.getProjectByFieldName(fieldName, arg);
        if (myList.isEmpty())
            return false;
        else
            return true;
    }


/////////////////
/////////////////
///////Task//////
/////////////////
/////////////////


    public void setTableTask(RuntimeExceptionDao<TableTask, Integer> TaskDao) {
        myTaskTable = TaskDao;
    }

    public void addTask(TableTask myTask) {
        myTaskTable.create(myTask);
    }

    public List<TableTask> getAllTask() {
        return myTaskTable.queryForAll();
    }

    public List<String> getAllTaskString() {
        List<TableTask> listTask = this.getAllTask();
        List<String> listString = new ArrayList<String>();
        for (TableTask n : listTask) listString.add(n.getTaskName());
        return listString;
    }

    public void updateTaskById(Integer id, String fieldName, String arg) {
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue(fieldName, arg);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TableTask getTaskById(int id) {
        return myTaskTable.queryForId(id);
    }

    public List<TableTask> getTaskByFieldName(String fieldName, String arg) {
        return myTaskTable.queryForEq(fieldName, arg);
    }

    public void deleteTaskById(int id) {
        myTaskTable.deleteById(id);
    }

    public void deleteTaskByFieldName(String fieldName, String arg) {
        DeleteBuilder<TableTask, Integer> deleteBuilder = myTaskTable.deleteBuilder();
        try {
            deleteBuilder.where().eq(fieldName, arg);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfTask() {
        return (int) myTaskTable.countOf();
    }

    public boolean checkTaskByFieldName(String fieldName, String arg) {
        List<TableTask> myList = this.getTaskByFieldName(fieldName, arg);
        if (myList.isEmpty())
            return false;
        return true;
    }


/////////////////
/////////////////
//ProjectMember//
/////////////////
/////////////////


    public void setTableProjectMember(RuntimeExceptionDao<TableProjectMember, Integer> ProjectMemberDao) {
        myProjectMemberTable = ProjectMemberDao;
    }

    public void addProjectMember(TableProjectMember myProjectMember) {
        myProjectMemberTable.create(myProjectMember);
    }

    public List<TableProjectMember> getAllProjectMember() {
        return myProjectMemberTable.queryForAll();
    }

    public void updateProjectMemberById(Integer id, String fieldName, String arg) {
        UpdateBuilder<TableProjectMember, Integer> updateBuilder = myProjectMemberTable.updateBuilder();
        try {
            updateBuilder.where().eq("id", id);
            updateBuilder.updateColumnValue(fieldName, arg);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TableProjectMember getProjectMemberById(int id) {
        return myProjectMemberTable.queryForId(id);
    }

    public List<TableProjectMember> getProjectMemberByFieldName(String fieldName, String arg) {
        return myProjectMemberTable.queryForEq(fieldName, arg);
    }

    public List<String> getProjectMemberByFieldNameString(String fieldName, String arg) {
        List<TableProjectMember> projectMemberList = getProjectMemberByFieldName(fieldName, arg);
        List<String> listString = new ArrayList<String>();
        for (TableProjectMember n : projectMemberList) listString.add(n.getMemberName());
        return listString;
    }

    public void deleteProjectMemberById(int id) {
        myProjectMemberTable.deleteById(id);
    }

    public void deleteProjectMemberByFieldName(String fieldName, String arg) {
        DeleteBuilder<TableProjectMember, Integer> deleteBuilder = myProjectMemberTable.deleteBuilder();
        try {
            deleteBuilder.where().eq(fieldName, arg);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getNumOfProjectMember() {
        return (int) myProjectMemberTable.countOf();
    }

    public boolean checkProjectMemberByFieldName(String fieldName, String arg) {
        List<TableProjectMember> myList = this.getProjectMemberByFieldName(fieldName, arg);
        if (myList.isEmpty())
            return false;
        return true;
    }
}