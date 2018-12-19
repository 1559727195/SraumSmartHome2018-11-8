package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.adapter.SelectSensorSingleAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/13.
 */

public class SelectSensorActivity extends BaseActivity implements
        AdapterView.OnItemClickListener,
        PullToRefreshLayout.OnRefreshListener {

    private static final int REQUEST_SENSOR = 101;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
   ImageView next_step_txt;
    @InjectView(R.id.refresh_view)
    PullToRefreshLayout refresh_view;
    @InjectView(R.id.maclistview_id)
    ListView maclistview_id;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;

    @InjectView(R.id.hand_txt)
    TextView hand_txt;
    @InjectView(R.id.hand_linear)
    LinearLayout hand_linear;

    private SelectSensorSingleAdapter selectexcutesceneresultadapter;
    private List<Map> list_hand_scene = new ArrayList<>();
    private Handler mHandler = new Handler();
    //    String[] again_elements = {"7", "8", "9",
//            "10", "11", "12", "13", "14", "15", "16"};
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private List<Boolean> list_bool = new ArrayList<>();
    private int position;
    private DialogUtil dialogUtil;
    private String type;

    @Override
    protected int viewId() {
        return R.layout.selection_sensor_lay;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        maclistview_id.setOnItemClickListener(this);
        refresh_view.setOnRefreshListener(this);
        rel_scene_set.setOnClickListener(this);
//        refresh_view.autoRefresh();
        type = (String) getIntent().getSerializableExtra("type");
        if (type != null) {
            hand_txt.setVisibility(View.GONE);
            hand_linear.setVisibility(View.GONE);
        } else {
            hand_txt.setVisibility(View.VISIBLE);
            hand_linear.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onEvent() {

    }


    //7-门磁，8-人体感应，9-水浸检测器，10-入墙PM2.5
    //11-紧急按钮，12-久坐报警器，13-烟雾报警器，14-天然气报警器，15-智能门锁，16-直流电阀机械手
//    R.drawable.magnetic_door_s,
//    R.drawable.human_induction_s, R.drawable.water_s, R.drawable.pm25_s,
//    R.drawable.emergency_button_s
    protected void onData() {

        getOtherDevices("");
        list_hand_scene = new ArrayList<>();

        selectexcutesceneresultadapter = new SelectSensorSingleAdapter(SelectSensorActivity.this,
                list_hand_scene, listint, listintwo, new SelectSensorSingleAdapter.SelectSensorListener() {

            @Override
            public void selectsensor(int position) {
                SelectSensorActivity.this.position = position;
            }
        });
        maclistview_id.setAdapter(selectexcutesceneresultadapter);
//        xListView_scan.setPullLoadEnable(false);
//        xListView_scan.setFootViewHide();
//        xListView_scan.setXListViewListener(this);
    }

    private void setPicture(String type) {
        switch (type) {
            case "7":
                listint.add(R.drawable.icon_menci_40);
                listintwo.add(R.drawable.icon_menci_40_active);
                break;
            case "8":
                listint.add(R.drawable.icon_rentiganying_40);
                listintwo.add(R.drawable.icon_rentiganying_40_active);
                break;
            case "9":
                listint.add(R.drawable.icon_shuijin_40);
                listintwo.add(R.drawable.icon_shuijin_40_active);
                break;
            case "10":
                listint.add(R.drawable.icon_pm25_40);
                listintwo.add(R.drawable.icon_pm25_40_active);
                break;
            case "11":
                listint.add(R.drawable.icon_jinjianniu_40);
                listintwo.add(R.drawable.icon_jinjianniu_40_active);
                break;
            case "12":
                listint.add(R.drawable.icon_rucebjq_40);
                listintwo.add(R.drawable.icon_rucebjq_40_active);
                break;
            case "13":
                listint.add(R.drawable.icon_yanwubjq_40);
                listintwo.add(R.drawable.icon_yanwubjq_40_active);
                break;
            case "14":
                listint.add(R.drawable.icon_ranqibjq_40);
                listintwo.add(R.drawable.icon_ranqibjq_40_active);
                break;
            case "15":
                listint.add(R.drawable.icon_zhinengmensuo_40);
                listintwo.add(R.drawable.icon_zhinengmensuo_40_active);
                break;
            case "16":
                listint.add(R.drawable.defaultpic);
                listintwo.add(R.drawable.defaultpic);
                break;
        }
    }

    /**
     * 获取门磁等第三方设备
     *
     * @param doit
     */
    private void getOtherDevices(final String doit) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(SelectSensorActivity.this));
        String areaNumber = (String) SharedPreferencesUtil.getData(SelectSensorActivity.this, "areaNumber", "");
        mapdevice.put("areaNumber", areaNumber);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getLinkSensor, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                getOtherDevices("load");
            }
        }, SelectSensorActivity.this, dialogUtil) {
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
                list_hand_scene = new ArrayList<>();
                listint.clear();
                listintwo.clear();
                for (int i = 0; i < user.deviceList.size(); i++) {
                    Map<String, String> mapdevice = new HashMap<>();
                    mapdevice.put("name", user.deviceList.get(i).name);
                    mapdevice.put("number", user.deviceList.get(i).number);
                    mapdevice.put("type", user.deviceList.get(i).type);
                    mapdevice.put("boxNumber", user.deviceList.get(i).boxNumber);
                    mapdevice.put("boxName", user.deviceList.get(i).boxName);
//                    if (user.deviceList.get(i).type.equals("10"))
//                        continue;
                    list_hand_scene.add(mapdevice);
                    setPicture(user.deviceList.get(i).type);
                }

                selectexcutesceneresultadapter.setLists(list_hand_scene, listint, listintwo);
                selectexcutesceneresultadapter.notifyDataSetChanged();
                switch (doit) {
                    case "refresh":

                        break;
                    case "load":
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectSensorActivity.this.finish();
                break;
            case R.id.next_step_txt:
                break;
            case R.id.rel_scene_set:
                Intent intent = null;
                Map map_link = new HashMap();
                map_link.put("type", "101");
                map_link.put("deviceType", "");
                map_link.put("deviceId", "");
                map_link.put("name", "手动执行");
                map_link.put("action", "执行");
                map_link.put("condition", "");
                map_link.put("minValue", "");
                map_link.put("maxValue", "");
                map_link.put("boxName", "");
                map_link.put("name1", "手动执行");
                boolean add_condition = (boolean) SharedPreferencesUtil.getData(SelectSensorActivity.this, "add_condition", false);
                if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    AppManager.getAppManager().finishActivity_current(SelectSensorActivity.class);
                    AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                    intent = new Intent(SelectSensorActivity.this, EditLinkDeviceResultActivity.class);
                    intent.putExtra("sensor_map", (Serializable) map_link);
                    startActivity(intent);
                    SelectSensorActivity.this.finish();
                } else {
                    intent = new Intent(SelectSensorActivity.this,
                            SelectiveLinkageActivity.class);
                    intent.putExtra("link_map", (Serializable) map_link);
                    startActivity(intent);
                }
                break;//执行某些手动场景
        }
    }


//    private void onLoad() {
//        xListView_scan.stopRefresh();
//        xListView_scan.stopLoadMore();
//        xListView_scan.setRefreshTime("刚刚");
//    }
//
//    @Override
//    public void onRefresh() {
//        onLoad();
//    }

//    @Override
//    public void onLoadMore() {
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                onLoad();
//            }
//        }, 1000);
//    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        View v = parent.getChildAt(position - maclistview_id.getFirstVisiblePosition());
//        CheckBox cb = (CheckBox) v.findViewById(R.id.checkbox);
//        ImageView img_guan_scene = (ImageView) v.findViewById(R.id.img_guan_scene);
//        TextView panel_scene_name_txt = (TextView) v.findViewById(R.id.panel_scene_name_txt);
//        cb.toggle();
        //设置checkbox现在状态
//        SelectSensorAdapter.getIsSelected().put(position, cb.isChecked());
//        if (cb.isChecked()) {
//            Intent intent = new Intent(SelectSensorActivity.this,  UnderWaterActivity.class);
//            intent.putExtra("type",(Serializable) again_elements[position]);
//            startActivityForResult(intent, REQUEST_SENSOR);
//            img_guan_scene.setImageResource(listintwo.get(position));
//            panel_scene_name_txt.setTextColor(getResources().getColor(R.color.gold_color));
//
//        } else {
//            img_guan_scene.setImageResource(listint.get(position));
//            panel_scene_name_txt.setTextColor(getResources().getColor(R.color.black_color));
//        }

//
//        SelectSensorSingleAdapter.ViewHolderContentType viewHolder = (SelectSensorSingleAdapter.ViewHolderContentType) view.getTag();
//        viewHolder.checkbox.toggle();// 把CheckBox的选中状态改为当前状态的反,gridview确保是单一选
        Intent intent = null;
        String deviceType = (String) list_hand_scene.get(position).get("type");
        String deviceId = (String) list_hand_scene.get(position).get("number");
        String name = (String) list_hand_scene.get(position).get("name");
        String boxName = (String) list_hand_scene.get(position).get("boxName");
        Map map = new HashMap();
        map.put("deviceType", deviceType);
        map.put("deviceId", deviceId);
        map.put("name", name);
        map.put("boxName", boxName);
        map.put("type", "100");
        if (deviceType == null) return;
        switch (deviceType) {
            case "10":
                intent = new Intent(SelectSensorActivity.this, SelectPmOneActivity.class);
                intent.putExtra("map_link", (Serializable) map);
                startActivity(intent);
                break;//pm2.5
            default:
                intent = new Intent(SelectSensorActivity.this, UnderWaterActivity.class);
                intent.putExtra("map_link", (Serializable) map);
                startActivityForResult(intent, REQUEST_SENSOR);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getOtherDevices("refresh");
        pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
