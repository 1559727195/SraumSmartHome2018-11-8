package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/11/9.
 */

public class FastEditPanelActivity extends BaseActivity {
    public static String ACTION_SRAUM_FAST_EDIT = "ACTION_SRAUM_FAST_EDIT";//notifactionId = 8 ->设置网关模式，sraum_setBox
    @InjectView(R.id.back)
    ImageView back;
    //    @InjectView(R.id.addscroll)
//    ScrollView addscroll;
    private MessageReceiver mMessageReceiver;

    private DialogUtil dialogUtil;

    private String panelType;
    private String panelName;
    private String panelNumber;
    private String deviceNumber;
    private String panelMAC;
    private List<User.device> deviceList = new ArrayList<>();
    private List<User.panellist> panelList = new ArrayList<>();
    private boolean is_index;

    @InjectView(R.id.second)
    TextView second_txt;
    @InjectView(R.id.miao)
    TextView miao;
    private String gateway_number = "";

    @Override
    protected int viewId() {
        return R.layout.fast_edit_panel_act;
    }

    @Override
    protected void onView() {
        back.setOnClickListener(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
//                addscroll.setVisibility(View.GONE);
        registerMessageReceiver();
        init_timer();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void init_timer() {
        final int[] second = {30};//90秒
        final int[] minute = {1};//90秒
        final int[] index = {0};
        is_index = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (is_index) {
                    try {
                        Thread.sleep(1000);
//                        roundProgressBar2.setProgress(i++);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                second[0]--;
                                if (second[0] > 9) {
                                    miao.setText(String.valueOf(second[0]));
                                } else if (second[0] > 0) {
                                    miao.setText("0" + String.valueOf(second[0]));
                                } else {
                                    index[0]++;
                                    if (index[0] >= 2) {
                                        FastEditPanelActivity.this.finish();
                                        //停止添加网关
                                        is_index = false;
                                        miao.setText("00");
                                        second[0] = 0;
                                        minute[0] = 0;
                                        miao.setText("00");
                                    } else {
                                        miao.setText("00");
                                        second[0] = 59;
                                        minute[0] = 0;
                                        miao.setText("59");
                                    }
                                }
                                second_txt.setText("0" + String.valueOf(minute[0]));
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    /*
     * 将秒数转为时分秒
     * */
    public String change(int second) {
        int h = 0;
        int d = 0;
        int s = 0;
        int temp = second % 3600;
        if (second > 3600) {
            h = second / 3600;
            if (temp != 0) {
                if (temp > 60) {
                    d = temp / 60;
                    if (temp % 60 != 0) {
                        s = temp % 60;
                    }
                } else {
                    s = temp;
                }
            }
        } else {
            d = second / 60;
            if (second % 60 != 0) {
                s = second % 60;
            }
        }

        return h + ":" + d + ":" + s + "";
    }


    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_SRAUM_FAST_EDIT);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(ACTION_SRAUM_FAST_EDIT)) {
                int messflag = intent.getIntExtra("notifactionId", 0);
                String panelid = intent.getStringExtra("panelid");
                String gateway_number = intent.getStringExtra("gateway_number");
                if (messflag == 1) {//快捷编辑。在我的面板添加快捷编辑按钮。
                    //在这个界面时，type=1.找面板下的设备列表。在编辑面板的界面。找面板按钮，找到面板后，
                    getPanel_devices(panelid);
                }
            }
        }
    }


    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    /**
     * 添加面板下的设备信息
     *
     * @param panelid
     */
    private void getPanel_devices(final String panelid) {
        Map<String, Object> map = new HashMap<>();
        String areaNumber = (String) SharedPreferencesUtil.getData(FastEditPanelActivity.this, "areaNumber", "");
        map.put("token", TokenUtil.getToken(FastEditPanelActivity.this));
        map.put("areaNumber", areaNumber);
        map.put("boxNumber", gateway_number);
        map.put("panelNumber", panelid);
        MyOkHttp.postMapObject(ApiHelper.sraum_getPanelDevices, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        getPanel_devices(panelid);
                    }
                }, FastEditPanelActivity.this, dialogUtil) {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void onSuccess(User user) {
                        super.onSuccess(user);
                        panelList.clear();
                        deviceList.clear();
                        deviceList.addAll(user.deviceList);

                        //面板的详细信息
                        panelType = user.panelType;
                        panelName = user.panelName;
                        panelMAC = user.panelMAC;

                        Intent intent = null;
                        if (!panelid.equals("")) {
                            switch (panelType) {
                                case "A201"://一灯控
                                case "A202"://二灯控
                                case "A203"://三灯控
                                case "A204"://四灯控
                                case "A301"://一键调光，3键灯控  设备4个
                                case "A302"://两键调光，2键灯控
                                case "A303"://三键调光，一键灯控
                                case "A311":
                                case "A312":
                                case "A313":
                                case "A321":
                                case "A322":
                                case "A331":
                                case "A401"://设备2个
                                case "A501"://设备2个
                                case "A601"://设备2个
                                case "A701"://设备2个
                                case "A511"://设备2个
                                case "A611"://设备2个
                                case "A711"://设备2个
                                case "A801":
                                    //门磁
                                case "A901":
                                    //人体感应
                                case "A902":
                                    //TOA久坐报警器
                                case "AB01":
                                    //烟雾报警器
                                case "AB04":
                                    //天然气报警器
                                case "AC01":
                                    //水浸检测器
                                case "AD01":
                                    //PM2.5入墙
                                case "AD02":
                                    //PM2.5魔方
                                case "B001":
                                    //紧急按钮
                                case "B101":
                                    //86插座一位
                                case "B201":
                                case "B202":
                                    //智能门锁
                                case "B301"://直流电阀机械手
                                    intent = new Intent(FastEditPanelActivity.this,
                                            ChangePanelAndDeviceActivity.class);
                                    break;//直流电阀机械手
                            }
                            intent.putExtra("panelid", panelid);
                            intent.putExtra("boxNumber", gateway_number);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("deviceList", (Serializable) deviceList);
//                            intent.putExtra("deviceList", (Serializable) deviceList);
                            intent.putExtra("panelType", panelType);
                            intent.putExtra("panelName", panelName);
                            intent.putExtra("panelMAC", panelMAC);
                            intent.putExtra("bundle_panel", bundle);
                            intent.putExtra("findpaneltype", "fastedit");
                            startActivity(intent);
//                                    FastEditPanelActivity.this.finish();
                            is_index = false;
                            FastEditPanelActivity.this.finish();
                        }
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(FastEditPanelActivity.this, "该网关不存在");
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                FastEditPanelActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        is_index = false;
        unregisterReceiver(mMessageReceiver);
    }
}
