package com.massky.sraum.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.transition.Scene;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.gson.GsonBuilder;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.UDPClient;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.FragmentViewPagerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.bean.GateWayInfoBean;
import com.massky.sraum.fragment.HomeFragment;
import com.massky.sraum.fragment.MineFragment;
import com.massky.sraum.fragment.NewFragment;
import com.massky.sraum.fragment.SceneFragment;
import com.massky.sraum.receiver.ApiTcpReceiveHelper;
import com.massky.sraum.service.MyService;
import com.massky.sraum.widget.ApplicationContext;


/**
 * Created by zhu on 2018/1/2.
 */

public class MainGateWayActivity extends BaseActivity {

    private HomeFragment fragment1;
    private SceneFragment fragment2;
    private MineFragment fragment3;
    private Fragment currentFragment;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";
    public static String ACTION_SRAUM_SETBOX = "ACTION_SRAUM_SETBOX";//notifactionId = 8 ->设置网关模式，sraum_setBox
    public static String UPDATE_GRADE_BOX = "com.massky.sraum.update_grade_box";

    @Override
    protected int viewId() {
        return R.layout.main_gateway_act;
    }

    @Override
    protected void onView() {
        add_page_select();
    }

    /**
     * fragment卡片选择
     */
    private void add_page_select() {
        for (int i = 0; i < 3; i++) {
            setTabSelection(i);
        }
        setTabSelection(0);
        radioGroup = (RadioGroup) findViewById(R.id.activity_group_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int tabId) {

                if (tabId == R.id.order_process) {
//                    switchFragment(fragment1).commit();
                    setTabSelection(0);
                }
                if (tabId == R.id.order_query) {
//                    switchFragment(fragment2).commit();
                    setTabSelection(1);
                }
                if (tabId == R.id.merchant_manager) {
//                    switchFragment(fragment3).commit();
                    setTabSelection(2);
                }
            }
        });
    }


    public void setTabSelection(int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragments(transaction);
        Fragment frament = null;
        switch (index) {
            case 0:
                if (fragment1 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
//                    firstPageFragment = new FirstPageFragment(menu1);
                    fragment1 = HomeFragment.newInstance();
                    transaction.add(R.id.container, fragment1);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment1);
                }
                break;
            case 1:
                if (fragment2 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragment2 = SceneFragment.newInstance();
                    transaction.add(R.id.container, fragment2);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment2);
                }
                break;
            case 2:
                if (fragment3 == null) {
                    // 如果MessageFragment为空，则创建一个并添加到界面上
                    fragment3 = MineFragment.newInstance();
                    transaction.add(R.id.container, fragment3);
                } else {
                    // 如果MessageFragment不为空，则直接将它显示出来
                    transaction.show(fragment3);
                }

                break;
        }
        transaction.commit();
    }


    private void hideFragments(FragmentTransaction transaction) {
        if (fragment1 != null) {
            transaction.hide(fragment1);
        }
        if (fragment2 != null) {
            transaction.hide(fragment2);
        }

        if (fragment3 != null) {
            transaction.hide(fragment3);
        }
    }


    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {

    }

    private RadioGroup radioGroup;

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton mTab = (RadioButton) radioGroup.getChildAt(i);
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag((String) mTab.getTag());
            FragmentTransaction ft = fm.beginTransaction();
            if (fragment != null) {
                if (!mTab.isChecked()) {
                    ft.hide(fragment);
                }
            }
            ft.commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
