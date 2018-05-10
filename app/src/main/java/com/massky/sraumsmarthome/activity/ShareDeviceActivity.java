package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.Util.SysUtil;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.widget.TakePhotoPopWin;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.chenglei.widget.datepicker.DatePicker;
import org.chenglei.widget.datepicker.Sound;

import java.lang.reflect.Field;
import java.util.concurrent.CopyOnWriteArrayList;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/19.
 */

public class ShareDeviceActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.load_app_pic)
    ImageView load_app_pic;
    private PopupWindow popupWindow;
    @InjectView(R.id.input_phonenumber)
    ImageView input_phonenumber;

    @Override
    protected int viewId() {
        return R.layout.share_device_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        load_app_pic.setOnClickListener(this);
        input_phonenumber.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                ShareDeviceActivity.this.finish();
                break;
            case R.id.load_app_pic:
                show_big_pic_popwindow();
                break;//android 图片点击一下就放大到全屏,再点一下就回到原界面
            case R.id.input_phonenumber:
                    startActivity(new Intent(ShareDeviceActivity.this,
                            InputPhoneNumberActivity.class));
                break;//输入手机号码
        }
    }

    /**
     * 显示大 的popwindow
     * @param
     */
    public void show_big_pic_popwindow() {
        View view = LayoutInflater.from(this).inflate(R.layout.pic_big_popwindow, null);
        ImageView erweima_big_pic = view.findViewById(R.id.erweima_big_pic);
        LinearLayout erweima_linear = (LinearLayout) view.findViewById(R.id.erweima_linear);
        DisplayMetrics disMetrics = new DisplayMetrics();
        ShareDeviceActivity.this.getWindowManager().getDefaultDisplay().getMetrics(disMetrics);
        int width = disMetrics.widthPixels;
        int height = disMetrics.heightPixels;
//        ImageView welcomeBackground = (ImageView) findViewById(R.id.welcome_bg);
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.img_erweima);//link the drable image
        SysUtil.setImageBackground(bitmap,erweima_big_pic,width / 2,height / 2);

        popupWindow = new PopupWindow(this);
        // 取消按钮
//        设置Popupwindow显示位置（从底部弹出）
        popupWindow.setContentView(view);
        // 设置弹出窗体的宽和高
        popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);

        //popupWindow置弹出窗体可点击
        popupWindow.setFocusable(true);

//         //实例化一个ColorDrawable颜色为半透明
//        ColorDrawable dw = new ColorDrawable(0xb0000000);
//        // 设置弹出窗体的背景
//        popupWindow.setBackgroundDrawable(dw);
        //设置背景透明才能显示
        popupWindow.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));

        popupWindow.setTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.root_layout),Gravity.CENTER, 0, 0);
        final WindowManager.LayoutParams[] params = {getWindow().getAttributes()};
        //当弹出Popupwindow时，背景变半透明
        params[0].alpha=0.7f;
        getWindow().setAttributes(params[0]);
        //设置Popupwindow关闭监听，当Popupwindow关闭，背景恢复1f
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                params[0] = getWindow().getAttributes();
                params[0].alpha=1f;
                getWindow().setAttributes(params[0]);
            }
        });

        erweima_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
}
