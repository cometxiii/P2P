package com.comet_000.myapplication;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
import com.j256.ormlite.field.DatabaseField;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by King on 1/21/2015.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil{
    private static final Class<?>[] classes = new Class[]{Note.class, TableAccount.class, TableProject.class, TableTask.class, TableProjectMember.class};

    public static void main(String[] args) throws IOException, SQLException
    {
        writeConfigFile(new File(".\\res\\raw\\ormlite_config.txt"),classes);
    }
}
