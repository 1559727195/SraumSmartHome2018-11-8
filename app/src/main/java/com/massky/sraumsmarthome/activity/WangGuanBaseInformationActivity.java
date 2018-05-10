package com.massky.sraumsmarthome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/16.
 */

public class WangGuanBaseInformationActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;

    @Override
    protected int viewId() {
        return R.layout.wangguan_baseinfor_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {//"主卫水浸","防盗门门磁","防盗门猫眼","人体监测","防盗门门锁"

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                WangGuanBaseInformationActivity.this.finish();//
                break;
        }
    }
}
