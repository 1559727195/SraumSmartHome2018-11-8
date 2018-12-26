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
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;
import cn.forward.androids.utils.StatusBarUtil;

/**
 * Created by xufuchao on 2017-02-21.
 */
/*
* 添加成员界面*/
public class AddfamilyActivity extends BaseActivity {
    @InjectView(R.id.familyedit)
    ClearEditText familyedit;
    @InjectView(R.id.nameedit)
    ClearEditText nameedit;
    @InjectView(R.id.fambtn_id)
    Button fambtn_id;
    @InjectView(R.id.back)
    ImageView   back;
    private DialogUtil dialogUtil;
    private Bundle bundle;

    @Override
    protected int viewId() {
        return R.layout.invitefamily;
    }

    @Override
    protected void onView() {
        bundle = new Bundle();
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(AddfamilyActivity.this);
        fambtn_id.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddfamilyActivity.this.finish();
                break;
            case R.id.fambtn_id:
                String mobilePhone = familyedit.getText().toString();
                String familyName = nameedit.getText().toString();
                bundle.putString("mobilePhone", mobilePhone);
                bundle.putString("familyName", familyName);
                if (!mobilePhone.equals("") && !familyName.equals("")) {
                    if (mobilePhone.length() > 5) {
                        dialogUtil.loadDialog();
                        sraum_checkMobile(mobilePhone);
                    } else {
                        ToastUtil.showDelToast(AddfamilyActivity.this, "手机号码格式不正确");
                    }
                } else {
                    ToastUtil.showDelToast(AddfamilyActivity.this, "信息不能为空");
                }
                break;
        }
    }

    private void sraum_checkMobile(final String mobilePhone) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobilePhone", mobilePhone);
        MyOkHttp.postMapObject(ApiHelper.sraum_checkMemberPhone, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_checkMobile(mobilePhone);
                    }
                }, AddfamilyActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        IntentUtil.startActivity(AddfamilyActivity.this, AddfamilyActivitytwo.class, bundle);
                    }

                    @Override
                    public void wrongToken() {
                        ToastUtil.showToast(AddfamilyActivity.this,"手机号已存在");
                    }
                });
    }
}
