package com.massky.sraum.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.HistoryBackAdapter;
import com.massky.sraum.adapter.SelectDeviceMessageAdapter;
import com.massky.sraum.adapter.SystemMessageAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.fragment.SystemMessageFragment;
import com.massky.sraum.view.XListView;
import com.massky.sraum.view.XListView_ForMessage;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

import static com.massky.sraum.Util.DipUtil.dip2px;
import static com.massky.sraum.activity.MessageActivity.MESSAGE_FRAGMENT;

/**
 * Created by zhu on 2018/2/12.
 */

public class SystemMessageActivity extends BaseActivity implements
        AdapterView.OnItemClickListener, XListView_ForMessage.IXListViewListener, AdapterView.OnItemLongClickListener {
    private static final int DEVICE_MESSAGE_BACK = 100;
    private SelectDeviceMessageAdapter selectdevicemessageAdapter;
    private String content = "";
    private View account_view;
    private DialogUtil dialogUtil;
    private List<Map> messageLists = new ArrayList<>();
    @InjectView(R.id.xListView_scan)
    XListView_ForMessage xListView_scan;
    @InjectView(R.id.linear_popcamera)
    LinearLayout linear_popcamera;
    @InjectView(R.id.all_read_linear)
    LinearLayout all_read_linear;
    @InjectView(R.id.all_select_linear)
    LinearLayout all_select_linear;
    @InjectView(R.id.delete_linear)
    LinearLayout delete_linear;
    private Handler mHandler = new Handler();
    private List<Map> messageLists_local = new ArrayList<>();
    private String str = "全选";
    @InjectView(R.id.all_select_txt)
    TextView all_select_txt;
    private static SystemMessageFragment.OnDeviceMessageFragListener onDeviceMessageFragListener1;
    private boolean backfrom_activity;
    private String messageId = "";
    @InjectView(R.id.addtxt_text_id)
    TextView addtxt_text_id;
    @InjectView(R.id.back)
    ImageView back;

    @Override
    protected int viewId() {
        return R.layout.system_message_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        addtxt_text_id.setVisibility(View.VISIBLE);
        addtxt_text_id.setText("编辑");
        this.content = "取消";
        common_del("");
        addtxt_text_id.setOnClickListener(this);
        xListView_scan.setOnItemClickListener(this);
        xListView_scan.setOnItemLongClickListener(this);
        selectdevicemessageAdapter = new SelectDeviceMessageAdapter(SystemMessageActivity.this, messageLists
        );
        xListView_scan.setAdapter(selectdevicemessageAdapter);
        xListView_scan.setPullLoadEnable(true);
        xListView_scan.setXListViewListener(this);
//        String stringBuffer = "1,2,3,";
//        String split = stringBuffer.toString().substring(0, stringBuffer.length() - 1);
//        addViewid();
        all_read_linear.setOnClickListener(this);
        all_select_linear.setOnClickListener(this);
        delete_linear.setOnClickListener(this);
        dialogUtil = new DialogUtil(SystemMessageActivity.this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SystemMessageActivity.this.finish();
                break;
            case R.id.addtxt_text_id:
                switch (addtxt_text_id.getText().toString()) {
                    case "编辑":
                        addtxt_text_id.setText("取消");
                        this.content = "编辑";
                        break;
                    case "取消":
                        this.content = "取消";
                        addtxt_text_id.setText("编辑");
                        break;
                }
                common_del("");
                break;

            case R.id.all_read_linear:
                //全部已读
                all_read();
                break;

            case R.id.all_select_linear://全选
                all_select(str);
                break;

            case R.id.delete_linear://删除
                delete();
                break;
        }
    }

    @Override
    public void onStart() {//onStart()-这个方法在屏幕唤醒时调用。
        super.onStart();
        addtxt_text_id.setText("编辑");
        this.content = "取消";
        common_del("");
        str = "全选";
        if (backfrom_activity) {
            backfrom_activity = false;
            for (int i = 0; i < messageLists.size(); i++) {
                if (messageLists.get(i).get("id").toString().equals(messageId)) {
                    messageLists.get(i).put("readStatus", "1");
                }
            }
            SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
            selectdevicemessageAdapter.setList(messageLists);
        } else {
            get_message(true, "doit");
        }
    }

    /**
     * 退出动画
     */
    private void exitanimation() {
        Animation animation = AnimationUtils.loadAnimation(
                SystemMessageActivity.this, R.anim.dialog_exit);
        linear_popcamera.clearAnimation();
        linear_popcamera.setAnimation(animation);
        linear_popcamera.setVisibility(View.GONE);
        xListView_scan.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 1.0f));
        linear_popcamera.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0.0f));
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    /**
     * 进入动画
     */
    private void enteranimation() {
        xListView_scan.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 1.0f));
        linear_popcamera.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT, 0.0f));
        Animation animation = AnimationUtils.loadAnimation(
                SystemMessageActivity.this, R.anim.dialog_enter);
        linear_popcamera.clearAnimation();
        linear_popcamera.setAnimation(animation);
        linear_popcamera.setVisibility(View.VISIBLE);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        onLoad();
        get_message(true, "doit");
    }

    @Override
    public void onLoadMore() {
        onLoad();
        new Thread(new Runnable() {
            @Override
            public void run() {
                get_message(false, "doit");
            }
        }).start();

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

        return true;
    }


    private int page = 1;

    private void get_message(final boolean isRefresh, String doit) {
        if (isRefresh) {
            page = 1;
        }
        get_message_by_page(isRefresh, doit);
    }

    private void get_message_by_page(final boolean isRefresh, final String doit) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialogUtil.loadDialog();
            }
        });
        final Map map = new HashMap();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(SystemMessageActivity.this));
//        map.put("projectCode",projectCode);
        map.put("page", page);
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_getMessage, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                get_message_by_page(isRefresh, doit);
            }
        }, SystemMessageActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(SystemMessageActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                if (isRefresh) {
                    messageLists.clear();
                    SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
                } else {
                    str = "全选";
                }
//                messageList.addAll(user.messageList);
                for (int i = 0; i < user.messageList.size(); i++) {
                    Map map = new HashMap();
                    map.put("id", user.messageList.get(i).id);
                    map.put("messageType", user.messageList.get(i).messageType);
                    map.put("messageTitle", user.messageList.get(i).messageTitle);
                    if (user.messageList.get(i).deviceName != null) {
                        map.put("deviceName", user.messageList.get(i).deviceName);
                    } else {
                        map.put("deviceName", "");
                    }
                    map.put("readStatus", user.messageList.get(i).readStatus);
                    map.put("eventTime", user.messageList.get(i).eventTime);
                    map.put("ischecked", false);
                    switch (user.messageList.get(i).messageType) {
                        case "100":
                            messageLists.add(map);
                            break;
                        default:
                            break;
                    }
                }
                common_del(doit);
                page++;
//                if( dialogUtil != null){
//                    dialogUtil.removeviewBottomDialog();
//                }
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    /**
     * 公共处理方法
     */
    private void common_del(String doit) {
        switch (content) {
            case "编辑":
                if (doit.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            dialogUtil.loadViewBottomdialog();
                            enteranimation();
                        }
                    });
                }
                for (int i = 0; i < messageLists.size(); i++) {
                    selectdevicemessageAdapter.getIsCheckBoxVisiable().put(i, true);
                }
                break;
            default:
            case "取消":
                if (doit.equals("")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
//                            dialogUtil.loadViewBottomdialog();
                            str = "全选";
                            all_select_txt.setText(str);
                            exitanimation();
                        }
                    });
                    for (int i = 0; i < messageLists.size(); i++) {
                        selectdevicemessageAdapter.getIsCheckBoxVisiable().put(i, false);
                        messageLists.get(i).put("ischecked", false);
                    }
                } else {
                    for (int i = 0; i < messageLists.size(); i++) {
                        selectdevicemessageAdapter.getIsCheckBoxVisiable().put(i, false);
                        messageLists_local = SharedPreferencesUtil.getInfo_List(SystemMessageActivity.this, "messageLists");
                        if (messageLists_local.size() != 0) {
                            SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
                            for (int j = 0; j < messageLists_local.size(); j++) {
                                if (messageLists_local.get(j).get("id").equals(
                                        messageLists.get(i).get("id")
                                )) {
                                    messageLists.get(i).put("ischecked", messageLists_local.get(j).get("ischecked"));
                                }
                            }
                        }
                    }
                }
                break;
        }
        SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
        if(selectdevicemessageAdapter != null)
        selectdevicemessageAdapter.setList(messageLists);
    }


    // 设置popupWindow背景半透明
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;// 0.0-1.0
        getWindow().setAttributes(lp);
    }


    /**
     * 删除
     */
    private void delete() {
        String temp = ",";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < messageLists.size(); i++) {
            if ((boolean) messageLists.get(i).get("ischecked")) {
                stringBuffer.append(messageLists.get(i).get("id") + temp);
            }
        }
        if (stringBuffer.toString().trim().equals("")) {
            ToastUtil.showToast(SystemMessageActivity.this, "请先进行勾选");
            return;
        }
        String split = stringBuffer.toString().substring(0, stringBuffer.length() - 1);

        switch (str) {
            case "全不选":
                delete_select(split, "all");
                break;
            default:
                delete_select(split, "");
                break;
        }

    }

    /**
     * 删除选中的
     *
     * @param split
     */
    private void delete_select(final String split, final String content) {
        final Map map = new HashMap();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(SystemMessageActivity.this));
//        map.put("projectCode",projectCode);
//        map.put("messageIds", split);
        switch (content) {
            case "all":
                map.put("messageIds", content);
                break;
            default:
                map.put("messageIds", split);
                break;
        }
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteMessage, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                delete_select(split, content);
            }
        }, SystemMessageActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(SystemMessageActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
                String[] indexs = split.split(",");
                for (int j = 0; j < indexs.length; j++) {

                    for (int i = 0; i < messageLists.size(); i++) {
                        if (messageLists.get(i).get("id").equals(indexs[j])) {
                            messageLists.remove(i);
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
                selectdevicemessageAdapter.setList(messageLists);
//                get_message(true, "doit");
                onDeviceMessageFragListener1.ondevice_message_frag();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }


    /**
     * 消息全选
     *
     * @param str
     */
    private void all_select(String str) {

        switch (str) {
            case "全选":
                this.str = "全不选";
                for (int i = 0; i < messageLists.size(); i++) {
                    messageLists.get(i).put("ischecked", true);
                }
                break;
            case "全不选":
                this.str = "全选";
                for (int i = 0; i < messageLists.size(); i++) {
                    messageLists.get(i).put("ischecked", false);
                }
                break;
        }
        all_select_txt.setText(this.str);
        selectdevicemessageAdapter.setList(messageLists);
        SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
    }

    /**
     * 全部标记已读
     */
    private void all_read() {
        String temp = ",";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < messageLists.size(); i++) {
            if ((boolean) messageLists.get(i).get("ischecked")) {
                stringBuffer.append(messageLists.get(i).get("id") + temp);
            }
        }
        if (stringBuffer.toString().trim().equals("")) {
            ToastUtil.showToast(SystemMessageActivity.this, "请先进行勾选");
            return;
        }
        String split = stringBuffer.toString().substring(0, stringBuffer.length() - 1);

        switch (str) {
            case "全不选":
                mark_all_read(split, "all");
                break;
            default:
                mark_all_read(split, "");
                break;
        }
    }


    /**
     * 标记为全部已读
     *
     * @param
     */
    private void mark_all_read(final String messageIds, final String content) {
        final Map map = new HashMap();
//        String roomNo = roomNums.get(roomIndex);
        map.put("token", TokenUtil.getToken(SystemMessageActivity.this));
//        map.put("projectCode",projectCode);
        switch (content) {
            case "all":
                map.put("messageIds", content);
                break;
            default:
                map.put("messageIds", messageIds);
                break;
        }
//        map.put("roomNo",roomNo == null ? "" : roomNo);
        MyOkHttp.postMapObject(ApiHelper.sraum_setReadStatus, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {
                mark_all_read(messageIds, content);
            }
        }, SystemMessageActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                ToastUtil.showDelToast(SystemMessageActivity.this, "网络连接超时");
            }

            @Override
            public void onSuccess(User user) {
//                for (int i = 0; i < messageList.size(); i++) {
////                    SelectDeviceMessageAdapter.getIsItemRead().put(i, true);
//                    selectdevicemessageAdapter.setList(messageList);
//                }
//                get_message(true, "doit");
                //标记成功后，局部刷新标记后的数据
                String[] indexs = messageIds.split(",");
                for (int j = 0; j < indexs.length; j++) {

                    for (int i = 0; i < messageLists.size(); i++) {
                        if (messageLists.get(i).get("id").equals(indexs[j])) {
                            messageLists.get(i).put("readStatus", "1");
                        }
                    }
                }
                SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
                selectdevicemessageAdapter.setList(messageLists);
                onDeviceMessageFragListener1.ondevice_message_frag();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch (content) {
            case "编辑":
//                View v = parent.getChildAt(position - xListView_scan.getFirstVisiblePosition());
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                cb.toggle();
                messageLists.get(position - 1).put("ischecked", cb.isChecked());
                SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", messageLists);
                break;
            case "取消":
            default:
                //
                Intent intent = new Intent(SystemMessageActivity.this, MessageDetailActivity.class);
                intent.putExtra("Message", (Serializable) messageLists.get(position - 1));
                startActivityForResult(intent, DEVICE_MESSAGE_BACK);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("peng", "MacFragment->onResume:name:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferencesUtil.saveInfo_List(SystemMessageActivity.this, "messageLists", new ArrayList<Map>());
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //得到新Activity 关闭后返回的数据
        messageId = data.getExtras().getString("result");
        if (messageId != null) {
            Log.e("zhu", "result:" + messageId);
            backfrom_activity = true;
        }
    }

}
