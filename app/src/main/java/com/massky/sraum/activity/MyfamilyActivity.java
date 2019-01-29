package com.massky.sraum.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.IntentUtil;
import com.massky.sraum.Util.LogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.MyfamilyAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.view.PullToRefreshLayout;
import com.yanzhenjie.statusview.StatusUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import okhttp3.Call;

/**
 * Created by masskywcy on 2016-09-21.
 */
public class MyfamilyActivity extends BaseActivity implements PullToRefreshLayout.OnRefreshListener {
    @InjectView(R.id.myfamlistview)
    ListView myfamlistview;
    @InjectView(R.id.refresh_view)
    PullToRefreshLayout refresh_view;
    @InjectView(R.id.addfamcircle)
    ImageView addfamcircle;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.manager_room_txt)
    TextView manager_room_txt;

    @InjectView(R.id.project_select)
    TextView project_select;

    private MyfamilyAdapter adapter;
    private DialogUtil dialogUtil;
    //进行判断是否进行创建刷新
    private boolean isFirstIn = true;
    private List<User.familylist> list = new ArrayList<>();
    private PullToRefreshLayout pullToRefreshLayout;
    private String accountType;
    private String authType;
    private String areaNumber;


    @Override
    protected int viewId() {
        return R.layout.myfamily;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        dialogUtil = new DialogUtil(MyfamilyActivity.this);
        refresh_view.setOnRefreshListener(this);
        addfamcircle.setOnClickListener(this);
        manager_room_txt.setOnClickListener(this);
        LogUtil.i("第二次查看");
        areaNumber = (String) getIntent().getSerializableExtra("areaNumber");
        authType = (String) getIntent().getSerializableExtra("authType");
        //addfamcircle
        switch (authType) {
            case "1":
                manager_room_txt.setVisibility(View.VISIBLE);
                break;//    break;//主机，业主-写死
            case "2":
                manager_room_txt.setVisibility(View.GONE);
                break;//从机，成员
        }
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    protected void onStart() {

        super.onStart();
        refresh_view.autoRefresh();
    }

    //获取家庭成员
    private void getFamily(final PullToRefreshLayout pullToRefreshLayout) {
        sraum_get_famliy(pullToRefreshLayout);
    }

    private void sraum_get_famliy(final PullToRefreshLayout pullToRefreshLayout) {
        Map<String, Object> map = new HashMap<>();
        map.put("token", TokenUtil.getToken(MyfamilyActivity.this));
//        String areaNumber = (String) SharedPreferencesUtil.getData(MyfamilyActivity.this,
//                "areaNumber","");
        map.put("areaNumber", areaNumber);
        MyOkHttp.postMapObject(ApiHelper.sraum_getFamily, map, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//获取gogglen刷新数据
                sraum_get_famliy(pullToRefreshLayout);
            }
        }, MyfamilyActivity.this, dialogUtil) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onSuccess(User user) {
                super.onSuccess(user);
                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                list.clear();
                list.addAll(user.familyList);
                if (isFirstIn) {
                    adapter = new MyfamilyAdapter(MyfamilyActivity.this, list, authType, areaNumber, TokenUtil.getToken(MyfamilyActivity.this), dialogUtil
                            , accountType);
                    myfamlistview.setAdapter(adapter);
                    isFirstIn = false;
                } else {
                    adapter.notifyDataSetChanged();
                }
                project_select.setText("家人列表(" + list.size() + ")");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                MyfamilyActivity.this.finish();
                break;
            case R.id.addfamcircle:
            case R.id.manager_room_txt:
                Intent intent = new Intent(MyfamilyActivity.this, AddfamilyActivity.class);
                intent.putExtra("areaNumber", areaNumber);
                startActivity(intent);
//                IntentUtil.startActivity(MyfamilyActivity.this, AddfamilyActivity.class);
                break;
        }
    }

    @Override
    public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
        getFamily(pullToRefreshLayout);
    }

    @Override
    public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

    }
}
