package com.massky.sraumsmarthome.activity;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.DeviceListAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/23.
 */

public class DeviceListActivity extends BaseActivity implements XListView.IXListViewListener{

    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private  String [] autoElements  = {"客厅开关","卧室开关"
            ,"PM2.5","PM检测","卧室窗帘"
    };
    private List<Map> list_hand_scene;
    private DeviceListAdapter devicelistadapter;

    @Override
    protected int viewId() {
        return R.layout.device_list_act;
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
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
            for (int i = 0; i < autoElements.length; i++) {
            Map map = new HashMap();
            map.put("name", autoElements[i]);
            list_hand_scene.add(map);
        }
        devicelistadapter = new DeviceListAdapter(
                DeviceListActivity.this,
                list_hand_scene);
        xListView_scan.setAdapter(devicelistadapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setFootViewHide();
        xListView_scan.setXListViewListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                DeviceListActivity.this.finish();
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
