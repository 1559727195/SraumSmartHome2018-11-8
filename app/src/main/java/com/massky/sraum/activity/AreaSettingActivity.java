package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.MyAreaListOwnerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/2/12.
 */

public class AreaSettingActivity extends BaseActivity implements XListView.IXListViewListener {

    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.add_area)
    ImageView add_area;
    @InjectView(R.id.btn_cancel_wangguan)
    Button btn_cancel_wangguan;
    private MyAreaListOwnerAdapter homesettingadapter;
    private DialogUtil dialogUtil;
    private List<Map> areaList = new ArrayList<>();

    @Override
    protected int viewId() {
        return R.layout.area_setting_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_cancel_wangguan.setOnClickListener(this);
        add_area.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        list_hand_scene.add(new HashMap());
        list_hand_scene.add(new HashMap());
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AreaSettingActivity.this.finish();
                break;
            case R.id.btn_cancel_wangguan:
            case R.id.add_area:
                startActivity(new Intent(AreaSettingActivity.this, AddAreaActivity.class));
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    /**
     * 获取所有区域
     */
    private void sraum_getAllArea() {
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(AreaSettingActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllArea();
                    }
                }, AreaSettingActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        areaList = new ArrayList<>();
                        for (int i = 0; i < user.areaList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.areaList.get(i).areaName);
                            mapdevice.put("number", user.areaList.get(i).number);
                            mapdevice.put("sign", user.areaList.get(i).sign);
                            mapdevice.put("authType", user.areaList.get(i).authType);
                            mapdevice.put("roomCount", user.areaList.get(i).roomCount);
                            areaList.add(mapdevice);
                        }

                        if (user.areaList != null && user.areaList.size() != 0) {//区域命名
                            homesettingadapter = new MyAreaListOwnerAdapter(AreaSettingActivity.this, areaList, new MyAreaListOwnerAdapter.RefreshListener() {
                                @Override
                                public void refresh() {//刷新
                                    sraum_getAllArea();//获取所有区域列表
                                }
                            });
                            xListView_scan.setAdapter(homesettingadapter);
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        sraum_getAllArea();//获取所有区域列表
    }

    @Override
    public void onRefresh() {
        onLoad();
        sraum_getAllArea();
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
