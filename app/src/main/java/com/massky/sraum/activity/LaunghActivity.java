package com.massky.sraum.activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import butterknife.InjectView;

/**
 * Created by zhu on 2017/12/25.
 */

public class LaunghActivity extends BaseActivity {

    private Timer timer;
    private TimerTask task;
    private boolean activity_destroy;//activity是否被销毁
    @InjectView(R.id.btn_next_step)
    Button btn_next_step;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.three_second_btn)
    Button three_second_btn;

    @Override
    protected int viewId() {
        return R.layout.laugh_layout;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        setAnyBarAlpha(0);//设置状态栏的颜色为透明
//        StatusUtils.setFullToNavigationBar(this); // NavigationBar.
        initTimer();
    }

    @Override
    protected void onEvent() {
        btn_next_step.setOnClickListener(this);
        three_second_btn.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }


    private int add = 3;
    /**
     * 初始化定时器
     */
    private void initTimer() {
        if (timer == null)
            timer = new Timer();
        //3min
        //关闭clientSocket即客户端socket
        if (task == null)
            task = new TimerTask() {

                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            three_second_btn.setText(add + "秒跳过");
                            add--;
                        }
                    });
                    Log.e("robin debug","add:" + add);
                    if (add <= 0) {//3min= 1000 * 60 * 3
                        try {
                            add = 0;
                            closeTimer();
                            //跳转到 启动第二页
                            startActivity(new Intent(LaunghActivity.this
                            ,LaunghSecondActivity.class));
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
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next_step:
                startActivity(new Intent(LaunghActivity.this,NextStepActivity.class));
                break;
            case R.id.three_second_btn:

                break;//3秒跳转
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activity_destroy = true;
    }

    /*
 *动态设置状态栏的颜色
 */
    private void setAnyBarAlpha(int alpha) {
//        mToolbar.getBackground().mutate().setAlpha(alpha);
       statusView.getBackground().mutate().setAlpha(alpha);
    }
}
