package com.guowei.qrscanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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

import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private FrameLayout containerRoot;
    private ZXingScannerView mScannerView;
    private FrameLayout container;
    private ImageView btnFlash;
    private ImageView btnImage;
    private final int IMG_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        mScannerView = new ZXingScannerView(this);
        ViewGroup.LayoutParams params = containerRoot.getLayoutParams();
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        container.addView(mScannerView, params);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        } else {
            startCamera();
        }

    }

    private void initView() {
        containerRoot = (FrameLayout) findViewById(R.id.container_root);
        container = (FrameLayout) findViewById(R.id.container);
        btnFlash = (ImageView) findViewById(R.id.btn_flash);
        btnImage = (ImageView) findViewById(R.id.btn_image);
        btnFlash.setOnClickListener(this);
        btnImage.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCamera();
    }

    public void startCamera() {
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setAutoFocus(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        mScannerView.stopCamera();
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        startResultActivity(result.getText());
    }

    private void startResultActivity(String result){
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra("result", result);
        startActivity(resultIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flash:
                mScannerView.setFlash(!mScannerView.getFlash());
                break;
            case R.id.btn_image:
                Intent imgSelect;
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    imgSelect = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                } else {
                    imgSelect = new Intent(Intent.ACTION_GET_CONTENT);
//                }
                imgSelect.setType("image/*");
                startActivityForResult(imgSelect, IMG_REQUEST_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case IMG_REQUEST_CODE:
                try {
                    //data中带有返回的uri
                    Uri photoUri = data.getData();
                    //由于这个方法返回的bitmap没有进行压缩处理，可能会OOM，但是要读取二维码，就不压缩了
                    Bitmap photoBitmap = null;
                    photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    String result = QRCodeUtils.getStringFromQRCode(photoBitmap);
                    startResultActivity(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
