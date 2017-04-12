package com.guowei.qrscanner.ui;

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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.guowei.qrscanner.R;
import com.guowei.qrscanner.dialog.ScannerDialog;
import com.guowei.qrscanner.utils.CopyUtil;

import java.util.regex.Pattern;

public class ResultActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView resultText;
    private String result;
    private ImageView btnCopy;
    private ImageView btnOpen;
    private ImageView btnShare;
    private Pattern httpPattern;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        Intent intent = getIntent();
        result = intent.getStringExtra("result");
        initView();
        //初始化正则
        httpPattern = Pattern
                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
        resultText.setText(result);
//        if (!httpPattern.matcher(result).matches()){
//            btnOpen.setVisibility(View.GONE);
//        }
    }

    private void initView() {
        resultText = (TextView) findViewById(R.id.result_text);
        btnCopy = (ImageView) findViewById(R.id.btn_copy);
        btnOpen = (ImageView) findViewById(R.id.btn_open);
        btnShare = (ImageView) findViewById(R.id.btn_share);
        adView = (AdView) findViewById(R.id.adView);
        btnOpen.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("73957908AF204D3C3BD6DD4DA2BD36F4")//红米4测试码
                .addTestDevice("46E4E6B0DD6C71F38DC6F64A53BEAC0D")//华为测试码
                .addTestDevice("00324B61CF9CF3A064D03C379CA05E5F")//联想测试码
                .addTestDevice("A8657DFAACEC4DAC0A9D8BCB836772F8")//oppo测试码
                .build();
        adView.loadAd(adRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_copy:
                CopyUtil.copy(result);
                break;
            case R.id.btn_open:
                if (httpPattern.matcher(result).matches()) {
                    Toast.makeText(this, "正在打开http: " + result, Toast.LENGTH_SHORT).show();
                    Intent websiteIntent = new Intent();
                    websiteIntent.setAction(Intent.ACTION_VIEW);
                    Uri websiteUri = Uri.parse(result);
                    websiteIntent.setData(websiteUri);
                    startActivity(websiteIntent);
                }else{
                    Intent websiteIntent = new Intent();
                    websiteIntent.setAction(Intent.ACTION_VIEW);
                    //这里踩了一个坑，如果这里没有加“http://”的话，就会报错，会找不到要启动的 activity
                    Uri websiteUri = Uri.parse("http://"+result);
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
