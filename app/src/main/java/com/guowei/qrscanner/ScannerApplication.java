package com.guowei.qrscanner;

import android.app.Application;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class ScannerApplication extends Application{
    public static ScannerApplication application=null;
    @Override
    public void onCreate() {
        super.onCreate();
        application=this;
    }
    public static ScannerApplication getApplication(){
        return application;
    }
}
