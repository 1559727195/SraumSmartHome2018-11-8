package com.massky.sraumsmarthome.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuanLianSceneRealBtnActivity extends BaseActivity{
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;

    @Override
    protected int viewId() {
        return R.layout.guanlian_scenereal_btn;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                GuanLianSceneRealBtnActivity.this.finish();
                break;
            case R.id.next_step_txt://保存
                ApplicationContext.getInstance().finishActivity(GuanLianSceneActivity.class);
                GuanLianSceneRealBtnActivity.this.finish();
               break;
        }
    }
}
