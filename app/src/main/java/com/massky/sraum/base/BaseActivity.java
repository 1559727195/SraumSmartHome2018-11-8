package com.massky.sraum.base;

import android.os.Build;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.massky.sraum.Utils.AppManager;

import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public static boolean isForegrounds = false;
    public static boolean isDestroy = false;
    public Bundle savedInstanceState;
    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewId());
        this.savedInstanceState = savedInstanceState;
        AppManager.getAppManager().addActivity(this);//添加activity
        ButterKnife.inject(this);
        isDestroy = false;
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
        isDestroy = true;
    }

}
