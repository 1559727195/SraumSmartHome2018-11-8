package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/6/19.
 */

public class SelectSmartDoorLockActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.wendu_linear)
    LinearLayout wendu_linear;
    @InjectView(R.id.shidu_rel)
    RelativeLayout shidu_rel;
    @InjectView(R.id.project_select)
    TextView project_select;



    @Override
    protected int viewId() {
        return R.layout.select_smart_door_lock_lay;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        onEvent();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        wendu_linear.setOnClickListener(this);
        shidu_rel.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                SelectSmartDoorLockActivity.this.finish();
                break;
            case R.id.wendu_linear:
                init_intent();
                break;
            case R.id.shidu_rel:

                break;
        }
//        init_intent();
    }


    private void init_intent() {
        Intent intent = null;
        intent = new Intent(SelectSmartDoorLockActivity.this, SelectSmartDoorLockTwoActivity.class);
        intent.putExtra("map", getIntent().getSerializableExtra("map"));
        startActivity(intent);
    }
}
