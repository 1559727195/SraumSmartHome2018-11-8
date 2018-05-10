package com.massky.sraumsmarthome.Util;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.massky.sraumsmarthome.R;


/**
 * Created by masskywcy on 2016-08-31.
 */
//dialog设置工具类
public class DialogUtil {
    private Context context;
    private View view;
    private Dialog viewDialog;
    private Dialog viewBottomDialog;
    private View viewbottom;
    private int bottom;
    private Dialog progressDialog;

    public DialogUtil(Context context) {
        this.context = context;
    }

    public DialogUtil(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public DialogUtil(Context context, View viewbottom, int bottom) {
        this.context = context;
        this.viewbottom = viewbottom;
        this.bottom = bottom;
    }

    /*用于加载progressbar dialog*/
    public Dialog loadDialog() {
        if (progressDialog == null) {
            progressDialog = new Dialog(context, R.style.progress_dialog);
        }
        progressDialog.setContentView(R.layout.dialog);
        progressDialog.setCancelable(true);//设置它可以取消
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        TextView msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);
        msg.setVisibility(View.GONE);
        msg.setText("卖力加载中");
        progressDialog.show();
        return progressDialog;
    }

    //用于自定义布局dialog加载
    public Dialog loadViewdialog() {
        //判断是否创建过
        if (viewDialog == null) {
            viewDialog = new AlertDialog.Builder(context).create();
        }
        ((AlertDialog) viewDialog).setView(view, 0, 0, 0, 0);
        viewDialog.setCanceledOnTouchOutside(false);
        viewDialog.show();
        return viewDialog;
    }

    //用于自定义布局dialog加载,点击dialog外部无响应
    public Dialog loadViewFalsedialog() {
        //判断是否创建过
        if (viewDialog == null) {
            viewDialog = new AlertDialog.Builder(context).create();
        }
        ((AlertDialog) viewDialog).setView(view, 0, 0, 0, 0);
        viewDialog.setCanceledOnTouchOutside(false);
        viewDialog.show();
        return viewDialog;
    }

    //设置位于底部的dialog从下到上弹出布局
    public Dialog loadViewBottomdialog() {
        //判断是否创建过
        if (viewBottomDialog == null) {
            viewBottomDialog = new AlertDialog.Builder(context).create();
        }
//        viewbottom.setAlpha(100);
        viewBottomDialog.setCanceledOnTouchOutside(true);
        viewBottomDialog.show();
        // 设置dialog没有title

        viewBottomDialog.setContentView(viewbottom, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = viewBottomDialog.getWindow();
        window.setWindowAnimations(R.style.mystyle);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x00000000);//设置弹出窗体背景透明
        // 设置弹出窗体的背景
        window.setBackgroundDrawable(dw);

        // 可以在此设置显示动画
        WindowManager.LayoutParams wl = window.getAttributes();
        //设置dialog背景是否变暗
        if (bottom == 2) {
            wl.dimAmount = 0f;
            window.setAttributes(wl);
        }
        wl.x = 0;
        wl.y = ((Activity) context).getWindowManager().getDefaultDisplay().getHeight();
        // 以下这两句是为了保证按钮可以水平满屏
        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        // 设置显示位置
        viewBottomDialog.onWindowAttributesChanged(wl);
        // 设置弹出的动画效果
        return viewBottomDialog;
    }

    /**
     * 中间显示的dialog
     */
    public Dialog loadcenterdialog() {

        //判断是否创建过
        if (viewBottomDialog == null) {
            viewBottomDialog = new Dialog(context,R.style.BottomDialog);
        }
//        viewbottom.setAlpha(100);
        //显示数据
       viewBottomDialog.setContentView(view);
       viewBottomDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = viewBottomDialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        viewBottomDialog.getWindow().setAttributes(p);  //设置生效
        viewBottomDialog.show();

        // 设置弹出的动画效果
        return viewBottomDialog;
    }

    /*用于自定义布局dialog移除*/
    public void removeviewDialog() {
        if (viewDialog != null) {
            if (viewDialog.isShowing()) {
                viewDialog.dismiss();
            }
        }
    }

    /*用于自定义底部布局dialog移除*/
    public void removeviewBottomDialog() {
        if (viewBottomDialog != null) {
            if (viewBottomDialog.isShowing()) {
                viewBottomDialog.dismiss();
            }
        }
    }

    /*网络加载dialog移除*/
    public void removeDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
