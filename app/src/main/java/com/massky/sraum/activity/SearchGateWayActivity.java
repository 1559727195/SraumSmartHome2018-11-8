package com.massky.sraum.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.massky.sraum.EditGateWayResultActivity;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.ByteUtils;
import com.massky.sraum.Util.ICallback;
import com.massky.sraum.Util.IConnectTcpback;
import com.massky.sraum.Util.NullStringToEmptyAdapterFactory;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.UDPClient;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.adapter.ShowUdpServerAdapter;
import com.massky.sraum.base.BaseActivity;
import com.massky.sraum.bean.DaoSession;
import com.massky.sraum.bean.GateWayInfoBean;
import com.massky.sraum.bean.GateWayInfoBeanDao;
import com.massky.sraum.fragment.ConfigDialogFragment;
import com.massky.sraum.fragment.SearchDialogFragment;
import com.massky.sraum.service.MyService;
import com.massky.sraum.view.SycleSearchView;
import com.massky.sraum.widget.ApplicationContext;
import com.yanzhenjie.statusview.StatusUtils;
import com.yanzhenjie.statusview.StatusView;

import org.greenrobot.greendao.query.Query;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;
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

    @InjectView(R.id.list_show_rev_item)
    ListView list_show_rev_item;
    @InjectView(R.id.search_txt)
    TextView search_txt;
    //    @InjectView(R.id.list_show_rev_item_detail)
//    ListView list_show_rev_item_detail;
    @InjectView(R.id.rel_list_show)
    RelativeLayout rel_list_show;
    @InjectView(R.id.goimage_id)
    ImageView goimage_id;
    @InjectView(R.id.detailimage_id)
    ImageView detailimage_id;
    @InjectView(R.id.stopimage_id)
    ImageView stopimage_id;
    boolean isFirst = true;
    WThread t;
    private int n = 0;
    private boolean flag = true;
    private static final int MAX_DATA_PACKET_LENGTH = 1400;//报文长度
    private byte[] buffer = new byte[MAX_DATA_PACKET_LENGTH];
    private boolean serSocFlag;
    //添加一个boolean变量测量那个UDPServerSocket是否已经死掉，只有死掉的时候才能重新去发送UDPSocket
    private boolean UDPServerSocket_is_death;
    CopyOnWriteArrayList<String> list_rev_udp = new CopyOnWriteArrayList<String>();//存储来自udp服务器端的数据列表
    private ShowUdpServerAdapter showUdpServerAdapter;
    private List<String> list = new ArrayList<>();
    private int REQUEST_SCAN_WANGGUAN = 0x006;
    private final int REQUEST_SCAN_NO_WANGGUAN = 0x007;
    private int REQUEST_SUBMIT_WANGGUAN = 0x009;
    private boolean tiaozhuan_bool;//跳转判断
    private boolean istiaozhuan;
    private SearchDialogFragment newFragment;

    @Override
    protected int viewId() {
        return R.layout.search_gate_act;
    }

    @Override
    protected void onView() {
        StatusUtils.setFullToStatusBar(this);  // StatusBar.
        toolbar_txt.setText("搜索网关");
        fangdajing.setOnClickListener(this);
        serSocFlag = true;
        startanim();
        search_txt.setText("正在搜索局域网");
        new BroadCastUdp("021001000021F403").start();
        showUdpServerAdapter = new ShowUdpServerAdapter(SearchGateWayActivity.this
                , list);

        /*
          这个是局部listview的监听事件
         */
        list_show_rev_item.setAdapter(showUdpServerAdapter);
        list_show_rev_item.setOnItemClickListener(new MyOnItemCLickListener());
        initDialog();
    }


    /*
     * 初始化dialog
     */
    private void initDialog() {
        // TODO Auto-generated method stub
        newFragment = SearchDialogFragment.newInstance(SearchGateWayActivity.this, "", ""
                , new SearchDialogFragment.DialogClickListener() {
                    @Override
                    public void dialogDismiss() {//重新进行搜索
                        init_udp_search();
                    }
                });//初始化快配和搜索设备dialogFragment
        sendParamsInterfacer = (SendParamsInterfacer) newFragment;
    }

    /*
     * 初始化udp搜索
     */
    private void init_udp_search() {
        serSocFlag = true;
        t.onResume();
        sycle_search.startsycle();
        search_txt.setText("正在搜索局域网");
        list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
        new BroadCastUdp("021001000021F403").start();//重新去搜
    }

    /**
     * 给全屏窗体传参数
     */
    private SendParamsInterfacer sendParamsInterfacer;

    public interface SendParamsInterfacer {
        void sendparams(List<String> list);
    }


    /**
     * 展示全窗体dialog对话框
     */
    private void show_dialog_fragment() {
        if (!newFragment.isAdded()) {//DialogFragment.show()内部调用了FragmentTransaction.add()方法，所以调用DialogFragment.show()方法时候也可能
            FragmentManager manager = getFragmentManager();
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(newFragment, "dialog");
            ft.commit();
        }
    }


    private class MyOnItemCLickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //销毁activity返回到AddGateWayActivity
//            Intent intent = getIntent();
//            intent.putExtra("gateWayMac", list.get(position).toString());
//            intent.putExtra("act_flag", "Search");//我是SearchgatewayActivity
//            setResult(RESULT_OK, intent);
//            SearchGateWayActivity.this.finish();
            Intent intent = new Intent(SearchGateWayActivity.this, EditGateWayResultActivity.class);
            intent.putExtra("gateid", list.get(position));//跳转到编辑网关密码界面
            startActivity(intent);
            serSocFlag = false;
            t.onStop();
        }
    }

    /*监听并开启动画展示*/
    private void startanim() {

        goimage_id.setOnClickListener(this);
        detailimage_id.setOnClickListener(this);
        stopimage_id.setOnClickListener(this);
        sycle_search.startsycle();
        if (isFirst) {
            isFirst = false;
            t = new WThread();
            t.start();
        }
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
                Intent intent = getIntent();
                istiaozhuan = true;//表明正在跳转到其他界面
                setResult(RESULT_OK, intent);
                finish();
//                } else {
//                    tiaozhuan_bool = false;
//                    list_show_rev_item_detail.setVisibility(View.INVISIBLE);
//                    rel_list_show.setVisibility(View.VISIBLE);
//                    serSocFlag = true;
//                    t.onResume();
//                    search_txt.setText("正在搜索局域网");
//                    list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
//                    new BroadCastUdp("021001000021F403").start();//重新去搜
//                }
                break;
            case R.id.sycle_search:
                test_start = !test_start;
                break;
            case R.id.fangdajing://

                break;
            case R.id.search_result:
                SearchGateWayActivity.this.finish();
                break;
            case R.id.goimage_id:
                t.onResume();
                serSocFlag = true;
                if (UDPServerSocket_is_death) {
                    sycle_search.startsycle();
                    list = new ArrayList<>();
                    showUdpServerAdapter.clear();
                    search_txt.setText("正在搜索局域网");
                    list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
                    new BroadCastUdp("021001000021F403").start();//重新去搜
                }
                break;
            case R.id.detailimage_id:
                serSocFlag = false;
                t.onPause();
                if (list.size() != 0) {
                    show_dialog_fragment();
                    if (sendParamsInterfacer != null)
                        sendParamsInterfacer.sendparams(list);
                }
                break;
            case R.id.stopimage_id://停止搜索
                serSocFlag = false;
                sycle_search.stopsycle();
                t.onPause();
                break;
        }
    }


    /*自定义线程类实现开启，暂停等操作*/
    public class WThread extends Thread {

        private final Object lock;
        boolean isPause;
        boolean isStop;
        int c;

        public WThread() {
            lock = new Object();
            isPause = false;
            isStop = false;
            c = 1;
        }

        //暂停
        public void onPause() {
            if (!isPause)
                isPause = true;
        }

        public void onWait() {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onResume() {
            synchronized (lock) {
                isPause = false;
                lock.notifyAll();
            }
        }

        public void onStop() {
            //如果线程处于wait状态，那么会唤醒它，但该中断也就被消耗了，无法捕捉到,退出操作会在下一个循环时实现
            //但如果线程处于running状态，那么该中断便可以被捕捉到
            isStop = true;
            this.interrupt();
        }


        @Override
        public void run() {
            super.run();
            try {
                while (!isStop) {
                    //捕获中断
                    if (Thread.interrupted()) {
                        //结束中断
                        if (isStop) {
                            return;
                            //如果中断不是由我们手动发出，那么就不予处理，直接交由更上层处理
                            //不应该生吞活剥
                        } else {
                            this.interrupt();
                            continue;
                        }

                    }
                    n++;
                    if (n == 13) {
                        n = 0;
                    }
                    Log.e("robin debug", "n:" + n);
                    if (isPause) onWait();
                    //lock.wait();需要在run中才能起作用
                    //模拟耗时操作
                    Thread.sleep(200);
                    Message m = h.obtainMessage();
                    m.what = n;
                    h.sendMessage(m);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public class BroadCastUdp extends Thread {
        private String dataString;
        private DatagramSocket udpSocket;

        public BroadCastUdp(String dataString) {
            this.dataString = dataString;
        }

        public void run() {
            DatagramPacket dataPacket = null;

            try {
                udpSocket = new DatagramSocket(9991);

                dataPacket = new DatagramPacket(buffer, MAX_DATA_PACKET_LENGTH);
//				byte[] data = dataString.getBytes();
                byte[] data = ByteUtils.hexStringToBytes(dataString);//字符串转换为byte
                dataPacket.setData(data);
                dataPacket.setLength(data.length);
                dataPacket.setPort(9991);
                InetAddress broadcastAddr;
                broadcastAddr = InetAddress.getByName("255.255.255.255");
                dataPacket.setAddress(broadcastAddr);
            } catch (Exception e) {
                Log.e("robin debug", e.toString());
            }
            // while( start ){
            try {
                udpSocket.send(dataPacket);
                udpSocket.close();
                //客户端UDP发送之后就开始监听，服务器端UDP返回数据

                new ReceivBroadCastUdp().start();
//				sleep(10);
            } catch (Exception e) {
                Log.e("robin debug", e.toString());
            }
            // }
        }
    }

    //处理接收到的UDPServer的数据
    android.os.Handler handler_udp_recev = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String wanggguan_mac = (String) msg.obj;//021001001262a001ff1008313030385352677700000000643603
            //实时显示列表数据
            list_rev_udp.add(wanggguan_mac);
            //去除list集合中重复项的几种方法
            HashSet<String> hs = new HashSet<String>(list_rev_udp); //此时已经去掉重复的数据保存在hashset中
            Iterator<String> stringIterator = hs.iterator();
            list = new ArrayList<>();
            for (int i = 0; i < hs.size(); i++) {
                list.add(stringIterator.next());
            }
            showUdpServerAdapter.clear();
            showUdpServerAdapter.addAll(list);
        }
    };

    /**
     * 接收来自UDP服务器端发送过来,UDP 通信是应答模式，你发我收
     */
    public class ReceivBroadCastUdp extends Thread {
        private DatagramSocket udpSocket;
        DatagramPacket udpPacket = null;
        public int add;
        private Timer timer;
        private TimerTask task;
        private boolean UDPServer;

        @Override
        public void run() {
            //搞个定时器10s后,关闭该UDPReceverSocket
            UDPServer = true;
            UDPServerSocket_is_death = false;
            initTimer();
            byte[] data = new byte[256];
            try {
                udpSocket = new DatagramSocket(8881);//服务器端UDP端口号，网关端口9991
                udpPacket = new DatagramPacket(data, data.length);
            } catch (SocketException e1) {
                e1.printStackTrace();
            }
            while (UDPServer) {
                try {
                    udpSocket.receive(udpPacket);
                } catch (Exception e) {

                }
                if (udpPacket != null) {
                    if (null != udpPacket.getAddress()) {
                        //02 1001 0012 62a001ff1008 313030385352677700000000643603
                        String codeString = ByteUtils.bytesToHexString(data, udpPacket.getLength());
                        if (codeString == null) {
                            return;
                        }
                        if (codeString.length() > 22) {
                            String wanggguan_mac = codeString.substring(10, 22);//62a001ff1008
                            Log.e("zhu", "wanggguan_mac:" + wanggguan_mac);
                            Message message = new Message();
                            message.obj = wanggguan_mac;
                            handler_udp_recev.sendMessage(message);
                        }
                    }
                }
            }
            if (udpSocket != null)
                udpSocket.close();
        }

        /**
         * 初始化定时器
         */
        private void initTimer() {
            if (timer == null)
                timer = new Timer();
            //3min
            //关闭clientSocket即客户端socket
            if (task == null)
                task = new TimerTask() {

                    @Override
                    public void run() {
                        add++;
                        Log.e("robin debug", "add: " + add);
                        if (add > 400) {//10s= 1000 * 10
                            try {
                                add = 0;
                                closeTimerAndClientSocket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else if (!serSocFlag) {//长连接倒计时3分钟未到，TcpServer退出，要停掉该定时器，并关闭clientSocket即客户端socket
                            try {
                                closeTimerAndClientSocket();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    /**
                     * 关闭定时器和socket客户端
                     *
                     * @throws IOException
                     */
                    private void closeTimerAndClientSocket() throws IOException {
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (task != null) {
                            task.cancel();
                            task = null;
                        }
                        closeUDPServerSocket();//关闭clientSocket即客户端socket
                    }
                };
            timer.schedule(task, 100, 25); // 1s后执行task,经过1ms再次执行
        }

        /**
         * 关闭UDPServerSocket服务器端
         */
        private void closeUDPServerSocket() {
            UDPServer = false;
            UDPServerSocket_is_death = true;
            sycle_search.stopsycle();
            if (udpSocket != null)
                udpSocket.close();
            t.onPause();
            if (list.size() == 0) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!istiaozhuan) {
                            search_txt.setText("未搜索到局域网");
                            //没有搜到局域网时，就跳进没有搜到局域网的界面
//                            Intent intent = new Intent(SearchGateWayActivity.this, GatedetailActivity.class);
//                            startActivityForResult(intent, REQUEST_SCAN_NO_WANGGUAN);
                            serSocFlag = false;
                            t.onPause();
                        }
                    }
                });
            } else {
//                list = new ArrayList<>();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        search_txt.setText("");
                    }
                });
            }
        }
    }

    android.os.Handler h = new android.os.Handler() {

        public void handleMessage(Message msg) {


        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        serSocFlag = false;
        t.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == REQUEST_SCAN_WANGGUAN && resultCode == RESULT_OK) {
            t.onResume();
            serSocFlag = true;
            if (UDPServerSocket_is_death) {
                list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
                search_txt.setText("正在搜索局域网");
                new BroadCastUdp("021001000021F403").start();//重新去搜
            }
        } else if (requestCode == REQUEST_SUBMIT_WANGGUAN && resultCode == RESULT_OK) {
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else if (requestCode == REQUEST_SCAN_NO_WANGGUAN && resultCode == RESULT_OK) {
            search_txt.setText("正在搜索局域网");
            t.onResume();
            serSocFlag = true;
            list = new ArrayList<>();
            list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
            showUdpServerAdapter.clear();
            new BroadCastUdp("021001000021F403").start();//重新去搜
//            list_show_rev_item_detail.setVisibility(View.INVISIBLE);
            rel_list_show.setVisibility(View.VISIBLE);
//            titlecen_id.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (!tiaozhuan_bool) {
            istiaozhuan = true;//表明正在跳转到其他界面
            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else {
            tiaozhuan_bool = false;
//            list_show_rev_item_detail.setVisibility(View.INVISIBLE);
            rel_list_show.setVisibility(View.VISIBLE);
//            titlecen_id.setVisibility(View.GONE);
//            titlecen_id.setText("");
            serSocFlag = true;
            t.onResume();
            list = new ArrayList<>();
            showUdpServerAdapter.clear();
            search_txt.setText("正在搜索局域网");
            list_rev_udp = new CopyOnWriteArrayList<>();//只有搜索完毕后，才能清空
            new BroadCastUdp("021001000021F403").start();//重新去搜
        }
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

}
