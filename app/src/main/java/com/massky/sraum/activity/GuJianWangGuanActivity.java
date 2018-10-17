package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuJianWangGuanActivity extends BaseActivity {
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
    @InjectView(R.id.progress_txt)
    TextView progress_txt;
    @InjectView(R.id.progress)
    ProgressBar progress;

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
    private Timer timer;
    private TimerTask task;
    private boolean activity_destroy;


    @Override
    protected int viewId() {
        return R.layout.gujian_wangguan_btn;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        version = "old_version";
        switch (version) {
            case "new_version" :
                banbenxin_linear.setVisibility(View.VISIBLE);
                icon_banbengenxin.setImageResource(R.drawable.icon_banben);
                current_gujian_version_linear.setVisibility(View.VISIBLE);

                break;
            case "old_version" :
                banbenxin_linear.setVisibility(View.VISIBLE);
                icon_banbengenxin.setImageResource(R.drawable.icon_banbengenxin);
                new_gujian_promat_txt.setVisibility(View.VISIBLE);
                current_gujian_version_linear.setVisibility(View.VISIBLE);
                upgrade_rel.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_upgrade.setOnClickListener(this);
    }

    @Override
    protected void onData() {
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
                GuJianWangGuanActivity.this.finish();
                break;
            case R.id.back:
                GuJianWangGuanActivity.this.finish();
                break;
            case R.id.btn_upgrade://更新固件
                switch (btn_upgrade.getText().toString()) {
                    case "取消" :
                        btn_upgrade.setText("更新");
                        activity_destroy = true;
                        banben_progress_linear.setVisibility(View.GONE);
                        banbenxin_linear.setVisibility(View.VISIBLE);
                        new_gujian_promat_txt.setVisibility(View.VISIBLE);
                        current_gujian_version_linear.setVisibility(View.VISIBLE);
                        break;
                    case  "更新" :
                        btn_upgrade.setText("取消");
                        banbenxin_linear.setVisibility(View.GONE);
                        new_gujian_promat_txt.setVisibility(View.GONE);
                        current_gujian_version_linear.setVisibility(View.GONE);
                        banben_progress_linear.setVisibility(View.VISIBLE);
                        initTimer();
                        break;
                }

                break;
        }
    }

    private int add = 0;
    /**
     * 初始化定时器
     */
    private void initTimer() {
        activity_destroy = false;
//        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
//        if (task == null)
            task = new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            three_second_btn.setText(add + "秒跳过");
                            add++;
                            progress.setProgress(10 * add);
                            progress_txt.setText(10 * add + "%");
                        }
                    });
                    Log.e("robin debug","add:" + add);
                    if (add >= 10) {//3min= 1000 * 60 * 3
                        try {
                            closeTimer();
                            //更新完成
                            handler_upgrade.sendEmptyMessage(0);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (activity_destroy) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                        try {
                            closeTimer();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
        timer.schedule(task, 1, 1000); // 1s后执行task,经过1ms再次执行
    }

    /**
     * 关闭定时器和socket客户端
     * @throws IOException
     */
    private void closeTimer() throws IOException {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (task != null) {
            task.cancel();
            task = null;
        }
        add = 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity_destroy = true;
    }


    /**
     * 查看固件更新处于什么阶段
     */
    Handler handler_upgrade = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0 :
                    banben_progress_linear.setVisibility(View.GONE);
                    upgrade_rel.setVisibility(View.GONE);

                    banbenxin_linear.setVisibility(View.VISIBLE);
                    icon_banbengenxin.setImageResource(R.drawable.icon_banben);
                    current_gujian_version_linear.setVisibility(View.VISIBLE);

                    current_gujian_version_txt.setText("当前已经是最新版本");
                    new_gujian_version_txt.setText("固件版本v1.1.305");

                    break;
                case 1:

                    break;
            }
        }
    };
}
