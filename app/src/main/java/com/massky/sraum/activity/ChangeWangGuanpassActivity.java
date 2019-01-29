package com.massky.sraum.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.EyeUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by masskywcy on 2016-09-26.
 */
/*重新设置密码界面*/
public class ChangeWangGuanpassActivity extends BaseActivity {
    @InjectView(R.id.backrela_id)
    RelativeLayout backrela_id;
    @InjectView(R.id.titlecen_id)
    TextView titlecen_id;
    @InjectView(R.id.completebtn_id)
    Button completebtn_id;
    @InjectView(R.id.originalpassword)
    ClearEditText originalpassword;
    @InjectView(R.id.newpassword)
    ClearEditText newpassword;
    @InjectView(R.id.againpassword)
    ClearEditText againpassword;
    @InjectView(R.id.eyeone)
    ImageView eyeone;
    @InjectView(R.id.eyetwo)
    ImageView eyetwo;
    @InjectView(R.id.eyethree)
    ImageView eyethree;
    @InjectView(R.id.back)
    ImageView back;

    private DialogUtil dialogUtil;
    private EyeUtil eyeUtilOne, eyeUtilTwo, eyeUtilThree;
    private Bundle bundle;
    private String boxname;
    private String boxnumber;
    private String areaNumber;

    @Override
    protected int viewId() {
        return R.layout.forgetpassword;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(ChangeWangGuanpassActivity.this);
        completebtn_id.setOnClickListener(this);
        backrela_id.setOnClickListener(this);
        eyeone.setOnClickListener(this);
        eyetwo.setOnClickListener(this);
        eyethree.setOnClickListener(this);
        eyeUtilOne = new EyeUtil(ChangeWangGuanpassActivity.this, eyeone, originalpassword, true);
        eyeUtilTwo = new EyeUtil(ChangeWangGuanpassActivity.this, eyetwo, newpassword, true);
        eyeUtilThree = new EyeUtil(ChangeWangGuanpassActivity.this, eyethree, againpassword, true);
        titlecen_id.setText(R.string.changepassword);
        dialogUtil = new DialogUtil(ChangeWangGuanpassActivity.this);
        bundle = IntentUtil.getIntentBundle(ChangeWangGuanpassActivity.this);
        boxname = bundle.getString("boxName", "");
        boxnumber = bundle.getString("boxnumber", "");
        areaNumber = bundle.getString("areaNumber", "");
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
            case R.id.backrela_id:
                ChangeWangGuanpassActivity.this.finish();
                break;
            case R.id.completebtn_id:
                String oldPwd = originalpassword.getText().toString();
                String newPwd = newpassword.getText().toString();
                String newPwdtwo = againpassword.getText().toString();
                if (!oldPwd.equals("") && !newPwd.equals("") && !newPwdtwo.equals("")) {
                    if (newPwd.equals(newPwdtwo)) {
                        accountmber_cahn(oldPwd, newPwd);

                    } else {
                        ToastUtil.showToast(ChangeWangGuanpassActivity.this, "新密码输入不一致");
                    }
                } else {
                    ToastUtil.showToast(ChangeWangGuanpassActivity.this, "信息不能为空");
                }
                break;
            case R.id.eyeone:
                eyeUtilOne.EyeStatus();
                break;
            case R.id.eyetwo:
                eyeUtilTwo.EyeStatus();
                break;
            case R.id.eyethree:
                eyeUtilThree.EyeStatus();
                break;
            case R.id.back:
                ChangeWangGuanpassActivity.this.finish();
                break;
        }
    }

    private void accountmber_cahn(final String oldPwd, final String newPwd) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(ChangeWangGuanpassActivity.this));
        map.put("number", boxnumber);
        map.put("oldPassword", oldPwd);
        map.put("newPassword", newPwd);
        map.put("areaNumber",areaNumber);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_updateGatewayPassword, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        accountmber_cahn(oldPwd, newPwd);
                    }
                }, ChangeWangGuanpassActivity.this, dialogUtil) {
            @Override
            public void wrongBoxnumber() {
                ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "请输入正确的原密码");
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                switch (user.result) {
                    case "100":
                        ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "密码修改成功");
                        ChangeWangGuanpassActivity.this.finish();
                        break;
//                    case "103":
//                        ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "请输入正确的新密码");
//                        break;
//                    case "101":
//                        TokenDialog.getTokenDialog().showToDialog(ChangeWangGuanpassActivity.this);
//                        break;
//                    default:
//                        ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "程序无响应");
//                        break;
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }


            @Override
            public void threeCode() {
                ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "number 不存在");
            }

            @Override
            public void fourCode() {
                ToastUtil.showDelToast(ChangeWangGuanpassActivity.this, "oldPassword 错误");
            }
        });
    }
}
