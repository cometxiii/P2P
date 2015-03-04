package com.comet_000.myapplication;

import com.j256.ormlite.field.DatabaseField;

import java.util.Date;

/**
 * Created by King on 1/21/2015.
 */
public class Note {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String subject;
    @DatabaseField
    String text;
    @DatabaseField
    Date date;

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }

    public Note(String subject, String text)
    {
        super();
        this.subject = subject;
        this.text = text;
        this.date = new Date(System.currentTimeMillis());
    }

    public String GetSubject()
    {
        return subject;
    }
    public Note()
    {

    }
}
