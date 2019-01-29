package com.massky.sraum.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/16.
 */

public class WangGuanBaseInformationActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private Map gateway_map = new HashMap();
    @InjectView(R.id.type)
    TextView type;
    @InjectView(R.id.mac)
    TextView mac;
    @InjectView(R.id.version)
    TextView version;
    @InjectView(R.id.pannel)
    TextView pannel;
    @InjectView(R.id.pan_id)
    TextView pan_id;
    private String gatewayNumber;//网关编号
    private String areaNumber;

    @Override
    protected int viewId() {
        return R.layout.wangguan_baseinfor_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        areaNumber = (String) getIntent().getSerializableExtra("areaNumber");
        gatewayNumber = (String) getIntent().getSerializableExtra("number");
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {//"主卫水浸","防盗门门磁","防盗门猫眼","人体监测","防盗门门锁"

    }

    @Override
    protected void onResume() {
        sraum_getGatewayInfo();
        super.onResume();
    }

    /**
     * 获取网关基本信息（APP->网关）
     */
    private void sraum_getGatewayInfo() {
        Map map = new HashMap();
        map.put("token", TokenUtil.getToken(this));
        map.put("number", gatewayNumber);
        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getGatewayInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, WangGuanBaseInformationActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
                        //"roomNumber":"1"
                        gateway_map = new HashMap();
                        gateway_map.put("type", user.type);
                        gateway_map.put("status", user.status);
                        gateway_map.put("name", user.name);
                        gateway_map.put("mac", user.mac);
                        gateway_map.put("versionType", user.versionType);
                        gateway_map.put("hardware", user.hardware);
                        gateway_map.put("firmware", user.firmware);
                        gateway_map.put("bootloader", user.bootloader);
                        gateway_map.put("coordinator", user.coordinator);
                        gateway_map.put("panid", user.panid);
                        gateway_map.put("channel", user.channel);
                        gateway_map.put("deviceCount", user.deviceCount);
                        gateway_map.put("sceneCount", user.sceneCount);

                        //显示
                        mac.setText(user.mac);
                        type.setText(user.type);
                        version.setText(user.hardware);
                        pannel.setText(user.channel);
                        pan_id.setText(user.panid);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                WangGuanBaseInformationActivity.this.finish();//
                break;
        }
    }
}
