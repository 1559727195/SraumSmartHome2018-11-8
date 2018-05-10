package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;
import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/1/8.
 */

public class AddDeviceScucessActivity extends BaseActivity{
    @InjectView(R.id.next_step_txt)
    ImageView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    private PopupWindow popupWindow;
    private String device_name;
    @InjectView(R.id.light_control_panel)
    LinearLayout light_control_panel;
    @InjectView(R.id.window_linear)
    LinearLayout window_linear;

    @Override
    protected int viewId() {
        return R.layout.adddeivice_scucess;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        device_name = (String) getIntent().getSerializableExtra("device_name");
        switch (device_name){
            case "空调面板":

                break;
            case "PM2.5":

                break;
            case "窗帘面板":
                window_linear.setVisibility(View.VISIBLE);
                break;
            case "灯控面板":
                light_control_panel.setVisibility(View.VISIBLE);
                break;
            case "网关":

                break;
            case "wifi":

                break;
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
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
                showPopWindow();
                break;
            case R.id.back:
                AddDeviceScucessActivity.this.finish();
                break;

            case R.id.btn_login_gateway:
                ApplicationContext.getInstance().finishActivity(ConRouInforActivity.class);
                ApplicationContext.getInstance().finishActivity(AddWifiDeviceActivity.class);
                ApplicationContext.getInstance().finishActivity(SelectZigbeeDeviceActivity.class);
                AddDeviceScucessActivity.this.finish();
                startActivity(new Intent(AddDeviceScucessActivity.this,SelectRoomActivity.class));
                break;
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(AddDeviceScucessActivity.this).inflate(
                    R.layout.add_devsucesspopupwindow, null);
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
            int xoff = dip2px(AddDeviceScucessActivity.this,20);
            popupWindow.showAsDropDown(next_step_txt, 0,dip2px(AddDeviceScucessActivity.this, 10));
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

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp =getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }
}
