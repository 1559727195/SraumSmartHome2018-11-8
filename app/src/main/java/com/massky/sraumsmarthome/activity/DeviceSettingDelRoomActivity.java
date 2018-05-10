package com.massky.sraumsmarthome.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/16.
 */

public class DeviceSettingDelRoomActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.other_information)
    RelativeLayout other_information;
    @InjectView(R.id.other_scroll)
    ScrollView other_scroll;
    @InjectView(R.id.wangguan_set_rel)
    RelativeLayout wangguan_set_rel;
    @InjectView(R.id.delete_device_rel)
    RelativeLayout delete_device_rel;
    @InjectView(R.id.room_list_rel)
    RelativeLayout room_list_rel;

    @Override
    protected int viewId() {
        return R.layout.devicesetting_delroom;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        other_information.setOnClickListener(this);
        wangguan_set_rel.setOnClickListener(this);
        delete_device_rel.setOnClickListener(this);
        room_list_rel.setOnClickListener(this);
    }

    @Override
    protected void onData() {//"主卫水浸","防盗门门磁","防盗门猫眼","人体监测","防盗门门锁"

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                DeviceSettingDelRoomActivity.this.finish();//
                break;
            case R.id.other_information:
                switch (other_scroll.getVisibility()) {
                    case View.VISIBLE:
                        other_scroll.setVisibility(View.GONE);
//                        other_information.setBackgroundColor(getResources().getColor(R.color.light_gray));
                        break;
                    case View.GONE:
                        other_scroll.setVisibility(View.VISIBLE);
//                        other_information.setBackgroundColor(getResources().getColor(R.color.white));
                        break;
                }
                break;//其他信息
            case R.id.wangguan_set_rel:
                startActivity(new Intent(DeviceSettingDelRoomActivity.this,ChangeDeviceNameActivity.class));
                break;
            case R.id.delete_device_rel:
                showCustomDialog();
                break;//删除设备
            case R.id.room_list_rel:
                startActivity(new Intent(DeviceSettingDelRoomActivity.this,SelectRoomActivity.class));
                break;//房间列表
        }
    }

    //自定义dialog
    public void showCustomDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(DeviceSettingDelRoomActivity.this).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(this,R.style.BottomDialog);
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
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
