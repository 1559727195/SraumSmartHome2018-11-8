package com.massky.sraumsmarthome.activity;

import android.view.View;
import android.widget.ImageView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/2/7.
 */

public class AlarmDetailActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @Override
    protected int viewId() {
        return R.layout.alarm_detail_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                AlarmDetailActivity.this.finish();
                break;
        }
    }
}
