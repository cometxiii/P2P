package com.comet_000.myapplication;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by comet_000 on 06/02/2015.
 */
public class TableAccount {
    @DatabaseField (generatedId = true)
    int id;
    @DatabaseField
    String Account;
    @DatabaseField
    String Password;

    @Override
    public String toString() {return Account;}
    public TableAccount(String user, String pass) {
        Account = user;
        Password = pass;
    }
    public TableAccount(){}
}
