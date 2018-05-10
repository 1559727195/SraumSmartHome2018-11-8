package com.massky.sraumsmarthome.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.ToastUtil;
import com.massky.sraumsmarthome.Utils.ApiHelper;
import com.massky.sraumsmarthome.Utils.MyOkHttp;
import com.massky.sraumsmarthome.Utils.Mycallback;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.receiver.ApiTcpReceiveHelper;
import com.massky.sraumsmarthome.service.MyService;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/30.
 */

public class CurtainWindowActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    private String status;
    private String number;
    private String type;
    private String name;
    private String name1;
    private String name2;
    @InjectView(R.id.project_select)
    TextView project_select;
    @InjectView(R.id.name1_txt)
    TextView name1_txt;
    @InjectView(R.id.name2_txt)
    TextView name2_txt;
    @InjectView(R.id.radio_group_out)
    RadioGroup radio_group_out;
    @InjectView(R.id.radio_group_in)
    RadioGroup radio_group_in;
    @InjectView(R.id.radio_group_all)
    RadioGroup radio_group_all;


    @Override
    protected int viewId() {
        return R.layout.curtain_window_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        Map map_item = (Map) getIntent().getSerializableExtra("map_item");
        status = (String) map_item.get("status");
        number = (String) map_item.get("number");
        type = (String) map_item.get("type");

        name = (String) map_item.get("name");
        name1 = (String) map_item.get("name1");
        name2 = (String) map_item.get("name2");
        project_select.setText(name);
        name1_txt.setText(name1);
        name2_txt.setText(name2);
        //
        switch_curtain_status();
        init_event();
        init_receiver_control();
    }

    private void init_receiver_control() {
        //注册广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApiTcpReceiveHelper.TIAO_GUANG_RECEIVE_ACTION);
        registerReceiver(mReceiver, filter);

    }

    /**
     * 广播接收
     */
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String result = intent.getStringExtra("result");
            ToastUtil.showToast(CurtainWindowActivity.this, "成功");
            //获取窗帘控制后状态
            //
            Map map = new HashMap();
            map.put("number", number);
            map.put("type", type);
            get_single_device_info(map);
        }
    };

    /**
     * 获取单个设备或是按钮信息（APP->网关）
     *
     * @param map
     */
    private void get_single_device_info(final Map map) {
        MyOkHttp.postMapObject(ApiHelper.sraum_getOneInfo, map,
                new Mycallback(CurtainWindowActivity.this) {
                    @Override
                    public void onSuccess(User user) {
//       switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
//                        String type = (String) map.get("type");
//                        map.put("status",user.status);
//                        map.put("name",user.name)
                        status = (String) user.status;
                        number = (String) user.number;
                        type = (String) user.type;
                        name = (String) user.name;
                        name1 = (String) user.name1;
                        name2 = (String) user.name2;
                        switch_curtain_status();
                    }
                });
    }


    /**
     * 初始化监听事件
     */
    private int radio_out_index;
    private int radio_in_index;
    private int radio_all_index;

    private void init_event() {
        radio_group_out.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //外纱监听，
                switch (checkedId) {
                    case 0:
                        radio_out_index = 0;
                        break;
                    case 1:
                        radio_out_index = 1;
                        break;
                    case 2:
                        radio_out_index = 2;
                        break;
                }
                qiehuan_select();
            }
        });

        radio_group_in.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //内纱监听，
                //外纱监听，
                switch (checkedId) {
                    case 0:
                        radio_in_index = 0;
                        break;
                    case 1:
                        radio_in_index = 1;
                        break;
                    case 2:
                        radio_in_index = 2;
                        break;
                }
                qiehuan_select();
            }
        });

        radio_group_all.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //全部监听，
                //外纱监听，
                switch (checkedId) {
                    case 0:
                        radio_all_index = 0;
                        break;
                    case 1:
                        radio_all_index = 1;
                        break;
                    case 2:
                        radio_all_index = 2;
                        break;
                }
                qiehuan_select();
            }
        });
    }

    /**
     * 切换选择
     */
    private void qiehuan_select() {
        if (radio_out_index == 2 && radio_in_index == 2 && radio_all_index == 2) {
            status = "0";
            return;
        }
        if (radio_out_index == 0 && radio_in_index == 0 && radio_all_index == 0) {
            status = "1";
            return;
        }
        if (radio_out_index == 1 && radio_in_index == 1 && radio_all_index == 1) {
            status = "2";
            return;
        }
        if (radio_out_index == 0 && radio_in_index == 2 && radio_all_index == -1) {
            status = "3";
            return;
        }
        if (radio_out_index == 0 && radio_in_index == 1 && radio_all_index == -1) {
            status = "4";
            return;
        }
        if (radio_out_index == 2 && radio_in_index == 0 && radio_all_index == -1) {
            status = "5";
            return;
        }
        if (radio_out_index == 2 && radio_in_index == 1 && radio_all_index == -1) {
            status = "6";
            return;
        }
        if (radio_out_index == 1 && radio_in_index == 2 && radio_all_index == -1) {
            status = "7";
            return;
        }
        if (radio_out_index == 1 && radio_in_index == 0 && radio_all_index == -1) {
            status = "8";
            return;
        }
        do_tcp_send_curtain();
    }

    /**
     * 去执行窗帘操作
     */
    private void do_tcp_send_curtain() {
        Map map = new HashMap();
        map.put("number", number);
        map.put("type", type);
        map.put("status", status);
        MyService.getInstance().sraum_send_tcp(map, "sraum_controlButton");
    }

    /**
     * 切换窗帘状态
     */
    private void switch_curtain_status() {
        switch (status) {
            case "0":
                select_radio_out(2);//打开，暂停，关闭
                select_radio_in(2);
                select_radio_all(2);
                break;
            case "1":
                select_radio_out(0);
                select_radio_in(0);
                select_radio_all(0);
                break;
            case "2":
                select_radio_out(1);
                select_radio_in(1);
                select_radio_all(1);
                break;
            case "3":
                select_radio_out(0);
                select_radio_in(2);
                select_radio_all(-1);
                break;
            case "4":
                select_radio_out(0);
                select_radio_in(1);
                select_radio_all(-1);
                break;
            case "5":
                select_radio_out(2);
                select_radio_in(0);
                select_radio_all(-1);
                break;
            case "6":
                select_radio_out(2);
                select_radio_in(1);
                select_radio_all(-1);
                break;
            case "7":
                select_radio_out(1);
                select_radio_in(2);
                select_radio_all(-1);
                break;
            case "8":
                select_radio_out(1);
                select_radio_in(0);
                select_radio_all(-1);
                break;
        }
    }

    /**
     * 外纱
     */
    private void select_radio_out(int index) {
        radio_out_index = index;
        //外纱
        for (int i = 0; i < radio_group_out.getChildCount(); i++) {
            final RadioButton child = (RadioButton) radio_group_out.getChildAt(i);
            if (index == i) {
                child.setChecked(true);
            } else
                child.setChecked(false);
        }
    }

    /**
     * 内纱
     */
    private void select_radio_in(int index) {
        //radio_group_in,内纱
        radio_in_index = index;
        for (int i = 0; i < radio_group_in.getChildCount(); i++) {
            final RadioButton child = (RadioButton) radio_group_in.getChildAt(i);
            if (index == i) {
                child.setChecked(true);
            } else
                child.setChecked(false);
        }
    }

    /**
     * 全部
     */
    private void select_radio_all(int index) {
        //radio_group_all，全部
        radio_all_index = index;
        for (int i = 0; i < radio_group_all.getChildCount(); i++) {
            final RadioButton child = (RadioButton) radio_group_all.getChildAt(i);
            if (index == i) {
                child.setChecked(true);
            } else
                child.setChecked(false);
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                CurtainWindowActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
