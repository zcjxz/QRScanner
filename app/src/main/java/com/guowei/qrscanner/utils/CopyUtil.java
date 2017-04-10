package com.guowei.qrscanner.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import com.guowei.qrscanner.ScannerApplication;

/**
 * Created by Administrator on 2017/4/10 0010.
 */

public class CopyUtil {
    public static void copy(String result){
        ScannerApplication application = ScannerApplication.getApplication();
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) application.getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("result", result);
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        Toast.makeText(application, "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
    }
}
