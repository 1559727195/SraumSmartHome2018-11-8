package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.sraum.R;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/18.
 */

public class BindNewPhoneActivity extends BaseActivity{
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.complute_setting)
    TextView complute_setting;
    @InjectView(R.id.edit_nicheng)
    ClearEditText edit_nicheng;
    @Override
    protected int viewId() {
        return R.layout.bind_new_phone_act;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);
        String account_id =  getIntent().getStringExtra("account_change_phone");
        if (account_id != null) {
            edit_nicheng.setText(account_id);
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        complute_setting.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                BindNewPhoneActivity.this.finish();
                break;
            case R.id.complute_setting:
//                String account = edit_nicheng.getText().toString();
//                Intent intent = new Intent();
//                Bundle bundle = new Bundle();
//                bundle.putString("account_change_phone", account);
//                intent.putExtras(bundle);
//                this.setResult(RESULT_OK, intent);
//                BindNewPhoneActivity.this.finish();
                startActivity(new Intent(BindNewPhoneActivity.this,
                        InputCheckActivity.class));
                BindNewPhoneActivity.this.finish();

                break;
        }
    }
}
