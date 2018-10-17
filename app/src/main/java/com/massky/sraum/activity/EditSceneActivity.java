package com.massky.sraum.activity;

import android.view.View;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;

/**
 * Created by zhu on 2018/1/12.
 */

public class EditSceneActivity extends BaseActivity{
    @Override
    protected int viewId() {
        return R.layout.edit_scene_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {

    }
}
