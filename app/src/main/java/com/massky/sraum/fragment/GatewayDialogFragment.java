package com.massky.sraum.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.EditGateWayResultActivity;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.AddZigbeeDevActivity;
import com.massky.sraum.activity.SearchGateWayActivity;
import com.massky.sraum.activity.SelectZigbeeDeviceActivity;
import com.massky.sraum.adapter.ShowGatewayListAdapter;
import com.massky.sraum.myzxingbar.qrcodescanlib.CaptureActivity;
import com.massky.sraum.tool.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.massky.sraum.Util.AES.Decrypt;


/**
 * Created by zhu on 2016/12/23.
 */

public class GatewayDialogFragment extends DialogFragment implements View.OnClickListener, SelectZigbeeDeviceActivity.GetGatewayInterfacer {

    private Dialog dialog;
    private ImageView back;
    private RelativeLayout sao_rel;
    private RelativeLayout search_rel;
    private static final String DECODED_CONTENT_KEY = "codedContent";
    List<String> list = new ArrayList<>();
    private ShowGatewayListAdapter showGatewayListAdapter;
    private ListView list_show_rev_item;
    private DialogUtil dialogUtil;
    private List<Map> gatewayList = new ArrayList<>();
    private Map map = new HashMap();

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (dialog != null) dialog.dismiss();
                break;
            case R.id.sao_rel:
                Intent openCameraIntent = new Intent(getActivity(), CaptureActivity.class);
                startActivityForResult(openCameraIntent, Constants.SCAN_REQUEST_CODE);
                break;
            case R.id.search_rel://局域网内扫描添加网关
                Intent searchGateWayIntent = new Intent(getActivity(), SearchGateWayActivity.class);
                startActivityForResult(searchGateWayIntent, Constants.SEARCH_GATEGAY);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 扫描二维码/条码回传
        if (requestCode == Constants.SCAN_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {

                String content = data.getStringExtra("result");
                Log.e("robin debug", "content:" + content);
                if (TextUtils.isEmpty(content))
                    return;
                //在这里解析二维码，变成房号
                // 密钥
                String key = "ztlmassky6206ztl";
                // 解密
                String DeString = null;
                try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                    DeString = Decrypt(content, key);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (DeString == null) {
                    ToastUtil.showToast(getActivity(), "此二维码不是网关二维码");
//                    scanlinear.setVisibility(View.VISIBLE);
//                    detairela.setVisibility(View.GONE);
                } else {
//                    scanlinear.setVisibility(View.GONE);
//                    detairela.setVisibility(View.VISIBLE);
//                    gateid.setText(DeString);
//                    gatedditexttwo.setText("");
                    //跳转到编辑网关界面

                    Intent intent = new Intent(getActivity(), EditGateWayResultActivity.class);
                    intent.putExtra("gateid", DeString);
                    startActivity(intent);
                }
            }
        }
    }


    public GatewayDialogFragment() {

    }

    public static GatewayDialogFragment newInstance(Context context1, String title, String message) {
        GatewayDialogFragment frag = new GatewayDialogFragment();
        Bundle b = new Bundle();
        b.putString("title", title);
        b.putString("message", message);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        dialog = new Dialog(getActivity(), R.style.DialogStyle);
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.gateway_dialog_fragment, null, false);
        //添加这一行
//       LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.linear);
//        linearLayout.getBackground().setAlpha(255);//0~255透明度值
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        initView(view);
        initEvent();
        //在这里配置wifi
        init_data();
        dialog.setContentView(view);
        setCancelable(true);//这句话调用这个方法时，按对话框以外的地方不起作用。按返回键也不起作用 -setCancelable (false);按返回键也不起作用
//        StatusBarCompat.compat(getActivity(), getResources().getColor(R.color.colorgraystatusbar));//更改标题栏的颜色
        return dialog;
    }

    private void init_data() {
        //获取区域下的网关列表

//        for (int i = 0; i < 4; i++) {
//            list.add("网关" + i);
//        }
//        showGatewayListAdapter = new ShowGatewayListAdapter(getActivity()
//                , list);
//        list_show_rev_item.setAdapter(showGatewayListAdapter);
        showAllGateWays();


    }

    /**
     * 展示该区域下的所有网关列表
     */
    private void showAllGateWays() {

    }


    /**
     * 让dialogFragment铺满整个屏幕的好办法
     */
    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Window win = getDialog().getWindow();
        // 一定要设置Background，如果不设置，window属性设置无效
        win.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.black)));

        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

        android.view.WindowManager.LayoutParams params = win.getAttributes();
        params.gravity = Gravity.BOTTOM;
        // 使用ViewGroup.LayoutParams，以便Dialog 宽度充满整个屏幕
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.WRAP_CONTENT;
        win.setAttributes(params);
    }


    private void initView(View view) {
        back = (ImageView) view.findViewById(R.id.back);//progress_loading_linear,loading_error_linear
//        sao_rel = (RelativeLayout) view.findViewById(R.id.sao_rel);
//        search_rel = (RelativeLayout) view.findViewById(R.id.search_rel);
        list_show_rev_item = (ListView) view.findViewById(R.id.list_show_rev_item);
        dialogUtil = new DialogUtil(getActivity());
    }

    private void initEvent() {
        back.setOnClickListener(this);
//        sao_rel.setOnClickListener(this);
//        search_rel.setOnClickListener(this);
        list_show_rev_item.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                set_gateway(position);
            }
        });
    }

    /**
     * 设置网关开启模式
     *
     * @param position
     */
    private void set_gateway(int position) {
        //去设置设置网关模式

        final String type = (String) map.get("type");
        String status = (String) map.get("status");
        final String gateway_number = (String) gatewayList.get(position).get("number");
        String areaNumber = (String) SharedPreferencesUtil.getData(getActivity(), "areaNumber", "");
        //在这里先调
        //设置网关模式-sraum-setBox
        Map map = new HashMap();
//        String phoned = getDeviceId(SelectZigbeeDeviceActivity.this);
        map.put("token", TokenUtil.getToken(getActivity()));
        map.put("boxNumber", gateway_number);
        String regId = (String) SharedPreferencesUtil.getData(getActivity(), "regId", "");
        map.put("regId", regId);
        map.put("status", status);
        map.put("areaNumber", areaNumber);

        dialogUtil.loadDialog();
        MyOkHttp.postMapObject(ApiHelper.sraum_setGateway, map, new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, getActivity(), dialogUtil) {
                    @Override
                    public void onSuccess(User user) {
                        Intent intent_position = null;
                        intent_position = new Intent(getActivity(), AddZigbeeDevActivity.class);
                        intent_position.putExtra("type", type);
                        intent_position.putExtra("boxNumber", gateway_number);
                        startActivity(intent_position);
                    }

                    @Override
                    public void wrongToken() {//
                        super.wrongToken();
                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(getActivity(),
                                "该网关不存在");
                    }
                }
        );
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        list.clear();
    }

    @Override
    public void sendGateWayparams(Map map, final List<Map> gatewayList1) {
        this.map = map;
        this.gatewayList = gatewayList1;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showGatewayListAdapter = new ShowGatewayListAdapter(getActivity(), gatewayList1);
                list_show_rev_item.setAdapter(showGatewayListAdapter);
            }
        }, 50);
    }
}
