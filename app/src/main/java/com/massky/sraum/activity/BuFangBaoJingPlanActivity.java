package com.massky.sraum.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.AddTogenInterface.AddTogglenInterfacer;
import com.ipcamera.demo.BaseActivity;
import com.ipcamera.demo.MyListView;
import com.ipcamera.demo.SCameraSetPushVideoTimingActivity;
import com.ipcamera.demo.adapter.PushVideoTimingAdapter;
import com.ipcamera.demo.bean.AlermBean;
import com.ipcamera.demo.bean.SwitchBean;
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
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

public class BuFangBaoJingPlanActivity extends com.massky.sraum.base.BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    private DialogUtil dialogUtil;
    public static String strDID;
    private static String strPWD;
    private static SwitchBean switchBean;
    private static AlermBean alermBean;
    private static String pushmark = "147258369";
    @InjectView(R.id.lv_info_plan)
    MyListView lv_info_plan;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    private static HashMap<Integer, Integer> pushplan;
    private PushVideoTimingAdapter pushAdapter;
    @InjectView(R.id.back)
    ImageView back;
    private List<Map> list_camera_list = new ArrayList<>();//录像列表
    private String areaNumber;

    @Override
    protected int viewId() {
        return R.layout.bufang_baojing_plan;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        getDataFromOther();
        switchBean = new SwitchBean();
        alermBean = new AlermBean();
        findView();
        setLister();

    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void setLister() {
        next_step_txt.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    private void getDataFromOther() {
        Intent intent = getIntent();
        strDID = intent.getStringExtra(ContentCommon.STR_CAMERA_ID);
        strPWD = intent.getStringExtra(ContentCommon.STR_CAMERA_PWD);
        areaNumber  = (String) getIntent().getSerializableExtra("areaNumber");

    }

    //初始化控件
    public void findView() {

        // 移动侦测推送
        pushplan = new HashMap<Integer, Integer>();
        pushAdapter = new PushVideoTimingAdapter(BuFangBaoJingPlanActivity.this, new PushVideoTimingAdapter.PushVideoTimingListener() {
            @Override
            public void delete(int position) {
                //弹出框
                Map item = list_camera_list.get(position);
                //sraum_deleteWifiCameraTimeZone
                sraum_deleteWifiCameraTimeZone((String) item.get("number"));


//                deleteHandler.sendMessage(msg);
            }

            @Override
            public void onItemClick(int position) {
//                Map<Integer, Integer> item = pushAdapter.movetiming.get(position);
//                int itemplan = item.entrySet().iterator().next().getValue();
//                int itemplanKey = item.entrySet().iterator().next().getKey();
                Intent it = new Intent(BuFangBaoJingPlanActivity.this,
                        SCameraSetPushVideoTimingActivity.class);
                it.putExtra("type", 1);//编辑
                Map item = list_camera_list.get(position);
                it.putExtra("map_item_record", (Serializable) item);
                it.putExtra("areaNumber",areaNumber);
//                it.putExtra("value", itemplan);
//                it.putExtra("key", itemplanKey);
                startActivityForResult(it, 1);
            }
        });
        lv_info_plan.setAdapter(pushAdapter);
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String panelNumber, final String name, final String type,
                                       final SwipeMenuLayout finalConvertView) {

        View view = LayoutInflater.from(BuFangBaoJingPlanActivity.this).inflate(R.layout.promat_dialog, null);
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
        final Dialog dialog = new Dialog(BuFangBaoJingPlanActivity.this, R.style.BottomDialog);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        finalConvertView.closeMenu();
                    }
                });
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                sraum_deletepanel(panelNumber, type, dialog, finalConvertView);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                BuFangBaoJingPlanActivity.this.finish();
                break;
            case R.id.next_step_txt:
                Intent intent1 = new Intent(BuFangBaoJingPlanActivity.this,
                        SCameraSetPushVideoTimingActivity.class);
                intent1.putExtra("type", 0);
                intent1.putExtra("areaNumber",areaNumber);
                startActivityForResult(intent1, 0);
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        sraum_getWifiCameraTimeZone();
    }

    /**
     * 获取摄像头移动侦测布防计划列表
     */
    private void sraum_getWifiCameraTimeZone() {
        dialogUtil.loadDialog();
        Map<String, Object> mapbox = new HashMap<String, Object>();
        mapbox.put("token", TokenUtil.getToken(BuFangBaoJingPlanActivity.this));
//        String areaNumber = (String) SharedPreferencesUtil.getData(BuFangBaoJingPlanActivity.this,"areaNumber","");
        mapbox.put("number", strDID);
        mapbox.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getWifiCameraTimeZone, mapbox, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_getWifiCameraTimeZone();
            }
        }, BuFangBaoJingPlanActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                list_camera_list.clear();
                for (int i = 0; i < user.list.size(); i++) {
                    Map map_camera_time_zone = new HashMap();
                    map_camera_time_zone.put("number", user.list.get(i).number);
                    map_camera_time_zone.put("startTime", user.list.get(i).startTime);
                    map_camera_time_zone.put("endTime", user.list.get(i).endTime);
                    map_camera_time_zone.put("monday", user.list.get(i).monday);
                    map_camera_time_zone.put("tuesday", user.list.get(i).tuesday);
                    map_camera_time_zone.put("wednesday", user.list.get(i).wednesday);
                    map_camera_time_zone.put("thursday", user.list.get(i).thursday);
                    map_camera_time_zone.put("friday", user.list.get(i).friday);
                    map_camera_time_zone.put("saturday", user.list.get(i).saturday);
                    map_camera_time_zone.put("sunday", user.list.get(i).sunday);
                    list_camera_list.add(map_camera_time_zone);
                }
                pushAdapter.setList(list_camera_list);
                pushAdapter.notifyDataSetChanged();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(BuFangBaoJingPlanActivity.this, "token 错误");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(BuFangBaoJingPlanActivity.this, "Number\n" +
                        "不正确");
            }
        });
    }


    /**
     * 删除摄像头移动侦测布防计划
     */
    private void sraum_deleteWifiCameraTimeZone(final String id) {
        dialogUtil.loadDialog();
        Map<String, Object> mapbox = new HashMap<String, Object>();
        mapbox.put("token", TokenUtil.getToken(BuFangBaoJingPlanActivity.this));
        mapbox.put("id", id);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteWifiCameraTimeZone, mapbox, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                sraum_deleteWifiCameraTimeZone(id);
            }
        }, BuFangBaoJingPlanActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);

                for (int i = 0; i < list_camera_list.size(); i++) {
                    if (list_camera_list.get(i).get("number").toString().equals(id)) {
                        list_camera_list.remove(i);
                    }
                }
                pushAdapter.setList(list_camera_list);
                pushAdapter.notifyDataSetChanged();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                ToastUtil.showToast(BuFangBaoJingPlanActivity.this, "token 错误");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                ToastUtil.showToast(BuFangBaoJingPlanActivity.this, "Number\n" +
                        "不正确");
            }
        });
    }

}
