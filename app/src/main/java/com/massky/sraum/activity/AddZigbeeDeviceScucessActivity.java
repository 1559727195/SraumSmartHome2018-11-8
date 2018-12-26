package com.massky.sraum.activity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
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
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;
import static com.massky.sraum.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/1/8.
 */

public class AddZigbeeDeviceScucessActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    ImageView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private PopupWindow popupWindow;
    private String device_name;
    private List<User.panellist> panelList = new ArrayList<>();
    private DialogUtil dialogUtil;
    private List<User.device> deviceList = new ArrayList<>();
    private String panelType;
    private String panelName;
    private String panelNumber;
    private String deviceNumber;
    private String panelMAC;
    @InjectView(R.id.dev_name)
    ClearLengthEditText dev_name;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;


    private int[] iconName = {R.string.yijianlight, R.string.liangjianlight, R.string.sanjianlight, R.string.sijianlight,
            R.string.yilutiaoguang, R.string.lianglutiaoguang, R.string.sanlutiao, R.string.window_panel, R.string.air_panel,
            R.string.air_mode, R.string.xinfeng_mode, R.string.dinuan_mode
            , R.string.menci, R.string.rentiganying, R.string.jiuzuo, R.string.yanwu, R.string.tianranqi, R.string.jinjin_btn,
            R.string.zhineng, R.string.pm25, R.string.shuijin, R.string.jixieshou, R.string.cha_zuo_1, R.string.cha_zuo, R.string.wifi_hongwai,
            R.string.wifi_camera, R.string.one_light_control, R.string.two_light_control, R.string.three_light_control
            , R.string.two_dimming_one_control, R.string.two_dimming_two_control, R.string.two_dimming_trhee_control, R.string.keshimenling
    };
    private TextView type_txt;
    private TextView mac_txt;
    private String boxNumber;


    @Override
    protected int viewId() {
        return R.layout.add_zigbee_deivice_scucess;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        dialogUtil = new DialogUtil(this);
        next_step_txt.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        get_panel_detail();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AddZigbeeDeviceScucessActivity.this.finish();
                break;
            case R.id.next_step_txt:
                showPopWindow();
                break;
            case R.id.btn_login_gateway:
                save_panel();
                break;
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(AddZigbeeDeviceScucessActivity.this).inflate(
                    R.layout.add_devsucesspopupwindow, null);
            type_txt = (TextView) view.findViewById(R.id.type);
//           //mac
            mac_txt = (TextView) view.findViewById(R.id.mac);
            set_type(panelType);
            mac_txt.setText(panelMAC);
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(cd);
            int[] location = new int[2];
            next_step_txt.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            int xoff = dip2px(AddZigbeeDeviceScucessActivity.this, 20);
            popupWindow.showAsDropDown(next_step_txt, 0, dip2px(AddZigbeeDeviceScucessActivity.this, 10));
//            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;// 当点击屏幕时，使popupWindow消失
                    backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消，1.0f为透明
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据面板类型命名
     *
     * @param type
     */
    private void set_type(String type) {
        switch (type) {
            case "A201":
                type_txt.setText(iconName[0]);
                break;
            case "A202":
                type_txt.setText(iconName[1]);
                break;
            case "A203":
                type_txt.setText(iconName[2]);
                break;
            case "A204":
                type_txt.setText(iconName[3]);
                break;
            case "A301":
                type_txt.setText(iconName[4]);
                break;
            case "A302":
                type_txt.setText(iconName[5]);
                break;
            case "A303":
                type_txt.setText(iconName[6]);
                break;
            case "A401":
                type_txt.setText(iconName[7]);
                break;
            case "A501":
                type_txt.setText(iconName[8]);
                break;
            case "A511":
                type_txt.setText(iconName[9]);
                break;
            case "A611":
                type_txt.setText(iconName[10]);
                break;
            case "A711":
                type_txt.setText(iconName[11]);
                break;
            case "A801":
                type_txt.setText(iconName[12]);
                break;
            case "A901":
                type_txt.setText(iconName[13]);
                break;
            case "A902":
                type_txt.setText(iconName[14]);
                break;
            case "AB01":
                type_txt.setText(iconName[15]);
                break;
            case "AB04":
                type_txt.setText(iconName[16]);
                break;
            case "B001":
                type_txt.setText(iconName[17]);
                break;
            case "B201":
                type_txt.setText(iconName[18]);
                break;
            case "AD01":
                type_txt.setText(iconName[19]);
                break;
            case "AC01":
                type_txt.setText(iconName[20]);
                break;
            case "B301":
                type_txt.setText(iconName[21]);
                break;
            case "B101":
                type_txt.setText(iconName[22]);
                break;
            case "B102":
                type_txt.setText(iconName[23]);
                break;
            case "AA02"://WIFI转发模块
                type_txt.setText(iconName[24]);
//                rel_yaokongqi.setVisibility(View.VISIBLE);
//                view_yaokongqi.setVisibility(View.VISIBLE);
//                dev_txt.setText("WIFI");
//                banben_txt.setText(panelItem_map.get("wifi").toString());
//                //controllerId
//                mac_txt.setText(panelItem_map.get("controllerId").toString());
                break;

            case "AA03"://WIFI转发模块
                type_txt.setText(iconName[25]);
//                dev_txt.setText("WIFI");
//                banben_txt.setText(panelItem_map.get("wifi").toString());
//                //controllerId
//                mac_txt.setText(panelItem_map.get("controllerId").toString());
                break;
            case "A311":
                type_txt.setText(iconName[26]);
                break;
            case "A312":
                type_txt.setText(iconName[27]);
                break;
            case "A313":
                type_txt.setText(iconName[28]);
                break;
            case "A321":
                type_txt.setText(iconName[29]);
                break;
            case "A322":
                type_txt.setText(iconName[30]);
                break;
            case "A331":
                type_txt.setText(iconName[31]);
                break;
            case "AA04"://WIFI转发模块
                type_txt.setText(iconName[32]);
//                dev_txt.setText("WIFI");
//                banben_txt.setText(panelItem_map.get("wifi").toString());
//                //controllerId
//                mac_txt.setText(panelItem_map.get("controllerId").toString());
                break;

        }


    }


    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }

    /**
     * 得到面板信息
     */
    private void get_panel_detail() {
        panelNumber = getIntent().getStringExtra("panelid");
        //根据panelid去查找相关面板信心
        //根据panelid去遍历所有面板
        boxNumber = getIntent().getStringExtra("boxNumber");
        Bundle bundle = getIntent().getBundleExtra("bundle_panel");
        deviceList = (List<User.device>) bundle.getSerializable("deviceList");
        panelType = getIntent().getStringExtra("panelType");
        panelName = getIntent().getStringExtra("panelName");
        panelMAC = getIntent().getStringExtra("panelMAC");
        if (panelName != null)
            dev_name.setText(panelName);
    }


    /**
     * 更新面板名称
     *
     * @param panelName
     * @param panelNumber
     */
    private void sraum_update_panel_name(final String panelName, final String panelNumber) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(AddZigbeeDeviceScucessActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(AddZigbeeDeviceScucessActivity.this));
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
                }, AddZigbeeDeviceScucessActivity.this, dialogUtil) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                        AddZigbeeDeviceScucessActivity.this.finish();
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        //
                        AddZigbeeDeviceScucessActivity.this.finish();
                        AppManager.getAppManager().removeActivity_but_activity_cls(MainGateWayActivity.class);
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void threeCode() {
                        super.threeCode();
                        ToastUtil.showToast(AddZigbeeDeviceScucessActivity.this, panelName + ":" + "面板编号不正确");
                    }

                    @Override
                    public void fourCode() {
                        super.fourCode();
                        ToastUtil.showToast(AddZigbeeDeviceScucessActivity.this, panelName + ":" + "面板名字已存在");
                    }
                });
    }

    /**
     * 保存面板
     */
    private void save_panel() {
        String panelName = dev_name.getText().toString().trim();
        sraum_update_panel_name(panelName, panelNumber);
    }

}
