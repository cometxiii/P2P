package com.comet_000.myapplication;
import java.sql.SQLException;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
/**
 * Created by King on 1/18/2015.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "TeamTaskManagement.db";
    private static final Integer DATABASE_VERSION = 1;


    private Dao<Note, Integer> noteDao = null;
    private RuntimeExceptionDao<Note, Integer> noteRuntimeDao = null;

    private RuntimeExceptionDao<TableAccount, Integer> accountRuntimeDao = null;
    private RuntimeExceptionDao<TableProject, Integer> projectRuntimeDao= null;
    private RuntimeExceptionDao<TableTask, Integer> taskRuntimeDao= null;
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION,R.raw.ormlite_config);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Note.class);
            TableUtils.createTable(connectionSource, TableAccount.class);
            TableUtils.createTable(connectionSource, TableProject.class);
            TableUtils.createTable(connectionSource, TableTask.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i2) {
        try {
            TableUtils.dropTable(connectionSource, Note.class, true);
            TableUtils.dropTable(connectionSource, TableAccount.class, true);
            TableUtils.dropTable(connectionSource, TableProject.class, true);
            TableUtils.dropTable(connectionSource, TableTask.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<Note, Integer> getNoteDao() throws SQLException {
        if (noteDao == null) {
            noteDao = getDao(Note.class);
        }
        return noteDao;
    }

    public RuntimeExceptionDao<Note, Integer> getNoteRuntimeExceptionDao()
    {
        if (noteRuntimeDao == null)
        {
            noteRuntimeDao = getRuntimeExceptionDao(Note.class);
        }
        return  noteRuntimeDao;
    }

    public RuntimeExceptionDao<TableAccount, Integer> getTableAccount()
    {
        if (accountRuntimeDao == null)
        {
            accountRuntimeDao = getRuntimeExceptionDao(TableAccount.class);
        }
        return  accountRuntimeDao;
    }

    public RuntimeExceptionDao<TableProject, Integer> getTableProject()
    {
        if (projectRuntimeDao == null)
        {
            projectRuntimeDao = getRuntimeExceptionDao(TableProject.class);
        }
        return  projectRuntimeDao;
    }

    public RuntimeExceptionDao<TableTask, Integer> getTableTask()
    {
        if (taskRuntimeDao == null)
        {
            taskRuntimeDao = getRuntimeExceptionDao(TableTask.class);
        }
        return  taskRuntimeDao;
    }
}