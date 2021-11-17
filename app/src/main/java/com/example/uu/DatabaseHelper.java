package com.example.uu;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "RunningRecord";
    public static final String TABLE_NAME = "record";
    //columns
    public static final String PRIMARY_KEY= "date";
    public static final String RUNNING_DISTANCE="distance";
    public static final String RUNNING_TIME="time";
    public static final String CONSUMED_CALORIES="calories";

    //for internal DB
    // 운동 날짜(PK) , 운동 거리, 운동 시간, 소비 칼로리
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    PRIMARY_KEY + " CHAR(20) PRIMARY KEY," +
                    RUNNING_DISTANCE + " INTEGER," +
                    RUNNING_TIME + " INTEGER," +
                    CONSUMED_CALORIES + " FLOAT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

}
