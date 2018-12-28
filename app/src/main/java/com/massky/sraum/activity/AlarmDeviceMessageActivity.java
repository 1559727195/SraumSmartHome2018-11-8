package com.massky.sraum.activity;

import android.content.res.Resources;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.massky.sraum.R;
import com.massky.sraum.adapter.DynamicFragmentViewPagerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.DevicePagerFragment;
import com.yanzhenjie.statusview.StatusUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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
        list_smart_frag = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Fragment fragment = DevicePagerFragment.newInstance(i);
            list_smart_frag.add(fragment);
        }

        //将名称加载tab名字列表，正常情况下，我们应该在values/arrays.xml中进行定义然后调用
        list_title = new ArrayList<>();
        list_title.add("全部");
        list_title.add("报警");
        //设置TabLayout的模式
        tab_FindFragment_title.setTabMode(TabLayout.MODE_FIXED);
//        tab_FindFragment_title.setTabMode(TabLayout.MODE_SCROLLABLE);
        //为TabLayout添加tab名称
        for (int i = 0; i < 2; i++) {
            tab_FindFragment_title.addTab(tab_FindFragment_title.newTab().setText(list_title.get(i)));
        }
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
                setIndicator_new(tab_FindFragment_title);
            }
        });
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
     * 添加tablayout指引器事件监听
     *
     * @param tab_findFragment_title
     */
    private void setIndicator_new(TabLayout tab_findFragment_title) {
        int tabCount = tab_findFragment_title.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            //这里tab可能为null 根据实际情况处理吧
            final TabLayout.Tab tab = tab_findFragment_title.getTabAt(i);
            //这里使用到反射，拿到Tab对象后获取Class

            Class c = tab.getClass();
            try {
                //c.getDeclaredField 获取私有属性。

                //“mView”是Tab的私有属性名称，类型是 TabView ，TabLayout私有内部类。

                Field field = c.getDeclaredField("view");

                if (field == null) {

                    continue;

                }

                field.setAccessible(true);

                final View view = (View) field.get(tab);

                if (view == null) {

                    continue;

                }

                view.setTag(i);

                view.setOnClickListener(new View.OnClickListener() {

                    @Override

                    public void onClick(View v) {
                        //这里就可以根据业务需求处理事件了。
                        mCurrentPageIndex = (int) view.getTag();
                        vp_FindFragment_pager.setCurrentItem(mCurrentPageIndex, true);
                    }
                });

            } catch (NoSuchFieldException e) {
                e.printStackTrace();

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
