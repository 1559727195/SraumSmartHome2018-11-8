package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.HistoryBackAdapter;
import com.massky.sraumsmarthome.adapter.HomeSettingAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/2/12.
 */

public class HomeSettingActivity extends BaseActivity implements XListView.IXListViewListener{
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    private HomeSettingAdapter homesettingadapter;

    @Override
    protected int viewId() {
        return R.layout.home_setting_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_cancel_wangguan.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        list_hand_scene.add(new HashMap());
        list_hand_scene.add(new HashMap());
        homesettingadapter = new HomeSettingAdapter(HomeSettingActivity.this,list_hand_scene);
        xListView_scan.setAdapter(homesettingadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HomeSettingActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan:
                startActivity(new Intent(HomeSettingActivity.this,AddHomeActivity.class));
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

}
