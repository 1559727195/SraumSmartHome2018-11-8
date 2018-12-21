package com.massky.sraum.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.ipcamera.demo.BaseActivity;
import com.ipcamera.demo.BridgeService;
import com.ipcamera.demo.PlayBackTFActivity;
import com.ipcamera.demo.SettingSDCardActivity;
import com.ipcamera.demo.bean.AlermBean;
import com.ipcamera.demo.utils.ContentCommon;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.fragment.MyEvent;
import com.massky.sraum.widget.SlideSwitchButton;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import com.ypy.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import androidx.percentlayout.widget.PercentRelativeLayout;
import butterknife.InjectView;
import vstc2.nativecaller.NativeCaller;


/**
 * Created by zhu on 2018/1/16.
 */

public class MyDeviceItemActivity extends com.massky.sraum.base.BaseActivity implements SlideSwitchButton.SlideListener
        , BridgeService.AlarmInterface {
    public static final String MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE = "com.sraum.massky.from.mydevice";
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private Map panelItem_map = new HashMap();
    @InjectView(R.id.device_name_txt)
    TextView device_name_txt;
    @InjectView(R.id.mac_txt)
    TextView mac_txt;
    @InjectView(R.id.status_txt)
    TextView status_txt;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.wangguan_set_rel)
    RelativeLayout wangguan_set_rel;
    @InjectView(R.id.scene_list_rel)
    RelativeLayout scene_list_rel;
    @InjectView(R.id.list_gujian_rel)
    PercentRelativeLayout list_gujian_rel;
    @InjectView(R.id.banben_txt)
    TextView banben_txt;
    @InjectView(R.id.panid_txt)
    TextView panid_txt;
    @InjectView(R.id.xindao_txt)
    TextView xindao_txt;
    @InjectView(R.id.gateway_id_txt)
    TextView gateway_id_txt;
    @InjectView(R.id.other_jiantou)
    ImageView other_jiantou;
    @InjectView(R.id.delete_device_rel)
    RelativeLayout delete_device_rel;
    private DialogUtil dialogUtil;
    private String panelNumber = "";
    @InjectView(R.id.next_step_id)
    Button next_step_id;
    @InjectView(R.id.wangguan_set)
    ImageView wangguan_set;
    @InjectView(R.id.rel_yaokongqi)
    RelativeLayout rel_yaokongqi;
    @InjectView(R.id.view_yaokongqi)
    View view_yaokongqi;
    @InjectView(R.id.dev_txt)
    TextView dev_txt;
    @InjectView(R.id.slide_btn)
    SlideSwitchButton slide_btn;
    @InjectView(R.id.view_bufang)
    View view_bufang;
    @InjectView(R.id.rel_bufang)
    RelativeLayout rel_bufang;

    @InjectView(R.id.view_bufang_baojing)
    View view_bufang_baojing;
    @InjectView(R.id.rel_bufang_baojing)
    RelativeLayout rel_bufang_baojing;

    @InjectView(R.id.view_bufang_plan)
    View view_bufang_plan;
    @InjectView(R.id.rel_bufang_plan)
    RelativeLayout rel_bufang_plan;
    //slide_btn_baojing
    @InjectView(R.id.slide_btn_baojing)
    SlideSwitchButton slide_btn_baojing;

    @InjectView(R.id.view_sd_set)
    View view_sd_set;
    @InjectView(R.id.rel_sd_set)
    RelativeLayout rel_sd_set;

    @InjectView(R.id.view_sdcard_remotevideo)
    View view_sdcard_remotevideo;
    @InjectView(R.id.rel_sdcard_remotevideo)
    RelativeLayout rel_sdcard_remotevideo;


    @InjectView(R.id.slide_btn_plan)
    SlideSwitchButton slide_btn_plan;
    private final int ALERMPARAMS = 3;

    private int[] iconName = {R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang, R.string.lianglutiaoguang, R.string.sanlutiao, R.string.window_panel, R.string.air_panel,
            R.string.air_mode, R.string.xinfeng_mode, R.string.dinuan_mode
            , R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.jixieshou, R.string.cha_zuo_1, R.string.cha_zuo, R.string.wifi_hongwai,
            R.string.wifi_camera, R.string.one_light_control, R.string.two_light_control, R.string.three_light_control
            , R.string.two_dimming_one_control, R.string.two_dimming_two_control, R.string.two_dimming_trhee_control, R.string.keshimenling
    };
    private String isUse;
    private int option = ContentCommon.INVALID_OPTION;
    private int CameraType = ContentCommon.CAMERA_TYPE_MJPEG;
    private static final String STR_DID = "did";
    private static final String STR_MSG_PARAM = "msgparam";
    List<Map> list_wifi_camera = new ArrayList<>();
    private int tag;
    private int connection_wifi_camera_index;
    private boolean again_connection;
    private Map mapdevice = new HashMap();
    private AlermBean alermBean;
    private String strDID = "";
    private String isfrom = "";
    private boolean isFirst;

    @Override
    protected int viewId() {
        return R.layout.my_device_item_act;
    }

    @Override
    protected void onView() {
//        isFirst = true;//第一次进入该界面
        EventBus.getDefault().register(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(this);
        onEvent1();
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        panelItem_map = (Map) getIntent().getSerializableExtra("panelItem");
        Integer imgtype = (Integer) getIntent().getSerializableExtra("imgtype");
        if (panelItem_map != null) {
            device_name_txt.setText(panelItem_map.get("name").toString());
            project_select.setText(panelItem_map.get("name").toString());
            mac_txt.setText(panelItem_map.get("mac").toString());
//            banben_txt.setText(panelItem_map.get("firmware").toString());
//            panid_txt.setText(panelItem_map.get("hardware").toString());
            panelNumber = panelItem_map.get("number").toString();
            isUse = panelItem_map.get("isUse").toString();
            switch (panelItem_map.get("type").toString()) {
                case "AA03":
                    camera_bufang_isuse(slide_btn_baojing);
                    break;
                default://传感器的布防状态
                    camera_bufang_isuse(slide_btn);
                    init_status();//初始化状态
                    break;
            }
            wangguan_set.setImageResource(imgtype);
            set_type(panelItem_map.get("type").toString());
            //成员，业主accountType->addrelative_id
            String accountType = (String) SharedPreferencesUtil.getData(MyDeviceItemActivity.this, "accountType", "");
            switch (accountType) {
                case "1":
                    delete_device_rel.setVisibility(View.VISIBLE);
                    wangguan_set_rel.setEnabled(true);
                    break;//业主
                case "2":
                    delete_device_rel.setVisibility(View.GONE);
                    wangguan_set_rel.setEnabled(false);
                    break;//家庭成员
            }
        }
//        onEvent();
    }

    /**
     * 摄像头和传感器获取布防状态的共用方法
     *
     * @param slide_btn_baojing
     */
    private void camera_bufang_isuse(SlideSwitchButton slide_btn_baojing) {
        switch (panelItem_map.get("isUse").toString() == null ? "" : panelItem_map.get("isUse").toString()) {
            //获取摄像头的布防状态
            case "1":
                slide_btn_baojing.changeOpenState(true);
                break;
            case "0":
                slide_btn_baojing.changeOpenState(false);
                break;

        }
    }

    private void onEvent1() {
        back.setOnClickListener(this);
        wangguan_set_rel.setOnClickListener(this);
        scene_list_rel.setOnClickListener(this);
        next_step_id.setOnClickListener(this);
        rel_sdcard_remotevideo.setOnClickListener(this);
    }

    private void init_status() {
        switch (panelItem_map.get("status") == null ? "" : panelItem_map.get("status").toString()) {
            case "0":
                status_txt.setText("离线");
                break;
            default:
                status_txt.setText("在线");
                break;
        }
    }

    //MESSAGE_TONGZHI_VIDEO_TO_MYDEVICE

    /**
     * 传感器类设置是否启用布防
     */
    private void sensor_set_protection(final String isUse) {
        dialogUtil.loadDialog();
        Map<String, Object> mapbox = new HashMap<String, Object>();
        mapbox.put("token", TokenUtil.getToken(MyDeviceItemActivity.this));
        mapbox.put("panelNumber", panelNumber);
        mapbox.put("isUse", isUse);
        MyOkHttp.postMapObject(ApiHelper.sraum_setLinkSensorPanelIsUse, mapbox, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sensor_set_protection(isUse);
            }
        }, MyDeviceItemActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                switch (isUse) {
                    case "1":
                        ToastUtil.showToast(MyDeviceItemActivity.this, "布防成功");
                        break;
                    case "0":
                        ToastUtil.showToast(MyDeviceItemActivity.this, "撤防成功");
                        break;
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(MyDeviceItemActivity.this, "token 错误");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(MyDeviceItemActivity.this, "panelNumber\n" +
                        "不正确");
            }
        });
    }


    //MESSAGE_TONGZHI_VIDEO_TO_MYDEVICE

    /**
     * 摄像头设置是否启用移动侦测布防
     */
    private void sraum_setWifiCameraIsUse(final String isUse) {
        dialogUtil.loadDialog();
        Map<String, Object> mapbox = new HashMap<String, Object>();
        mapbox.put("token", TokenUtil.getToken(MyDeviceItemActivity.this));
        mapbox.put("number", panelNumber);
        mapbox.put("isUse", isUse);
        MyOkHttp.postMapObject(ApiHelper.sraum_setWifiCameraIsUse, mapbox, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sensor_set_protection(isUse);
            }
        }, MyDeviceItemActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                switch (isUse) {
//                    case "1":
//                        ToastUtil.showToast(MyDeviceItemActivity.this, "布防成功");
//                        break;
//                    case "0":
//                        ToastUtil.showToast(MyDeviceItemActivity.this, "撤防成功");
//                        break;
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(MyDeviceItemActivity.this, "token 错误");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(MyDeviceItemActivity.this, "panelNumber\n" +
                        "不正确");
            }
        });
    }


    @Override
    protected void onEvent() {
        rel_yaokongqi.setOnClickListener(this);
        slide_btn.setSlideListener(this);
        slide_btn_baojing.setSlideListener(this);
        slide_btn_plan.setSlideListener(this);
        rel_bufang_plan.setOnClickListener(this);
        rel_sd_set.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private void set_type(String type) {
        switch (type) {
            case "A201":
                gateway_id_txt.setText(iconName[0]);
                break;
            case "A202":
                gateway_id_txt.setText(iconName[1]);
                break;
            case "A203":
                gateway_id_txt.setText(iconName[2]);
                break;
            case "A204":
                gateway_id_txt.setText(iconName[3]);
                break;
            case "A301":
                gateway_id_txt.setText(iconName[4]);
                break;
            case "A302":
                gateway_id_txt.setText(iconName[5]);
                break;
            case "A303":
                gateway_id_txt.setText(iconName[6]);
                break;
            case "A401":
                gateway_id_txt.setText(iconName[7]);
                break;
            case "A501":
                gateway_id_txt.setText(iconName[8]);
                break;
            case "A511":
                gateway_id_txt.setText(iconName[9]);
                break;
            case "A611":
                gateway_id_txt.setText(iconName[10]);
                break;
            case "A711":
                gateway_id_txt.setText(iconName[11]);
                break;
            case "A801":
                gateway_id_txt.setText(iconName[12]);
                sensor_common_select();
                break;
            case "A901":
                gateway_id_txt.setText(iconName[13]);
                sensor_common_select();
                break;
            case "A902":
                gateway_id_txt.setText(iconName[14]);
                sensor_common_select();
                break;
            case "AB01":
                gateway_id_txt.setText(iconName[15]);
                sensor_common_select();
                break;
            case "AB04":
                gateway_id_txt.setText(iconName[16]);
                sensor_common_select();
                break;
            case "B001":
                gateway_id_txt.setText(iconName[17]);
                break;
            case "B201":
                gateway_id_txt.setText(iconName[18]);
                break;
            case "AD01":
                gateway_id_txt.setText(iconName[19]);
                break;
            case "AC01":
                gateway_id_txt.setText(iconName[20]);
                sensor_common_select();
                break;
            case "B301":
                gateway_id_txt.setText(iconName[21]);
                break;
            case "B101":
                gateway_id_txt.setText(iconName[22]);
                break;
            case "B102":
                gateway_id_txt.setText(iconName[23]);
                break;
            case "AA02"://WIFI转发模块
                gateway_id_txt.setText(iconName[24]);
                rel_yaokongqi.setVisibility(View.VISIBLE);
                view_yaokongqi.setVisibility(View.VISIBLE);
                dev_txt.setText("WIFI");
                banben_txt.setText(panelItem_map.get("wifi").toString());
                //controllerId
                mac_txt.setText(panelItem_map.get("controllerId").toString());
                break;

            case "AA03"://摄像头
                common_parameter(25);
                break;
            case "A311":
                gateway_id_txt.setText(iconName[26]);
                break;
            case "A312":
                gateway_id_txt.setText(iconName[27]);
                break;
            case "A313":
                gateway_id_txt.setText(iconName[28]);
                break;
            case "A321":
                gateway_id_txt.setText(iconName[29]);
                break;
            case "A322":
                gateway_id_txt.setText(iconName[30]);
                break;
            case "A331":
                gateway_id_txt.setText(iconName[31]);
                break;
            case "AA04"://门铃
//                common_parameter(32);
                gateway_id_txt.setText(iconName[32]);
                dev_txt.setText("WIFI");
                banben_txt.setText(panelItem_map.get("wifi").toString());
                //controllerId
                mac_txt.setText(panelItem_map.get("controllerId").toString());
                break;
        }
    }

    /**
     * 摄像头，和门铃共同的代码
     *
     * @param i
     */
    private void common_parameter(int i) {
        gateway_id_txt.setText(iconName[i]);
        dev_txt.setText("WIFI");
        banben_txt.setText(panelItem_map.get("wifi").toString());
        //controllerId
        mac_txt.setText(panelItem_map.get("number").toString());

        //去获取摄像头的状态
//                this.mapdevice = mapdevice;
        mapdevice = new HashMap();
        //dimmer,temperature,mode
        mapdevice.put("dimmer", panelItem_map.get("number").toString());
        mapdevice.put("temperature", "888888");
        mapdevice.put("mode","admin");
//                onitem_wifi_shexiangtou(mapdevice);//(String strUser, String strPwd, String strDID
        //去首页获取状态，发送广播
        tongzhi_video(mapdevice);
        init_nativeCaller();
    }

    /**
     * 初始化布防参数
     */
    private void init_nativeCaller() {
        if (dialogUtil != null) dialogUtil.loadDialog();
        strDID = panelItem_map.get("number").toString();
//        NativeCaller.PPPPGetSystemParams(strDID,
//                ContentCommon.MSG_TYPE_GET_PARAMS);
        alermBean = new AlermBean();
        BridgeService.setAlarmInterface(this);
    }

    /**
     * 发送给首页，获取摄像头状态，并返回
     */
    private void tongzhi_video(Map map) {
        Intent mIntent = new Intent(MESSAGE_TONGZHI_VIDEO_FROM_MYDEVICE);
        mIntent.putExtra("video_item", (Serializable) map);
        sendBroadcast(mIntent);
    }

    /**
     * 传感器共有
     */
    private void sensor_common_select() {
        view_bufang.setVisibility(View.VISIBLE);
        rel_bufang.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;
        switch (v.getId()) {
            case R.id.back:
                MyDeviceItemActivity.this.finish();
                break;
            case R.id.wangguan_set_rel:
                if (panelItem_map != null) {
                    intent = new Intent(MyDeviceItemActivity.this, EditMyDeviceActivity.class);
                    intent.putExtra("panelItem", (Serializable) panelItem_map);
                    startActivity(intent);
                }
                break;
            case R.id.scene_list_rel:
                if (list_gujian_rel.getVisibility() == View.VISIBLE) {
                    list_gujian_rel.setVisibility(View.GONE);
                    other_jiantou.setImageResource(R.drawable.bg_you);
                } else {
                    list_gujian_rel.setVisibility(View.VISIBLE);
                    other_jiantou.setImageResource(R.drawable.bg_xia);
                }
                break;
            case R.id.next_step_id://删除设备
                showCenterDeleteDialog(panelNumber, panelItem_map.get("name").toString(), panelItem_map.get("type").toString());
                break;
            case R.id.rel_yaokongqi://跳转到遥控器列表界面
                intent = new Intent(MyDeviceItemActivity.this, SelectYaoKongQiActivity.class);
                intent.putExtra("controllerNumber", panelItem_map.get("controllerId").toString());//controllerNumber
                startActivity(intent);
                break;
            case R.id.rel_bufang_plan://布防报警计划
//                startActivity(new Intent(MyDeviceItemActivity.this,BuFangBaoJingPlanActivity.class));

                Intent intentalam = new Intent(MyDeviceItemActivity.this, BuFangBaoJingPlanActivity.class);
                intentalam.putExtra(ContentCommon.STR_CAMERA_PWD, "888888");
                intentalam.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
                startActivity(intentalam);
                break;
            //rel_sd_set

            case R.id.rel_sd_set://布防报警计划
//                startActivity(new Intent(MyDeviceItemActivity.this,BuFangBaoJingPlanActivity.class));

                Intent intent_sd_set = new Intent(MyDeviceItemActivity.this, SettingSDCardActivity.class);
                intent_sd_set.putExtra(ContentCommon.STR_CAMERA_PWD, "888888");
                intent_sd_set.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
                startActivity(intent_sd_set);
                break;
            case R.id.rel_sdcard_remotevideo://看SD卡录像回放列表
                Intent intentVid = new Intent(MyDeviceItemActivity.this, PlayBackTFActivity.class);
                intentVid.putExtra(ContentCommon.STR_CAMERA_NAME, "admin");
                intentVid.putExtra(ContentCommon.STR_CAMERA_ID, strDID);
                intentVid.putExtra(ContentCommon.STR_CAMERA_PWD, "888888");
                intentVid.putExtra(ContentCommon.STR_CAMERA_USER, "admin");
                startActivity(intentVid);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
        }
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String panelNumber, final String name,
                                       final String type) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(MyDeviceItemActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setText(name);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(MyDeviceItemActivity.this, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sraum_deletepanel(panelNumber, type, dialog);
            }
        });
    }

    /**
     * 删除设备
     *
     * @param
     * @param dialog
     */
    private void sraum_deletepanel(final String panelNumber, final String type,
                                   final Dialog dialog) {
        if (dialogUtil != null)
            dialogUtil.loadDialog();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("token", TokenUtil.getToken(MyDeviceItemActivity.this));
        String send_method = "";
        switch (type) {
            case "AA02":
                map.put("number", panelNumber);
                send_method = ApiHelper.sraum_deleteWifiApple;
                break;//wifi模块
            case "AA03":
                map.put("number", panelNumber);
                send_method = ApiHelper.sraum_deleteWifiCamera;
                break;//wifi模块
            default:
                map.put("boxNumber", TokenUtil.getBoxnumber(MyDeviceItemActivity.this));
                map.put("panelNumber", panelNumber);
                send_method = ApiHelper.sraum_deletePanel;
                break;
        }

        MyOkHttp.postMapObject(send_method, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_deletepanel(panelNumber, type, dialog);
                    }
                }, MyDeviceItemActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        dialog.dismiss();
                        MyDeviceItemActivity.this.finish();
                        switch (type) {
                            case "AA02":
                                SharedPreferencesUtil.saveInfo_List(MyDeviceItemActivity.this, "remoteairlist",
                                        new ArrayList<Map>());
                                SharedPreferencesUtil.saveData(MyDeviceItemActivity.this, "mGizWifiDevice", "");
                                break;
                        }
                    }

                    @Override
                    public void fourCode() {
                        dialog.dismiss();
                        ToastUtil.showToast(MyDeviceItemActivity.this, "删除失败");
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    private void setAlerm() {
//        if (successFlag) {
        Log.e("setAlerm", "setAlermTemp: " + alermBean.getAlarm_temp());
        NativeCaller.PPPPAlarmSetting(strDID, alermBean.getAlarm_audio(),
                alermBean.getMotion_armed(),
                alermBean.getMotion_sensitivity(),
                alermBean.getInput_armed(), alermBean.getIoin_level(),
                alermBean.getIoout_level(), alermBean.getIolinkage(),
                alermBean.getAlermpresetsit(), alermBean.getMail(),
                alermBean.getSnapshot(), alermBean.getRecord(),
                alermBean.getUpload_interval(),
                alermBean.getSchedule_enable(),
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF,
                0xFFFFFFFF, 0xFFFFFFFF, -1);
//        } else {
//            showToast(R.string.alerm_set_failed);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(MyEvent event) {
        String status = event.getMsg();
        switch (status) {
            case "1":
                view_bufang_baojing.setVisibility(View.VISIBLE);
                rel_bufang_baojing.setVisibility(View.VISIBLE);
                rel_bufang_plan.setVisibility(View.VISIBLE);
                view_bufang_plan.setVisibility(View.VISIBLE);
                rel_sd_set.setVisibility(View.VISIBLE);
                view_sd_set.setVisibility(View.VISIBLE);
                view_sdcard_remotevideo.setVisibility(View.VISIBLE);
                rel_sdcard_remotevideo.setVisibility(View.VISIBLE);
                //在线

                break;
            case "0":
                view_bufang_baojing.setVisibility(View.GONE);
                rel_bufang_baojing.setVisibility(View.GONE);
                rel_bufang_plan.setVisibility(View.GONE);
                view_bufang_plan.setVisibility(View.GONE);
                rel_sd_set.setVisibility(View.GONE);
                view_sd_set.setVisibility(View.GONE);
                view_sdcard_remotevideo.setVisibility(View.GONE);
                rel_sdcard_remotevideo.setVisibility(View.GONE);
                //离线
                break;
        }

        switch (status) {
            case "0":
                status_txt.setText("离线");
                break;
            default:
                status_txt.setText("在线");
                break;
        }
        if (dialogUtil != null) dialogUtil.removeDialog();
//        ToastUtil.showToast(MyDeviceItemActivity.this, "status:" + status);
    }

    @Override
    public void callBackAlarmParams(String did, int alarm_audio, int motion_armed,
                                    int motion_sensitivity, int input_armed, int ioin_level,
                                    int iolinkage, int ioout_level, int alarmpresetsit, int mail,
                                    int snapshot, int record, int upload_interval,
                                    int schedule_enable, int schedule_sun_0, int schedule_sun_1,
                                    int schedule_sun_2, int schedule_mon_0, int schedule_mon_1,
                                    int schedule_mon_2, int schedule_tue_0, int schedule_tue_1,
                                    int schedule_tue_2, int schedule_wed_0, int schedule_wed_1,
                                    int schedule_wed_2, int schedule_thu_0, int schedule_thu_1,
                                    int schedule_thu_2, int schedule_fri_0, int schedule_fri_1,
                                    int schedule_fri_2, int schedule_sat_0, int schedule_sat_1,
                                    int schedule_sat_2) {

        alermBean.setDid(did);
        alermBean.setMotion_armed(motion_armed);
        alermBean.setMotion_sensitivity(motion_sensitivity);
        alermBean.setInput_armed(input_armed);
        alermBean.setIoin_level(ioin_level);
        alermBean.setIolinkage(iolinkage);
        alermBean.setIoout_level(ioout_level);
        alermBean.setAlermpresetsit(alarmpresetsit);
        alermBean.setMail(mail);
        alermBean.setSnapshot(snapshot);
        alermBean.setRecord(record);
        alermBean.setUpload_interval(upload_interval);
        alermBean.setAlarm_audio(alarm_audio);
        alermBean.setAlarm_temp(input_armed);
        alermBean.setSchedule_enable(schedule_enable);
        mHandler.sendEmptyMessage(ALERMPARAMS);//这个跳转到这个界面后的获取的
    }

    @Override
    public void callBackSetSystemParamsResult(String did, int paramType, int result) {//这个是报警开关后的直接，设置后的直接回调
        mHandler.sendEmptyMessage(result);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    ToastUtil.showToast(MyDeviceItemActivity.this, "报警设置失败");
                    break;
                case 1:
//                    ToastUtil.showToast(MyDeviceItemActivity.this, "报警设置成功");
//                    finish();
                    break;
                case ALERMPARAMS:
//                    if (dialogUtil != null) dialogUtil.removeDialog();
//                    isfrom = "ALERMPARAMS";
//                    if (isFirst) {
//                        isFirst = false;
//                    }
//                    if (0 == alermBean.getMotion_armed()) {//首次读取报警设置
//                        slide_btn_baojing.setChecked(false);
//                    } else {
//                        slide_btn_baojing.setChecked(true);
//                    }

                    if (0 == alermBean.getInput_armed()) {


                    } else {

                    }

                    if (0 == alermBean.getIoin_level()) {

                    } else {

                    }

                    if (0 == alermBean.getAlarm_audio()) {

                    } else {

                        if (1 == alermBean.getAlarm_audio()) {

                        } else if (2 == alermBean.getAlarm_audio()) {

                        } else if (3 == alermBean.getAlarm_audio()) {

                        }
                    }

                    if (0 == alermBean.getAlarm_temp()) {

                    } else {

                        if (1 == alermBean.getAlarm_temp()) {

                        } else if (2 == alermBean.getAlarm_temp()) {

                        } else if (3 == alermBean.getAlarm_temp()) {

                        }
                    }

                    if (0 == alermBean.getIolinkage()) {

                    } else {

                    }

                    if (0 == alermBean.getIoout_level()) {

                    } else {

                    }

                    if (alermBean.getAlermpresetsit() == 0) {

                    } else {

                    }

                    if (1 == alermBean.getMotion_armed()
                            || 1 == alermBean.getInput_armed()
                            || alermBean.getAlarm_audio() != 0) {

                    } else {

                    }

                    break;
                default:
                    break;
            }
        }
    };


    @Override
    public void onBackPressed() {
        MyDeviceItemActivity.this.finish();
    }

    @Override
    public void openState(boolean isChecked, View view) {
        switch (view.getId()) {
            case R.id.slide_btn:
                if (isChecked) {
                    sensor_set_protection("1");
                } else {
                    sensor_set_protection("0");
                }

                //
                break;
            case R.id.slide_btn_baojing:
//
//                switch (isfrom) {
//                    case "ALERMPARAMS"://回调的
//                        isfrom = "";
//
//                        break;
//                    default://主动控制的,去响应
//                        if (isChecked) {
////                    sensor_set_protection("1");
//                            alermBean.setMotion_armed(1);
//                        } else {
////                    sensor_set_protection("0");
//                            alermBean.setMotion_armed(0);
//                        }
//                        setAlerm();
//                        break;
//                }

                if (isChecked) {
//                    sensor_set_protection("1");
                    alermBean.setMotion_armed(1);
                    sraum_setWifiCameraIsUse("1");

                } else {
//                    sensor_set_protection("0");
                    alermBean.setMotion_armed(0);
                    sraum_setWifiCameraIsUse("0");
                }
                setAlerm();


                break;
            case R.id.slide_btn_plan:
                if (isChecked) {
//                    sensor_set_protection("1");
                } else {
//                    sensor_set_protection("0");
                }
                break;
        }
    }
}
