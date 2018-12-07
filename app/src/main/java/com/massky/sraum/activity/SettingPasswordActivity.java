package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.MycallbackNest;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

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
    private String mobilePhone;
    private String code;
    private DialogUtil dialogUtil;
    private Map mapcode = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.setting_password_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }

        mapcode = (Map) getIntent().getSerializableExtra("mapcode");
        if (mapcode != null) {
            mobilePhone = (String) mapcode.get("mobilePhone");
            code = (String) mapcode.get("code");
        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        btn_login_gateway.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
    }


    /**
     * 提交验证码
     */
    private void commit_code() {
        String password = edit_password_again.getText().toString();
//        String loginPwd = password_id.getText().toString();
//        String loginPwdtwo = confirm_id.getText().toString();
//        int pwdleng = checkcode_id.length();
        if (password.equals("")) {
            ToastUtil.showDelToast(SettingPasswordActivity.this, "密码不能为空");
        } else {
            // mapcode.put("mobilePhone", mobilePhone);

//            mapcode.remove("code");
            mapcode.put("loginPwd", password);
            show_detail_togglen();

        }
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


    /**
     * ApiHelper.sraum_register
     */
    private void show_detail_togglen() {
        MyOkHttp.postMapObjectnest(ApiHelper.sraum_register, mapcode, new MycallbackNest(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                show_detail_togglen();
            }
        }, SettingPasswordActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(SettingPasswordActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                switch (user.result) {
                    case "100":
//                        startActivity(new Intent(SettingPasswordActivity.this,
//                              LoginCloudActivity.class));
//                        SettingPasswordActivity.this.finish();
                        AppManager.getAppManager().finishAllActivity();
                        startActivity(new Intent(SettingPasswordActivity.this,
                                LoginCloudActivity.class));
                        break;
                    default:
                        ToastUtil.showDelToast(SettingPasswordActivity.this, "注册失败");
                        break;
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SettingPasswordActivity.this.finish();
                break;
            case R.id.btn_login_gateway:
//                startActivity(new Intent(SettingPasswordActivity.this,
//                        LoginGateWayActivity.class));
//                SettingPasswordActivity.this.finish();
                commit_code();
                break;//登录网关
            case R.id.eyeimageview_id:
                eyeUtil.EyeStatus();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
