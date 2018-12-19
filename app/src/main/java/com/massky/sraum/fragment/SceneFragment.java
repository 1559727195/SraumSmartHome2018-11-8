package com.massky.sraum.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.activity.EditLinkDeviceResultActivity;
import com.massky.sraum.activity.SelectSensorActivity;
import com.massky.sraum.activity.SelectiveLinkageActivity;
import com.massky.sraum.adapter.DynamicFragmentViewPagerAdapter;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

import static com.massky.sraum.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2017/11/30.
 */

public class SceneFragment extends BaseFragment1 {
    private List<Fragment> _fragments = new ArrayList<>();
    @InjectView(R.id.status_view)
    StatusView statusView;
    //    @InjectView(R.id.mViewPager)
//    NoSlideViewPager mContentPager;
//    @InjectView(R.id.hand_scene_line)
//    LinearLayout hand_scene_line;
//    @InjectView(R.id.auto_scene_line)
//    LinearLayout auto_scene_line;
//    @InjectView(R.id.hand_scene_view)
//    View hand_scene_view;
//    @InjectView(R.id.auto_scene_view)
//    View auto_scene_view;
    @InjectView(R.id.add_scene)
    ImageView add_scene;
    private LinearLayout[] _navItemLayouts;
    private HandSceneFragment handSceneFragment;
    private AutoSceneFragment autoSceneFragment;
    private View[] views;
    private PopupWindow popupWindow;

    @InjectView(R.id.tab_FindFragment_title)
    TabLayout tab_FindFragment_title;
    @InjectView(R.id.vp_FindFragment_pager)
    ViewPager vp_FindFragment_pager;
    private List<Fragment> list_smart_frag;
    private List<String> list_title;
    private int mCurrentPageIndex;
    private DynamicFragmentViewPagerAdapter fragmentViewPagerAdapter;
    public static String ACTION_INTENT_RECEIVER = "com.massky.sraum.sceceiver";
    private DialogUtil dialogUtil;
    private String manuallyCount;
    private String autoCount;
    private int intfirst;

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {
        add_scene.setOnClickListener(this);
    }

    @Override
    public void onEvent(MyDialogEvent eventData) {

    }

    @Override
    protected int viewId() {
        return R.layout.scene_frag;
    }

    @Override
    protected void onView(View view) {
        StatusUtils.setFullToStatusBar(getActivity());  // StatusBar.
//        initView();
        intfirst = 1;
        dialogUtil = new DialogUtil(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (intfirst == 1) {
            intfirst = 2;
            initControls();
        }
        sraum_getAllScenesCount();
    }


    /**
     * 获取场景数量
     */
    /**
     * 获取网关在线状态
     */
    private void sraum_getAllScenesCount() {
        Map map = new HashMap();
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(), "areaNumber", "");
        map.put("areaNumber", areaNumber);
        map.put("token", TokenUtil.getToken(getActivity()));
        if (dialogUtil != null) {
            dialogUtil.loadDialog();
        }
        MyOkHttp.postMapString(ApiHelper.sraum_getAllScenesCount, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//这个是获取togglen来刷新数据
                sraum_getAllScenesCount();
            }
        }, getActivity(), dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                manuallyCount = user.manuallyCount;
                autoCount = user.autoCount;
                init_tab_layout();


            }
        });
    }

    /**
     * 初始化tablayout的数据加载
     */
    private void init_tab_layout() {
        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        manuallyCount = manuallyCount == null ? "0" : manuallyCount;
        autoCount = autoCount == null ? "0" : autoCount;
        list_title.add("手动(" + manuallyCount + ")");
        list_title.add("自动(" + autoCount + ")");
        tab_FindFragment_title.removeAllTabs();
        for (int i = 0; i < 2; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(i)));
        }

        fragmentViewPagerAdapter = new DynamicFragmentViewPagerAdapter(getActivity().getSupportFragmentManager(),
                vp_FindFragment_pager, _fragments, list_title);
        //viewpager加载adapter
        vp_FindFragment_pager.setAdapter(fragmentViewPagerAdapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_scene:
                showPopWindow();
                break;
        }
    }

    public static SceneFragment newInstance() {
        SceneFragment newFragment = new SceneFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

    /**
     * 初始化各控件
     *
     * @param
     */
    private void initControls() {

        handSceneFragment = HandSceneFragment.newInstance();
        autoSceneFragment = AutoSceneFragment.newInstance();//new PropertyFragment () 物业
        _fragments.add(handSceneFragment);
        _fragments.add(autoSceneFragment);

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        manuallyCount = manuallyCount == null ? "0" : manuallyCount;
        autoCount = autoCount == null ? "0" : autoCount;
        list_title.add("手动(" + manuallyCount + ")");
        list_title.add("自动(" + autoCount + ")");

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
//        tab_FindFragment_title.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        for (int i = 0; i < 2; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(i)));
        }
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(3)));

//        fAdapter = new Find_Smart_Home_Adapter(getSupportFragmentManager(),list_smart_frag,list_title);
        fragmentViewPagerAdapter = new DynamicFragmentViewPagerAdapter(getActivity().getSupportFragmentManager(),
                vp_FindFragment_pager, _fragments, list_title);
        //viewpager加载adapter
        vp_FindFragment_pager.setAdapter(fragmentViewPagerAdapter);
        vp_FindFragment_pager.setOffscreenPageLimit(2);//设置这句话的好处就是在viewapger下可以同时刷新3个fragment
        //tab_FindFragment_title.setViewPager(vp_FindFragment_pager);
        //TabLayout加载viewpager
        tab_FindFragment_title.setupWithViewPager(vp_FindFragment_pager);
        //tab_FindFragment_title.set
        setPageChangeListener();

        tab_FindFragment_title.post(new Runnable() {
            @Override
            public void run() {
//                setIndicator(tab_FindFragment_title, 5, 5);
            }
        });
    }

    //更新ViewPager的Title信息
    private void upgrate_title() {
        if (fragmentViewPagerAdapter != null) {
            for (int i = 0; i < 2; i++)
                fragmentViewPagerAdapter.setPageTitle(i, "");
        }
    }

    private void setPageChangeListener() {
        vp_FindFragment_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
//                mCurrentViewPagerName = mChannelNames.get(position);
                mCurrentPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 通过反射调整TabLayout指引器的宽度
     *
     * @param tabs
     * @param leftDip
     * @param rightDip
     */
    public void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout llTab = null;
        try {
            llTab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, leftDip, Resources.getSystem().getDisplayMetrics());
        int right = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, rightDip, Resources.getSystem().getDisplayMetrics());

        for (int i = 0; i < llTab.getChildCount(); i++) {
            final View child = llTab.getChildAt(i);
            child.setPadding(0, 0, 0, 0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
            params.leftMargin = left;
            params.rightMargin = right;
            child.setLayoutParams(params);
            child.invalidate();
            child.setTag(i);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (Integer) child.getTag();
//                    vp_FindFragment_pager.setCurrentItem(position,false);
                    Log.e("robin debug", "position:" + position);//监听viewpager+Tablayout -》item点击事件
                }
            });
        }
    }


    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_scene_pop_lay, null);
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
            int xoff = dip2px(getActivity(), 20);
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
//                    startActivity(new Intent(getActivity(), AddHandSceneActivity.class));
                    Intent intent = null;
                    Map map_link = new HashMap();
                    map_link.put("type", "101");
                    map_link.put("deviceType", "");
                    map_link.put("deviceId", "");
                    map_link.put("name", "手动执行");
                    map_link.put("action", "执行");
                    map_link.put("condition", "");
                    map_link.put("minValue", "");
                    map_link.put("maxValue", "");
                    map_link.put("boxName", "");
                    map_link.put("name1", "手动执行");
                    boolean add_condition = (boolean) SharedPreferencesUtil.getData(getActivity(), "add_condition", false);
                    if (add_condition) {
//            AppManager.getAppManager().removeActivity_but_activity_cls(MainfragmentActivity.class);
                        AppManager.getAppManager().finishActivity_current(EditLinkDeviceResultActivity.class);
                        intent = new Intent(getActivity(), EditLinkDeviceResultActivity.class);
                        intent.putExtra("sensor_map", (Serializable) map_link);
                        startActivity(intent);
                    } else {
                        intent = new Intent(getActivity(),
                                SelectiveLinkageActivity.class);
                        intent.putExtra("link_map", (Serializable) map_link);
                        startActivity(intent);
                    }

                }
            });

            add_auto_scene_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//添加自动场景
                    popupWindow.dismiss();
//                    startActivity(new Intent(getActivity(), AddAutoSceneActivity.class));
                    Intent intent = new Intent(getActivity(), SelectSensorActivity.class);
                    intent.putExtra("type", "100");//自动场景
                    startActivity(intent);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getActivity().getWindow().setAttributes(lp);
    }
}



