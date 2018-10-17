package com.massky.sraum.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.alibaba.fastjson.JSON;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MD5Util;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.MycallbackNest;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.Timeuti;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraum.permissions.RxPermissions;
import com.massky.sraum.tool.Constants;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.Call;

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

    private EyeUtil eyeUtil;
    @InjectView(R.id.regist_new)
    TextView regist_new;
    @InjectView(R.id.forget_pass)
    TextView forget_pass;

    @InjectView(R.id.usertext_id)
    ClearEditText usertext_id;
    @InjectView(R.id.phonepassword)
    ClearEditText phonepassword;
    private DialogUtil dialogUtil;
    private String token;

    @Override
    protected int viewId() {
        return R.layout.login_cloud;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        dialogUtil = new DialogUtil(this);
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
        eyeUtil = new EyeUtil(LoginCloudActivity.this, eyeimageview_id_gateway, usertext_id, true);
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
//                login();
                break;//登录网关
            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
            case R.id.regist_new:
//                startActivity(new Intent(LoginCloudActivity.this, RegistActivity.class));
                String registerphone = usertext_id.getText().toString();
                IntentUtil.startActivity(LoginCloudActivity.this, RegistActivity.class, "registerphone", registerphone);
                break;
            case R.id.forget_pass:
//                startActivity(new Intent(LoginCloudActivity.this, ForgetActivity.class));
                String findonepho = usertext_id.getText().toString();
                IntentUtil.startActivity(LoginCloudActivity.this, ForgetActivity.class, "registerphone", findonepho);
                break;
        }
    }


    /**
     * 登录方法
     */
    private void login() {
        String loginAccount = usertext_id.getText().toString();
        String pwd = phonepassword.getText().toString();
        if (loginAccount.equals("") || pwd.equals("")) {
            ToastUtil.showDelToast(LoginCloudActivity.this, "用户名或密码不能为空");
        } else {
            String time = Timeuti.getTime();
            Map<String, Object> maptwo = new HashMap<>();
            maptwo.put("loginAccount", loginAccount);
            maptwo.put("timeStamp", time);
            maptwo.put("signature", MD5Util.md5(loginAccount + pwd + time));
            LogUtil.eLength("传入时间戳", JSON.toJSONString(maptwo) + "时间戳" + time);
            dialogUtil.loadDialog();
            get_token(maptwo);
        }
    }

    /**
     * 获取token
     *
     * @param maptwo
     */
    private void get_token(Map<String, Object> maptwo) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_getToken, maptwo, new MycallbackNest(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {

            }
        }, LoginCloudActivity.this, dialogUtil) {

            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(LoginCloudActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                Map<String, Object> map = getStringObjectMap(user);
                if (map == null) return;
                init_login(map);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();//继承父类，实现父类的方法
                ToastUtil.showDelToast(LoginCloudActivity.this, "登录失败，账号未注册");
            }
        });
    }

    /**
     * 保存并传值
     *
     * @param user
     * @return
     */
    @Nullable
    private Map<String, Object> getStringObjectMap(User user) {
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "loginPassword", phonepassword.getText().toString());//保存密码
        token = user.token;
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "tokenTime", true);
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "sraumtoken", user.token);
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "expires_in", user.expires_in);
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "logintime", (int) System.currentTimeMillis());
        SharedPreferencesUtil.saveData(LoginCloudActivity.this, "tagint", 0);
        Map<String, Object> map = new HashMap<>();
        TelephonyManager TelephonyMgr = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(LoginCloudActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        String szImei = TelephonyMgr.getDeviceId();
        String regId = (String) SharedPreferencesUtil.getData(LoginCloudActivity.this, "regId", "");
        map.put("token", user.token);
        map.put("regId", regId);
        map.put("phoneId", regId);
        LogUtil.eLength("查看数据", JSON.toJSONString(map));
        return map;
    }

    /**
     * 登录
     *
     * @param map
     */
    private void init_login(Map<String, Object> map) {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_login, map, new MycallbackNest(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {

            }
        }, LoginCloudActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(LoginCloudActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
//                                    ToastUtil.showToast(LoginActivity.this,"登录成功");
                SharedPreferencesUtil.saveData(LoginCloudActivity.this, "loginPhone", usertext_id.getText().toString());
                SharedPreferencesUtil.saveData(LoginCloudActivity.this, "avatar", user.avatar);
                SharedPreferencesUtil.saveData(LoginCloudActivity.this, "accountType", user.accountType);
                SharedPreferencesUtil.saveData(LoginCloudActivity.this, "loginflag", true);
                if (user.userName != null && !user.userName.equals("")) {
                    SharedPreferencesUtil.saveData(LoginCloudActivity.this, "userName", user.userName);
                }
                IntentUtil.startActivityAndFinishFirst(LoginCloudActivity.this, MainGateWayActivity.class);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showDelToast(LoginCloudActivity.this, "登录失败");
            }
        });
    }
}
