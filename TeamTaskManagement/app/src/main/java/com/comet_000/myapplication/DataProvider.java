package com.comet_000.myapplication;


import android.accounts.Account;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by King on 2/1/2015.
 */
public class DataProvider {
    public RuntimeExceptionDao<TableAccount, Integer> myAccountTable = null;
    public RuntimeExceptionDao<TableProject, Integer> myProjectTable = null;
    public RuntimeExceptionDao<TableTask, Integer> myTaskTable = null;
    public RuntimeExceptionDao<TableProjectMember, Integer> myProjectMemberTable = null;


    public DataProvider() {
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

    public void updatePass(String pass) {
        UpdateBuilder<TableAccount, Integer> updateBuilder = myAccountTable.updateBuilder();
        try {
            updateBuilder.where().eq("id", 1
            );
            updateBuilder.updateColumnValue("Password", pass);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public TableAccount getAccountById(int id) {
        return myAccountTable.queryForId(id);
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

    public TableProject getProject(String projectName, String owner)
    {
        QueryBuilder<TableProject, Integer> queryBuilder =  myProjectTable.queryBuilder();
        List<TableProject> projectList = null;
        try {
            projectList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return projectList.get(0);
    }
    public List<String> getAllProjectString() {
        List<TableProject> listProject = this.getAllProject();
        List<String> listString = new ArrayList<String>();
        for (TableProject n : listProject) listString.add(n.getProjectName() + " - " + n.getOwner());
        return listString;
    }

    public List<String> getAllProjectStringDelete(String owner) {
        QueryBuilder<TableProject, Integer> queryBuilder =  myProjectTable.queryBuilder();
        List<TableProject> projectList = null;
        List<TableProjectMember> memberList = null;
        try {
            projectList = queryBuilder.where()
                    .eq("Owner", owner).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableProject n : projectList) {
            QueryBuilder<TableProjectMember, Integer> queryBuilder1 = myProjectMemberTable.queryBuilder();
            try {
                memberList = queryBuilder1.where()
                        .eq("ProjectName", n.getProjectName())
                        .and()
                        .eq("Owner", owner)
                        .query();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (memberList.size() == 1)
                listString.add(n.getProjectName());
        }
        return listString;
    }

    public void deleteProject(String projectName, String owner) {
        DeleteBuilder<TableProject, Integer> deleteBuilder = myProjectTable.deleteBuilder();
        DeleteBuilder<TableTask, Integer> deleteBuilderTask = myTaskTable.deleteBuilder();
        DeleteBuilder<TableProjectMember, Integer> deleteBuilderMember = myProjectMemberTable.deleteBuilder();
        try {
            deleteBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            deleteBuilder.delete();
            deleteBuilderTask.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            deleteBuilderTask.delete();
            deleteBuilderMember.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            deleteBuilderMember.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkProject(String name, String owner) {
        QueryBuilder<TableProject, Integer> queryBuilder =  myProjectTable.queryBuilder();
        List<TableProject> projectList = null;
        try {
            projectList = queryBuilder.where()
                    .eq("ProjectName", name)
                    .and()
                    .eq("Owner", owner).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (projectList.isEmpty())
            return true;
        else
            return false;
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

    public TableTask get1Task(String taskName, String projectName, String owner){
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return taskList.get(0);
    }

    public List<String> getTaskProject(String projectName, String owner) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableTask n : taskList) listString.add(n.getTaskName());
        return listString;
    }

    public List<String> getTaskProjectMem(String projectName, String owner) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableTask n : taskList) {
            if (!n.MemberName.equals("")) {
                listString.add(n.getTaskName() + " - " + n.MemberName);
            } else {
                listString.add(n.getTaskName());
            }
        }
        return listString;
    }

    public String[] getTaskString(String projectName, String owner) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableTask n : taskList) listString.add(n.getTaskName());
        String[] arrayString = new String[listString.size()];
        for (int i=0; i<listString.size(); i++) arrayString[i] = listString.get(i);
        return arrayString;
    }

    public void deleteTask(String projectName, String taskName, String owner) {
        DeleteBuilder<TableTask, Integer> deleteBuilder = myTaskTable.deleteBuilder();
        try {
            deleteBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkTaskMember(String taskName, String projectName, String owner, String member) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .eq("MemberName", member)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (taskList.isEmpty())
            return true;
        return false;
    }
    public boolean checkTaskAssignment(String taskName, String projectName, String owner, String member, String status) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .eq("MemberName", member)
                    .and()
                    .eq("Status", status)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (taskList.isEmpty())
            return true;
        return false;
    }
    public boolean checkTask(String taskName, String projectName, String owner) {
        QueryBuilder<TableTask, Integer> queryBuilder =  myTaskTable.queryBuilder();
        List<TableTask> taskList = null;
        try {
            taskList = queryBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (taskList.isEmpty())
            return true;
        return false;
    }

    public void updateTask(String projectName, String taskName, String des, String member, String status){
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName);
            updateBuilder.updateColumnValue("MemberName", member);
            updateBuilder.updateColumnValue("TaskDescriptions", des);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskMemSta(String projectName, String taskName, String member, String status){
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName);
            updateBuilder.updateColumnValue("MemberName", member);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskStatus(String projectName, String taskName, String owner, String status) {
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskDeny(String projectName, String taskName, String owner, String status, String member) {
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .eq("MemberName", member);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.updateColumnValue("MemberName", "");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskDes(String projectName, String taskName, String owner, String des) {
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner);
            updateBuilder.updateColumnValue("TaskDescriptions", des);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateTaskAssignment(String projectName, String taskName, String member, String status){
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("TaskName", taskName)
                    .and()
                    .eq("ProjectName", projectName);
            updateBuilder.updateColumnValue("MemberName", member);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public void updateProjectMember(String projectName, String memberName, String owner, String status) {
        UpdateBuilder<TableProjectMember, Integer> updateBuilder = myProjectMemberTable.updateBuilder();
        try {
            updateBuilder.where().eq("ProjectName", projectName)
                    .and()
                    .eq("MemberName", memberName)
                    .and()
                    .eq("Owner", owner);
            updateBuilder.updateColumnValue("Status", status);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getProjectMember(String projectName, String owner) {
        QueryBuilder<TableProjectMember, Integer> queryBuilder =  myProjectMemberTable.queryBuilder();
        List<TableProjectMember> memberList = null;
        try {
            memberList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .eq("Status", "Accepted")
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableProjectMember n : memberList) listString.add(n.getMemberName());
        return listString;
    }

    public List<String> getAllProjectMember(String projectName, String owner) {
        QueryBuilder<TableProjectMember, Integer> queryBuilder =  myProjectMemberTable.queryBuilder();
        List<TableProjectMember> memberList = null;
        try {
            memberList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listString = new ArrayList<String>();
        for (TableProjectMember n : memberList) listString.add(n.getMemberName() + " - " + n.Status);
        return listString;
    }

    public boolean checkProjectMember(String projectName, String memberName, String owner) {
        QueryBuilder<TableProjectMember, Integer> queryBuilder =  myProjectMemberTable.queryBuilder();
        List<TableProjectMember> memberList = null;
        try {
            memberList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .eq("MemberName", memberName)
                    .and()
                    .eq("Status", "Waiting")
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (memberList.isEmpty())
            return false;
        return true;
    }

    public void deleteProjectMember(String projectName, String memberName, String owner) {
        DeleteBuilder<TableProjectMember, Integer> deleteBuilder = myProjectMemberTable.deleteBuilder();
        try {
            deleteBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("MemberName", memberName)
                    .and()
                    .eq("Owner", owner);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String[] getAllProjectMember1(String projectName, String owner) {
        QueryBuilder<TableProjectMember, Integer> queryBuilder =  myProjectMemberTable.queryBuilder();
        List<TableProjectMember> memberList = null;
        try {
            memberList = queryBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("Owner", owner)
                    .and()
                    .not().eq("MemberName", owner)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String[] arrayString = new String[memberList.size()];
        for (int i=0; i<memberList.size(); i++) arrayString[i] = memberList.get(i).getMemberName();
        return arrayString;
    }

    public void updateDeleteMember(String projectName, String owner, String memberName) {
        UpdateBuilder<TableTask, Integer> updateBuilder = myTaskTable.updateBuilder();
        try {
            updateBuilder.where()
                    .eq("ProjectName", projectName)
                    .and()
                    .eq("MemberName", memberName)
                    .and()
                    .eq("Owner", owner);
            updateBuilder.updateColumnValue("Status", "New");
            updateBuilder.updateColumnValue("MemberName", "");
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}