package com.massky.sraumsmarthome.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;
import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/1/23.
 */

public class SettingNiChengActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)//
    StatusView statusView;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    private PopupWindow popupWindow;
    private final String[] strs = new String[] {
        "爸爸", "妈妈", "爷爷", "奶奶", "儿子","女儿"
    };//定义一个String数组用来显示ListView的内容private ListView lv;/** Called when the activity is first created. */
    @InjectView(R.id.xiala_nicheng)
    ImageView xiala_nicheng;

    @Override
    protected int viewId() {
        return R.layout.setting_nicheng_act;
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
        next_step_txt.setOnClickListener(this);
        xiala_nicheng.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SettingNiChengActivity.this.finish();
                break;
            case R.id.next_step_txt:
                ApplicationContext.getInstance().finishActivity(InputPhoneNumberActivity.class);
                SettingNiChengActivity.this.finish();
                break;
            case R.id.xiala_nicheng:
//                showPopWindow();
                showRenameDialog();
                break;//下拉
        }
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {

            View view = LayoutInflater.from(this).inflate(
                    R.layout.nicheng_list, null);
            ListView list_show = (ListView) view.findViewById(R.id.list_show);
            list_show.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, strs));
            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(cd);
            int[] location = new int[2];
            xiala_nicheng.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(1.0f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            popupWindow.showAsDropDown(xiala_nicheng,0,dip2px(this,10));
            //popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;// 当点击屏幕时，使popupWindow消失
                    backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消，1.0f为透明
                }
            });
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(SettingNiChengActivity.this).inflate(R.layout.edit_famillys_dialog, null);
//        TextView confirm; //确定按钮
//        TextView cancel; //确定按钮
//        TextView tv_title;
////        final TextView content; //内容
//        cancel = (TextView) view.findViewById(R.id.call_cancel);
//        confirm = (TextView) view.findViewById(R.id.call_confirm);
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);

        ListView list_show = (ListView) view.findViewById(R.id.list_show);
        list_show.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, strs));

        //显示数据
        final Dialog dialog = new Dialog(SettingNiChengActivity.this, R.style.BottomDialog);
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
        list_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dialog.dismiss();
            }
        });
//        cancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
//
//        confirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });


    }



    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }
}
