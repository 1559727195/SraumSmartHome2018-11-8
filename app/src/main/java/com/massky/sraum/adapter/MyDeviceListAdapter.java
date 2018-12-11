package com.massky.sraum.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.activity.GuanLianSceneRealBtnActivity;
import com.massky.sraum.activity.MyDeviceListActivity;
import com.massky.sraum.view.ClearEditText;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class MyDeviceListAdapter extends android.widget.BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private boolean is_open_to_close;
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private int temp = -1;
    private Context context;//上下文
    private String accountType;
    private RefreshListener refreshListener;


    public MyDeviceListAdapter(Context context, List<Map> list, List<Integer> listint, List<Integer> listintwo, String accountType, RefreshListener refreshListener) {

        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.context = context;
        this.accountType = accountType;
        this.refreshListener = refreshListener;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.mydevicelist_item, null);
            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (LinearLayout) convertView.findViewById(R.id.swipe_context);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.hand_scene_btn = (ImageView) convertView.findViewById(R.id.hand_scene_btn);
            viewHolderContentType.swipe_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipe_layout);
            viewHolderContentType.delete_btn = (Button) convertView.findViewById(R.id.delete_btn);
            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.device_type_pic.setImageResource(listint.get(position));
        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());
        final ViewHolderContentType finalViewHolderContentType1 = viewHolderContentType;

        ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {


            @Override
            public void onItemClick() {
                Intent intent = new Intent(context, EditSceneSecondActivity.class);
                context.startActivity(intent);
            }

            @Override
            public void onItemClick_By_btn(boolean is_open_to_close1) {//SwipeLayout是否在打开到关闭的过程
                is_open_to_close = is_open_to_close1;
            }
        });

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;


        viewHolderContentType.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ToastUtil.showToast(context,"onDelete");
                //弹出删除对话框
                finalViewHolderContentType.swipe_layout.quickClose();
                showCenterDeleteDialog(list.get(position).get("name").toString()
                        , list.get(position).get("number").toString(), list.get(position).get("type").toString()
                        , list.get(position).get("boxNumber") == null ? "" :
                                list.get(position).get("boxNumber").toString());
            }
        });

        return convertView;
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name, final String number, final String type, final String boxNumber) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(context).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
//        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        tv_title.setText(name);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(context, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int displayWidth = dm.widthPixels;
        int displayHeight = dm.heightPixels;
        android.view.WindowManager.LayoutParams p = dialog.getWindow().getAttributes(); //获取对话框当前的参数值
        p.width = (int) (displayWidth * 0.8); //宽度设置为屏幕的0.5
//        p.height = (int) (displayHeight * 0.5); //宽度设置为屏幕的0.5
//        dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
        dialog.getWindow().setAttributes(p);  //设置生效
        dialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String areaNumber = (String) SharedPreferencesUtil.getData(context, "areaNumber", "");

                sraum_deleteDevice(areaNumber, number, type, boxNumber);
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                linkage_delete(linkId, dialog);
                dialog.dismiss();
            }
        });
    }

    /**
     * 删除设备
     * *
     *
     * @param areaNumber
     */
    private void sraum_deleteDevice(final String areaNumber, final String number, final String type,
                                    final String boxNumber) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        String send_method = "";
        switch (type) {
            case "AA02":
                mapdevice.put("number", number);
                send_method = ApiHelper.sraum_deleteWifiApple;
                break;//wifi模块
            case "AA03":
            case "AA04":
                mapdevice.put("number", number);
                send_method = ApiHelper.sraum_deleteWifiCamera;
                break;//wifi模块
            default:
                mapdevice.put("gatewayNumber", boxNumber);
                mapdevice.put("deviceNumber", number);
                send_method = ApiHelper.sraum_deleteDevice;
                break;
            case "网关":
                mapdevice.put("number", number);
                send_method = ApiHelper.sraum_deleteGateway;
                break;
        }

//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(send_method, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_deleteDevice(areaNumber, number, type, boxNumber);
            }
        }, context, null) {
            @Override
            public void onError(Call call, Exception e, int id) {
                super.onError(call, e, id);
            }

            @Override
            public void pullDataError() {
                super.pullDataError();
            }

            @Override
            public void emptyResult() {
                super.emptyResult();
            }

            @Override
            public void wrongToken() {
                super.wrongToken();
                //重新去获取togglen,这里是因为没有拉到数据所以需要重新获取togglen

            }

            @Override
            public void wrongBoxnumber() {
                ToastUtil.showToast(context, "areaNumber\n" +
                        "不存在");
            }

            @Override
            public void onSuccess(final User user) {
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
            }
        });
    }


    public void setLists(List<Map> list_hand_scene, List<Integer> listint, List<Integer> listintwo) {
        this.list = list_hand_scene;
        this.listint = listint;
        this.listintwo = listintwo;
    }


    class ViewHolderContentType {
        public Button delete_btn;
        ImageView device_type_pic;
        TextView hand_device_content;
        TextView hand_gateway_content;
        ImageView hand_scene_btn;
        LinearLayout swipe_context;
        SwipeMenuLayout swipe_layout;

    }

    public interface RefreshListener {
        void refresh();
    }
}
