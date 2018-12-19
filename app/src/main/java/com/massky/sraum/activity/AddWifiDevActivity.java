package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/5/30.
 */

public class AddWifiDevActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_id)
    Button next_step_id;
    @InjectView(R.id.img_show_zigbee)
    ImageView img_show_zigbee;
    @InjectView(R.id.txt_title)
    TextView txt_title;

    private int[] icon_wifi = {
            R.drawable.pic_wifi_hongwai
    };

    private String type = "";

    @Override
    protected int viewId() {
        return R.layout.add_wifi_dev_act_new;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }

        type = (String) getIntent().getSerializableExtra("type");
        switch (type) {
            case "101":
                img_show_zigbee.setImageResource(R.drawable.pic_wifi_shexiangtou);
                txt_title.setText("请将摄像机按住Reset键10秒左右恢复出厂设置，恢复成功后会有提示音(复位完成)，即将重启设备");
                break;
            case "103":
                img_show_zigbee.setImageResource(R.drawable.pic_wifi_dianzimenling);
                txt_title.setText("请将可视门铃按住Reset键10秒左右恢复出厂设置，恢复成功后会有提示音(复位完成)，即将重启设备");
                break;
            case "hongwai":
                img_show_zigbee.setImageResource(icon_wifi[0]);
                break;
            case "102":

                break;
            case "yaokong":
                img_show_zigbee.setImageResource(icon_wifi[0]);
                break;
        }
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {

        Intent intent = null;
        switch (v.getId()) {

            case R.id.back:
                AddWifiDevActivity.this.finish();
                break;
            case R.id.next_step_id:
                switch (type) {
                    case "101":
                    case "103":
                        intent = new Intent(AddWifiDevActivity.this, ConnWifiCameraActivity.class);
                        intent.putExtra("type", type);
                        startActivity(intent);
                        break;
                    case "hongwai":
                        intent = new Intent(AddWifiDevActivity.this, ConnWifiActivity.class);
                        startActivity(intent);
                        break;
                    case "102":

                        break;
                    case "yaokong":

                        break;
                }
        }
    }
}
