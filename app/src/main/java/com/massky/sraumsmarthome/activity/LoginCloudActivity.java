package com.massky.sraumsmarthome.activity;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.Util.EyeUtil;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraumsmarthome.permissions.RxPermissions;
import com.massky.sraumsmarthome.tool.Constants;
import com.massky.sraumsmarthome.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by zhu on 2017/12/29.
 */

public class LoginCloudActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    @InjectView(R.id.scan_gateway)
    TextView scan_gateway;
    @InjectView(R.id.search_gateway_btn)
    TextView search_gateway_btn;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    @InjectView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    @InjectView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    private EyeUtil eyeUtil;
    @InjectView(R.id.regist_new)
    TextView regist_new;
    @InjectView(R.id.forget_pass)
    TextView forget_pass;

    @Override
    protected int viewId() {
        return R.layout.login_cloud;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        toolbar_txt.setText("登录网关");
        scan_gateway.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        init_permissions();
        search_gateway_btn.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        eyeUtil = new EyeUtil(LoginCloudActivity.this, eyeimageview_id_gateway, edit_password_gateway, true);
        forget_pass.setOnClickListener(this);
        regist_new.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                LoginCloudActivity.this.finish();
                break;
            case R.id.scan_gateway:
                Intent openCameraIntent = new Intent(LoginCloudActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE);
                break;
            case R.id.search_gateway_btn:
                startActivity(new Intent(LoginCloudActivity.this, LoginGateWayActivity.class));
                break;
            case R.id.btn_login_gateway:
                //开始登录

                startActivity(new Intent(LoginCloudActivity.this, MainGateWayActivity.class));
                break;//登录网关
            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
            case R.id.regist_new:
                startActivity(new Intent(LoginCloudActivity.this, RegistActivity.class));
                break;
            case R.id.forget_pass:
                startActivity(new Intent(LoginCloudActivity.this, ForgetActivity.class));
                break;
        }
    }
}
