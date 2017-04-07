package com.guowei.qrscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultText;
    private String result;
    private ImageView btnCopy;
    private ImageView btnOpen;
    private ImageView btnShare;
    private ClipboardManager cm;
    private Pattern w3Pattern;
    private Pattern httpPattern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        //获取剪贴板管理器：
        cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        initView();
        //初始化正则
        httpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        resultText.setText(result);
        if (!httpPattern.matcher(result).matches()){
            btnOpen.setVisibility(View.GONE);
        }
    }

    private void initView() {
        resultText = (TextView) findViewById(R.id.result_text);
        btnCopy = (ImageView) findViewById(R.id.btn_copy);
        btnOpen = (ImageView) findViewById(R.id.btn_open);
        btnShare = (ImageView) findViewById(R.id.btn_share);
        btnOpen.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnShare.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy:
                // 创建普通字符型ClipData
                ClipData mClipData = ClipData.newPlainText("result", result);
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(this, "成功复制到剪贴板", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_open:
                if (httpPattern.matcher(result).matches()) {
                    Toast.makeText(this, "正在打开http: " + result, Toast.LENGTH_SHORT).show();
                    Intent websiteIntent = new Intent();
                    websiteIntent.setAction(Intent.ACTION_VIEW);
                    Uri websiteUri = Uri.parse(result);
                    websiteIntent.setData(websiteUri);
                    startActivity(websiteIntent);
                }
                break;
            case R.id.btn_share:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, result);
                startActivity(shareIntent);
                break;
        }
    }
}
