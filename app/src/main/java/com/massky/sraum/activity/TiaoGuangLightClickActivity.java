package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.massky.sraum.R;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.receiver.ApiTcpReceiveHelper;
import com.massky.sraum.service.MyService;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/30.
 */

public class TiaoGuangLightClickActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.seek_one)
    SeekBar seek_one;
    private String progress_now;
    private String status;
    private String number;
    private String type;

    @Override
    protected int viewId() {
        return R.layout.tiaoguanglight_click_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        Map map_item = (Map) getIntent().getSerializableExtra("map_item");
        if (map_item != null) {
            status = (String) map_item.get("status");
            number = (String) map_item.get("number");
            type = (String) map_item.get("type");
            //调光灯，
            progress_now = (String) map_item.get("dimmer");
            seek_one.setProgress(Integer.parseInt(progress_now));
        }
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApiTcpReceiveHelper.TIAO_GUANG_RECEIVE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        seek_one.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return status == "0" ? true : false;//默认情况下，seekbar可以滑动,false时可以滑动
            }
        });


        seek_one.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress_now = progress + "";
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //
                Log.e("robin debug", "seekbar:" + progress_now);
                //在这里提交，控制按钮（APP->网关） （改用TCP）
                Map map = new HashMap();
                map.put("number", number);
                map.put("type", type);
                map.put("status", status);
                map.put("dimmer", "" + progress_now);

//        switch (status) {
//            case "0":
//                //调光灯
////                map.put("status", "1");
//                MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
//                break;
//            case "1":
//                //
////                map.put("status", "0");
//                MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
//                break;
//        }
                MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
            }
        });
    }

    /**
     * 广播接收
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String result = intent.getStringExtra("result");
            ToastUtil.showToast(TiaoGuangLightClickActivity.this, "成功");
        }
    };

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                TiaoGuangLightClickActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
