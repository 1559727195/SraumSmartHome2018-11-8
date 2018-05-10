package com.massky.sraumsmarthome.activity;

import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.HandSceneAdapter;
import com.massky.sraumsmarthome.adapter.HistoryBackAdapter;
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

public class HistoryBackActivity extends BaseActivity implements XListView.IXListViewListener{
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private HistoryBackAdapter historybackdapter;
    @InjectView(R.id.back)
    ImageView back;

    @Override
    protected int viewId() {
        return R.layout.history_back_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        list_hand_scene.add(new HashMap());
        historybackdapter = new HistoryBackAdapter(HistoryBackActivity.this,list_hand_scene);
        xListView_scan.setAdapter(historybackdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HistoryBackActivity.this.finish();
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
