package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.Util.EyeUtil;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/29.
 */

public class SettingPasswordActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;

    @InjectView(R.id.eyeimageview_id)
    ImageView eyeimageview_id;

    @InjectView(R.id.edit_password_again)
    ClearEditText edit_password_again;
    private EyeUtil eyeUtil;

    @Override
    protected int viewId() {
        return R.layout.setting_password_act;
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
        eyeimageview_id.setOnClickListener(this);
        eyeUtil = new EyeUtil(SettingPasswordActivity.this, eyeimageview_id, edit_password_again, true);
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
                SettingPasswordActivity.this.finish();
                break;
            case R.id.btn_login_gateway:
                startActivity(new Intent(SettingPasswordActivity.this,
                       LoginGateWayActivity.class));
                SettingPasswordActivity.this.finish();
                break;//登录网关
            case R.id.eyeimageview_id:
                eyeUtil.EyeStatus();
        }
    }
}
