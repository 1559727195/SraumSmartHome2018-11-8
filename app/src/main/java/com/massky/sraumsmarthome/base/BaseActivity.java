package com.massky.sraumsmarthome.base;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.massky.sraumsmarthome.widget.ApplicationContext;

import butterknife.ButterKnife;

/**
 * Created by zhu on 2017/7/18.
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    public static boolean isForegrounds = false;
    public Bundle savedInstanceState;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(viewId());
        this.savedInstanceState = savedInstanceState;
        ApplicationContext.getInstance().addActivity(this);
        ButterKnife.inject(this);
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
    }
}
