package com.massky.sraumsmarthome.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.sraumsmarthome.R;
import com.massky.sraumsmarthome.User;
import com.massky.sraumsmarthome.Util.EyeUtil;
import com.massky.sraumsmarthome.Util.ICallback;
import com.massky.sraumsmarthome.Util.IConnectTcpback;
import com.massky.sraumsmarthome.Util.MD5Util;
import com.massky.sraumsmarthome.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraumsmarthome.Util.SharedPreferencesUtil;
import com.massky.sraumsmarthome.Util.Timeuti;
import com.massky.sraumsmarthome.Util.ToastUtil;
import com.massky.sraumsmarthome.Util.UDPClient;
import com.massky.sraumsmarthome.Utils.ApiHelper;
import com.massky.sraumsmarthome.base.BaseActivity;
import com.massky.sraumsmarthome.bean.DaoSession;
import com.massky.sraumsmarthome.bean.GateWayInfoBean;
import com.massky.sraumsmarthome.bean.GateWayInfoBeanDao;
import com.massky.sraumsmarthome.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraumsmarthome.permissions.RxPermissions;
import com.massky.sraumsmarthome.service.MyService;
import com.massky.sraumsmarthome.tool.Constants;
import com.massky.sraumsmarthome.view.ClearEditText;
import com.massky.sraumsmarthome.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.greenrobot.greendao.query.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static com.massky.sraumsmarthome.Util.AES.Encrypt;

/**
 * Created by zhu on 2017/12/29.
 */

public class LoginGateWayActivity extends BaseActivity {
    @InjectView(R.id.status_view)
    StatusView statusView;
    @InjectView(R.id.back)
    ImageView back;
    @InjectView(R.id.toolbar_txt)
    TextView toolbar_txt;
    @InjectView(R.id.scan_gateway)
    ImageView scan_gateway;
    @InjectView(R.id.search_gateway_btn)
    TextView search_gateway_btn;
    @InjectView(R.id.btn_login_gateway)
    Button btn_login_gateway;
    @InjectView(R.id.edit_wangguan_id)
    ClearEditText edit_gateway_id;
    @InjectView(R.id.edit_password_gateway)
    ClearEditText edit_password_gateway;
    @InjectView(R.id.eyeimageview_id_gateway)
    ImageView eyeimageview_id_gateway;
    private EyeUtil eyeUtil;
    public static final String MESSAGE_SRAUM_VERIFI_SOCKET = "com.massky.sraumsmarthome.sraum_verifySocket";
    public static final String MESSAGE_SRAUM_LOGIN = "com.massky.sraumsmarthome.sraum_login";

    //sraum_login
    @Override
    protected int viewId() {//
        return R.layout.login_gateway;
    }

    @Override
    protected void onView() { //
//        if (!StatusUtils.setStatusBarDarkFont(this, true)) {// Dark font for StatusBar.
//            statusView.setBackgroundColor(Color.BLACK);
//        }


        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        toolbar_txt.setText("登录网关");
        scan_gateway.setOnClickListener(this);
        UDPClient.activity_destroy(false);
        Intent intent = new Intent(this, MyService.class);
        startService(intent);
//        udp_searchGateway();//初始化UDP，和TCP连接...
        addBrodcastAction();
        //
        String number = (String) SharedPreferencesUtil.getData(LoginGateWayActivity.this, "number",
                "");
        String password = (String) SharedPreferencesUtil.getData(LoginGateWayActivity.this, "password",
                "");
        if (number != null)
            edit_gateway_id.setText(number);

        if (password != null)
            edit_password_gateway.setText(password);
    }


    /**
     * 添加TCP接收广播通知
     */
    private void addBrodcastAction() {
        // add Action1
        addCanReceiveAction(new Intent(MESSAGE_SRAUM_VERIFI_SOCKET), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                //处理action1
                User user = init_user_from_tcp(intent);
                if (user == null) return;
                if (user.result.equals("100")) {
                    //去登录网关
                    init_login_gateway();
                }
            }
        });

        // add Action2
        addCanReceiveAction(new Intent(MESSAGE_SRAUM_LOGIN), new OnActionResponse() {

            @Override
            public void onResponse(Intent intent) {
                //处理action2
                //处理action1
                User user = init_user_from_tcp(intent);
                if (user == null) return;
                if (user.result.equals("100")) {
                    //去登录网关
                    ToastUtil.showToast(LoginGateWayActivity.this, "网关登录成功");
                }
            }
        });
    }

    /**
     * 登录网关
     */
    private void init_login_gateway() {
//        String edit_gateway_id_str =  edit_gateway_id.getText().toString();
//        String edit_password_gateway_str = edit_password_gateway.getText().toString();
        GateWayInfoBean gateWayInfoBean = queryGateWayInfo();
        if (gateWayInfoBean == null) return;
        final String number = gateWayInfoBean.getNumber();
        //密码是后6位
        StringBuffer password = new StringBuffer();
        String[] strs = number.split(":");
        for (int i = strs.length - 4; i < strs.length; i++) {
            password.append(strs[i]);
        }
        //密码是后6位
//        String password = number.substring(number.length() - 6);

        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_Login);//sraum_verifySocket
        map.put("number", number);//sraum_verifySocket
        map.put("password", password);//系统分配用户
//                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
        MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_Login);
    }

    /**
     * 解析获取的user
     *
     * @param intent
     */
    private User init_user_from_tcp(Intent intent) {
        String tcpreceiver = intent.getStringExtra("strcontent");
//                ToastUtil.showToast(context, "tcpreceiver:" + tcpreceiver);
        //解析json数据
        final User user = new GsonBuilder().registerTypeAdapterFactory(
                new NullStringToEmptyAdapterFactory()).create().fromJson(tcpreceiver, User.class);//json字符串转换为对象
        return user;
    }


   /* // command:命令标识 sraum_searchFGateway

    private void udp_searchGateway() {
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_SearchGateway);
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
                        insert_gateway_infor(user);
//                        init_tcp_connect();
                    }
                });
            }

            @Override
            public void error(String data) {//Socket close
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginGateWayActivity.this, "网关断线或异常");
                    }
                });

            }//
        });
    }


     // 插入网关信息表
     //
     // @param user
     //
     //

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
*/
    // 查找GateWayInfoMation

    private GateWayInfoBean queryGateWayInfo() {
        //查询某条数据是否存在
        DaoSession daoSession = ((ApplicationContext) getApplication()).getDaoSession();
        GateWayInfoBeanDao gateWayInfoBeanDao = daoSession.getGateWayInfoBeanDao();
        Query<GateWayInfoBean> beanQuery = gateWayInfoBeanDao.queryBuilder()
                .build();
        GateWayInfoBean gateWayInfoBean = beanQuery.unique();
//        if (gateWayInfoBean != null) {
//            Toast.makeText(this, "数据查询成功", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "这条数据不存在", Toast.LENGTH_SHORT).show();
//        }

        return gateWayInfoBean;
//        queryMovieBean();
    }


    /**
     * 初始化TCP连接
     */
    private void init_tcp_connect() {
//        final  String ip = (String) SharedPreferencesUtil.getData(LoginGateWayActivity.this,"tcp_server_ip","");
        GateWayInfoBean gateWayInfoBean = queryGateWayInfo();
        if (gateWayInfoBean == null) return;
        final String ip = gateWayInfoBean.getIp();//192.168.169.42
        new Thread(new Runnable() {
            @Override
            public void run() {

                MyService.getInstance().connectTCP(ip, new IConnectTcpback() {//连接tcpServer成功
                    @Override
                    public void process() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //ToastUtil.showToast(LoginGateWayActivity.this, "连接TCPServer成功");
                                sraum_verifySocket();//认证有效的TCP链接 (APP ->网关)
                            }
                        });
                    }

                    @Override
                    public void error(final int connect_ctp) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (connect_ctp >= 0) {//去主动，网关断线后，每隔10s去连接。
                                    // 收到异常，重连，没收到，心跳之后，第一步，再次发心跳。超时5s，再次收到异常，显示网关断线。去连网关。
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            init_tcp_connect();
                                        }
                                    }, 10000);//10s
                                } else {//退出后，界面相应变化，网关异常断线，显示断线，不能直接退出。
                                    ToastUtil.showToast(LoginGateWayActivity.this, "显示网关断线。去连网关");
                                }
                            }
                        });
                    }
                }, new ICallback() {

                    @Override
                    public void process(Object data) {

                    }

                    @Override
                    public void error(String data) {
                        //收到tcp服务端断线
                        init_tcp_connect(); //网关断线后，每隔10s去连接。
                    }
                });
            }
        }).start();
    }

    /***
     *
     //认证有效的TCP链接（APP-》网关）
     */
    private void sraum_verifySocket() {
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_VerifySocket);//sraum_verifySocket
        map.put("user", "sraum");//系统分配用户
        String time = Timeuti.getTime();
        map.put("timeStamp", time);
        map.put("signature", MD5Util.md5("sraum" + "massky_gw2_6206" + time));
//                    sraum_send_tcp(map,"sraum_verifySocket");//认证有效的TCP链接
        MyService.getInstance().sraum_send_tcp(map, ApiHelper.Sraum_VerifySocket);
    }

    @Override
    protected void onEvent() {
        back.setOnClickListener(this);
        init_permissions();
        search_gateway_btn.setOnClickListener(this);
        btn_login_gateway.setOnClickListener(this);
        eyeimageview_id_gateway.setOnClickListener(this);
        eyeUtil = new EyeUtil(LoginGateWayActivity.this, eyeimageview_id_gateway, edit_password_gateway, true);
    }

    @Override
    protected void onData() {

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
                        ToastUtil.showToast(LoginGateWayActivity.this, "sraum_setGatewayTime_success!");
                        //去连接TCPServer

                    }
                });
            }

            @Override
            public void error(String data) {

            }
        });
    }

    private void init_permissions() {

        // 清空图片缓存，包括裁剪、压缩后的图片 注意:必须要在上传完成后调用 必须要获取权限
        RxPermissions permissions = new RxPermissions(this);
        permissions.request(Manifest.permission.CAMERA).subscribe(new Observer<Boolean>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Boolean aBoolean) {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                LoginGateWayActivity.this.finish();
                break;
            case R.id.scan_gateway:
                Intent openCameraIntent = new Intent(LoginGateWayActivity.this, CaptureActivity.class);
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE);
                break;
            case R.id.search_gateway_btn:

                Intent searchGateWayIntent = new Intent(LoginGateWayActivity.this, SearchGateWayActivity.class);
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY);
                break;
            case R.id.btn_login_gateway:
//                udp_searchGateway();
//                startActivity(new Intent(LoginGateWayActivity.this,MainGateWayActivity.class));

                String edit_gateway_id_str = edit_gateway_id.getText().toString();
                String edit_password_gateway_str = edit_password_gateway.getText().toString();
                if (edit_gateway_id_str.equals("") || edit_password_gateway_str.equals("")) {
                    ToastUtil.showDelToast(LoginGateWayActivity.this, "网关编号或网关" +
                            "密码不能为空");
                } else
                    init_tcp_connect();//去连接tcp
                break;//登录网关

            case R.id.eyeimageview_id_gateway:
                eyeUtil.EyeStatus();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        UDPClient.activity_destroy(true);//udp线程被杀死,暂时不能被杀死
    }

    /**
     * 二维码扫描回调操作
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.SCAN_REQUEST_CODE) {
            camera_receive(data);
        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.SEARCH_GATEGAY) {
            search_gate_way_receive(data);
        }
    }

    /*
    * 显示搜出来的网关ID和网关密码
    * */
    private void search_gate_way_receive(Intent data) {
//        String edit_gateway_id_str = edit_gateway_id.getText().toString();
//        String edit_password_gateway_str = edit_password_gateway.getText().toString();
        GateWayInfoBean gateWayInfoBean = queryGateWayInfo();
        if (gateWayInfoBean == null) return;
        final String number = gateWayInfoBean.getNumber();
        //密码是后6位
        String password = number.substring(number.length() - 6);
        edit_gateway_id.setText(number);
        edit_password_gateway.setText(password);
        SharedPreferencesUtil.saveData(LoginGateWayActivity.this, "number", number);
        SharedPreferencesUtil.saveData(LoginGateWayActivity.this, "password", password);
    }

    private void camera_receive(Intent data) {
        final String result = data.getExtras().getString("result");
        if (TextUtils.isEmpty(result))//ab42bc2fa4223430774240259671db94
            return;
        String key = "masskysraum-6206";//masskysraum-6206
        // 解密
        String DeString = null;
        try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
            DeString = Encrypt(result, key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (DeString == null) {
            ToastUtil.showToast(LoginGateWayActivity.this, "非本系统二维码");
        } else {
            //搜索网关信息 ，udp->tcp
            udp_searchGateway(DeString);
        }
    }


    /**
     * command:命令标识 sraum_searchFGateway
     *
     * @param number
     */
    private void udp_searchGateway(String number) {
        Map map = new HashMap();
        map.put("command", ApiHelper.Sraum_SearchGateway);
        //number：网关编号，唯一编号 网关的MAC地址去掉冒号，所有字母小写；若为””，则搜索所有网关
        map.put("number", number);
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
                        deleteGateWay();
                        insert_gateway_infor(user);
                        //init_tcp_connect();
                        //
                        search_gate_way_receive(null);//填充网关ID和密码
                    }
                });
            }

            @Override
            public void error(String data) {//Socket close
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginGateWayActivity.this, "网关断线或异常");
                    }
                });

            }//
        });
    }

    private void deleteGateWay() {
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

}
