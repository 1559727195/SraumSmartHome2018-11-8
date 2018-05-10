package com.massky.sraumsmarthome.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zhu on 2018/2/2.
 */

public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    public boolean isintercept;

    public MySwipeRefreshLayout(Context context) {
        super(context);
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isintercept) {
            return  true;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {//下拉的时候，拦截不把事件分发给子view
        return super.dispatchTouchEvent(ev);
    }


//    public  interface  setOndispatchSwipeRefreshListener {
//        void  dispatchIntercept(Boolean isintercept);
//    }

}
