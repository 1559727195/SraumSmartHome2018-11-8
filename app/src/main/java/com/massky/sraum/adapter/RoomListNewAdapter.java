package com.massky.sraum.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.massky.sraum.activity.MyDeviceItemActivity;
import com.massky.sraum.view.ClearEditText;
import com.massky.sraum.view.ClearLengthEditText;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class RoomListNewAdapter extends BaseAdapter {
    private String areaNumber;
    //    private List<Map> list = new ArrayList<>();
    private RefreshListener refreshListener;

    public RoomListNewAdapter(Context context, List<Map> list, RefreshListener refreshListener) {
        super(context, list);
//        this.list = list;
        this.refreshListener = refreshListener;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.room_listnew_item, null);
            viewHolderContentType.room_name_txt = (TextView) convertView.findViewById(R.id.room_name_txt);
            viewHolderContentType.img_again_autoscene = (ImageView) convertView.findViewById(R.id.img_again_autoscene);
            //pic_room_img
            viewHolderContentType.pic_room_img = (ImageView) convertView.findViewById(R.id.pic_room_img);
            viewHolderContentType.txt_device_num = (TextView) convertView.findViewById(R.id.txt_device_num);
            viewHolderContentType.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);
            viewHolderContentType.remove_btn = (Button) convertView.findViewById(R.id.remove_btn);
            viewHolderContentType.swipemenu_layout = (SwipeMenuLayout) convertView.findViewById(R.id.swipemenu_layout);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.room_name_txt.setText(((Map) getList().get(position)).get("name").toString());

        String authType = ((Map) getList().get(position)).get("authType").toString();

        switch (authType) {
            case "1":
                viewHolderContentType.swipemenu_layout.setSwipeEnable(true);
                break;
            case "2":
                viewHolderContentType.swipemenu_layout.setSwipeEnable(false);
                break;
        }


        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        viewHolderContentType.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                finalViewHolderContentType.swipemenu_layout.quickClose();
                showRenameDialog(((Map) getList().get(position)).get("name").toString(),
                        ((Map) getList().get(position)).get("number").toString());

            }
        });

        viewHolderContentType.remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalViewHolderContentType.swipemenu_layout.quickClose();

                showCenterDeleteDialog(((Map) getList().get(position)).get("name").toString(),
                        ((Map) getList().get(position)).get("number").toString(), areaNumber);
            }
        });
        return convertView;//
    }


    //自定义dialog,centerDialog删除对话框
    public void showCenterDeleteDialog(final String name, final String number, final String areaNumber) {
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
                sraum_deleteRoom(areaNumber, number);
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
     * 删除房间（）APP->网关
     */
    private void sraum_deleteRoom(final String areaNumber, final String number) {
        Map<String, Object> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("roomNumber", number);
        MyOkHttp.postMapObject(ApiHelper.sraum_deleteRoom, mapdevice,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {

                    }
                }, context, null) {
                    @Override
                    public void onSuccess(User user) {
                        if (refreshListener != null)
                            refreshListener.refresh();

                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(context, "areaNumber\n" +
                                "不存在");
                    }


                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(context, "名字已存在");
                    }

                    @Override
                    public void fourCode() {
                        ToastUtil.showToast(context, "默认房间不能删除");
                    }
                });
    }

    /**
     * 修改房间名称
     */
    private void sraum_updateRoomName(final String areaNumber, final String name, final String number) {
        //获取网关名称（APP->网关）

        Map<String, Object> mapdevice = new HashMap<>();
        mapdevice.put("token", TokenUtil.getToken(context));
        mapdevice.put("areaNumber", areaNumber);
        mapdevice.put("roomNumber", number);
        mapdevice.put("newName", name);
        MyOkHttp.postMapObject(ApiHelper.sraum_updateRoomName, mapdevice,
                new Mycallback(new AddTogglenInterfacer() {
                    @Override
                    public void addTogglenInterfacer() {
                        sraum_updateRoomName(areaNumber, name, number);
                    }
                }, context, null) {
                    @Override
                    public void onSuccess(User user) {
                        if (refreshListener != null)
                            refreshListener.refresh();

                    }

                    @Override
                    public void wrongBoxnumber() {
                        ToastUtil.showToast(context, "areaNumber\n" +
                                "不存在");
                    }


                    @Override
                    public void threeCode() {
                        ToastUtil.showToast(context, "名字已存在");
                    }

                });
    }

    public interface RefreshListener {
        void refresh();
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog(String name, final String number) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        // 布局填充器
//        LayoutInflater inflater = LayoutInflater.from(getActivity());
//        View view = inflater.inflate(R.layout.user_name_dialog, null);
//        // 设置自定义的对话框界面
//        builder.setView(view);
//
//        cus_dialog = builder.create();
//        cus_dialog.show();


        View view = LayoutInflater.from(context).inflate(R.layout.editscene_dialog, null);
        TextView confirm; //确定按钮
        TextView cancel; //确定按钮
        TextView tv_title;
//        final TextView content; //内容
        cancel = (TextView) view.findViewById(R.id.call_cancel);
        confirm = (TextView) view.findViewById(R.id.call_confirm);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        final ClearLengthEditText edit_password_gateway = (ClearLengthEditText) view.findViewById(R.id.edit_password_gateway);
        edit_password_gateway.setText(name);
        edit_password_gateway.setSelection(edit_password_gateway.getText().length());
//        tv_title.setText("name");
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
                if (edit_password_gateway.getText().toString() == null ||
                        edit_password_gateway.getText().toString().trim().equals("")) {
                    ToastUtil.showToast(context, "区域名称为空");
                    return;
                }
                sraum_updateRoomName(areaNumber, edit_password_gateway.getText().toString(), number);//修改房间名称(areaNumber, edit_password_gateway.getText().toString());
                dialog.dismiss();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    public void setAreaNumber(String areaNumber) {
        this.areaNumber = areaNumber;
    }

    class ViewHolderContentType {
        ImageView img_again_autoscene;
        TextView room_name_txt;
        ImageView pic_room_img;
        TextView txt_device_num;
        Button rename_btn;
        Button remove_btn;
        SwipeMenuLayout swipemenu_layout;
    }
}
