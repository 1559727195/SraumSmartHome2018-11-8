package com.massky.sraumsmarthome.base;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.massky.sraumsmarthome.tool.Constants;
import com.massky.sraumsmarthome.widget.ApplicationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean isForegrounds = false;
    public Bundle savedInstanceState;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private static final String TAG = "BaseActivity";
    // 用来记录需要处理的action和响应函数
    private Map<String, List<OnActionResponse>> mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewId());
        this.savedInstanceState = savedInstanceState;
        ApplicationContext.getInstance().addActivity(this);
        ButterKnife.inject(this);
        mIntentFilter = new IntentFilter();
        mReceiver = new CommonReceiver();
        mCallbacks = new HashMap<String, List<OnActionResponse>>();
        onView();
        onEvent();
        onData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 手势密码两种状态（点击home键和手机屏幕状态进行判定）
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        isForegrounds = false;
        unregisterReceiver(mReceiver);
        super.onPause();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            }
        }
        return super.onTouchEvent(event);
    }

    protected abstract int viewId();

    @Override
    protected void onResume() {
        isForegrounds = true;
        registerReceiver(mReceiver, mIntentFilter);
        super.onResume();
    }

    protected abstract void onView();

    protected abstract void onEvent();

    protected abstract void onData();

    /**
     * 取消广播状态
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            registerReceiver(mReceiver, mIntentFilter);
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
            Log.d(TAG, "CommonReceiver receiver intent:" + intent.getAction());
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
//        unregisterReceiver(mReceiver);
//        super.onPause();
//    }
//
//    @Override
//    protected void onResume() {
//        registerReceiver(mReceiver, mIntentFilter);
//        super.onResume();
//    }

}
