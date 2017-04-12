package com.guowei.qrscanner.dialog;


import android.animation.ValueAnimator;
import android.app.DialogFragment;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.guowei.qrscanner.R;
import com.guowei.qrscanner.utils.DensityUtil;

public class ScannerDialog extends DialogFragment{

    private View dialogView;
    private ImageView scannerImg;
    private ImageView scannerLine;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题
        Bundle bundle = getArguments();
        String photoPath = bundle.getString("photoPath");
        dialogView = inflater.inflate(R.layout.dialog_scanner, container, false);
        scannerImg = (ImageView) dialogView.findViewById(R.id.scanner_img);
        scannerLine = (ImageView) dialogView.findViewById(R.id.scanner_line);
        Glide.with(getActivity())
                .load(photoPath)
                .into(scannerImg);
        //根据缩放比例获取展示图片的高度
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(photoPath,options);
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;
        float scale = outWidth / DensityUtil.dip2px(getActivity(), 300f);
        int img_height = (int) (outHeight / scale);
        //扫描动画
        ValueAnimator animator=ValueAnimator.ofInt(10,img_height-10,10);
        animator.setDuration(3000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                scannerLine.setPadding(0,value,0,0);
            }
        });
        animator.setRepeatCount(-1);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.start();
        return dialogView;
    }
}
