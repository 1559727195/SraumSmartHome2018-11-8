package com.ipcamera.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.ipcamera.demo.adapter.VideoTimingAdapter;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.Map;

import butterknife.InjectView;
import vstc2.nativecaller.NativeCaller;

import static com.ipcamera.demo.SettingSDCardActivity.strDID;
import static com.ipcamera.demo.SettingSDCardActivity.strPWD;
import static com.ipcamera.demo.SettingSDCardActivity.switchBean;

public class PlanToVideoActivity extends BaseActivity implements OnClickListener {

    /**
     * 添加计划录像
     */
    private MyListView lv_video_plan; //计划录像列表
    private VideoTimingAdapter adapter = null;
    private Map<Integer, Integer> planmap;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    private static String cmark = "147258369"; //APP唯一标示



//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getDataFromOther();
////		setContentView(R.layout.plan_to_video_message);
//		findView();
//		setLister();
//
//	}

    @Override
    protected int viewId() {
        return R.layout.plan_to_video_message;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
        getDataFromOther();
//		setContentView(R.layout.plan_to_video_message);
        findView();
        setLister();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void getDataFromOther() {
        Intent intent = getIntent();
//		strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
//		strPWD = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
        planmap = (Map<Integer, Integer>) intent.getSerializableExtra("planmap");
    }

    public void setLister() {
        back.setOnClickListener(this);
//        rl_add_infoplan.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    //初始化控件
    public void findView() {
        lv_video_plan = (MyListView) findViewById(R.id.lv_video_plan);
        //计划录像
        adapter = new VideoTimingAdapter(PlanToVideoActivity.this, new VideoTimingAdapter.VideoTimingListener() {
            @Override
            public void delete(int position) {
                //弹出框
                Map<Integer, Integer> item = adapter.sdtiming.get(position);
                int itemplan = item.entrySet().iterator().next().getValue();
                int key = item.entrySet().iterator().next().getKey();
//                int key = data.getIntExtra("key", -1);
                if (key == -1)
                    return;
                Message msg = new Message();
                msg.what = 1;
                msg.arg1 = key;
                deleteHandler.sendMessage(msg);
            }

            @Override
            public void onItemClick(int position) {
                Map<Integer, Integer> item = adapter.sdtiming.get(position);
                int itemplan = item.entrySet().iterator().next().getValue();
                int itemplanKey = item.entrySet().iterator().next().getKey();
                Intent it = new Intent(PlanToVideoActivity.this,
                        SCameraSetSDCardVideoTimingActivity.class);
                it.putExtra("type", 1);
                it.putExtra("value", itemplan);
                it.putExtra("key", itemplanKey);
                startActivityForResult(it, 1);
            }
        });
        lv_video_plan.setAdapter(adapter);
        callbackHandler.sendEmptyMessage(1);

    }


    private Handler callbackHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    for (int i = 1; i < 22; i++) {
                        int plan = planmap.get(i);
                        if (plan != 0 && plan != -1) {
                            adapter.addPlan(i, plan);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
//            case R.id.rl_add_infoplan:
//                Intent intent1 = new Intent(PlanToVideoActivity.this,
//                        SCameraSetPushVideoTiming.class);
//                intent1.putExtra("type", 0);
//                startActivityForResult(intent1, 0);
//                break;
            case R.id.next_step_txt:
                Intent it = new Intent(PlanToVideoActivity.this,
                        SCameraSetSDCardVideoTimingActivity.class);
                it.putExtra("type", 0);
                startActivityForResult(it, 0);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub

        if (resultCode == 2015) {
            int time = data.getIntExtra("jnitime", 1);
            Message msg = new Message();
            msg.what = 0;
            msg.arg1 = time;
            upHandler.sendMessage(msg);
        }

        if (resultCode == 2016) {
            int time = data.getIntExtra("jnitime", 1);
            int key = data.getIntExtra("key", -1);
            if (key == -1)
                return;
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = time;
            msg.arg2 = key;
            upHandler.sendMessage(msg);
        }

        if (resultCode == 2017) {
            int key = data.getIntExtra("key", -1);
            if (key == -1)
                return;
            Message msg = new Message();
            msg.what = 1;
            msg.arg1 = key;
            deleteHandler.sendMessage(msg);
        }
    }


    //计划录像
    private Handler upHandler = new Handler() {
        public void handleMessage(Message msg) {
            int what = msg.what;
            int time = msg.arg1;
            switch (what) {
                case 0:
                    for (int i = 1; i < 22; i++) {
                        int value = planmap.get(i);

                        if (value == -1 || value == 0) {
                            planmap.put(i, time);
                            adapter.addPlan(i, time);
                            break;
                        }
                    }

                    adapter.notifyDataSetChanged();
                    setAlarmHandler.sendEmptyMessage(1);
                    break;
                case 1:
                    int key = msg.arg2;
                    planmap.put(key, time);
                    adapter.notify(key, time);
                    adapter.notifyDataSetChanged();
                    setAlarmHandler.sendEmptyMessage(1);
                    break;

                default:
                    break;
            }

        }
    };

    /**
     * 删除计划
     */
    private Handler deleteHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int key = msg.arg1;
                    adapter.removePlan(key);
                    planmap.put(key, -1);
                    adapter.notifyDataSetChanged();
                    setAlarmHandler.sendEmptyMessage(1);
                    break;
                default:
                    break;
            }
        }

        ;
    };

    private Handler setAlarmHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    setTiming(strDID, strPWD);
                    break;
                default:
                    break;
            }
        }
    };

    //设置计划录像
    private void setTiming(String udid, String pwd) {
        NativeCaller
                .TransferMessage(udid,
                        "trans_cmd_string.cgi?cmd=2017&command=3&mark=" + cmark + "&record_plan1="
                                + planmap.get(1) + "&record_plan2=" + planmap.get(2) + "&record_plan3="
                                + planmap.get(3) + "&record_plan4=" + planmap.get(4) + "&record_plan5="
                                + planmap.get(5) + "&record_plan6=" + planmap.get(6) + "&record_plan7="
                                + planmap.get(7) + "&record_plan8=" + planmap.get(8) + "&record_plan9="
                                + planmap.get(9) + "&record_plan10=" + planmap.get(10) + "&record_plan11="
                                + planmap.get(11) + "&record_plan12=" + planmap.get(12) + "&record_plan13="
                                + planmap.get(13) + "&record_plan14=" + planmap.get(14) + "&record_plan15="
                                + planmap.get(15) + "&record_plan16=" + planmap.get(16) + "&record_plan17="
                                + planmap.get(17) + "&record_plan18=" + planmap.get(18) + "&record_plan19="
                                + planmap.get(19) + "&record_plan20=" + planmap.get(20) + "&record_plan21="
                                + planmap.get(21) + "&record_plan_enable=" + switchBean.getRecord_plan_enable() + "&loginuse=" + "admin" + "&loginpas="
                                + pwd, -1);
    }

}
