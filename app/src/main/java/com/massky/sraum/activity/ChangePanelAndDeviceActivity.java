package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;

import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearLengthEditText;
import com.massky.sraum.widget.ApplicationContext;
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
 * Created by zhu on 2017/10/27.
 */

public class ChangePanelAndDeviceActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    ImageView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    private PopupWindow popupWindow;
    private String device_name;
    @InjectView(R.id.light_control_panel)
    LinearLayout light_control_panel;
    @InjectView(R.id.window_linear)
    LinearLayout window_linear;

    @InjectView(R.id.onekey_device)
    TextView onekey_device;
    @InjectView(R.id.twokey_device)
    TextView twokey_device;
    @InjectView(R.id.threekey_device)
    TextView threekey_device;
    @InjectView(R.id.fourkey_device)
    TextView fourkey_device;

    @InjectView(R.id.onekey_device_txt)
    ClearLengthEditText onekey_device_txt;
    @InjectView(R.id.twokey_device_txt)
    ClearLengthEditText twokey_device_txt;
    @InjectView(R.id.threekey_device_txt)
    ClearLengthEditText threekey_device_txt;
    @InjectView(R.id.fourkey_device_txt)
    ClearLengthEditText fourkey_device_txt;
    @InjectView(R.id.linear_one_only)
    LinearLayout linear_one_only;
    @InjectView(R.id.linear_two_only)
    LinearLayout linear_two_only;
    @InjectView(R.id.linear_three_only)
    LinearLayout linear_three_only;
    @InjectView(R.id.linear_four_only)
    LinearLayout linear_four_only;


    @InjectView(R.id.panelname)
    ClearLengthEditText panelname;

    @InjectView(R.id.findButton_four)
    ImageView findButton_four;

    @InjectView(R.id.findButton_three)
    ImageView findButton_three;
    @InjectView(R.id.findButton_two)
    ImageView findButton_two;
    @InjectView(R.id.findButton_one)
    ImageView findButton_one;

    private List<User.panellist> panelList = new ArrayList<>();
    private DialogUtil dialogUtil;
    private List<User.device> deviceList = new ArrayList<>();
    private String panelType;
    private String panelName;
    private String panelNumber;
    private String deviceNumber;
    private String panelMAC;
    private String onekey_device_txt_str;
    private String twokey_device_txt_str;
    private String threekey_device_txt_str;
    private String fourkey_device_txt_str;
    private int device_index;
    private boolean isPanelAndDeviceSame;
    private String findpaneltype;
    private boolean upgradete;
    private String boxNumber;
    private String input_panel_name_edit_txt_str;
    private String areaNumber;

    @Override
    protected int viewId() {
        return R.layout.changepanel_and_device;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        //根据面板类型，显示不同的设备列表UI
        get_panel_detail();
        //调取面板下的设备信息
//        getPanel_devices();
        show_device_from_panel(panelType);
        panel_and_device_information();
//        save_panel.setOnClickListener(this);
//        backrela_id.setOnClickListener(this);
        panelname.setClearIconVisible(false);
        onekey_device_txt.setClearIconVisible(false);
        twokey_device_txt.setClearIconVisible(false);
        threekey_device_txt.setClearIconVisible(false);
        fourkey_device_txt.setClearIconVisible(false);
//        findpanel.setOnClickListener(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        findButton_four.setOnClickListener(this);
        findButton_three.setOnClickListener(this);
        findButton_two.setOnClickListener(this);
        findButton_one.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    /**
     * 显示面板和设备内容信息
     */
    private void panel_and_device_information() {
        switch (panelType) {
            case "A201"://一灯控
            case "A411"://1窗帘0灯
                onekey_device_txt.setText(deviceList.get(0).name);
                break;
            case "A202"://二灯控
            case "A311":
            case "A412"://1窗帘1灯
            case "A414"://2窗帘0灯
                onekey_device_txt.setText(deviceList.get(0).name);
                twokey_device_txt.setText(deviceList.get(1).name);

                break;
            case "A203"://三灯控
            case "A312":
            case "A321":
            case "A413"://1窗帘2灯
                onekey_device_txt.setText(deviceList.get(0).name);
                twokey_device_txt.setText(deviceList.get(1).name);
                threekey_device_txt.setText(deviceList.get(2).name);
                break;
            case "A204"://四灯控
            case "A313":
            case "A322":
            case "A331":
                onekey_device_txt.setText(deviceList.get(0).name);
                twokey_device_txt.setText(deviceList.get(1).name);
                threekey_device_txt.setText(deviceList.get(2).name);
                fourkey_device_txt.setText(deviceList.get(3).name);
                break;

            case "A301"://一键调光，3键灯控  设备4个
            case "A302"://两键调光，2键灯控
            case "A303"://三键调光，一键灯控
//            case "A304"://四键调光
                onekey_device_txt.setText(deviceList.get(0).name);
                twokey_device_txt.setText(deviceList.get(1).name);
                threekey_device_txt.setText(deviceList.get(2).name);
                fourkey_device_txt.setText(deviceList.get(3).name);//-----
                break;//一键-4键调光

            case "A401"://设备2个
                onekey_device_txt.setText(deviceList.get(0).name);
                twokey_device_txt.setText(deviceList.get(0).name1);
                threekey_device_txt.setText(deviceList.get(0).name2);
                fourkey_device_txt.setText(deviceList.get(1).name);
                //窗帘前3个搞定，最后一个按钮为八键灯控名称修改
                break;//窗帘 ，窗帘第八个按钮为八键灯控名称修改

            case "A501"://空调-设备1个
            case "A511":
            case "A601"://新风
            case "A701"://地暖
            case "A611"://新风
            case "A711"://地暖
            case "A801":
            case "A901":
            case "A902":
            case "AB01":
            case "AB04":
            case "B001":
            case "B201":
            case "AD01":
            case "AC01":
            case "B301":
                onekey_device_txt.setText(deviceList.get(0).name);
                break;
            default:

                break;
            //updateDeviceInfo();
        }
    }

    /**
     * 得到面板信息
     */
    private void get_panel_detail() {
        panelNumber = getIntent().getStringExtra("panelid");
        //         intent.putExtra("boxNumber", gateway_number);
        boxNumber = getIntent().getStringExtra("boxNumber");
        Bundle bundle = getIntent().getBundleExtra("bundle_panel");
        deviceList = (List<User.device>) bundle.getSerializable("deviceList");
        panelType = getIntent().getStringExtra("panelType");
        panelName = getIntent().getStringExtra("panelName");
        panelMAC = getIntent().getStringExtra("panelMAC");
        findpaneltype = getIntent().getStringExtra("findpaneltype");
        areaNumber = getIntent().getStringExtra("areaNumber");
        panelname.setText(panelName);
    }

    /**
     * 根据面板类型显示相应设备信息
     *
     * @param type
     */
    private void show_device_from_panel(String type) { //
        switch (type) {
            case "A201"://一灯控
                show_one_item();
                onekey_device.setText("一路灯控名称");
                break;
            case "A411":
                show_one_item();
                onekey_device.setText("一路窗帘名称");
                break;
            case "A202"://二灯控
                show_two_item();
                onekey_device.setText("一路灯控名称");
                twokey_device.setText("二路灯控名称");
                break;
            case "A412":
                show_two_item();
                onekey_device.setText("一路窗帘名称");
                twokey_device.setText("一路灯控名称");
                break;
            case "A414":
                show_two_item();
                onekey_device.setText("一路窗帘名称");
                twokey_device.setText("二路窗帘名称");
                break;
            case "A311"://二灯控
                show_two_item();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("一路灯控名称");
                break;
            case "A312"://二灯控
                show_three_item();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("一路灯控名称");
                threekey_device.setText("二路灯控名称");
                break;
            case "A413":
                show_three_item();
                onekey_device.setText("一路窗帘名称");
                twokey_device.setText("一路灯控名称");
                threekey_device.setText("二路灯控名称");
                break;
            case "A313"://二灯控
                show_three_item();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("一路灯控名称");
                threekey_device.setText("二路灯控名称");
                fourkey_device.setText("三路灯控名称");
                break;
            case "A321"://二灯控
                show_three_item();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路调光名称");
                threekey_device.setText("一路灯控名称");
                break;
            case "A322"://二灯控
                four_all_show();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路调光名称");
                threekey_device.setText("一路灯控名称");
                fourkey_device.setText("二路灯控名称");
                break;
            case "A331"://二灯控
                four_all_show();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路调光名称");
                threekey_device.setText("三路调光名称");
                fourkey_device.setText("一路灯控名称");
                break;

            case "A203"://三灯控
                show_three_item();
                onekey_device.setText("一路灯控名称");
                twokey_device.setText("二路灯控名称");
                threekey_device.setText("三路灯控名称");
                break;
            case "A204"://四灯控
                four_all_show();
                onekey_device.setText("一路灯控名称");
                twokey_device.setText("二路灯控名称");
                threekey_device.setText("三路灯控名称");
                fourkey_device.setText("四路灯控名称");
                break;//一键-到4键灯控

            case "A301"://一键调光，3键灯控
                four_all_show();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路灯控名称");
                threekey_device.setText("三路灯控名称");
                fourkey_device.setText("四路灯控名称");
                break;

            case "A302"://两键调光，2键灯控
                four_all_show();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路调光名称");
                threekey_device.setText("三路灯控名称");
                fourkey_device.setText("四路灯控名称");
                break;

            case "A303"://三键调光，一键灯控
                four_all_show();
                onekey_device.setText("一路调光名称");
                twokey_device.setText("二路调光名称");
                threekey_device.setText("三路调光名称");
                fourkey_device.setText("四路灯控名称");
                break;

//            case "A304"://四键调光
//                four_all_show();
//                onekey_device.setText("一路调光");
//                twokey_device.setText("二路调光");
//                threekey_device.setText("三路调光");
//                fourkey_device.setText("四路调光");
//                break;//一键-4键调光

            case "A401":
                four_all_show();
                onekey_device.setText("窗帘名称");
                twokey_device.setText("一路窗帘名称");
                threekey_device.setText("二路窗帘名称");
                fourkey_device.setText("八路灯控名称");
                break;//窗帘 ，窗帘第八个按钮为八键灯控名称修改

            case "A501"://空调
                show_one_item();
                onekey_device.setText("空调名称");
                break;
            case "A601"://新风
                show_one_item();
                onekey_device.setText("新风名称");
                break;
            case "A701"://地暖
                show_one_item();
                onekey_device.setText("地暖名称");
            case "A511":
                show_one_item();
                onekey_device.setText("空调名称");
            case "A801":
                show_one_item();
                onekey_device.setText("门磁名称");
                break;
            case "A901":
                show_one_item();
                onekey_device.setText("人体感应名称");
                break;
            case "A902":
                show_one_item();
                onekey_device.setText("地暖名称");
                break;
            case "AB01":
                show_one_item();
                onekey_device.setText("久坐报警器名称");
                break;
            case "AB04":
                show_one_item();
                onekey_device.setText("天然气报警名称");
                break;
            case "B001":
                show_one_item();
                onekey_device.setText("紧急按钮名称");
                break;
            case "B201":
                show_one_item();
                onekey_device.setText("智能门锁名称");
                break;
            case "AD01":
                show_one_item();
                onekey_device.setText("PM2.5名称");
                break;
            case "AC01":
                show_one_item();
                onekey_device.setText("水浸传感器名称");
                break;
            case "B301":
                show_one_item();
                onekey_device.setText("直流电机名称");
                break;
            default:
                break;
        }
    }

    /**
     * 显示三个
     */
    private void show_three_item() {
        onekey_device.setVisibility(View.VISIBLE);
        twokey_device.setVisibility(View.VISIBLE);
        threekey_device.setVisibility(View.VISIBLE);
        fourkey_device.setVisibility(View.GONE);

        onekey_device_txt.setVisibility(View.VISIBLE);
        twokey_device_txt.setVisibility(View.VISIBLE);
        threekey_device_txt.setVisibility(View.VISIBLE);
        fourkey_device_txt.setVisibility(View.GONE);
        linear_one_only.setVisibility(View.VISIBLE);
        linear_two_only.setVisibility(View.VISIBLE);
        linear_three_only.setVisibility(View.VISIBLE);
    }

    /**
     * 显示两个
     */
    private void show_two_item() {
        onekey_device.setVisibility(View.VISIBLE);
        twokey_device.setVisibility(View.VISIBLE);
        threekey_device.setVisibility(View.GONE);
        fourkey_device.setVisibility(View.GONE);

        onekey_device_txt.setVisibility(View.VISIBLE);
        twokey_device_txt.setVisibility(View.VISIBLE);
        threekey_device_txt.setVisibility(View.GONE);
        fourkey_device_txt.setVisibility(View.GONE);
        linear_one_only.setVisibility(View.VISIBLE);
        linear_two_only.setVisibility(View.VISIBLE);
    }

    /**
     * 显示第一个
     */
    private void show_one_item() {
        onekey_device.setVisibility(View.VISIBLE);
        twokey_device.setVisibility(View.GONE);
        threekey_device.setVisibility(View.GONE);
        fourkey_device.setVisibility(View.GONE);

        onekey_device_txt.setVisibility(View.VISIBLE);
        twokey_device_txt.setVisibility(View.GONE);
        threekey_device_txt.setVisibility(View.GONE);
        fourkey_device_txt.setVisibility(View.GONE);
        linear_one_only.setVisibility(View.VISIBLE);
    }

    /**
     * 四个全部显示
     */
    private void four_all_show() {
        onekey_device.setVisibility(View.VISIBLE);
        twokey_device.setVisibility(View.VISIBLE);
        threekey_device.setVisibility(View.VISIBLE);
        fourkey_device.setVisibility(View.VISIBLE);

        onekey_device_txt.setVisibility(View.VISIBLE);
        twokey_device_txt.setVisibility(View.VISIBLE);
        threekey_device_txt.setVisibility(View.VISIBLE);
        fourkey_device_txt.setVisibility(View.VISIBLE);

        linear_one_only.setVisibility(View.VISIBLE);
        linear_two_only.setVisibility(View.VISIBLE);
        linear_three_only.setVisibility(View.VISIBLE);
        linear_four_only.setVisibility(View.VISIBLE);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.save_panel://保存面板信息
//                save_panel();
//                break;
//            case R.id.backrela_id://返回
//                ChangePanelAndDeviceActivity.this.finish();
//                break;
//            case R.id.findpanel://查找面板
//                clickanimation();
//                setFindpanel();
//                break;

            case R.id.back:
                ChangePanelAndDeviceActivity.this.finish();
                break;

            case R.id.btn_login_gateway:
                //
                switch (panelType) {
                    case "A201":
                    case "A202":
                    case "A203":
                    case "A204":
                    case "A301"://
                    case "A302":
                    case "A303":
                    case "A401":

                    case "A311":
                    case "A312":
                    case "A313":
                    case "A321":
                    case "A322"://
                    case "A331":
                        save_panel();
                        break;
                    case "A511":
                    case "A611":
                    case "A711":
                        save_air_model();
                        break;
                    case "B201":
                    case "B101":
                    case "B301":
                    case "A501":
                    case "A801":
                    case "A901":
                    case "AB01":
                    case "A902":
                    case "AB04":
                    case "AC01":
                    case "AD01":
                    case "B001":
                        dialogUtil.loadDialog();
                        input_panel_name_edit_txt_str = panelname.getText().toString().trim() == null
                                || panelname.getText().toString().trim() == "" ? "" : panelname.getText().toString().trim();
                        if (input_panel_name_edit_txt_str.equals("")) {
                            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "设备名称为空");
                        } else {
                            if (panelName.equals(input_panel_name_edit_txt_str)) {
//                                AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                                updateDeviceInfo();//更新设备信息
                            } else {
                                sraum_update_panel_name(input_panel_name_edit_txt_str, panelNumber);//更新面板信息
                            }
                        }
                        break;
                    case "AA02"://WIFI红外模块
                        input_panel_name_edit_txt_str = panelname.getText().toString().trim() == null
                                || panelname.getText().toString().trim() == "" ? "" : panelname.getText().toString().trim();
                        if (input_panel_name_edit_txt_str.equals("")) {
                            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "设备名称为空");
                        } else {

                            if (panelName.equals(input_panel_name_edit_txt_str)) {
                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                            } else {
                                sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str);
                            }
                        }
                        break;
                    case "AA03"://WIFI红外模块
                    case "AA04":
                        input_panel_name_edit_txt_str = panelname.getText().toString().trim() == null
                                || panelname.getText().toString().trim() == "" ? "" : panelname.getText().toString().trim();
                        if (input_panel_name_edit_txt_str.equals("")) {
                            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "设备名称为空");
                        } else {
                            if (panelName.equals(input_panel_name_edit_txt_str)) {
                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                            } else {
                                sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str);
                            }
                        }
                        break;
                    case "202":
                    case "206":
                        input_panel_name_edit_txt_str = panelname.getText().toString().trim() == null
                                || panelname.getText().toString().trim() == "" ? "" : panelname.getText().toString().trim();
                        if (input_panel_name_edit_txt_str.equals("")) {
                            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "设备名称为空");
                        } else {
                            if (panelName.equals(input_panel_name_edit_txt_str)) {
                                AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                            } else {
                                sraum_updateWifiAppleName(panelNumber, input_panel_name_edit_txt_str);
                            }
                        }
                        break;
                }

                break;

            case R.id.findButton_four://找按钮
                sraum_find_outer(3);
                break;
            case R.id.findButton_three:
                sraum_find_outer(2);
                break;
            case R.id.findButton_two:
                sraum_find_outer(1);
                break;
            case R.id.findButton_one:
                sraum_find_outer(0);
                break;
        }
    }

    private void sraum_find_outer(int position) {
        switch (panelType) {
            case "A201"://一灯控
            case "A202"://二灯控
            case "A311":
            case "A203"://三灯控
            case "A312":
            case "A321":
            case "A204"://四灯控
            case "A313":
            case "A322":
            case "A331":
            case "A301"://一键调光，3键灯控  设备4个
            case "A302"://两键调光，2键灯控
            case "A303"://三键调光，一键灯控
                sraum_find_button(deviceList.get(position).number);
                break;
            case "A401"://设备2个
                switch (position) {
                    case 0:
                    case 1:
                    case 2:
                        sraum_find_button(deviceList.get(0).number);
                        break;
                    case 3:
                        sraum_find_button(deviceList.get(1).number);
                        break;
                }
                break;//窗帘 ，窗帘第八个按钮为八键灯控名称修改
            case "A501"://空调-设备1个
            case "A511":
            case "A601"://新风
            case "A701"://地暖
            case "A611"://新风
            case "A711"://地暖
            case "A801":
            case "A901":
            case "A902":
            case "AB01":
            case "AB04":
            case "B001":
            case "B201":
            case "AD01":
            case "AC01":
            case "B301":
                sraum_find_button(deviceList.get(position).number);
                break;
        }
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     */
    private void sraum_updateWifiAppleName(final String number, final String newName) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(this));
//        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this
//                , "areaNumber", "");
        mapdevice.put("areaNumber", areaNumber);
        String method = "";
        switch (panelType) {
            case "AA02":
                method = ApiHelper.sraum_updateWifiAppleName;
                break;
            case "AA03":
            case "AA04":
                method = ApiHelper.sraum_updateWifiCameraName;
                break;
            case "202":
            case "206":
                method = ApiHelper.sraum_updateWifiAppleDeviceName;
                break;
        }
        mapdevice.put("number", number);
        mapdevice.put("name", newName);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(method, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_updateWifiAppleName(number, newName);
            }
        }, ChangePanelAndDeviceActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "更新失败");
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
            public void fourCode() {

            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
            }

            @Override
            public void onSuccess(final User user) {
                switch (panelType) {
                    case "AA02":
                    case "AA03":
                    case "AA04":
                        Map map = new HashMap();
                        map.put("deviceId", panelNumber);
                        map.put("deviceType", panelType);
                        map.put("areaNumber", areaNumber);
                        map.put("type", "2");
                        Intent intent = new Intent(ChangePanelAndDeviceActivity.this, SelectRoomActivity.class);
                        intent.putExtra("map_deivce", (Serializable) map);
                        startActivity(intent);
                        break;
                    case "202":
                    case "206":
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "更新成功");
                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        break;
                }
            }
        });
    }

    /**
     * 保存空调模块
     */
    private void save_air_model() {
        int count_device = deviceList.size();
        //判断面板和设备名称是否相同，相同就不提交
        input_panel_name_edit_txt_str = panelname.getText().toString().trim() == null
                || panelname.getText().toString().trim() == "" ? "" : panelname.getText().toString().trim();
        List<String> list = new ArrayList<>();
        list.add(input_panel_name_edit_txt_str);
        for (int i = 0; i < deviceList.size(); i++) {
            list.add(deviceList.get(i).name);
        }

        //遍历面板和设备名称有相同的吗
        isPanelAndDeviceSame = false;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if ((list.get(i).equals(list.get(j)) && !list.get(i).equals("")
                        && !list.get(j).equals(""))) {
                    isPanelAndDeviceSame = true;
                    break;
                }
            }
        }

        if (!isPanelAndDeviceSame) {
            for (int i = 0; i < count_device + 1; i++) {
                if (list.get(i).equals("")) {
                    isPanelAndDeviceSame = true;
                }
            }

            if (isPanelAndDeviceSame) {
                ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "输入框不能为空");
            } else {
                dialogUtil.loadDialog();
                if (panelName.equals(input_panel_name_edit_txt_str)) {
//                    AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                    updateDeviceInfo();//更新设备信息
                } else {
                    sraum_update_panel_name(input_panel_name_edit_txt_str, panelNumber);//更新面板信息
                }
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息\

            }
        } else {
            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "所输入内容重复");
        }
    }

    private void setFindpanel() {
        dialogUtil.loadDialog();
        sraum_find_panel();
    }

    private void sraum_find_panel() {
        Map<String, Object> map = new HashMap<>();
//        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("gatewayNumber", boxNumber);
        map.put("deviceNumber", panelNumber);
        map.put("token", TokenUtil.getToken(ChangePanelAndDeviceActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_findDevice, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen获取新数据
                        sraum_find_panel();
                    }
                }, ChangePanelAndDeviceActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "操作完成，查看对应面板");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "面板未找到");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "面板未找到");
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    /**
     * 查找按钮
     */
    private void sraum_find_button(String buttonNumber) {
        Map<String, Object> map = new HashMap<>();
//        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("gatewayNumber", boxNumber);
        map.put("deviceNumber", panelNumber);
        map.put("buttonNumber", buttonNumber);
        map.put("token", TokenUtil.getToken(ChangePanelAndDeviceActivity.this));
        MyOkHttp.postMapObject(ApiHelper.sraum_findButton, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen获取新数据
                        sraum_find_panel();
                    }
                }, ChangePanelAndDeviceActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "操作完成，查看对应面板");
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "gatewayNumber 不存在");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "-deviceNumber 不\n" +
                                "存在");
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        //areaNumber 不存在
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "areaNumber 不存在");
                    }

                    @Override
                    public void fiveCode() {
                        //buttonNumber 不存在
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "buttonNumber 不存在");
                    }
                });
    }

    /**
     * 保存面板
     */
    private void save_panel() {
        //                panelmac.setText(panelList.get(i).mac);
//                paneltype.setText(panelList.get(i).type);
//                panelname.setText(panelList.get(i).name);
        String panelName = panelname.getText().toString().trim();
        int count_device = deviceList.size();
        //判断面板和设备名称是否相同，相同就不提交

        onekey_device_txt_str = onekey_device_txt.getText().toString().trim() == null
                || onekey_device_txt.getText().toString().trim() == "" ? "" : onekey_device_txt.getText().toString().trim();
        twokey_device_txt_str = twokey_device_txt.getText().toString().trim() == null
                || twokey_device_txt.getText().toString().trim() == "" ? "" : twokey_device_txt.getText().toString().trim();
        threekey_device_txt_str = threekey_device_txt.getText().toString().trim() == null
                || threekey_device_txt.getText().toString().trim() == "" ? "" : threekey_device_txt.getText().toString().trim();
        fourkey_device_txt_str = fourkey_device_txt.getText().toString().trim() == null
                || fourkey_device_txt.getText().toString().trim() == "" ? "" : fourkey_device_txt.getText().toString().trim();
        List<String> list = new ArrayList<>();
        list.add(panelName);
        list.add(onekey_device_txt_str);
        list.add(twokey_device_txt_str);
        list.add(threekey_device_txt_str);
        list.add(fourkey_device_txt_str);

//                switch (count_device) {
//                    case 1:
//
//                        break;
//                    case 2:
//
//                        break;
//                    case 3:
//
//                        break;
//                    case 4:
//
//                        break;
//                }

        //遍历面板和设备名称有相同的吗
        isPanelAndDeviceSame = false;
        for (int i = 0; i < list.size() - 1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if ((list.get(i).equals(list.get(j)) && !list.get(i).equals("")
                        && !list.get(j).equals(""))) {
                    isPanelAndDeviceSame = true;
                    break;
                }
            }
        }

        if (!isPanelAndDeviceSame) {
            for (int i = 0; i < count_device + 1; i++) {
                if (list.get(i).equals("")) {
                    isPanelAndDeviceSame = true;
                }
            }

            if (isPanelAndDeviceSame) {
                ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "输入框不能为空");
                return;
            } else {
                dialogUtil.loadDialog();
                sraum_update_panel_name(panelName, panelNumber);//更新面板信息
                //更新面板下的设备列表信息
//                int count_device = deviceList.size();
                //updateDeviceInfo();//更新设备信息
            }
        } else {
            ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "所输入内容重复");
            return;
        }
    }

    /**
     * 更新设备信息
     */
    private void updateDeviceInfo() {
        switch (panelType) {
            case "A201"://一灯控
            case "A411"://1窗帘0灯
                onekey_device_txt_str = onekey_device_txt.getText().toString().trim();
                device_index = 0;
                control_device_name_change_one(onekey_device_txt_str, 0);
                break;
            case "A202"://二灯控
            case "A311"://二灯控
            case "A412"://1窗帘1灯
            case "A414"://2窗帘0灯
                onekey_device_txt_str = onekey_device_txt.getText().toString().trim();
                twokey_device_txt_str = twokey_device_txt.getText().toString().trim();
                device_index = 1;
                control_device_name_change_one(onekey_device_txt_str, 0);//从0 -1 开始
                break;
            case "A203"://三灯控
            case "A312"://二灯控
            case "A321"://二灯控
            case "A413"://1窗帘2灯
                onekey_device_txt_str = onekey_device_txt.getText().toString().trim();
                twokey_device_txt_str = twokey_device_txt.getText().toString().trim();
                threekey_device_txt_str = threekey_device_txt.getText().toString().trim();
                device_index = 2;
                control_device_name_change_one(onekey_device_txt_str, 0);//从0 - 2开始
                break;
            case "A204"://四灯控
            case "A313"://二灯控
            case "A322"://二灯控
            case "A331"://二灯控
                onekey_device_txt_str = onekey_device_txt.getText().toString().trim();
                twokey_device_txt_str = twokey_device_txt.getText().toString().trim();
                threekey_device_txt_str = threekey_device_txt.getText().toString().trim();
                fourkey_device_txt_str = fourkey_device_txt.getText().toString().trim();
                device_index = 3;
                control_device_name_change_one(onekey_device_txt_str, 0);//从0-3开始
                break;
            case "A301"://一键调光，3键灯控  设备4个
            case "A302"://两键调光，2键灯控
            case "A303"://三键调光，一键灯控
//            case "A304"://四键调光
//                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(0).number, "");
//                updateDeviceInfo(twokey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(1).number, "");
//                updateDeviceInfo(threekey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(2).number, "");
//                updateDeviceInfo(fourkey_device_txt.getText().toString().trim(), "", "",
//                        deviceList.get(3).number, "");
                onekey_device_txt_str = onekey_device_txt.getText().toString().trim();
                twokey_device_txt_str = twokey_device_txt.getText().toString().trim();
                threekey_device_txt_str = threekey_device_txt.getText().toString().trim();
                fourkey_device_txt_str = fourkey_device_txt.getText().toString().trim();
                device_index = 3;
                control_device_name_change_one(onekey_device_txt_str, 0);//从0-3开始
//                control_device_name_change(twokey_device_txt_str,1);
//                control_device_name_change(threekey_device_txt_str,2);
//                control_device_name_change(fourkey_device_txt_str,3);

                break;//一键-4键调光

            case "A401"://设备2个

                final String customName = onekey_device_txt.getText().toString().trim();
                final String name1 = twokey_device_txt.getText().toString().trim();
                final String name2 = threekey_device_txt.getText().toString().trim();
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String deviceNumber = deviceList.get(0).number;
                        updateDeviceInfo(customName, name1, name2, deviceNumber, "窗帘前3", 0);
                    }
                }).start();


                //窗帘前3个搞定，最后一个按钮为八键灯控名称修改
                break;//窗帘 ，窗帘第八个按钮为八键灯控名称修改

            case "A501"://空调-设备1个
                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
                        deviceList.get(0).number, "", 0);
                break;
            case "A601"://新风
                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
                        deviceList.get(0).number, "", 0);
                break;
            case "A701"://地暖
                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
                        deviceList.get(0).number, "", 0);
                break;
            case "A801":
            case "A901":
            case "A902":
            case "AB01":
            case "AB04":
            case "B001":
            case "B201":
            case "AD01":
            case "AC01":
            case "B301":
                updateDeviceInfo(onekey_device_txt.getText().toString().trim(), "", "",
                        deviceList.get(0).number, "", 0);
                break;

            default:
                break;
//                updateDeviceInfo();
        }
    }

    /**
     * 窗帘第八键设备控制
     *
     * @param chuanglian
     */
    private void select_window_bajian(final String chuanglian) {
        deviceNumber = deviceList.get(1).number; //
        final String customName_window = fourkey_device_txt.getText().toString().trim();
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateDeviceInfo(customName_window, "", "", deviceNumber, chuanglian, 0);
            }
        }).start();
    }

    /**
     * 第一个设备控制
     *
     * @param device_name
     * @param index
     */
    private void control_device_name_change_one(final String device_name, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index <= deviceList.size() - 1) //
                    updateDeviceInfo(device_name, "", "",
                            deviceList.get(index).number, "", index);
            }
        }).start();
    }

    /**
     * 第三个设备控制
     *
     * @param device_name
     * @param index
     */
    private void control_device_name_change_three(final String device_name, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index <= deviceList.size() - 1) //
                    updateDeviceInfo(device_name, "", "",
                            deviceList.get(index).number, "", index);
            }
        }).start();
    }

    /**
     * 第二个设备控制
     *
     * @param device_name
     * @param index
     */
    private void control_device_name_change_two(final String device_name, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index <= deviceList.size() - 1) //
                    updateDeviceInfo(device_name, "", "",
                            deviceList.get(index).number, "", index);
            }
        }).start();
    }

    /**
     * 第四个设备控制
     *
     * @param device_name
     * @param index
     */
    private void control_device_name_change_four(final String device_name, final int index) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (index <= deviceList.size() - 1) //
                    updateDeviceInfo(device_name, "", "",
                            deviceList.get(index).number, "", index);
            }
        }).start();
    }

    private void updateDeviceInfo(String customName, String name1, String name2, String deviceNumber, String chuanglian, int index) {
        sraum_update_s(customName, name1, name2, deviceNumber, chuanglian, index);
    }

    private void sraum_update_s(final String customName, final String name1, final String name2, final String deviceNumber, final String chuanglian, final int index) {
        Map<String, Object> map = new HashMap<>();
//        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(ChangePanelAndDeviceActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("gatewayNumber", boxNumber);
        map.put("deviceNumber", deviceNumber);
        map.put("newName", customName);
        if (chuanglian.equals("窗帘前3")) {
            map.put("newName1", name1);
            map.put("newName2", name2);
        }

        MyOkHttp.postMapObject(ApiHelper.sraum_updateButtonName, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_update_s(customName, name1, name2, deviceNumber, chuanglian, index);
            }
        }, ChangePanelAndDeviceActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                select_device_byway("不存在");
            }

            @Override
            public void threeCode() {
                super.threeCode();
//                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "设备编号不正确");
                select_device_byway("设备编号不正确");
            }

            @Override
            public void fourCode() {
                super.fourCode();
//                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "自定义名称重复");
                select_device_byway("自定义名称重复");
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
//                ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, "上传成功");
                select_device_byway("上传成功");
            }

            /**
             * 选设备4-1，依次执行
             * @param content
             */
            private void select_device_byway(String content) {//失败就提示添加失败名称，和失败类型
                if (chuanglian.equals("窗帘前3")) {
                    if (content.equals("上传成功")) {//当前不成功，就不继续加下去了
                        select_window_bajian("窗帘第八键");//控制窗帘第八键
                    } else {
                        ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, customName + ":" + content);
                    }
                    return;
                }

                //窗帘第八键
                if (chuanglian.equals("窗帘第八键")) {
                    if (content.equals("上传成功")) {//当前不成功，就不继续加下去了
                        //
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "更新成功");
                        switch (findpaneltype) {
                            case "fastedit"://快速编辑
                                AppManager.getAppManager().finishActivity_current(FastEditPanelActivity.class);
                                break;
                            case "wangguan_status":
                                break;
                        }
//                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        Map map = new HashMap();
                        map.put("deviceId", panelNumber);
                        map.put("deviceType", panelType);
                        map.put ("areaNumber", areaNumber);
                        map.put("type", "1");
                        Intent intent = new Intent(ChangePanelAndDeviceActivity.this, SelectRoomActivity.class);
                        intent.putExtra("map_deivce", (Serializable) map);
                        startActivity(intent);
                        //添加完，添加完设备区关联房间
                    } else {
                        ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, customName + ":" + content);
                    }
                    return;
                }

                int index_now = index;
                index_now++;
                if (content.equals("上传成功")) {//当前不成功，就不继续加下去了
                    switch (index_now) {
                        case 0://调1
                            control_device_name_change_one(onekey_device_txt_str, 0);
                            //control_device_name_change_one(onekey_device_txt_str,3);
                            break;
                        case 1:
                            control_device_name_change_two(twokey_device_txt_str, 1);
                            break;
                        case 2:
                            control_device_name_change_three(threekey_device_txt_str, 2);
                            break;
                        case 3:
                            control_device_name_change_four(fourkey_device_txt_str, 3);
                            break;
                    }

                    if (index_now > device_index) {
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, "更新成功");
                        switch (findpaneltype) {
                            case "fastedit"://快速编辑
                                AppManager.getAppManager().finishActivity_current(FastEditPanelActivity.class);
                                break;
                            case "wangguan_status":
                                break;
                        }
//                        ChangePanelAndDeviceActivity.this.finish();//修改完毕
//                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                        //添加完设备区关联房间
                        Map map = new HashMap();
                        map.put("deviceId", panelNumber);
                        map.put("deviceType", panelType);
                        map.put ("areaNumber", areaNumber);
                        map.put("type", "1");
                        Intent intent = new Intent(ChangePanelAndDeviceActivity.this, SelectRoomActivity.class);
                        intent.putExtra("map_deivce", (Serializable) map);
                        startActivity(intent);
                        return;
                    }
                } else {
                    ToastUtil.showDelToast(ChangePanelAndDeviceActivity.this, customName + ":" + content);
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    private void sraum_update_panel_name(final String panelName, final String panelNumber) {
        Map<String, Object> map = new HashMap<>();
//        String areaNumber = (String) SharedPreferencesUtil.getData(ChangePanelAndDeviceActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(ChangePanelAndDeviceActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("gatewayNumber", boxNumber);
        map.put("deviceNumber", panelNumber);
        map.put("newName", panelName);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateDeviceName, map,
                new Mycallback(new AddTogglenInterfacer() {//刷新togglen获取新数据
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_update_panel_name(panelName, panelNumber);
                    }
                }, ChangePanelAndDeviceActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        ChangePanelAndDeviceActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
//                        ChangePanelAndDeviceActivity.this.finish();
//                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, panelName+":"+"面板名字更新成功");
                        updateDeviceInfo();//更新设备信息
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, panelName + ":" + "面板编号不正确");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(ChangePanelAndDeviceActivity.this, panelName + ":" + "面板名字已存在");
                    }
                });
    }

//    private void clickanimation() {
//        Animation clickAnimation = AnimationUtils.loadAnimation(this, R.anim.scale_animation_small);
//        findpanel.startAnimation(clickAnimation);
//
//        //如果你想要点下去然后弹上来就这样
//        clickAnimation.setAnimationListener(new Animation.AnimationListener() {
//
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                //动画执行完后的动作
//                findpanel.clearAnimation();
//                Animation clickAnimation = AnimationUtils.loadAnimation(ChangePanelAndDeviceActivity.this, R.anim.scale_animation_big);
//                findpanel.startAnimation(clickAnimation);
//            }
//        });
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
