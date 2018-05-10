package com.massky.sraumsmarthome.activity;

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
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.ICallback;
import com.massky.sraumsmarthome.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraumsmarthome.Util.SharedPreferencesUtil;
import com.massky.sraumsmarthome.Util.UDPClient;
import com.massky.sraumsmarthome.Utils.ApiHelper;
import com.massky.sraumsmarthome.adapter.FragmentViewPagerAdapter;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.base.BaseFragment1;
import com.massky.sraumsmarthome.bean.GateWayInfoBean;
import com.massky.sraumsmarthome.fragment.HomeFragment;
import com.massky.sraumsmarthome.fragment.MineFragment;
import com.massky.sraumsmarthome.fragment.NewFragment;
import com.massky.sraumsmarthome.fragment.SceneFragment;
import com.massky.sraumsmarthome.receiver.ApiTcpReceiveHelper;
import com.massky.sraumsmarthome.service.MyService;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2018/1/2.
 */

public class MainGateWayActivity extends BaseActivity {

    private HomeFragment fragment1;
    private SceneFragment fragment2;
    private MineFragment fragment3;

    @Override
    protected int viewId() {
        return R.layout.main_gateway_act;
    }

    @Override
    protected void onView() {

        radioGroup = (RadioGroup) findViewById(R.id.activity_group_radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                fragment1 = (HomeFragment) fm.findFragmentByTag(fragment1Tag);
                fragment2 = (SceneFragment) fm.findFragmentByTag(fragment2Tag);
                fragment3 = (MineFragment) fm.findFragmentByTag(fragment3Tag);
                if (fragment1 != null) {
                    ft.hide(fragment1);
                }
                if (fragment2 != null) {
                    ft.hide(fragment2);
                }
                if (fragment3 != null) {
                    ft.hide(fragment3);
                }
                switch (checkedId) {
                    case R.id.order_process:
                        if (fragment1 == null) {
                            fragment1 = HomeFragment.newInstance();
                            ft.add(R.id.container, fragment1, fragment1Tag);
                        } else {
                            ft.show(fragment1);
                        }
                        break;
                    case R.id.order_query:
                        if (fragment2 == null) {
                            fragment2 = SceneFragment.newInstance();
                            ft.add(R.id.container, fragment2, fragment2Tag);
                        } else {
                            ft.show(fragment2);
                        }
                        break;
                    case R.id.merchant_manager:
                        if (fragment3 == null) {
                            fragment3 = MineFragment.newInstance();
                            ft.add(R.id.container, fragment3,
                                    fragment3Tag);
                        } else {
                            ft.show(fragment3);
                        }
                        break;
                    default:
                        break;
                }
                ft.commitAllowingStateLoss();
            }
        });
        if (savedInstanceState == null) {
            FragmentManager fm = getSupportFragmentManager();
//            Fragment fragment = HomeFragment.newInstance();
//            fm.beginTransaction()
//                    .replace(R.id.container, fragment, fragment1Tag).commitAllowingStateLoss();;


            FragmentTransaction ft = fm.beginTransaction();

            fragment1 = HomeFragment.newInstance();
            ft.add(R.id.container, fragment1, fragment1Tag);

            fragment2 = SceneFragment.newInstance();
            ft.add(R.id.container, fragment2, fragment2Tag);

            fragment3 = MineFragment.newInstance();
            ft.add(R.id.container, fragment3,
                    fragment3Tag);

            fm.beginTransaction().replace(R.id.container, fragment1, fragment1Tag).commitAllowingStateLoss();//首页先显示
        }
    }


    /**
     * 添加TCP接收广播通知
     * // add Action1
     */
    private void addBrodcastAction() {
        //推送修改网关名称（网关->APP）
        push_gatewayName();

    }

    /**
     * 推送修改网关名称（网关->APP）
     */
    private void push_gatewayName() {
        addCanReceiveAction(new Intent(ApiTcpReceiveHelper.Sraum_PushGatewayPassword), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
                //解析json数据
                final User user = new GsonBuilder().registerTypeAdapterFactory(
                        new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
                if (user == null) return;
                if (user.newPassword != null) {//APP收到修改密码命令后应主动退出网关
                    ApplicationContext.getInstance().removeActivity_but_activity(MainGateWayActivity.this);
                    startActivity(new Intent(MainGateWayActivity.this, LoginGateWayActivity.class));
                    MainGateWayActivity.this.finish();
                    SharedPreferencesUtil.saveData(MainGateWayActivity.this, "password", user.newPassword.toString());
                    //退出网关（APP->网关），断开TCP连接
                    MyService.getInstance().quitTCP();
                    UDPClient.activity_destroy(true);
                }
            }
        });
    }


    /**
     * 登录网关
     */
    private void logout_gateway() {

        //MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_Login);
        //退出网关，断开TCP连接

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
    public static final String fragment1Tag = "fragment1";
    public static final String fragment2Tag = "fragment2";
    public static final String fragment3Tag = "fragment3";

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
        UDPClient.activity_destroy(true);//udp线程被杀死,暂时不能被杀死
    }
}
