package com.guowei.qrscanner.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.zxing.Result;
import com.guowei.qrscanner.R;
import com.guowei.qrscanner.db.DBOperater;
import com.guowei.qrscanner.utils.QRCodeUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler, View.OnClickListener {

    private FrameLayout containerRoot;
    private ZXingScannerView mScannerView;
    private FrameLayout container;
    private ImageView btnFlash;
    private ImageView btnImage;
    private final int IMG_REQUEST_CODE = 1;
    private AdView adView;
    private ImageView btnHistory;

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
        adView = (AdView) findViewById(R.id.adView);
        btnHistory = (ImageView) findViewById(R.id.btn_history);
        btnFlash.setOnClickListener(this);
        btnImage.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("73957908AF204D3C3BD6DD4DA2BD36F4")//红米4测试码
                .addTestDevice("46E4E6B0DD6C71F38DC6F64A53BEAC0D")//华为测试码
                .addTestDevice("00324B61CF9CF3A064D03C379CA05E5F")//联想测试码
                .build();
        adView.loadAd(adRequest);
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
        DBOperater dbOperater=new DBOperater(this,1);
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy - MM - dd");
        String timeFormat = sdf.format(new Date());
        dbOperater.updateHistory(result.getText(),timeFormat);
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
                //区分版本不同，调用新的api，实践后发现，其实都可以用
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    imgSelect = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                } else {
                    imgSelect = new Intent(Intent.ACTION_GET_CONTENT);
//                }
                imgSelect.setType("image/*");
                startActivityForResult(imgSelect, IMG_REQUEST_CODE);
                break;
            case R.id.btn_history:
                startActivity(new Intent(this,HistoryActivity.class));
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
                    Bitmap photoBitmap = null;
                    //由于这个方法返回的bitmap没有进行压缩处理，可能会OOM，但是要读取二维码，就不压缩了。
                    // 补充，还是要进行压缩，或者判断图片大小，或者裁剪，否则用户点了一个大图，就 OOM 了
                    photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                    //下面这个方法是从网上找到，英文文档看不懂，其他的解决方法都是过时的，就这个是好用的
                    //来源：http://blog.csdn.net/a102111/article/details/48377537
                    //感谢这位作者
                    String result = QRCodeUtils.getStringFromQRCode(photoBitmap);
                    startResultActivity(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
