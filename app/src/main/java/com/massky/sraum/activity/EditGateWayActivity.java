package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.w3c.dom.Text;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/2.
 */

public class EditGateWayActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    @InjectView(R.id.edit_next_step)
    TextView edit_next_step;
    @Override
    protected int viewId() {
        return R.layout.edit_gateway;
    }

    /**
     * CTRL + H 是此時android studio的全局搜索快捷鍵
     */

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        toolbar_txt.setText("手动输入");
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        edit_next_step.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                EditGateWayActivity.this.finish();
                break;
            case R.id.edit_next_step:
//                startActivity(new Intent(EditGateWayActivity.this,
//                        LoginGateWayActivity.class));
                ApplicationContext.getInstance().removeActivity();
                startActivity(new Intent(EditGateWayActivity.this,
                        LoginGateWayActivity.class));
                break;//下一步
        }
    }
}
