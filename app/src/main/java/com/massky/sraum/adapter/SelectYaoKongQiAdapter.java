package com.massky.sraum.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.AddTogenInterface.AddTogglenInterfacer;
import com.massky.sraum.R;
import com.massky.sraum.User;
import com.massky.sraum.Util.DialogUtil;
import com.massky.sraum.Util.MyOkHttp;
import com.massky.sraum.Util.Mycallback;
import com.massky.sraum.Util.SharedPreferencesUtil;
import com.massky.sraum.Util.ToastUtil;
import com.massky.sraum.Util.TokenUtil;
import com.massky.sraum.Utils.ApiHelper;
import com.massky.sraum.activity.EditMyDeviceActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.widget.SlideSwitchButton;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class SelectYaoKongQiAdapter extends android.widget.BaseAdapter {
    private List<Map> list = new ArrayList<>();
    private List<Integer> listint = new ArrayList<>();
    private List<Integer> listintwo = new ArrayList<>();
    private int temp = -1;
    private Activity activity;//上下文
    DialogUtil dialogUtil;
    RefreshListener refreshListener;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();

    public SelectYaoKongQiAdapter(Activity context, List<Map> list, List<Integer> listint, List<Integer> listintwo, DialogUtil dialogUtil,
                                  RefreshListener refreshListener) {
        this.list = list;
        this.listint = listint;
        this.listintwo = listintwo;
        this.activity = context;
        this.activity = context;
        this.dialogUtil = dialogUtil;
        this.refreshListener = refreshListener;
    }
//
//    // 初始化isSelected的数据
//    private void initDate() {
//        for (int i = 0; i < list_bool.size(); i++) {
//            getIsSelected().put(i, false);
//        }
//    }


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
            convertView = LayoutInflater.from(activity).inflate(R.layout.select_yaokongqi_item, null);
            viewHolderContentType.img_guan_scene = (ImageView) convertView.findViewById(R.id.img_guan_scene);
            viewHolderContentType.panel_scene_name_txt = (TextView) convertView.findViewById(R.id.panel_scene_name_txt);
            viewHolderContentType.execute_scene_txt = (TextView) convertView.findViewById(R.id.execute_scene_txt);
            viewHolderContentType.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            //gateway_name_txt
            viewHolderContentType.gateway_name_txt = (TextView) convertView.findViewById(R.id.gateway_name_txt);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);
            viewHolderContentType.delete_btn = (Button) convertView.findViewById(R.id.delete_btn);
            viewHolderContentType.edit_btn = (Button) convertView.findViewById(R.id.edit_btn);
            viewHolderContentType.swipe_content_linear = (LinearLayout) convertView.findViewById(R.id.swipe_content_linear);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.panel_scene_name_txt.setText((String) list.get(position).get("name"));
        viewHolderContentType.gateway_name_txt.setText("");
        viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));

        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        //成员，业主accountType->addrelative_id
        String accountType = (String) SharedPreferencesUtil.getData(activity, "accountType", "");
        switch (accountType) {
            case "1":
                finalViewHolderContentType.swipemenu_layout.setLeftSwipe(true);
                viewHolderContentType.swipe_content_linear.setEnabled(true);
                break;//业主
            case "2":
                finalViewHolderContentType.swipemenu_layout.setLeftSwipe(false);
                viewHolderContentType.swipe_content_linear.setEnabled(false);
                break;//家庭成员
        }
        viewHolderContentType.delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalViewHolderContentType.swipemenu_layout.quickClose();
//                    delete_item();
                showCenterDeleteDialog(list.get(position).get("deviceId").toString(), list.get(position).get("number").toString(), list.get(position).get("name").toString());
            }
        });

        viewHolderContentType.edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finalViewHolderContentType.swipemenu_layout.quickClose();
//                    delete_item();
                showRenameDialog(list.get(position).get("number").toString(), list.get(position).get("name").toString(), position);
            }
        });

        viewHolderContentType.swipe_content_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map map = new HashMap();
                map.put("type", list.get(position).get("type").toString());
                map.put("name", list.get(position).get("name").toString());
                map.put("id", list.get(position).get("number").toString());
                Intent intent = new Intent(activity, EditMyDeviceActivity.class);
                intent.putExtra("panelItem", (Serializable) map);
                activity.startActivity(intent);
            }
        });

        return convertView;
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(final String id, final String name, final int position) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(activity).inflate(R.layout.editscene_dialog, null);
        final TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        final ClearEditText edit_password_gateway = (ClearEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title = (TextView) view.findViewById(R.id.tv_title);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(activity, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
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
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(activity, " wifi 红外转发设备名称为空");
                    return;
                }

                boolean isexist = false;
                String var = edit_password_gateway.getText().toString();
                for (int i = 0; i < list.size(); i++) {
                    if (i == position) {
                        continue;
                    }

                    if (var.equals(list.get(i).get("name"))) {
                        isexist = true;
                    }
                }

                if (isexist) {
                    ToastUtil.showToast(activity, "云场景名称已存在");
                    return;
                }


                if (!name.equals(edit_password_gateway.getText().toString() == null ? "" :
                        edit_password_gateway.getText().toString().trim())) {
                    sraum_updateWifiAppleName(id, edit_password_gateway.getText().toString() == null ? "" :
                                    edit_password_gateway.getText().toString()
                            , dialog);
                } else {
                    dialog.dismiss();
                }
            }
        });
    }

    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String deviceId, final String linkId, final String name) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();

        View view = LayoutInflater.from(activity).inflate(R.layout.promat_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
        TextView name_gloud;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);//name_gloud
        name_gloud = (TextView) view.findViewById(R.id.name_gloud);
        name_gloud.setText(name);
//        tv_title.setText("是否拨打119");
//        content.setText(message);
        //显示数据
        final Dialog dialog = new Dialog(activity, R.style.BottomDialog);
        dialog.setContentView(view);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DisplayMetrics dm = activity.getResources().getDisplayMetrics();
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
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sraum_deleteWifiApple(deviceId, linkId, dialog);
            }
        });
    }

    /**
     * 修改 wifi 红外转发设备名称
     *
     * @param
     * @param dialog
     */
    private void sraum_updateWifiAppleName(final String number, final String newName, final Dialog dialog) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(activity));
        mapdevice.put("number", number);
        mapdevice.put("name", newName);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_updateWifiAppleDeviceName, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_updateWifiAppleName(number, newName, dialog);
            }
        }, activity, dialogUtil) {
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
            public void fourCode() {
                ToastUtil.showToast(activity, "云场景名称已存在");
            }

            @Override
            public void wrongBoxnumber() {
                super.wrongBoxnumber();
            }

            @Override
            public void onSuccess(final User user) {
                dialog.dismiss();
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
            }
        });
    }

    /**
     * 删除 wifi 遥控器
     *
     * @param
     * @param deviceId
     * @param dialog
     */
    private void sraum_deleteWifiApple(final String deviceId, final String number, final Dialog dialog) {
        Map<String, String> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(activity));
        mapdevice.put("number", number);
//        mapdevice.put("boxNumber", TokenUtil.getBoxnumber(LinkageListActivity.this));
        MyOkHttp.postMapString(ApiHelper.sraum_deleteWifiAppleDevice, mapdevice, new Mycallback(new AddTogglenInterfacer() {
            @Override
            public void addTogglenInterfacer() {//刷新togglen数据
                sraum_deleteWifiApple(deviceId, number, dialog);
            }
        }, activity, dialogUtil) {
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
                super.wrongBoxnumber();
            }

            @Override
            public void onSuccess(final User user) {
                dialog.dismiss();
//                refreshLayout.autoRefresh();
                if (refreshListener != null)
                    refreshListener.refresh();
                SharedPreferencesUtil.remove_current_values(activity, "remoteairlist", deviceId,
                        "rid");
            }
        });
    }


    public void setLists(List<Map> list_hand_scene, List<Integer> listint, List<Integer> listintwo) {
        this.list = list_hand_scene;
        this.listint = listint;
        this.listintwo = listintwo;
    }

//    public static HashMap<Integer, Boolean> getIsSelected() {
//        return isSelected;
//    }
//
//    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
//        SelectSensorSingleAdapter.isSelected = isSelected;
//    }


    public static class ViewHolderContentType {
        public ImageView img_guan_scene;
        public TextView panel_scene_name_txt;
        public TextView execute_scene_txt;
        public CheckBox checkbox;
        public TextView gateway_name_txt;
        SlideSwitchButton hand_scene_btn;
        SwipeMenuLayout swipemenu_layout;
        Button delete_btn;
        Button edit_btn;
        LinearLayout swipe_content_linear;
    }

    public interface RefreshListener {
        void refresh();
    }
}
