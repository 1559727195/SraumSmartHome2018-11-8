package com.massky.sraumsmarthome.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.ICallback;
import com.massky.sraumsmarthome.Util.IConnectTcpback;
import com.massky.sraumsmarthome.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraumsmarthome.Util.ToastUtil;
import com.massky.sraumsmarthome.Util.UDPClient;
import com.massky.sraumsmarthome.Utils.ApiHelper;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.bean.DaoSession;
import com.massky.sraumsmarthome.bean.GateWayInfoBean;
import com.massky.sraumsmarthome.bean.GateWayInfoBeanDao;
import com.massky.sraumsmarthome.service.MyService;
import com.massky.sraumsmarthome.view.SycleSearchView;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

import butterknife.InjectView;
import lecho.lib.hellocharts.model.Line;

/**
 * Created by zhu on 2018/1/2.
 */

public class SearchGateWayActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    @InjectView(R.id.sycle_search)
    SycleSearchView sycle_search;
    Boolean test_start = false;
    @InjectView(R.id.fangdajing)
    ImageView fangdajing;
    @InjectView(R.id.search_result)
    LinearLayout search_result;

    @Override
    protected int viewId() {
        return R.layout.search_gate_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        toolbar_txt.setText("搜索网关");
        fangdajing.setOnClickListener(this);
//        String str = "11:22:33:44:55:6a";
//        //密码是后6位
//        StringBuffer password = new StringBuffer();
//        String[] strs = str.split(":");
//         for (int i = strs.length - 4; i < strs.length; i++) {
//             password.append(strs[i]);
//         }

    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        sycle_search.setOnClickListener(this);
        search_result.setOnClickListener(this);
    }

    @Override
    protected void onData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                SearchGateWayActivity.this.finish();
                break;
            case R.id.sycle_search:
                test_start = !test_start;
                if (test_start) {
                    sycle_search.startsycle();
                } else {
                    sycle_search.stopsycle();
                }
                break;
            case R.id.fangdajing://
                fangdajing.setVisibility(View.GONE);
                search_result.setVisibility(View.GONE);
//                new android.os.Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        fangdajing.setVisibility(View.VISIBLE);
//                        sycle_search.stopsycle();
//                        sycle_search.setVisibility(View.GONE);
//                        search_result.setVisibility(View.VISIBLE);
//                    }
//                },2000);
                //搜索网关


                udp_searchGateway();

                sycle_search.setVisibility(View.VISIBLE);
                sycle_search.startsycle();

                break;
            case R.id.search_result:
                SearchGateWayActivity.this.finish();
                break;
        }
    }

    /**
     * command:命令标识 sraum_searchFGateway
     */
    private void udp_searchGateway() {
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_SearchGateway);
        //number：网关编号，唯一编号 网关的MAC地址去掉冒号，所有字母小写；若为””，则搜索所有网关
        map.put("number", "");
//                send_udp(new Gson().toJson(map),"sraum_searchGateway");
        UDPClient.initUdp(new Gson().toJson(map), ApiHelper.Sraum_SearchGateway, new ICallback() {
            @Override
            public void process(final Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        udp_sraum_setGatewayTime();
//                        ToastUtil.showToast(Main_New_Activity.this,"sraum_searchGateway_success!");
                        Map map = (Map) data;
                        String command = (String) map.get("command");
                        String content = (String) map.get("content");
                        final User user = new GsonBuilder().registerTypeAdapterFactory(
                                new NullStringToEmptyAdapterFactory()).create().fromJson(content, User.class);//json字符串转换为对象
                        if (user == null) return;
//                        SharedPreferencesUtil.saveData(LoginGateWayActivity.this,"tcp_server_ip",user.ip);
                        //存储数据库，搜索网关信息表
                        deleteMovie();
                        insert_gateway_infor(user);
                        //init_tcp_connect();
                        end_search_gateway_ui();
//                        Log.d("SCAN", code);
                        end_activity();
                    }
                });
            }

            @Override
            public void error(String data) {//Socket close
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SearchGateWayActivity.this, "网关断线或异常");
                    }
                });
            }//
        });
    }

    private void end_activity() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
//                        bundle.putString("result", code);
        intent.putExtras(bundle);
        SearchGateWayActivity.this.setResult(RESULT_OK, intent);
        SearchGateWayActivity.this.finish();
    }

    private void end_search_gateway_ui() {
        fangdajing.setVisibility(View.VISIBLE);
        sycle_search.stopsycle();
        sycle_search.setVisibility(View.GONE);
        search_result.setVisibility(View.VISIBLE);
    }

    /**
     * command:命令标识 sraum_searchFGateway
     */
    private void udp_sraum_setGatewayTime() {

        SimpleDateFormat foo = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.println("foo:" + foo.format(new Date()));
        String time = foo.format(new Date());
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_SetGatewayTime);
        map.put("time", time);
//        send_udp(new Gson().toJson(map), "sraum_setGatewayTime");

//        UDPClient.activity_destroy(false);
        UDPClient.initUdp(new Gson().toJson(map), ApiHelper.Sraum_SetGatewayTime, new ICallback() {
            @Override
            public void process(Object data) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(SearchGateWayActivity.this, "sraum_setGatewayTime_success!");
                        //去连接TCPServer
                    }
                });
            }

            @Override
            public void error(String data) {

            }
        });
    }

    /**
     * 插入网关信息表
     *
     * @param user
     */
    private void insert_gateway_infor(User user) {
//        MovieBean movie = new MovieBean();
//        movie.setTitle("这是第" + ++i + "条数据。");
//        DaoSession daoSession = ((MyApp) getApplication()).getDaoSession();
//        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
//        queryMovieBean();
        GateWayInfoBean gateWayInfoBean = new GateWayInfoBean();
        gateWayInfoBean.setIp(user.ip);
        gateWayInfoBean.setMAC(user.MAC);
        gateWayInfoBean.setName(user.name);
        gateWayInfoBean.setNumber(user.number);
        gateWayInfoBean.setProto(user.proto);
        gateWayInfoBean.setStatus(user.status);
        gateWayInfoBean.setTimeStamp(user.timeStamp);
        DaoSession daoSession = ((ApplicationContext) getApplication()).getDaoSession();
//        MovieBeanDao movieBeanDao = daoSession.getMovieBeanDao();
//        movieBeanDao.insert(movie);
        GateWayInfoBeanDao gateWayInfoBeanDao = daoSession.getGateWayInfoBeanDao();
        gateWayInfoBeanDao.insert(gateWayInfoBean);
    }

    private void deleteMovie() {
        DaoSession daoSession = ((ApplicationContext) getApplication()).getDaoSession();
        GateWayInfoBeanDao gateWayInfoBeanDao = daoSession.getGateWayInfoBeanDao();
        gateWayInfoBeanDao.deleteAll();
//        Query<MovieBean> query = movieBeanDao.queryBuilder()
//                .where(MovieBeanDao.Properties.Id.eq(10))
//                .build();
//        Query<GateWayInfoBean> query = gateWayInfoBeanDao.queryBuilder()
//                .build();
////        MovieBean movieBean = query.unique();
////        if (movieBean != null) {
////            movieBeanDao.delete(movieBean);
////            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
////        } else {
////            Toast.makeText(this, "此条数据不存在", Toast.LENGTH_SHORT).show();
////        }
//        List<GateWayInfoBean> beanList = query.list();
//        if (beanList != null) {
//            for (GateWayInfoBean movie : beanList) {
////                movie.setTitle("这条是更新的数据。");
////                movieBeanDao.update(movie);
//                gateWayInfoBeanDao.delete(movie);
//            }
//        }
    }


    /**
     * 查找GateWayInfoMation
     */
    private GateWayInfoBean queryGateWayInfo() {
        //查询某条数据是否存在
        DaoSession daoSession = ((ApplicationContext) getApplication()).getDaoSession();
        GateWayInfoBeanDao gateWayInfoBeanDao = daoSession.getGateWayInfoBeanDao();
        Query<GateWayInfoBean> beanQuery = gateWayInfoBeanDao.queryBuilder()
                .build();
        GateWayInfoBean gateWayInfoBean = beanQuery.unique();
        if (gateWayInfoBean != null) {
            Toast.makeText(this, "数据查询成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "这条数据不存在", Toast.LENGTH_SHORT).show();
        }

        return gateWayInfoBean;
//        queryMovieBean();
    }
}
