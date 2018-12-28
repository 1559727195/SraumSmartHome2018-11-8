package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/6/20.
 */

public class SetSelectLinkActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private TimePickerView pvCustomTime;
    @InjectView(R.id.sleep_time_rel)
    RelativeLayout sleep_time_rel;
    @InjectView(R.id.get_up_rel)
    RelativeLayout get_up_rel;
    //    @InjectView(R.id.sleep_time_txt)
//    TextView sleep_time_txt;
//    @InjectView(R.id.get_up_time_txt)
//    TextView get_up_time_txt;
    private String hourPicker;
    private String minutePicker;
    String time_content;
    @InjectView(R.id.start_time_txt)
    TextView start_time_txt;
    @InjectView(R.id.end_time_txt)
    TextView end_time_txt;
    private int index_select;
    @InjectView(R.id.link_name_edit)
    ClearEditText link_name_edit;
    @InjectView(R.id.time_select_linear)
    LinearLayout time_select_linear;
    private DialogUtil dialogUtil;
    private List<Map> list_condition = new ArrayList<>();
    private List<Map> list_result = new ArrayList<>();
    private String startTime = "";
    private String endTime = "";
    private boolean is_editlink;
    private String linkId = "";
    private Map link_information = new HashMap();
    private String linkName = "";
    private String type;

    @Override
    protected int viewId() {
        return R.layout.set_select_link_lay;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//        linkId = (String) getIntent().getSerializableExtra("linkId");

        init_get();
        init_data();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_get() {
        link_information = (Map) getIntent().getSerializableExtra("link_information");
//        String link_edit = (String) link_information.get("linkId");
//        map.put("link_edit", true);
//        map.put("linkId", list.get(position).id);
//        map.put("linkName", list.get(position).name);
////                intent.putExtra("link_edit", true);
////                intent.putExtra("linkId", list.get(position).id);
//        intent.putExtra("link_information", (Serializable) map);
        if (link_information != null) {//来自联动列表的编辑按钮
//            list_result = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_result");
//            list_condition = SharedPreferencesUtil.getInfo_List(EditLinkDeviceResultActivity.this, "list_condition");
            //获取接口的联动列表信息，设备联动信息，sraum_deviceLinkInfo，
//            linkId = (String) getIntent().getSerializableExtra("linkId");
            linkId = (String) link_information.get("linkId");
            linkName = (String) link_information.get("linkName");
            startTime = (String) link_information.get("startTime");
            endTime = (String) link_information.get("endTime");
            start_time_txt.setText(startTime);
            end_time_txt.setText(endTime);
            link_name_edit.setText(linkName);
        } else {
            linkId = (String) SharedPreferencesUtil.getData(SetSelectLinkActivity.this, "linkId", "");
        }
    }

    private void init_data() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        initCustomTimePicker();
        sleep_time_rel.setOnClickListener(this);
        get_up_rel.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        list_result = SharedPreferencesUtil.getInfo_List(SetSelectLinkActivity.this, "list_result");
        list_condition = SharedPreferencesUtil.getInfo_List(SetSelectLinkActivity.this, "list_condition");
        type = (String) list_condition.get(0).get("type");
        switch (type) {
            case "100"://自动执行条件
                time_select_linear.setVisibility(View.VISIBLE);
                break;
            case "101"://手动执行条件
                time_select_linear.setVisibility(View.GONE);
                break;
        }
        startTime = start_time_txt.getText().toString();
        endTime = end_time_txt.getText().toString();
        is_editlink = (boolean) SharedPreferencesUtil.getData(SetSelectLinkActivity.this, "editlink", false);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SetSelectLinkActivity.this.finish();
                break;
            case R.id.next_step_txt://场景设置
                startTime = start_time_txt.getText().toString();
                endTime = end_time_txt.getText().toString();
                String content = link_name_edit.getText().toString();
                if (content == null || content.equals("")) {
                    ToastUtil.showToast(SetSelectLinkActivity.this, "请输入联动名称");
                } else {
                    if (is_editlink) {//说明联动要更新了，编辑更新
                        switch (type) {
                            case "100"://自动执行条件
                                getData(is_editlink, content, linkId, ApiHelper.sraum_updateDeviceLink);
                                break;
                            case "101"://手动执行条件
                                set_Hand_Data(is_editlink, content, linkId, ApiHelper.sraum_editManuallyScene
                                );
                                break;
                        }
                    } else {//直接设置联动

                        switch (type) {
                            case "100"://自动执行条件
                                getData(is_editlink, content, linkId, ApiHelper.sraum_setDeviceLink);
                                break;
                            case "101"://手动执行条件
                                set_Hand_Data(is_editlink, content, linkId, ApiHelper.sraum_addManuallyScene
                                );
                                break;
                        }
                    }
//                    SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_result", new ArrayList<Map>());
//                    SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_condition", new ArrayList<Map>());
//                }
                }
                break;
            case R.id.sleep_time_rel://睡觉
                index_select = 1;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
            case R.id.get_up_rel://起床
                index_select = 0;
                pvCustomTime.show(); //弹出自定义时间选择器
                break;
        }
    }

    private void initCustomTimePicker() {

        /**
         * @description
         *
         * 注意事项：
         * 1.自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针.
         * 具体可参考demo 里面的两个自定义layout布局。
         * 2.因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
         * setRangDate方法控制起始终止时间(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
         */
        Calendar selectedDate = Calendar.getInstance();//系统当前时间
        Calendar startDate = Calendar.getInstance();
        startDate.set(2014, 1, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2027, 2, 28);
        //时间选择器 ，自定义布局
        pvCustomTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                getTime(date);
                Log.e("robin debug", "getTime(date):" + getTime(date));
                hourPicker = String.valueOf(date.getHours());
                minutePicker = String.valueOf(date.getMinutes());
                switch (String.valueOf(minutePicker).length()) {
                    case 1:
                        minutePicker = "0" + minutePicker;
                        break;
                }

                switch (String.valueOf(hourPicker).length()) {
                    case 1:
                        hourPicker = "0" + hourPicker;
                        break;
                }

                time_content = hourPicker + ":" + minutePicker;
                handler.sendEmptyMessage(index_select);
//                start_scenetime.setText(hourPicker + ":" + minutePicker);
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final ImageView tvSubmit = (ImageView) v.findViewById(R.id.ok_bt);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.finish_bt);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.returnData();
                                pvCustomTime.dismiss();
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomTime.dismiss();
                            }
                        });
                    }
                })
                .setContentSize(18)
                .setType(new boolean[]{false, false, false, true, true, false})
                .setLabel("年", "月", "日", "小时", "分钟", "秒")
                .setLineSpacingMultiplier(1.2f)
                .setTextXOffset(0, 0, 0, 0, 0, 0)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();
    }

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    start_time_txt.setText(time_content);
                    break;//开始
                case 1:
                    end_time_txt.setText(time_content);
                    break;//结束
            }
        }
    };

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 添加手动场景
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private void set_Hand_Data(final boolean flag, final String linkName, final String linkId, final String apiname) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(SetSelectLinkActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(SetSelectLinkActivity.this));
        map.put("areaNumber", areaNumber);

        if (flag) {
            map.put("number", linkId);
        } else {
            map.put("sceneName", linkName);
            map.put("sceneType", "1");
            map.put("panelNumber", "");
            map.put("buttonNumber", "");
        }
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < list_result.size(); i++) {
            Map map_device = new HashMap();
            map_device.put("type", list_result.get(i).get("type"));
            map_device.put("number", list_result.get(i).get("number"));
            map_device.put("status", list_result.get(i).get("status"));
            map_device.put("dimmer", list_result.get(i).get("dimmer"));
            map_device.put("mode", list_result.get(i).get("mode"));
            map_device.put("temperature", list_result.get(i).get("temperature"));
            map_device.put("speed", list_result.get(i).get("speed"));
            map_device.put("panelMac", list_result.get(i).get("panelMAC") == null ? "" : list_result.get(i).get("panelMAC"));
            map_device.put("gatewayMac", list_result.get(i).get("gatewayMAC") == null ? "" : list_result.get(i).get("gatewayMAC"));
            list.add(map_device);
        }
        map.put("deviceList", list);
        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(apiname, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(flag, linkName, linkId, apiname);
                    }
                }, SetSelectLinkActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                        common();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        common();
                        String boxNumber = user.boxNumber;
//                      手动场景 添加的时候我会返回一个boxNumber,这个参数有值，代表你加的是网关场景，然后你可以做关联面板，这个参数没有值，那就是云场景
//                                没有关联面板这个操作
                        if (boxNumber != null && !boxNumber.equals("")) {//去关联面板
                            Bundle bundle1 = new Bundle();
                            bundle1.putString("sceneName", linkName);
                            bundle1.putString("sceneType", "1");
                            bundle1.putString("boxNumber", boxNumber);
                            bundle1.putString("panelType", "");
                            bundle1.putString("panelNumber", "");
                            bundle1.putString("buttonNumber", "");
                            IntentUtil.startActivity(SetSelectLinkActivity.this, AssociatedpanelActivity.class, bundle1);

                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        common();
                    }

                    @Override
                    public void pullDataError() {
                        common();
                    }

                    @Override
                    public void sevenCode() {
                        common();
                    }

                    @Override
                    public void fiveCode() {
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"添加失败（硬件\n" +
                                "返回）");
                    }

                    @Override
                    public void fourCode() {
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"设备列表信息有误");
                    }

                    @Override
                    public void threeCode() {
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"sceneName 已存在");
                    }

                    @Override
                    public void wrongBoxnumber() {
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"areaNumber\n" +
                                "不存在");
                    }
                });
    }


    /**
     * 添加自动场景
     *
     * @param flag
     * @param linkName
     * @param linkId
     * @param apiname
     */
    private void getData(final boolean flag, final String linkName, final String linkId, final String apiname) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(SetSelectLinkActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(SetSelectLinkActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("deviceId", list_condition.get(0).get("deviceId"));
        map.put("deviceType", list_condition.get(0).get("deviceType"));
        map.put("linkName", linkName);
        map.put("type", list_condition.get(0).get("type"));
        map.put("condition", list_condition.get(0).get("condition"));
        map.put("minValue", list_condition.get(0).get("minValue"));
        map.put("maxValue", list_condition.get(0).get("maxValue"));
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        if (flag) {
            map.put("linkId", linkId);
        } else {

        }
        List<Map> list = new ArrayList<>();
        for (int i = 0; i < list_result.size(); i++) {
            Map map_device = new HashMap();
            map_device.put("type", list_result.get(i).get("type"));
            map_device.put("number", list_result.get(i).get("number"));
            map_device.put("status", list_result.get(i).get("status"));
            map_device.put("dimmer", list_result.get(i).get("dimmer"));
            map_device.put("mode", list_result.get(i).get("mode"));
            map_device.put("temperature", list_result.get(i).get("temperature"));
            map_device.put("speed", list_result.get(i).get("speed"));
            list.add(map_device);
        }
        map.put("deviceList", list);

        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(apiname, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getData(flag, linkName, linkId, apiname);
                    }
                }, SetSelectLinkActivity.this, dialogUtil) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
//                        refresh_view.stopRefresh(false);
                        common();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        common();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"areaNumber 错\n" +
                                "误");
                    }

                    @Override
                    public void pullDataError() {
                        common();
                    }

                    @Override
                    public void sevenCode() {
                        common();
                    }

                    @Override
                    public void fiveCode() {
                        common();
                    }

                    @Override
                    public void fourCode() {
                        common();
                    }

                    @Override
                    public void threeCode() {
                        common();
                        ToastUtil.showToast(SetSelectLinkActivity.this,"deviceId 错误");
                    }

                    @Override
                    public void wrongBoxnumber() {
                        common();
                    }
                });
    }

    /**
     * 共同执行的代码
     */
    private void common() {
        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
//        Intent intent = new Intent(SetSelectLinkActivity.this, LinkageListActivity.class);
//        startActivity(intent);
        SetSelectLinkActivity.this.finish();
        SharedPreferencesUtil.saveData(SetSelectLinkActivity.this, "linkId", "");
        SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_result", new ArrayList<Map>());
        SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "list_condition", new ArrayList<Map>());
        SharedPreferencesUtil.saveData(SetSelectLinkActivity.this, "editlink", false);
        SharedPreferencesUtil.saveInfo_List(SetSelectLinkActivity.this, "link_information_list", new ArrayList<Map>());
    }
}
