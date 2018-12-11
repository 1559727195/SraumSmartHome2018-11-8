package com.massky.sraum.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.massky.sraum.event.MyDialogEvent;
import com.massky.sraum.tool.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/27.
 */

public abstract class BaseFragment1 extends Fragment implements View.OnClickListener {
    public static boolean isDestroy = false;
    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    private boolean isLoadData = false;

    public static boolean isForegrounds = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(viewId(), null);
        ButterKnife.inject(this, rootView);
        isDestroy = false;
        onView(rootView);
        onEvent();
        onData();
        isCreateView = true;
        return rootView;
    }

    protected abstract void onData();


    protected abstract void onEvent();

    public abstract void onEvent(MyDialogEvent eventData);


    @Override
    public void onStart() {
        super.onStart();
    }

    protected abstract int viewId();

    protected abstract void onView(View view);

    private void initViews() {
        //初始化控件
    }

    //此方法在控件初始化前调用，所以不能在此方法中直接操作控件会出现空指针
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isCreateView) {
            lazyLoad();
        }
    }

    private void lazyLoad() {
        //如果没有加载过就加载，否则就不再加载了
        if (!isLoadData) {
            //加载数据操作
            isLoadData = true;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //第一个fragment会调用
        if (getUserVisibleHint())
            lazyLoad();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    @Override
    public void onPause() {
        isForegrounds = false;
//        getActivity().unregisterReceiver(mReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        isForegrounds = true;
//        getActivity().registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
    }


    @Override
    public void onDestroy() {
        isDestroy = true;
        super.onDestroy();
    }

}
