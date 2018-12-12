package com.diary.own1ch.diary.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class Database extends SQLiteOpenHelper {
    public Database(Context context) {
        // конструктор суперкласса
        super(context, "diary", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table diary(" +
                "id integer primary key autoincrement," +
                "theme text," +
                "description text," +
                "done integer," +
                "date date" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
