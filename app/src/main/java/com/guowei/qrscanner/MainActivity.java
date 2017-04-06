package com.guowei.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler,View.OnClickListener{

    private FrameLayout containerRoot;
    private ZXingScannerView mScannerView;
    private FrameLayout container;
    private ImageView btnFlash;
    private ImageView btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mScannerView = new ZXingScannerView(this);
        ViewGroup.LayoutParams params = containerRoot.getLayoutParams();
        params.width=FrameLayout.LayoutParams.MATCH_PARENT;
        params.height=FrameLayout.LayoutParams.MATCH_PARENT;
        container.addView(mScannerView,params);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},1);
        }else{
            startCamera();
        }

    }

    private void initView(){
        containerRoot = (FrameLayout) findViewById(R.id.container_root);
        container = (FrameLayout) findViewById(R.id.container);
        btnFlash = (ImageView) findViewById(R.id.btn_flash);
        btnImage = (ImageView) findViewById(R.id.btn_image);
        btnFlash.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }
    public void startCamera(){
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setAutoFocus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra("result",result.getText());
        startActivity(resultIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    startCamera();
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_flash:
                mScannerView.setFlash(!mScannerView.getFlash());
                break;
        }
    }
}
