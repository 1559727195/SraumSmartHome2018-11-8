package com.massky.sraum.activity;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.base.BaseActivity;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;
import java.util.HashMap;
import java.util.Map;
import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by zhu on 2018/7/6.
 */

public class MessageDetailActivity extends BaseActivity {
    Map messageList = new HashMap();
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.time_txt)
    TextView time_txt;
    //    @InjectView(R.id.quyu_name)
//    TextView quyu_name;
    @InjectView(R.id.device_name)
    TextView device_name;
    @InjectView(R.id.action_text)
    TextView action_text;
    @InjectView(R.id.status_view)
    StatusView statusView;
    private DialogUtil dialogUtil;
    private String deviceName;
    private String messageTitle;
    private String eventTime;
    @InjectView(R.id.first_linear)
    LinearLayout first_linear;
    @InjectView(R.id.second_linear)
    LinearLayout second_linear;

    @Override
    protected int viewId() {
        return R.layout.message_detail_act;
    }

    @Override
    protected void onView() {
        dialogUtil = new DialogUtil(this);
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        back.setOnClickListener(this);
        messageList = (Map) getIntent().getSerializableExtra("Message");
        if (messageList != null) {
            sraum_getMessageById((String) messageList.get("id"));
        }
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
    private void sraum_getMessageById(final String id) {
        final Map map = new HashMap();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(MessageDetailActivity.this));
//        map.put("projectCode",projectCode);
        map.put("messageId", id);
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getMessageById, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() { //
                sraum_getMessageById(id);
            }
        }, MessageDetailActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(MessageDetailActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                deviceName = user.deviceName;
                messageTitle = user.messageTitle;
                eventTime = user.eventTime;
                if (deviceName != null) device_name.setText(deviceName);
                if (messageTitle != null) action_text.setText(messageTitle);
                if (eventTime != null) time_txt.setText(eventTime);
                action_text.setVisibility(View.VISIBLE);
                first_linear.setVisibility(View.VISIBLE);
                second_linear.setVisibility(View.VISIBLE);
                //设置消息已读
                all_read(id);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 全部标记已读
     */
    private void all_read(String id) {

        mark_all_read(id);
    }

    /**
     * 标记为全部已读
     *
     * @param
     */
    private void mark_all_read(final String messageIds) {
        final Map map = new HashMap();
//        dialogUtil.loadDialog();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(MessageDetailActivity.this));
//        map.put("projectCode",projectCode);
        map.put("messageIds", messageIds);
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_setReadStatus, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                mark_all_read(messageIds);
            }
        }, MessageDetailActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(MessageDetailActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
//                for (int i = 0; i < messageList.size(); i++) {
////                    SelectDeviceMessageAdapter.getIsItemRead().put(i, true);
//                    selectdevicemessageAdapter.setList(messageList);
//                }
//                get_message(false);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                //数据是使用Intent返回
                Intent intent = new Intent();
                //把返回数据存入Intent
                intent.putExtra("result", messageList.get("id").toString());
                //设置返回数据
                MessageDetailActivity.this.setResult(RESULT_OK, intent);
                MessageDetailActivity.this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("result", messageList.get("id").toString());
        //设置返回数据
        MessageDetailActivity.this.setResult(RESULT_OK, intent);
        MessageDetailActivity.this.finish();
    }
}
