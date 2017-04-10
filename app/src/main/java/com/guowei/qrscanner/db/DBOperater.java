package com.guowei.qrscanner.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.guowei.qrscanner.bean.HistoryBean;

import java.util.ArrayList;
import java.util.List;

public class DBOperater {
    private static final String DB_NAME="History.db";
    private MyOpenHelper openHelper;

    public DBOperater(Context context ,int version){
        openHelper = new MyOpenHelper(context,DB_NAME,null,version);
    }
    public void addHistory(String text,String time){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(MyOpenHelper.CL_TEXT,text);
        values.put(MyOpenHelper.CL_TIME,time);
        db.insert(MyOpenHelper.TABLE_NAME,null,values);
        db.close();
    }
    public void deleteHistory(String text){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        db.delete(MyOpenHelper.TABLE_NAME,MyOpenHelper.CL_TEXT+" = ? ",new String[]{text});
        db.close();
    }
    public void updateHistory(String text, String time){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        Cursor cursor = db.query(MyOpenHelper.TABLE_NAME,
                new String[]{MyOpenHelper.CL_TEXT},
                MyOpenHelper.CL_TEXT + " = ?",
                new String[]{text}, null, null, null, null);
        if (cursor.moveToFirst()){
            ContentValues values=new ContentValues();
            values.put(MyOpenHelper.CL_TEXT,text);
            values.put(MyOpenHelper.CL_TIME,time);
            //由于字符串不能倒序查询，所以，删掉当前的数据，在添加，根据 id 来倒序查询
//            db.update(MyOpenHelper.TABLE_NAME,values,MyOpenHelper.CL_TEXT+" = ? ",new String[]{text});
            addHistory(text,time);
        }else{
            addHistory(text,time);
        }
        cursor.close();
        db.close();
    }
    public List<HistoryBean> queryAllHistory(){
        List<HistoryBean> historyList=new ArrayList<>();
        SQLiteDatabase db=openHelper.getWritableDatabase();
        //倒序查询
        Cursor cursor = db.query(MyOpenHelper.TABLE_NAME, null, null, null, null, null,"id desc");
        if (cursor.moveToFirst()){
            do {
                String history = cursor.getString(cursor.getColumnIndex(MyOpenHelper.CL_TEXT));
                String time = cursor.getString(cursor.getColumnIndex(MyOpenHelper.CL_TIME));
                HistoryBean histotyBean=new HistoryBean(history,time);
                historyList.add(histotyBean);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return historyList;
    }
    public void cleanHistory(){
        SQLiteDatabase db = openHelper.getWritableDatabase();
        //清空表的内容
        db.execSQL("delete from "+MyOpenHelper.TABLE_NAME);
        //把自增长的指数重置为0
        db.execSQL("update sqlite_sequence SET seq = 0 where name ='"+MyOpenHelper.TABLE_NAME+"';");
    }
}
