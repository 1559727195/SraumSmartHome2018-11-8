package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.BitmapUtil;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Utils.ApiHelper.sraum_getMessageById;

/**
 * Created by zhu on 2018/7/6.
 */

public class HistoryMessageDetailActivity extends BaseActivity {
    Map messageList = new HashMap();
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.img_show_6)
    ImageView img_show_6;
    @InjectView(R.id.type_txt)
    TextView type_txt;
    @InjectView(R.id.content_txt)
    TextView content_txt;
    @InjectView(R.id.time_txt)
    TextView time_txt;
    private String id;
    private DialogUtil dialogUtil;
    private String img;
    private Bitmap bitmap;

    @Override
    protected int viewId() {
        return R.layout.history_message_detail_act;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        id = (String) getIntent().getSerializableExtra("id");
        dialogUtil.loadDialog();
        sraum_getComplaintById(id == null ? "" : id);

    }

    @Override
    protected void onEvent() {

    }

    @Override
    protected void onData() {

    }

    /**
     * 根据编号获取详细详情
     *
     * @param id
     */
    private void sraum_getComplaintById(final String id) {
        final Map map = new HashMap();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(HistoryMessageDetailActivity.this));
//        map.put("projectCode",projectCode);
        map.put("id", id);
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getComplaintById, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() { //
                sraum_getComplaintById(id);
            }
        }, HistoryMessageDetailActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(HistoryMessageDetailActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(final User user) {
//                user.opinionList
//                deviceName = user.deviceName;
//                messageTitle = user.messageTitle;
//                eventTime = user.eventTime;
//                if (deviceName != null) device_name.setText(deviceName);
//                if (messageTitle != null) action_text.setText(messageTitle);
//                if (eventTime != null) time_txt.setText(eventTime);
//                action_text.setVisibility(View.VISIBLE);
//                first_linear.setVisibility(View.VISIBLE);
//                second_linear.setVisibility(View.VISIBLE);
//                //设置消息已读
//                all_read(id);
                String content = user.opinionInfo.content;
                img = user.opinionInfo.img;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bitmap = BitmapUtil.stringtoBitmap(user.opinionInfo.img == null ? "" : user.opinionInfo.img);
                        handler.sendEmptyMessage(0);
                    }
                }).start();
                if (content != null) {
                    content_txt.setText(content);
                }
                String type = user.opinionInfo.type;
                if (type != null) {
                    switch (type) {
                        case "1":
                            type_txt.setText("功能建议");
                            break;
                        case "2":
                            type_txt.setText("性能问题");
                            break;
                        case "3":
                            type_txt.setText("其他");
                            break;
                    }
                }

                String dt = user.opinionInfo.dt;
                if (dt != null) {
                    time_txt.setText(dt);
                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (bitmap != null)
                img_show_6.setImageBitmap(bitmap);
        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HistoryMessageDetailActivity.this.finish();
                break;
        }
    }
}
