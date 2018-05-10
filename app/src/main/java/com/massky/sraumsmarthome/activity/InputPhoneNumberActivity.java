package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/23.
 */

public class InputPhoneNumberActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @Override
    protected int viewId() {
        return R.layout.input_phone_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                InputPhoneNumberActivity.this.finish();
                break;
            case R.id.next_step_txt:
                startActivity(new Intent(InputPhoneNumberActivity.this,SettingNiChengActivity.class));
                break;
        }
    }
}
