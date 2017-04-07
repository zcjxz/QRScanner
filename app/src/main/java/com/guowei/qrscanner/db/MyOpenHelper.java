package com.guowei.qrscanner.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class MyOpenHelper extends SQLiteOpenHelper{
    private Context mContext;
    private static final String TABLE_NAME="History";
    private static final String CL_ID="id";
    private static final String CL_TEXT="text";
    private static final String CREATE_DB="create table if no exists"+TABLE_NAME+" (" +
            CL_ID+" integer primary key autoincrement" +
            CL_TEXT+" text)";

    public MyOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DB);
        Toast.makeText(mContext, "Create DB succeeded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
