package com.massky.sraumsmarthome.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.activity.AddAutoSceneActivity;
import com.massky.sraumsmarthome.activity.AddHandSceneActivity;
import com.massky.sraumsmarthome.adapter.FragmentViewPagerAdapter;
import com.massky.sraumsmarthome.base.BaseFragment1;
import com.massky.sraumsmarthome.event.MyDialogEvent;
import com.massky.sraumsmarthome.widget.NoSlideViewPager;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

import static com.massky.sraumsmarthome.Util.DipUtil.dip2px;

/**
 * Created by zhu on 2017/11/30.
 */

public class SceneFragment extends BaseFragment1 {
    private List<Fragment> _fragments = new ArrayList<>();
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.mViewPager)
    NoSlideViewPager mContentPager;
    @InjectView(R.id.hand_scene_line)
    LinearLayout hand_scene_line;
    @InjectView(R.id.auto_scene_line)
    LinearLayout auto_scene_line;
    @InjectView(R.id.hand_scene_view)
    View hand_scene_view;
    @InjectView(R.id.auto_scene_view)
    View auto_scene_view;
    @InjectView(R.id.add_scene)
    ImageView add_scene;
    private LinearLayout[] _navItemLayouts;
    private HandSceneFragment handSceneFragment;
    private AutoSceneFragment autoSceneFragment;
    private View[] views;
    private PopupWindow popupWindow;

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {
        initEvent();
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
        initView();
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


    private void initEvent() {
        // TODO Auto-generated method stub
//	     mPagerAdapter = getPagerAdapter();
        FragmentViewPagerAdapter fragmentViewPagerAdapter = new FragmentViewPagerAdapter(getActivity().getSupportFragmentManager(),
                mContentPager, _fragments);
        mContentPager.setAdapter(fragmentViewPagerAdapter);
        mContentPager.setOffscreenPageLimit(2);//设置这句话的好处就是在viewapger下可以同时刷新3个fragment
        mContentPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

                //ViewPager选中的项为position
                Resources res = getResources();
                for( int i = 0; i < _navItemLayouts.length; i++){
                    if (i != position){
                        views[i].setVisibility(View.GONE);
                    }
                }

                views[position].setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    /**
     *
     */
    private void initView() {//

        _navItemLayouts = new LinearLayout[2];
        _navItemLayouts[0] =  hand_scene_line;
        _navItemLayouts[1] =   auto_scene_line;

        views = new View[2];
        views[0] = hand_scene_view;
        views[1] = auto_scene_view;

        //
        for(int i = 0 ; i < _navItemLayouts.length;i++){
            _navItemLayouts[i].setOnClickListener(new LinearLayoutOnClickListener());
        }

        handSceneFragment = HandSceneFragment.newInstance();
        autoSceneFragment = AutoSceneFragment.newInstance();//new PropertyFragment () 物业
        _fragments.add(handSceneFragment);
        _fragments.add(autoSceneFragment);
    }

    /**
     *
     * @param selectedIndex
     */
    private void refreshFragment(int selectedIndex) {
        /**
         *  取消动画效果，消除跨页动画（比如0到3会经过1，2页面） viewPager.setCurrentItem(index,false);
         *  具备动画效果  viewPager.setCurrentItem(index);
         * @param view
         */
        mContentPager.setCurrentItem(selectedIndex,false);
    }

    /**
     *
     * @author Administrator
     *
     */
    private class LinearLayoutOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Resources res = getResources();
            int index = 0;
            for( int i = 0; i < _navItemLayouts.length; i++){
                if(v.getId() == _navItemLayouts[i].getId()){
                    index = i;
                    views[i].setVisibility(View.VISIBLE);
                }
                else{
                    views[i].setVisibility(View.GONE);
                }
            }
            refreshFragment(index);
        }
    }


    /**
     * Android popupwindow在指定控件正下方滑动弹出效果
     */
    private void showPopWindow() {
        try {
            View view = LayoutInflater.from(getActivity()).inflate(
                    R.layout.add_scene_window, null);
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
            popupWindow.setAnimationStyle(R.style.style_right_animation);// 动画效果必须放在showAsDropDown()方法上边，否则无效
            backgroundAlpha(1.0f);// 设置背景半透明 ,0.0f->1.0f为不透明到透明变化。
//            popupWindow.showAsDropDown(add_scene,0,dip2px(getActivity(),10));

            popupWindow.showAtLocation(add_scene, Gravity.NO_GRAVITY, location[0],location[1]);
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
                    startActivity(new Intent(getActivity(),AddHandSceneActivity.class));
                }
            });

            add_auto_scene_txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {//添加自动场景
                    popupWindow.dismiss();
                    startActivity(new Intent(getActivity(),AddAutoSceneActivity.class));
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
