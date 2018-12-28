package com.massky.sraum.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.adapter.FragmentViewPagerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.base.Basecfragment;
import com.massky.sraum.fragment.DeviceMessageFragment;
import com.massky.sraum.fragment.SceneFragment;
import com.massky.sraum.fragment.SystemMessageFragment;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;
import butterknife.InjectView;
import butterknife.OnClick;

public class MessageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    @InjectView(R.id.viewpager_id)
    ViewPager viewpager_id;
    @InjectView(R.id.macrelative_id)
    RelativeLayout macrelative_id;
    @InjectView(R.id.scenerelative_id)
    RelativeLayout scenerelative_id;
    @InjectView(R.id.viewone)
    View viewone;
    @InjectView(R.id.viewtwo)
    View viewtwo;
    //    @InjectView(R.id.viewthree)
//    View viewthree;
    @InjectView(R.id.addtxt_text_id)
    TextView addtxt_text_id;
    @InjectView(R.id.mactext_id)
    TextView mactext_id;
    @InjectView(R.id.scene_id)
    TextView scene_id;
    @InjectView(R.id.back)
    ImageView back;
    private FragmentManager fm;
    private List<Fragment> list = new ArrayList<Fragment>();
    //    private RoomFragment roomFragment;
    private SceneFragment sceneFragment;
    private int golden = Color.parseColor("#37CC99");
    private int black = Color.parseColor("#6f6f6f");
    private FragmentViewPagerAdapter adapter;
    private boolean flag = false;

    private DeviceMessageFragment devicemessagefragment;
    private SystemMessageFragment systemmessagefragment;

    public static String MESSAGE_FRAGMENT = "com.fragment.messagefragment";
    private ImageView common_setting_image;

    @Override
    protected int viewId() {
        return R.layout.message_frag_lay;
    }


    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        addtxt_text_id.setVisibility(View.VISIBLE);
        addtxt_text_id.setText("编辑");
        LogUtil.i("这是oncreate方法", "onView: ");
        fm = getSupportFragmentManager();
        addFragment();
        onclick();
        startState();
        addtxt_text_id.setOnClickListener(this);
//        addViewid();
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    private void sendBroad(String content) {
        Intent mIntent = new Intent(MESSAGE_FRAGMENT);
        mIntent.putExtra("action", content);
        sendBroadcast(mIntent);
    }


//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        myInterface = (MyInterface) activity;
//    }

    //添加fragment
    private void addFragment() {
        devicemessagefragment = DeviceMessageFragment.newInstance(new DeviceMessageFragment.OnDeviceMessageFragListener() {

            @Override
            public void ondevice_message_frag() {
                addtxt_text_id.setText("编辑");
                sendBroad("取消");
            }
        });
//        roomFragment = new RoomFragment();
        systemmessagefragment = SystemMessageFragment.newInstance(new SystemMessageFragment.OnDeviceMessageFragListener() {
            @Override
            public void ondevice_message_frag() {
                addtxt_text_id.setText("编辑");
                sendBroad("取消");
            }
        });
        list.add(devicemessagefragment);
        list.add(systemmessagefragment);
//        list.add(roomFragment);
        adapter = new FragmentViewPagerAdapter(fm, viewpager_id, list);
        adapter.setOnExtraPageChangeListener(new FragmentViewPagerAdapter.OnExtraPageChangeListener() {
            @Override
            public void onExtraPageSelected(int i) {
                LogUtil.i("Extra...i:", i + "onExtraPageSelected: ");
            }
        });
        viewpager_id.addOnPageChangeListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        devicemessagefragment = new DeviceMessageFragment();
//        roomFragment = new RoomFragment();
        systemmessagefragment = new SystemMessageFragment();
        list.add(devicemessagefragment);
        list.add(systemmessagefragment);
//            list.add(roomFragment);
        adapter = new FragmentViewPagerAdapter(fm, viewpager_id, list);
        startState();
        addtxt_text_id.setText("编辑");
    }

    private void onclick() {
        MyOnclick on = new MyOnclick();
        macrelative_id.setOnClickListener(on);
//        roomrelative_id.setOnClickListener(on);
        scenerelative_id.setOnClickListener(on);
        back.setOnClickListener(this);
    }

    /**
     * 设置一个监听类
     */
    class MyOnclick implements View.OnClickListener {

        @Override
        public void onClick(View arg0) {
            clear();
            viewchange(arg0.getId());
        }
    }

    private void clear() {
        mactext_id.setTextColor(black);
//        roomtext_id.setTextColor(black);
        scene_id.setTextColor(black);
        viewone.setVisibility(View.GONE);
        viewtwo.setVisibility(View.GONE);
//        viewthree.setVisibility(View.GONE);
    }

    // 进行匹配设置一个方法，判断是否被点击
    private void viewchange(int num) {
        switch (num) {
            case R.id.macrelative_id:
            case 0:
                mactext_id.setTextColor(golden);
                viewone.setVisibility(View.VISIBLE);
                viewpager_id.setCurrentItem(0);
                break;
            case R.id.scenerelative_id:
            case 1:
                scene_id.setTextColor(golden);
                viewtwo.setVisibility(View.VISIBLE);
                viewpager_id.setCurrentItem(1);
                break;

            default:
                break;
        }
    }

    private void startState() {
        clear();
        viewchange(0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addtxt_text_id:
                switch (addtxt_text_id.getText().toString()) {
                    case "编辑":
                        addtxt_text_id.setText("取消");
                        sendBroad("编辑");
//                        dialogUtil.loadViewBottomdialog();
                        break;
                    case "取消":
                        addtxt_text_id.setText("编辑");
                        sendBroad("取消");
//                        dialogUtil.removeviewBottomDialog();
                        break;
                }
                break;

            case R.id.all_read_linear:

                break;

            case R.id.all_select_linear:

                break;

            case R.id.delete_linear:

                break;
            case R.id.back:
                MessageActivity.this.finish();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        LogUtil.eLength("这是滚动", position + "");
        if (flag) {
            Basecfragment fagmentbase = (Basecfragment) list.get(position);
            fagmentbase.initData();
        }
        flag = true;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        clear();
        viewchange(viewpager_id.getCurrentItem());
    }

    @Override
    public void onStart() {//onStart方法是屏幕唤醒时弹执行该方法。
        super.onStart();
        addtxt_text_id.setText("编辑");
        sendBroad("取消");
    }
}
