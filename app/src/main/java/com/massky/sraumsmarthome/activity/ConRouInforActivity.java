package com.massky.sraumsmarthome.activity;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraumsmarthome.permissions.RxPermissions;
import com.massky.sraumsmarthome.tool.Constants;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhu on 2017/12/29.
 */

public class ConRouInforActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;

    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    @Override
    protected int viewId() {
        return R.layout.con_rouinfor_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        btn_login_gateway.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

//    private void init_permissions() {
//
//        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
//        RxPermissions permissions = new RxPermissions(this);
//        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(Boolean aBoolean) {
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ConRouInforActivity.this.finish();
                break;
            case R.id.btn_login_gateway:
                startActivity(new Intent(ConRouInforActivity.this,AddZigbeeDeviceConnectingActivity.class));
                break;//登录网关
        }
    }
}
