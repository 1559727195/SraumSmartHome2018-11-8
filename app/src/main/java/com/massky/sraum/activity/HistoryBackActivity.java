package com.massky.sraum.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.king.photo.activity.MessageSendActivity;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.HandSceneAdapter;
import com.massky.sraum.adapter.HistoryBackAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.XListView;
import com.yanzhenjie.statusview.StatusUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;

/**
 * Created by zhu on 2018/2/12.
 */

public class HistoryBackActivity extends BaseActivity implements XListView.IXListViewListener {
    private List<Map> list_hand_scene;
    @InjectView(R.id.xListView_scan)
    XListView xListView_scan;
    private Handler mHandler = new Handler();
    private HistoryBackAdapter historybackdapter;
    @InjectView(R.id.back)
    ImageView back;
    private DialogUtil dialogUtil;
    private List<Map> opinionList = new ArrayList<>();
    @InjectView(R.id.hostory_back_txt)
    TextView hostory_back_txt;

    @Override
    protected int viewId() {
        return R.layout.history_back_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);
        dialogUtil = new DialogUtil(this);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        hostory_back_txt.setOnClickListener(this);
    }

    @Override
    protected void onData() {
        list_hand_scene = new ArrayList<>();
        list_hand_scene.add(new HashMap());
        historybackdapter = new HistoryBackAdapter(HistoryBackActivity.this, opinionList);
        xListView_scan.setAdapter(historybackdapter);
        xListView_scan.setPullLoadEnable(false);
        xListView_scan.setXListViewListener(this);
        xListView_scan.setFootViewHide();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                HistoryBackActivity.this.finish();
                break;
            case R.id.hostory_back_txt:
                HistoryBackActivity.this.finish();
                break;
        }
    }

    private void onLoad() {
        xListView_scan.stopRefresh();
        xListView_scan.stopLoadMore();
        xListView_scan.setRefreshTime("刚刚");
    }

    @Override
    public void onRefresh() {
        onLoad();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sraum_getComplaint();
    }

    @Override
    public void onLoadMore() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onLoad();
            }
        }, 1000);
    }

    //获取意见反馈记录

    private void sraum_getComplaint() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(HistoryBackActivity.this));

        MyOkHttp.postMapObject(ApiHelper.sraum_getComplaint, map, new Mycallback
                (new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, HistoryBackActivity.this, dialogUtil) {
            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                opinionList = new ArrayList<>();
                for (int i = 0; i < user.opinionList.size(); i++) {
                    Map map = new HashMap();
                    map.put("id", user.opinionList.get(i).id);
                    map.put("content", user.opinionList.get(i).content);
                    map.put("dt", user.opinionList.get(i).dt);
                    map.put("type", user.opinionList.get(i).type);
                    opinionList.add(map);
                }


                historybackdapter.addAll(opinionList);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
            }
        });
    }

//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//        Intent intent = new Intent(HistoryBackActivity.this, HistoryMessageDetailActivity.class);
//            intent.putExtra("id", opinionList.get(i-1).get("id").toString());
//            startActivity(intent);
//        }

}
