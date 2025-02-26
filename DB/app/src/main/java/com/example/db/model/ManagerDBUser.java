package com.example.db.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.NoCopySpan;

import androidx.annotation.Nullable;

public class ManagerDBUser extends SQLiteOpenHelper {

    private static final String DATA_BASE="dbUsers";
    private static final int VERSION = 1;
    private static final String TABLE_USERS="users";
    private static final String QUERY_TABLE_USERS ="CREATE TABLE "+TABLE_USERS+"(use_document INTEGER PRIMARY KEY, use_names varchar(150) NOT NULL, use_last_names varchar(150) NOT NULL, use_user varchar(100) NOT NULL, use_password varchar(25) NOT NULL, use_status INTEGER (1));";


    public ManagerDBUser(@Nullable Context context) {
        super(context, DATA_BASE, null , VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dataBase) {
        dataBase.execSQL(QUERY_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase dataBase, int oldVersion, int newVersion) {
        final String DOWN_USER="DROP TABLE IF EXISTS" + TABLE_USERS;
        dataBase.execSQL(DOWN_USER);
        onCreate(dataBase);
    }
}
