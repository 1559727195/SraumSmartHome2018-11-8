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
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.Utils.AppManager;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/1/8.
 */

public class AddNewRoomActivity extends BaseActivity {
    @InjectView(R.id.next_step_txt)
    TextView next_step_txt;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private String name;
    private String areaNumber;
    @InjectView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    private String doit;

    @Override
    protected int viewId() {
        return R.layout.add_new_room;
    }

    @Override
    protected void onView() {
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        doit = (String) getIntent().getSerializableExtra("doit");
    }

    /**
     * 添加房间（APP->网关）
     */
    private void sraum_addRoomInfo(final String name) {
        Map map = new HashMap();
        map.put("areaNumber", areaNumber);
        map.put("token", TokenUtil.getToken(AddNewRoomActivity.this));
        map.put("name", name);
        map.put("type", "4");

//        token：用于身份标识 其它指令都要带该访问号来标识身份信息
//        areaNumber：区域名称
//        name：房间名称
//        type：类型
        MyOkHttp.postMapObject(ApiHelper.sraum_addRoom, map,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_addRoomInfo(name);
                    }
                }, AddNewRoomActivity.this, null) {
                    @Override
                    public void onSuccess(User user) {
                        //"roomNumber":"1"
                        switch (doit == null ? "" : doit) {
                            case "sraum_deviceRelatedRoom":
                                AddNewRoomActivity.this.finish();
                                AppManager.getAppManager().finishActivity_current(RoomListActivity.class);
                                break;
                            default:
                                AddNewRoomActivity.this.finish();
                                break;
                        }

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
        areaNumber = (String) intent.getSerializableExtra("areaNumber");
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
                if (edit_password_gateway.getText().toString().equals("")) {
                    ToastUtil.showToast(AddNewRoomActivity.this, "房间为空");
                    return;
                }
                sraum_addRoomInfo(edit_password_gateway.getText().toString().trim());
//                ApplicationContext.getInstance().finishActivity(AddRoomActivity.class);
//                ApplicationContext.getInstance().finishActivity(ManagerRoomActivity.class);
                AddNewRoomActivity.this.finish();
                break;
            case R.id.back:
                AddNewRoomActivity.this.finish();
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }
}
