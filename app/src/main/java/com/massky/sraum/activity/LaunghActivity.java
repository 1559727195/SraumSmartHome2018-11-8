package com.massky.sraum.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.example.jpushdemo.Constants;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.App;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2017/12/25.
 */

public class LaunghActivity extends BaseActivity {

    private static final String PROCESS_NAME = "com.massky.sraum";
    private Timer timer;
    private TimerTask task;
    private boolean activity_destroy;//activity是否被销毁
    @InjectView(R.id.btn_next_step)
    Button btn_next_step;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.three_second_btn)
    Button three_second_btn;
    private List<Map> areaList = new ArrayList<>();
    private List<Map> roomList = new ArrayList<>();//fang jian lie biao
    private List<Map> list = new ArrayList<>();

    @Override
    protected int viewId() {
        return R.layout.laugh_layout;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        setAnyBarAlpha(0);//设置状态栏的颜色为透明
//        StatusUtils.setFullToNavigationBar(this); // NavigationBar.
        isAppMainProcess();
        initTimer();
    }


    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public void isAppMainProcess() {

        int pid = android.os.Process.myPid();//4731
        int pid_past = (int) SharedPreferencesUtil.getData(LaunghActivity.this, "pid", 0);
        if (pid_past == pid) {//则说明app没有被杀死,直接跳转
            tiaozhuan();
            SharedPreferencesUtil.saveData(LaunghActivity.this, "newProcess", false);
            return;
        } else {
            SharedPreferencesUtil.saveData(LaunghActivity.this, "newProcess", true);
            saveProcess();
        }
    }

    /**
     * 判断是不是UI主进程，因为有些东西只能在UI主进程初始化
     */
    public void saveProcess() {
        int pid = android.os.Process.myPid();//4731
        SharedPreferencesUtil.saveData(LaunghActivity.this, "pid", pid);
    }


    @Override
    protected void onEvent() {
        btn_next_step.setOnClickListener(this);
        three_second_btn.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    protected void onResume() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sraum_getAllArea();
            }
        }).start();
        super.onResume();
    }

    /**
     * 获取所有区域
     */
    private void sraum_getAllArea() {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(LaunghActivity.this));
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(SelectSensorActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_getAllArea
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getAllArea();
                    }
                }, LaunghActivity.this, null) {


                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        super.wrongBoxnumber();
                    }

                    @Override
                    public void onSuccess(final User user) {
                        areaList = new ArrayList<>();
                        for (int i = 0; i < user.areaList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            switch (user.areaList.get(i).authType) {
                                case "1":
                                    mapdevice.put("name", user.areaList.get(i).areaName + "(" + "业主" + ")");
                                    break;
                                case "2":
                                    mapdevice.put("name", user.areaList.get(i).areaName + "(" + "成员" + ")");
                                    break;
                            }
//
//                            mapdevice.put("name", user.areaList.get(i).areaName);
                            mapdevice.put("number", user.areaList.get(i).number);
                            mapdevice.put("sign", user.areaList.get(i).sign);
                            mapdevice.put("authType", user.areaList.get(i).authType);
                            mapdevice.put("roomCount", user.areaList.get(i).roomCount);
                            areaList.add(mapdevice);
                        }

                        if (areaList.size() != 0) {
                            SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().getApplicationContext(), "areaList", areaList);
                        } else {
                            SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().getApplicationContext(), "areaList", new ArrayList<Map>());
                        }
                        if (areaList.size() != 0) {//区域命名
                            for (Map map : areaList) {
                                if ("1".equals(map.get("sign").toString())) {
                                    sraum_getRoomsInfo(map.get("number").toString());
                                    break;
                                }
                            }
                        }
                    }
                });
    }


    /**
     * 获取区域的所有房间信息
     *
     * @param areaNumber
     */
    private void sraum_getRoomsInfo(final String areaNumber) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(LaunghActivity.this));
        mapdevice.put("areaNumber", areaNumber);
        MyOkHttp.postMapString(ApiHelper.sraum_getRoomsInfo
                , mapdevice, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {//刷新togglen数据
                        sraum_getRoomsInfo(areaNumber);
                    }
                }, LaunghActivity.this, null) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        super.onError(call, e, id);
                    }

                    @Override
                    public void pullDataError() {
                        super.pullDataError();
                    }

                    @Override
                    public void emptyResult() {
                        super.emptyResult();
                    }

                    @Override
                    public void wrongToken() {
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {

                    }

                    @Override
                    public void onSuccess(final User user) {
                        //qie huan cheng gong ,获取区域的所有房间信息
                        roomList = new ArrayList<>();
                        for (int i = 0; i < user.roomList.size(); i++) {
                            Map<String, String> mapdevice = new HashMap<>();
                            mapdevice.put("name", user.roomList.get(i).name);
                            mapdevice.put("number", user.roomList.get(i).number);
                            mapdevice.put("count", user.roomList.get(i).count);
                            roomList.add(mapdevice);
                        }
                        if (roomList.size() != 0) {
                            SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().getApplicationContext(), "roomList", roomList);
                        } else {
                            SharedPreferencesUtil.saveInfo_Second_List(App.getInstance().getApplicationContext(), "roomList", new ArrayList<Map>());
                        }
                    }

                    @Override
                    public void threeCode() {

                    }
                });
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
                    Log.e("robin debug", "add:" + add);
                    if (add <= 0) {//3min= 1000 * 60 * 3
                        try {
                            add = 0;
                            closeTimer();
                            tiaozhuan();

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
     * 跳转到主界面
     */
    private void tiaozhuan() {
        boolean flag = (boolean) SharedPreferencesUtil.getData(LaunghActivity.this, "loginflag", false);
        //登录状态保存
        if (flag) {
//            IntentUtil.startActivityAndFinishFirst(LoginActivity.this, MainfragmentActivity.class);
            Intent intent = new Intent(LaunghActivity.this, MainGateWayActivity.class);
            if (getIntent().getBundleExtra(Constants.EXTRA_BUNDLE) != null) {
                intent.putExtra(Constants.EXTRA_BUNDLE,
                        getIntent().getBundleExtra(Constants.EXTRA_BUNDLE));
            }
            startActivity(intent);
//            SharedPreferencesUtil.saveData(LoginActivity.this,"loginflag",true);
            finish();
        } else {
            //跳转到 启动第二页
            startActivity(new Intent(LaunghActivity.this
                    , LaunghSecondActivity.class));
            LaunghActivity.this.finish();
        }
    }

    /**
     * 关闭定时器和socket客户端
     *
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
                startActivity(new Intent(LaunghActivity.this, NextStepActivity.class));
                break;
            case R.id.three_second_btn:
                add = 0;
                try {
                    closeTimer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                tiaozhuan();
                activity_destroy = true;
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
