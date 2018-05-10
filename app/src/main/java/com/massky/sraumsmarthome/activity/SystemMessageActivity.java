package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.adapter.HistoryBackAdapter;
import com.massky.sraumsmarthome.adapter.SystemMessageAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/2/12.
 */

public class SystemMessageActivity extends BaseActivity implements XListView.IXListViewListener {
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    @InjectView(R.id.back)
    ImageView back;
    private SystemMessageAdapter systemmessageAdapter;
    private PopupWindow popupWindow;
    @InjectView(R.id.add_scene)
    ImageView add_scene;

    @Override
    protected int viewId() {
        return R.layout.system_message_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        add_scene.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        list_hand_scene.add(new HashMap());
        list_hand_scene.add(new HashMap());
        systemmessageAdapter = new SystemMessageAdapter(SystemMessageActivity.this, list_hand_scene);
        xListView_scan.setAdapter(systemmessageAdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SystemMessageActivity.this.finish();
                break;
            case R.id.add_scene:
                showPopWindow();
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }


    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(SystemMessageActivity.this).inflate(
                    R.layout.system_message_pop_lay, null);
            //add_hand_scene_txt, add_auto_scene_txt
            TextView add_auto_scene_txt = (TextView) view.findViewById(R.id.add_auto_scene_txt);
            TextView add_hand_scene_txt = (TextView) view.findViewById(R.id.add_hand_scene_txt);

            popupWindow = new PopupWindow(view, WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(true);
            ColorDrawable cd = new ColorDrawable(0x00ffffff);// 背景颜色全透明
            popupWindow.setBackgroundDrawable(cd);
            int[] location = new int[2];
            add_scene.getLocationOnScreen(location);//获得textview的location位置信息，绝对位置
            popupWindow.setAnimationStyle(R.style.style_pop_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(0.5f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
            int xoff = dip2px(SystemMessageActivity.this, 20);
            popupWindow.showAsDropDown(add_scene, 0, 0);
//            popupWindow.showAtLocation(tv_pop, Gravity.NO_GRAVITY, location[0]+tv_pop.getWidth(),location[1]);
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    popupWindow = null;// 当点击屏幕时，使popupWindow消失
                    backgroundAlpha(1.0f);// 当点击屏幕时，使半透明效果取消，1.0f为透明
                }
            });

            add_hand_scene_txt.setOnClickListener(new View.OnClickListener() {//while(bool) {postdelay { }}也可以起到定时器的作用
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
//                    startActivity(new Intent(SystemMessageActivity.this, AddHandSceneActivity.class));
                }
            });

            add_auto_scene_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//添加自动场景
                    popupWindow.dismiss();
//                    startActivity(new Intent(SystemMessageActivity.this, AddAutoSceneActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }
}
