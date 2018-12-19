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

public class SelectPmOneActivity extends BaseActivity {
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
    @InjectView(R.id.pm25_rel)
    RelativeLayout pm25_rel;

    @InjectView(R.id.project_select)
    TextView project_select;

    private String condition = "0";
    private Map map_link = new HashMap();

    @Override
    protected int viewId() {
        return R.layout.select_pm_one_lay;
    }

    @Override
    protected void onView() {
        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
            statusView.setBackgroundColor(Color.BLACK);
        }
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
        wendu_linear.setOnClickListener(this);
        shidu_rel.setOnClickListener(this);
        pm25_rel.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    private void setPicture() {
        project_select.setText(map_link.get("name").toString());
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.back:
                SelectPmOneActivity.this.finish();
                break;
            case R.id.wendu_linear:
                map_link.put("pm_action", "0");
                break;
            case R.id.shidu_rel:
                map_link.put("pm_action", "1");
                break;
            case R.id.pm25_rel:
                map_link.put("pm_action", "2");
                break;
        }
        init_intent();
    }


    private void init_intent() {
        Intent intent = null;
        intent = new Intent(SelectPmOneActivity.this, SelectPmTwoActivity.class);
        map_link.put("condition", condition);
        map_link.put("minValue", "");
        map_link.put("maxValue", "");
        map_link.put("name1", map_link.get("name"));

        intent.putExtra("map_link", (Serializable) map_link);
        startActivity(intent);
    }
}
