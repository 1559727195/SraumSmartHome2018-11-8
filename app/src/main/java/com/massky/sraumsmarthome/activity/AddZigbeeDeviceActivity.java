package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.RoundProgressBar;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/3.
 */

public class AddZigbeeDeviceActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.roundProgressBar2)
    RoundProgressBar roundProgressBar2;
    private boolean is_index = true;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    private String device_name;

    @Override
    protected int viewId() {
        return R.layout.add_zigbee_dev_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }

        init_status_bar();
        device_name = (String) getIntent().getSerializableExtra("device_name");
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
                is_index = false;
                AddZigbeeDeviceActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan:
                is_index = false;
                AddZigbeeDeviceActivity.this.finish();
                break;
        }
    }

    private void init_status_bar() {
        roundProgressBar2.setMax(6);
        final int[] index = {6};
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (is_index) {
                    index[0]--;
                    try {
                        Thread.sleep(1000);
                        roundProgressBar2.setProgress(i++);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (index[0] < 0) {
                        AddZigbeeDeviceActivity.this.finish();
                        //添加灯控面板成功
                       // device_name
                        Intent intent = new Intent(AddZigbeeDeviceActivity.this
                                , AddDeviceScucessActivity.class);
                        intent.putExtra("device_name",device_name);
                        startActivity(intent);
                        //停止添加网关
                        is_index = false;
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_index = false;
    }
}
