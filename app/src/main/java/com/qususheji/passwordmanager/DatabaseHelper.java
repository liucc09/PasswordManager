package com.qususheji.passwordmanager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by liucc09 on 2015/1/17.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Password.db";

    public static abstract class TableContent implements BaseColumns {
        public static final String TABLE_NAME = "Content";
        public static final String COLUMN_NAME_DES = "description";
        public static final String COLUMN_NAME_ACCOUNT = "account";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    public static abstract class TableLogin implements BaseColumns {
        public static final String TABLE_NAME = "Login";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PASSWORD = "password";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_CONTENT =
            "CREATE TABLE " + TableContent.TABLE_NAME + " (" +
                    TableContent._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableContent.COLUMN_NAME_DES + TEXT_TYPE + COMMA_SEP +
                    TableContent.COLUMN_NAME_ACCOUNT + TEXT_TYPE + COMMA_SEP +
                    TableContent.COLUMN_NAME_PASSWORD + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_CONTENT =
            "DROP TABLE IF EXISTS " + TableContent.TABLE_NAME;

    private static final String SQL_CREATE_LOGIN =
            "CREATE TABLE " + TableLogin.TABLE_NAME + " (" +
                    TableLogin._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    TableLogin.COLUMN_NAME_ID + INT_TYPE + " UNIQUE " + COMMA_SEP  +
                    TableLogin.COLUMN_NAME_PASSWORD + TEXT_TYPE +
                    " ) ";

    private static final String SQL_DELETE_LOGIN =
            "DROP TABLE IF EXISTS " + TableLogin.TABLE_NAME;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_CONTENT);
        db.execSQL(SQL_CREATE_LOGIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_CONTENT);
        db.execSQL(SQL_DELETE_LOGIN);
        onCreate(db);
    }
}
