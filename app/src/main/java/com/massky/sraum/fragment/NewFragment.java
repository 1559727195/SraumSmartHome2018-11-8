package com.massky.sraum.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseFragment1;
import com.massky.sraum.event.MyDialogEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by zhu on 2017/11/30.
 */

public class NewFragment extends BaseFragment1{
    private List<Map> list_alarm = new ArrayList<>();
    private int currentpage = 1;
    private Handler mHandler = new Handler();

    @Override
    protected void onData() {

    }

    @Override
    protected void onEvent() {

    }

    @Override
    public void onEvent(MyDialogEvent eventData) {
        currentpage = 1;
    }

    @Override
    protected int viewId() {
        return R.layout.new_fragment_lay;
    }

    @Override
    protected void onView(View view) {
        list_alarm = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {

    }

    public static NewFragment newInstance() {
        NewFragment newFragment = new NewFragment();
        Bundle bundle = new Bundle();
        newFragment.setArguments(bundle);
        return newFragment;
    }

}
