package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.yanzhenjie.statusview.StatusView;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import androidx.core.app.ActivityCompat;
import androidx.percentlayout.widget.PercentRelativeLayout;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuJianWangGuanNewActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.banbenxin_linear)
    LinearLayout banbenxin_linear;
    @InjectView(R.id.banben_pic)
    ImageView icon_banbengenxin;
    @InjectView(R.id.new_gujian_promat_txt)
    TextView new_gujian_promat_txt;
    @InjectView(R.id.banben_progress_linear)
    LinearLayout banben_progress_linear;
    //    @InjectView(R.id.progress)
//    ProgressBar progress;
    @InjectView(R.id.current_gujian_version_linear)
    LinearLayout current_gujian_version_linear;
    @InjectView(R.id.current_gujian_version_txt)
    TextView current_gujian_version_txt;
    @InjectView(R.id.new_gujian_version_txt)
    TextView new_gujian_version_txt;
    @InjectView(R.id.upgrade_rel)
    PercentRelativeLayout upgrade_rel;
    @InjectView(R.id.btn_upgrade)
    Button btn_upgrade;
    private String version;
    private boolean isupgrade;//是否正在更新固态版本
    private TimerTask task;
    private boolean activity_destroy;
    private String newVersion;
    private int tenMSecs = 0;
    private Timer timer = new Timer();
    private TimerTask timerTask = null;
    private TimerTask showTimeTask = null;
    private static final int MSG_WHAT_SHOW_TIME = 1;
    @InjectView(R.id.progress_second)
    TextView progress_second;
    @InjectView(R.id.progress_second__ss)
    TextView progress_second__ss;
    private MessageReceiver mMessageReceiver;
    private DialogUtil dialogUtil;
    public static String UPDATE_GRADE_BOX = "com.massky.sraum.update_grade_box";

    @InjectView(R.id.second)
    TextView second_txt;
    @InjectView(R.id.miao)
    TextView miao;
    private boolean is_index;
    private String areaNumber;
    private String gatewayNumber;


    @Override
    protected int viewId() {
        return R.layout.gujian_wangguan_btn_new;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        registerMessageReceiver();
        dialogUtil = new DialogUtil(this);
        String newVersion = (String) getIntent().getSerializableExtra("newVersion");
        String currentVersion = (String) getIntent().getSerializableExtra("currentVersion");
        String doit = (String) getIntent().getSerializableExtra("doit");
        areaNumber  =  (String) getIntent().getSerializableExtra("areaNumber");
        gatewayNumber = (String) getIntent().getSerializableExtra("number");
//        intent.putExtra("newVersion",newVersion);
//        intent.putExtra("currentVersion",currentVersion);

        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        switch (doit) {
            case "doit":
                start_upgrade(newVersion, currentVersion);
                break;
            case "scuess":
                update_complete_response();
                break;
        }

        onEvent1();
        onData1();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    /**
     * 开始更新
     *
     * @param newVersion
     * @param currentVersion
     */
    private void start_upgrade(String newVersion, String currentVersion) {
        version = "old_version";
        switch (version) {
            case "new_version":
                banbenxin_linear.setVisibility(View.VISIBLE);
                icon_banbengenxin.setImageResource(R.drawable.icon_edition);
                current_gujian_version_linear.setVisibility(View.VISIBLE);
                break;
            case "old_version":
                banbenxin_linear.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
                icon_banbengenxin.setImageResource(R.drawable.pic_youshengji);
                new_gujian_promat_txt.setVisibility(View.VISIBLE);
                current_gujian_version_linear.setVisibility(View.VISIBLE);
                upgrade_rel.setVisibility(View.VISIBLE);
                current_gujian_version_txt.setText("当前固件版本:" + currentVersion);
                new_gujian_version_txt.setText("最新固件版本:" + newVersion);
//                new_gujian_promat_txt.setText(newVersion);
                break;
        }

        // 向Handler发送消息
        showTimeTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(MSG_WHAT_SHOW_TIME);
            }
        };
        // 开始计时
        timer.schedule(showTimeTask, 200, 200);

    }

    protected void onEvent1() {
        back.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
    }

    private void onData1() {
        next_step_txt.setOnClickListener(this);
        Intent intent = getIntent();
//        if (intent == null) return;
//        String excute = (String) intent.getSerializableExtra("excute");
//        switch (excute) {
//            case "auto"://自动
//                rel_scene_set.setVisibility(View.GONE);
//                break;
//            default:
//                rel_scene_set.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_txt:
//                GuanLianSceneBtnActivity.this.finish();
                GuJianWangGuanNewActivity.this.finish();
                break;
            case R.id.back:
            case R.id.btn_upgrade://更新固件
                back_response();
                break;
        }
    }

    /**
     * 按返回键的事件响应
     */
    private void back_response() {
        switch (btn_upgrade.getText().toString()) {
            case "取消":
                btn_upgrade.setText("升级");
                activity_destroy = true;
                banben_progress_linear.setVisibility(View.GONE);
                banbenxin_linear.setVisibility(View.VISIBLE);
                new_gujian_promat_txt.setVisibility(View.VISIBLE);
                current_gujian_version_linear.setVisibility(View.VISIBLE);
                break;
            case "升级":
                btn_upgrade.setText("升级中");
                banbenxin_linear.setVisibility(View.GONE);
                new_gujian_promat_txt.setVisibility(View.GONE);
//                        current_gujian_version_linear.setVisibility(View.GONE);
                banben_progress_linear.setVisibility(View.VISIBLE);
//                startTimer();
                init_timer();
                updatebox_version();
                break;
            case "返回":
              GuJianWangGuanNewActivity.this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        back_response();
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
                                        GuJianWangGuanNewActivity.this.finish();
                                        //停止添加网关
//                                        is_index = false;
                                        miao.setText("00");
                                        second[0] = 0;
                                        minute[0] = 0;
                                        miao.setText("00");
                                        stopTimer();
                                        GuJianWangGuanNewActivity.this.finish();
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

    // 开始
    private void startTimer() {
        if (timerTask == null) {
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    tenMSecs++;
                }
            };
            timer.schedule(timerTask, 10, 10);
        }
    }

    // 结束
    private void stopTimer() {
//        if (timerTask != null) {
//            timerTask.cancel();
//            timerTask = null;
//        }
//
        is_index = false;
    }

    // 取消计时
    public void onDestory() {
        timer.cancel();
        unregisterReceiver(mMessageReceiver);
    }

    int second = 0;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 如果消息匹配，则将相应时间计算后显示在相应TextView上
                case MSG_WHAT_SHOW_TIME:
//                    tvHour.setText(tenMSecs/100/60/60);
//                    tvMinute.setText(tenMSecs/100/60%60);
//                    tvSecond.setText(tenMSecs/100%60);
//                    tvMSecond.setText(tenMSecs%100);
                    int temp = 0;
                    temp = tenMSecs / 100 % 60;
                    if (tenMSecs / 100 / 60 % 60 == 1) {
                        if (second == 1) {
                            onDestory();
                            stopTimer();
//                            GuJianWangGuanActivity.this.finish();
                        }
                        second--;
                    } else {
                        second = 90 - temp;
                    }
                    progress_second__ss.setText(tenMSecs % 100 + "");
                    progress_second.setText(second + "");
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity_destroy = true;
        is_index = false;
    }


    /**
     * 查看固件更新处于什么阶段
     */
    Handler handler_upgrade = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    update_complete_response();
                    break;
                case 1:

                    break;
            }
        }
    };

    /**
     * 更新完成的页面跳转
     */
    private void update_complete_response() {
        banben_progress_linear.setVisibility(View.GONE);
        upgrade_rel.setVisibility(View.VISIBLE);
        btn_upgrade.setText("返回");
        banbenxin_linear.setVisibility(View.VISIBLE);
        new_gujian_promat_txt.setVisibility(View.GONE);
        icon_banbengenxin.setImageResource(R.drawable.icon_edition);
        current_gujian_version_linear.setVisibility(View.VISIBLE);
        current_gujian_version_txt.setText("当前已经是最新版本");
        new_gujian_version_txt.setText(newVersion);
    }


    /**
     * 升级网关
     */

    private void updatebox_version() {
        //在这里先调
        //设置网关模式-sraum-setBox
        Map map = new HashMap();
//        String phoned = getDeviceId(this);
        String regId = (String) SharedPreferencesUtil.getData(GuJianWangGuanNewActivity.this, "regId", "");
        map.put("token", TokenUtil.getToken(this));
        map.put("boxNumber", gatewayNumber);
        map.put("regId", regId);
        map.put("areaNumber",areaNumber);
//        map.put("status", "0");//进入设置模式
//        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_updateGateway, map, new Mycallback(new AddTogglenInterfacer() {

                    @Override
                    public void addTogglenInterfacer() {
//
                        updatebox_version();

                    }
                }, GuJianWangGuanNewActivity.this, dialogUtil) {
                    @Override
                    public void onSuccess(User user) {

                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(GuJianWangGuanNewActivity.this, "areaNumer 不\n" +
                                "存在 ");
                    }

                    @Override
                    public void threeCode() {
                        //number 不存在
                        ToastUtil.showToast(GuJianWangGuanNewActivity.this, "number 不存在");
                    }
                }
        );
    }

    /**
     * 获取手机唯一标示码
     *
     * @param context
     * @return
     */
    public String getDeviceId(Context context) {
        String id;
        //android.telephony.TelephonyManager
        TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(GuJianWangGuanNewActivity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            return "";
        }
        if (mTelephony.getDeviceId() != null) {
            id = mTelephony.getDeviceId();
        } else {
            //android.provider.Settings;
            id = Settings.Secure.getString(context.getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return id;
    }

    /**
     * 动态注册广播
     */
    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_GRADE_BOX);
        registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(UPDATE_GRADE_BOX)) {
                int messflag = intent.getIntExtra("notifactionId", 0);
                String panelid = intent.getStringExtra("panelid");
                if (messflag == 50) {//notifactionId = 8 ->设置网关模式，sraum_setBox
                    //收到服务器端设置网关成功以后，跳转到修改面板名称，以及该面板下设备列表名称

                    //在网关转圈界面，下去拉设备，判断设备类型，不是我们的。网关不关，是我们的设备类型；在关网关。
                    //然后跳转到显示设备列表界面。
//                    ToastUtil.showToast(MacdeviceActivity.this,"messflag:" + messflag);
//                    getPanel_devices(panelid);
//                    String panelid = "62a001ff1006,1";
                    String[] items = panelid.split(",");
                    if (items != null) {
                        if (items[1].equals("1")) {
                            ToastUtil.showToast(GuJianWangGuanNewActivity.this, "网关" +
                                    "升级成功");
                            onDestory();
                            stopTimer();
                            handler_upgrade.sendEmptyMessage(0);
                           GuJianWangGuanNewActivity.this.finish();
                        } else {
                            ToastUtil.showToast(GuJianWangGuanNewActivity.this, "网关" +
                                    "升级失败");
                            GuJianWangGuanNewActivity.this.finish();
                        }
                    }
                }
            }
        }
    }
}
