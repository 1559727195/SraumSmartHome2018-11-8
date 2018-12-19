package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class GuanLianSceneBtnActivity extends BaseActivity{
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.rel_scene_set)
    RelativeLayout rel_scene_set;
    private Serializable list_result = new ArrayList<>();
    @Override
    protected int viewId() {
        return R.layout.guanlian_scene_btn;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
    }

    @Override
    protected void onEvent() {
        rel_scene_set.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        next_step_txt.setOnClickListener(this);
        Intent intent = getIntent();
        if (intent == null) return;
//        String excute = (String) intent.getSerializableExtra("excute");
//
//        switch (excute) {
//            case "auto"://自动
//                rel_scene_set.setVisibility(View.GONE);
//                break;
//            default:
//                rel_scene_set.setVisibility(View.VISIBLE);
//                break;
//        }
        list_result =   intent.getSerializableExtra("deviceList");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rel_scene_set:
//                GuanLianSceneActivity
                        startActivity(new Intent(GuanLianSceneBtnActivity.this,
                                GuanLianSceneActivity.class));
                break;
            case R.id.next_step_txt:
//                GuanLianSceneBtnActivity.this.finish();
                ApplicationContext.getInstance().finishButActivity(MainGateWayActivity.class);
                break;
            case R.id.back:
                GuanLianSceneBtnActivity.this.finish();
                break;
        }
    }
}
