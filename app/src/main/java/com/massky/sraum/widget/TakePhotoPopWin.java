package com.massky.sraum.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;

/**
 * Created by zhu on 2018/1/10.
 */

public class TakePhotoPopWin extends PopupWindow {
    private Context mContext;

    private View view;

    private TextView btn_cancel;


    public TakePhotoPopWin(Context mContext, PopWindowListener itemsOnClick, final View view) {
        this.view = view;
        // 设置外部可点击
        this.setOutsideTouchable(true);

    /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置弹出窗体的背景
        this.setBackgroundDrawable(dw);

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.time_detail_anim);

    }

    public  interface  PopWindowListener {
            void confirm(String time);
    }
}

