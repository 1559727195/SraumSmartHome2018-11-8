package com.massky.sraum.activity;

import android.content.res.Resources;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.massky.sraum.R;
import com.massky.sraum.adapter.DynamicFragmentViewPagerAdapter;
import com.massky.sraum.adapter.FragmentViewPagerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.DevicePagerFragment;
import com.yanzhenjie.statusview.StatusUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/2/7.
 */

public class AlarmDeviceMessageActivity extends BaseActivity {
    @InjectView(R.id.tab_FindFragment_title)
    TabLayout tab_FindFragment_title;
    @InjectView(R.id.vp_FindFragment_pager)
    ViewPager vp_FindFragment_pager;
    private List<Fragment> list_smart_frag;
    private List<String> list_title;
    private int mCurrentPageIndex;
    private DynamicFragmentViewPagerAdapter fragmentViewPagerAdapter;
    @InjectView(R.id.back_rel)
    RelativeLayout back_rel;

    @Override
    protected int viewId() {
        return R.layout.alarm_device_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        initControls();
    }

    @Override
    protected void onEvent() {
        back_rel.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_rel:
                AlarmDeviceMessageActivity.this.finish();
                break;
        }
    }


    /**
     * 初始化各控件
     *
     * @param
     */
    private void initControls() {
//        add_tab = (Button) view.findViewById(add_tab);
//        add_tab.setOnClickListener(this);
//        tab_FindFragment_title = (TabLayout)view.findViewById(tab_FindFragment_title);
//        vp_FindFragment_pager = (ViewPager)view.findViewById(vp_FindFragment_pager);

        //初始化各fragment
//        hotRecommendFragment =  Find_hotRecommendFragment.newInstance(0);
//        hotCollectionFragment = Find_hotCollectionFragment.newInstance(1);
//        hotMonthFragment =  Find_hotMonthFragment.newInstance(2);
//        hotToday =Find_hotToday.newInstance(3);
//
//        //将fragment装进列表中
//        list_fragment = new ArrayList<>();
//        list_fragment.add(hotRecommendFragment);
//        list_fragment.add(hotCollectionFragment);
//        list_fragment.add(hotMonthFragment);
//        list_fragment.add(hotToday);
        list_smart_frag = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Fragment fragment = DevicePagerFragment.newInstance(i);
            list_smart_frag.add(fragment);
        }

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("全部(1)");
        list_title.add("报警(1)");
        list_title.add("通知");
        list_title.add("共享");
        list_title.add("老人房");

        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
//        tab_FindFragment_title.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        for (int i = 0; i < 4; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(i)));
        }
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(0)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(1)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(2)));
//        tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(3)));

//        fAdapter = new Find_Smart_Home_Adapter(getSupportFragmentManager(),list_smart_frag,list_title);
        fragmentViewPagerAdapter = new DynamicFragmentViewPagerAdapter(getSupportFragmentManager(),
                vp_FindFragment_pager, list_smart_frag, list_title);
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
                setIndicator(tab_FindFragment_title, 5, 5);
            }
        });
    }

    //更新ViewPager的Title信息
    private void upgrate_title() {
        if (fragmentViewPagerAdapter != null) {
            for (int i = 0; i < 4; i++)
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
                    Log.e("robin debug","position:" + position);//监听viewpager+Tablayout -》item点击事件
                }
            });
        }
    }
}
