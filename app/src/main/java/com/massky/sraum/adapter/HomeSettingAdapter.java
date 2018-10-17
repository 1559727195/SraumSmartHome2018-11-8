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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.massky.sraum.activity.EditSceneSecondActivity;
import com.massky.sraum.activity.ManagerRoomActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class HomeSettingAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public HomeSettingAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.home_setting_item, null);
//            viewHolderContentType.device_type_pic = (ImageView) convertView.findViewById(R.id.device_type_pic);
//            viewHolderContentType.hand_device_content = (TextView) convertView.findViewById(R.id.hand_device_content);
            viewHolderContentType.swipe_context = (LinearLayout) convertView.findViewById(R.id.swipe_context);
//            viewHolderContentType.hand_gateway_content = (TextView) convertView.findViewById(R.id.hand_gateway_content);
            viewHolderContentType.rename_btn = (Button) convertView.findViewById(R.id.rename_btn);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

//        int element = (Integer) list.get(position).get("image");
//        viewHolderContentType.device_type_pic.setImageResource(element);
//        viewHolderContentType.hand_device_content.setText(list.get(position).get("name").toString());
//
        viewHolderContentType.swipe_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ManagerRoomActivity.class);
                context.startActivity(intent);
            }
        });
        viewHolderContentType.rename_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                showRenameDialog();
            }
        });
        return convertView;
    }

    class ViewHolderContentType {
       ImageView device_type_pic;
       TextView  hand_device_content;
       TextView  hand_gateway_content;
       Button    rename_btn;
       LinearLayout swipe_context;
    }

    //自定义dialog,自定义重命名dialog

    public void showRenameDialog() {
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

}
