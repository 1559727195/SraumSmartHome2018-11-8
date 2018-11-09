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
    //控件是否已经初始化
    private boolean isCreateView = false;
    //是否已经加载过数据
    private boolean isLoadData = false;

    public static boolean isForegrounds = false;

    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    //private static final String TAG = "BaseActivity";
    // 用来记录需要处理的action和响应函数
    private Map<String, List<OnActionResponse>> mCallbacks;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(viewId(), null);
        ButterKnife.inject(this, rootView);
        mIntentFilter = new IntentFilter();
//        mReceiver = new CommonReceiver();
        mCallbacks = new HashMap<String, List<OnActionResponse>>();
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


    //子类调用该方法，注册所要处理的广播action

    /**
     * if subclass need response BroadcastReceiver, need invoke this method to
     * add can Receive Action
     *
     * @param intent
     * @param callback
     */
    public void addCanReceiveAction(Intent intent, OnActionResponse callback) {
        final String action = intent.getAction();

        if (!mIntentFilter.hasAction(action)) {
            mIntentFilter.addAction(action);
            getActivity().registerReceiver(mReceiver, mIntentFilter);
        }

        if (!mCallbacks.containsKey(action)) {
            mCallbacks.put(action, Collections.synchronizedList(new ArrayList<OnActionResponse>()));
        }

        mCallbacks.get(action).add(callback);
        intent.putExtra(Constants.EXTRA_ACTION_CALLBACK_HASH_CODE, callback.hashCode());
    }

    private class CommonReceiver extends BroadcastReceiver {

        // 子类收到广播后的逻辑
        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.d(TAG, "CommonReceiver receiver intent:" + intent.getAction());
            final String action = intent.getAction();
            if (mCallbacks != null && mCallbacks.containsKey(action)) {
                int hashCode = intent.getIntExtra(Constants.EXTRA_ACTION_CALLBACK_HASH_CODE, -1);
                List<OnActionResponse> list = mCallbacks.get(action);
                if (list != null) {
                    int index = -1;
                    int count = list.size();

                    for (int i = 0; i < count; i++) {
                        if (hashCode == list.get(i).hashCode()) {
                            index = i;
                            break;
                        }
                    }

                    if (index >= 0) {
                        list.get(index).onResponse(intent);
                    } else {
                        list.get(count - 1).onResponse(intent);
                    }

                    if (list.isEmpty()) {
                        mCallbacks.remove(action);
                    }
                }
            }
        }
    }

    //子类具体实现处理逻辑
    protected interface OnActionResponse {
        void onResponse(Intent intent);
    }

//    @Override
//    protected void onPause() {
//        isForegrounds = false;
//        unregisterReceiver(mReceiver);
//        super.onPause();
//    }


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
}
