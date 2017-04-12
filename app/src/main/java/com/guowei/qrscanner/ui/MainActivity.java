package com.guowei.qrscanner.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import com.guowei.qrscanner.dialog.ScannerDialog;
import com.guowei.qrscanner.event.DismissScannerDialog;
import com.guowei.qrscanner.event.JumpResultActivity;
import com.guowei.qrscanner.utils.QRCodeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private final int CAMERA_REQUEST_CODE = 2;
    private final int READ_AND_WRITE_EXTERNAL_REQUEST_CODE = 3;
    private final int photoTargeSize = 600;
    private ScannerDialog scannerDialog;

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
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            startCamera();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
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
                .addTestDevice("A8657DFAACEC4DAC0A9D8BCB836772F8")//oppo测试码
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
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(this, result.getText(), Toast.LENGTH_SHORT).show();
        DBOperater dbOperater = new DBOperater(this, 1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MM - dd");
        String timeFormat = sdf.format(new Date());
        dbOperater.updateHistory(result.getText(), timeFormat);
        startResultActivity(result.getText());
    }

    private void startResultActivity(String result) {
        Intent resultIntent = new Intent(this, ResultActivity.class);
        resultIntent.putExtra("result", result);
        startActivity(resultIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera();
                }
                break;
            case READ_AND_WRITE_EXTERNAL_REQUEST_CODE:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "同意这些权限，才能获取到图片", Toast.LENGTH_SHORT).show();
                            return;
                        }
//                        startImageSelecter();
                    }
                }
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_flash:
                mScannerView.setFlash(!mScannerView.getFlash());
                break;
            case R.id.btn_image:
                Intent imgSelect;
                imgSelect = new Intent(Intent.ACTION_GET_CONTENT);
                imgSelect.setType("image/*");
                startActivityForResult(imgSelect, IMG_REQUEST_CODE);
                break;
            case R.id.btn_history:
                startActivity(new Intent(this, HistoryActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "获取图片失败", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (requestCode) {
            case IMG_REQUEST_CODE:
                String photoPath = getRealFilePath(this, data.getData());
                Bundle bundle = new Bundle();
                bundle.putString("photoPath",photoPath);
                scannerDialog = new ScannerDialog();
                scannerDialog.setArguments(bundle);
                scannerDialog.show(getFragmentManager(),"scannerDialog");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String result = resolve(data);
                        EventBus.getDefault().post(new DismissScannerDialog());
                        if (result!=null){
                            JumpResultActivity jumpEvent = new JumpResultActivity();
                            jumpEvent.setResult(result);
                            EventBus.getDefault().post(jumpEvent);
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"解析不到二维码",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();

                break;
        }
    }

    //获取图片，解析出二维码
    private String resolve(Intent data) {
        //data中带有返回的uri
        Uri photoUri = data.getData();
        Bitmap photoBitmap = null;
        //由于这个方法返回的bitmap没有进行压缩处理，可能会OOM，但是要读取二维码，就不压缩了。
        // 补充，还是要进行压缩，或者判断图片大小，或者裁剪，否则用户点了一个大图，就 OOM 了
        //   现在 ，补充了一个压缩图片的方法，需要使用的时候，可以添加上去
//            photoBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
        Bitmap scaleBitmap = getScaleBitmap(getRealFilePath(this, photoUri));
        //下面这个方法是从网上找到，英文文档写得很复制，看不懂，其他的解决方法都是过时的，就这个是好用的
        //来源：http://blog.csdn.net/a102111/article/details/48377537
        //感谢这位作者
        return QRCodeUtils.getStringFromQRCode(scaleBitmap);
    }

    /**
     * 压缩图片
     */
    private Bitmap getScaleBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //设置 inJustDecodeBounds 这个属性为true 的话，
        // 解码 bitmap 时，只返回其宽高等的属性，不会申请内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        //获取图片的宽高
        int photoWidth = options.outWidth;
        int photoHeight = options.outHeight;
        //获取缩放比例
        int scaleSize = 1;
        if (photoWidth > photoTargeSize || photoHeight > photoTargeSize) {
            int widthScale = Math.round(photoWidth / photoTargeSize);
            int heightScale = Math.round(photoHeight / photoTargeSize);
            scaleSize = Math.min(widthScale, heightScale);
        }
        //使用现在的 options 来获取 bitmap
        options.inSampleSize = scaleSize;
        options.inJustDecodeBounds = false;
        Bitmap scaleBitmap = BitmapFactory.decodeFile(path, options);
        return scaleBitmap;
    }
    //uri -> path
    public static String getRealFilePath(final Context context, final Uri uri ) {
        if ( null == uri ) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if ( scheme == null )
            data = uri.getPath();
        else if ( ContentResolver.SCHEME_FILE.equals( scheme ) ) {
            data = uri.getPath();
        } else if ( ContentResolver.SCHEME_CONTENT.equals( scheme ) ) {
            Cursor cursor = context.getContentResolver().query( uri, new String[] { MediaStore.Images.ImageColumns.DATA }, null, null, null );
            if ( null != cursor ) {
                if ( cursor.moveToFirst() ) {
                    int index = cursor.getColumnIndex( MediaStore.Images.ImageColumns.DATA );
                    if ( index > -1 ) {
                        data = cursor.getString( index );
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dismissScannerDialog(DismissScannerDialog event){
        if (scannerDialog!=null){
            scannerDialog.dismiss();
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void jumpResultActivity(JumpResultActivity event){
        startResultActivity(event.getResult());
    }
}
