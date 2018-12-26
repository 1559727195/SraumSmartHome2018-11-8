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
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/6/19.
 */

public class SelectPmTwoActivity extends BaseActivity {
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.big_linear)
    LinearLayout big_linear;

    @InjectView(R.id.small_rel)
    RelativeLayout small_rel;

    @InjectView(R.id.project_select)
    TextView project_select;

    private String condition = "0";
    private Map map_link = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.select_pm_two_lay;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        onEvent();
        map_link = (Map) getIntent().getSerializableExtra("map_link");
        if (map_link == null) return;
        setPicture();
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        next_step_txt.setOnClickListener(this);
        big_linear.setOnClickListener(this);
        small_rel.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private void setPicture() {
        switch (map_link.get("pm_action").toString()) {
            case "0":
                project_select.setText("温度");
                break;
            case "1":
                project_select.setText("湿度");
                break;
            case "2":
                project_select.setText("PM2.5");
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SelectPmTwoActivity.this.finish();
                break;
            case R.id.big_linear:
                map_link.put("pm_condition", "0");
                break;
            case R.id.small_rel:
                map_link.put("pm_condition", "1");
                break;
        }
        init_intent();
    }

    private void init_intent() {
        Intent intent = null;
        intent = new Intent(SelectPmTwoActivity.this
                , SelectPmDataActivity.class);
        intent.putExtra("map_link", (Serializable) map_link);
        startActivity(intent);
    }
}
