package com.guowei.qrscanner.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class DBOperater {
    private static final String DB_NAME="History.db";
    private MyOpenHelper openHelper;

    public void DBOperater(Context context ,int version){
        openHelper = new MyOpenHelper(context,DB_NAME,null,version);
    }
    public void addHistory(String text){
        SQLiteDatabase db = openHelper.getWritableDatabase();
    }
}
