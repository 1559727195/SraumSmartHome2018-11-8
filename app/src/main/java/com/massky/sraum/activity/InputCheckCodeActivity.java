package com.massky.sraum.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.MycallbackNest;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import cn.smssdk.EventHandler;
import cn.smssdk.OnSendMessageHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;

/**
 * Created by zhu on 2017/12/29.
 */

public class InputCheckCodeActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    @InjectView(R.id.checkcode_id)
    ClearEditText checkcode_id;
    @InjectView(R.id.phone_id)
    TextView phone_id;
    private DialogUtil dialogUtil;
    private String des;
    private Handler handler;
    @InjectView(R.id.timebutton_id)
    TextView timebutton_id;

    //mob短信的key
    protected static final String SmsKey = "1a65fe6cfd250";
    protected static final String SmsSecret = "991ff716e52088581327423bf04509b0";
    private Map<String, Object> mapcode = new HashMap<>();
    private String phoneId;
    private TimeCount time;

    @Override
    protected int viewId() {
        return R.layout.input_checkcode_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        phoneId = IntentUtil.getIntentString(InputCheckCodeActivity.this, "registerphone");
        phone_id.setText("请输入" + phoneId + "收到的短信验证码");
        phone_id.setOnClickListener(this);
        //请输入13812391092收到的短信验证码
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        btn_login_gateway.setOnClickListener(this);
        timebutton_id.setOnClickListener(this);
        dialogUtil = new DialogUtil(InputCheckCodeActivity.this);
        getSms();
        getCode();
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
    }


    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            timebutton_id.setText("重新获取");
            timebutton_id.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            timebutton_id.setClickable(false);
            timebutton_id.setText("已发送" + "(" + millisUntilFinished / 1000 + "秒" + ")");
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                InputCheckCodeActivity.this.finish();
                break;
            case R.id.btn_login_gateway:
                checkRes();
                break;//登录网关
            case R.id.timebutton_id:
                getCode();
                break;
        }
    }


    private void getCode() {
        timebutton_id.setText("已发送...");
        SMSSDK.getVerificationCode("86", phoneId, new OnSendMessageHandler() {
            @Override
            public boolean onSendMessage(String s, String s1) {
                return false;
            }
        });
    }

    //提交注册信息
    private void checkRes() {
        String mobilePhone = phone_id.getText().toString();
        String code = checkcode_id.getText().toString();
//        String loginPwd = password_id.getText().toString();
//        String loginPwdtwo = confirm_id.getText().toString();
//        int pwdleng = checkcode_id.length();
        if (mobilePhone.equals("") || code.equals("") || checkcode_id.equals("") || checkcode_id.equals("")) {
            ToastUtil.showDelToast(InputCheckCodeActivity.this, "注册信息不能为空");
        } else {
            mapcode.put("mobilePhone", phoneId);
//            mapcode.put("code", code);
//                    mapcode.put("loginPwd", loginPwd);
//            SMSSDK.submitVerificationCode("86", mobilePhone, code);
            SMSSDK.submitVerificationCode("86", phoneId, code);
        }
    }


    private void getSms() {
        code();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        ToastUtil.showDelToast(InputCheckCodeActivity.this, "回调完成");
                        break;
                    case 2:
                        /*
                        * Intent intent = new Intent(RegisterActivity.this, ResetPassword.class);
                        intent.putExtra("phoneNumber", phoneNumber);
                        startActivity(intent); */

                        Intent intent = new Intent(InputCheckCodeActivity.this,
                                SettingPasswordActivity.class);
                        intent.putExtra("mapcode", (Serializable) mapcode);
                        startActivity(intent);
                        break;
                    case 3:
                        time.start();
                        ToastUtil.showDelToast(InputCheckCodeActivity.this, "验证码已发送，请查收");
                        break;
                    case 4:
                        ToastUtil.showDelToast(InputCheckCodeActivity.this, "返回支持发送国家验证码");
                        break;
                    case 5:
                        timebutton_id.setText("重新获取");
                        ToastUtil.showDelToast(InputCheckCodeActivity.this, des);
                        break;
                }
            }
        };
    }


    private void code() {
        SMSSDK.initSDK(this, SmsKey, SmsSecret);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) {
                    //回调完成
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        //提交验证码成功
                        handler.sendEmptyMessage(2);
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        //获取验证码成功
                        handler.sendEmptyMessage(3);
                    } else if (event == SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES) {
                        //返回支持发送验证码的国家列表
                        handler.sendEmptyMessage(4);
                    }
                } else {
                    ((Throwable) data).printStackTrace();
                    Log.i("异常", "afterEvent: ");
                    Throwable throwable = (Throwable) data;
                    int status = 0;
                    JSONObject object = null;
                    try {
                        object = new JSONObject(throwable.getMessage());
                        des = object.optString("detail");
                        status = object.optInt("status");
                        Log.i("这是返回错误", status + "afterEvent: ");
                        if (!TextUtils.isEmpty(des)) {
                            handler.sendEmptyMessage(5);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }

    @Override
    protected void onDestroy() {
        time.cancel();
        SMSSDK.unregisterAllEventHandler();
        super.onDestroy();
    }
}
