package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.RoundProgressBar;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/3.
 */

public class AddWifiDeviceActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    @Override
    protected int viewId() {
        return R.layout.add_wifi_dev_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_cancel_wangguan.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddWifiDeviceActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan://下一步
                startActivity(new Intent(AddWifiDeviceActivity.this,ConRouInforActivity.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
