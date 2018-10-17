package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/9.
 */

public class AddAutoSceneActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.auto_time_exeute)
    RelativeLayout auto_time_exeute;
    @InjectView(R.id.smart_device_exeute)
   RelativeLayout smart_device_exeute;

    @Override
    protected int viewId() {
        return R.layout.add_autoscene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        auto_time_exeute.setOnClickListener(this);
        smart_device_exeute.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddAutoSceneActivity.this.finish();
                break;
            case R.id.auto_time_exeute:
                    startActivity(new Intent(AddAutoSceneActivity.this,TimeExcuteCordinationActivity.class));
                break; //定时执行
            case R.id.smart_device_exeute://
                startActivity(new Intent(AddAutoSceneActivity.this,ExcuteSmartDeviceCordinationActivity.class));
                break;//智能设备执行
        }
    }
}
