package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class SettingRoomActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private String name;

    @Override
    protected int viewId() {
        return R.layout.setting_room_btn;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.

    }

    /**
     * 添加房间（APP->网关）
     */
    private void sraum_addRoomInfo() {
        name = (String) getIntent().getSerializableExtra("name");
        Map map = new HashMap();
        map.put("name", name);
        map.put("type", "1");
        MyOkHttp.postMapObject(ApiHelper.sraum_addRoomInfo, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_addRoomInfo();
                    }
                }, SettingRoomActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
                        //"roomNumber":"1"

                    }
                });
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        next_step_txt.setOnClickListener(this);
        Intent intent = getIntent();
//        if (intent == null) return;
//        String excute = (String) intent.getSerializableExtra("excute");
//        switch (excute) {
//            case "auto"://自动
//                rel_scene_set.setVisibility(View.GONE);
//                break;
//            default:
//                rel_scene_set.setVisibility(View.VISIBLE);
//                break;
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_step_txt:
//                GuanLianSceneBtnActivity.this.finish();
                sraum_addRoomInfo();
                ApplicationContext.getInstance().finishActivity(AddRoomActivity.class);
                ApplicationContext.getInstance().finishActivity(ManagerRoomActivity.class);
                SettingRoomActivity.this.finish();
                break;
            case R.id.back:
                SettingRoomActivity.this.finish();
                break;
        }
    }
}
