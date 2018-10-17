package com.massky.sraum.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.massky.sraum.R;
import com.massky.sraum.activity.AirControlActivity;
import com.massky.sraum.activity.CurtainWindowActivity;
import com.massky.sraum.activity.DeviceSettingDelRoomActivity;
import com.massky.sraum.activity.Pm25Activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class DetailDeviceHomeAdapter extends BaseAdapter {
    private List<Map> list = new ArrayList<>();

    public DetailDeviceHomeAdapter(Context context, List<Map> list) {
        super(context, list);
        this.list = list;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolderContentType viewHolderContentType = null;
        if (null == convertView) {
            viewHolderContentType = new ViewHolderContentType();
            convertView = LayoutInflater.from(context).inflate(R.layout.detail_home_device, null);
            // Calculate the item width by the column number to let total width fill the screen width
            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
//            int itemWidth = (int) (context.getResources().getDisplayMetrics().widthPixels - 4 * 10) / 4;
//            // Calculate the height by your scale rate, I just use itemWidth here
//            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
//            int itemHeight = itemWidth;
//            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
//            if (params == null) {
//                params = new AbsListView.LayoutParams(
//                        itemWidth / 10 * 9,
//                        itemHeight / 10 * 9);
//                convertView.setLayoutParams(params);
//            } else {
//                params.height = itemHeight;
//                params.width = itemWidth;
//            }


            viewHolderContentType.title_room = (TextView) convertView.findViewById(R.id.title_room);
            viewHolderContentType.device_name = (TextView) convertView.findViewById(R.id.device_name);
            viewHolderContentType.device_action_or_father_name = (TextView) convertView.findViewById(R.id.device_action_or_father_name);
            viewHolderContentType.image = (ImageView) convertView.findViewById(R.id.image);//title_room;//device_name,
            // device_action_or_father_name

            //linear_select
            viewHolderContentType.linear_select = (LinearLayout) convertView.findViewById(R.id.linear_select);
            //title_room;//device_name,

            //scene_img;//场景图片，scene_checkbox;//场景选中
            //
            viewHolderContentType.scene_img = (ImageView) convertView.findViewById(R.id.scene_img);
            viewHolderContentType.scene_checkbox = (CheckBox) convertView.findViewById(R.id.scene_checkbox);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        String type = (String) list.get(position).get("type");
        String status = (String) list.get(position).get("status");
        if (type != null) {//
            switch (status) {
                case "0":
                    status_bolt(position, viewHolderContentType, type);
                    break;
                case "1":
                    status_normal(position, viewHolderContentType, type);
                    break;
            }
            viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
            //roomName
            String roomName = list.get(position).get("roomName").toString();
            if (roomName != null) { //说明是所有下的
                viewHolderContentType.title_room.setText(list.get(position).get("roomName").toString());
            }

//            switch (list.get(position).get("title_room").toString()) {
//                case "吃饭":
//                case "睡觉":
//                    viewHolderContentType.scene_img.setVisibility(View.VISIBLE);
////                viewHolderContentType.scene_checkbox.setVisibility(View.VISIBLE);
//                    break;
//                default:
//                    viewHolderContentType.scene_img.setVisibility(View.GONE);
//                    viewHolderContentType.scene_checkbox.setVisibility(View.GONE);
//                    break;
//            }


//            final ViewHolderContentType finalViewHolderContentType1 = viewHolderContentType;
//            viewHolderContentType.linear_select.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                Intent intent = new Intent(context, SettingRoomActivity.class);
//////                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
////                context.startActivity(intent);
//
//                    String type = (String) list.get(position).get("type");
//                    String status = (String) list.get(position).get("status");
//                    //获取单个设备或是按钮信息（APP->网关） ->command：命令标识 sraum_getOneInfo
//
//
//                    switch (type) {//空调,PM检测,客厅窗帘,门磁,主灯
//                        case "4":
//                            Intent intent_air_control = new Intent(context,
//                                    AirControlActivity.class);
//                            intent_air_control.putExtra("air_control", (Serializable) list.get(position));
//                            context.startActivity(intent_air_control);
//                            break;
//                        case "12":
//                            Intent intent_pm25 = new Intent(context,
//                                    Pm25Activity.class);
//                            intent_pm25.putExtra("air_control", (Serializable) list.get(position));
//                            context.startActivity(intent_pm25);
//                            break;
//                        case "3":
//                            Intent intent_window = new Intent(context,
//                                    CurtainWindowActivity.class);
//                            intent_window.putExtra("air_control", (Serializable) list.get(position));
//                            context.startActivity(intent_window);
//                            break;
//                        case "7":
//                            Intent intent_magnetic_door = new Intent(context,
//                                    DeviceSettingDelRoomActivity.class);
//                            intent_magnetic_door.putExtra("air_control", (Serializable) list.get(position));
//                            context.startActivity(intent_magnetic_door);
//                            break;
//                        case "1"://灯，控制灯的开关
//                            switch (status) { //
//                                case "1":
////                                    finalViewHolderContentType1.image.setImageResource(R.drawable.icon_deng);
////                                    //home_dev_select_bg
////                                    finalViewHolderContentType1.linear_select.setBackgroundResource(
////                                            R.drawable.home_dev_select_bg);
//                                    //去关灯
//
//                                    break;
//                                case "0":
////                                    finalViewHolderContentType1.image.setImageResource(R.drawable.icon_deng);
////                                    finalViewHolderContentType1.linear_select.setBackgroundResource(
////                                            R.drawable.home_dev_bg);
//                                    //去开灯
//
//                                    break;
//                            }
//
//                            break;
//                    }
//                }
//            });
        }

        return convertView;
    }

    /**
     * 设备正常状态
     *
     * @param position
     * @param viewHolderContentType
     * @param type
     */
    private void status_normal(int position, ViewHolderContentType viewHolderContentType, String type) {
        viewHolderContentType.linear_select.setBackgroundResource(
                R.drawable.home_dev_select_bg);
        switch (type) {
            case "1"://灯
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                break;
            case "2"://调光灯
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "3"://窗帘
                viewHolderContentType.image.setImageResource(R.drawable.icon_chuanglian_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "4"://空调
                viewHolderContentType.image.setImageResource(R.drawable.icon_kongtiao_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "5"://地暖
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "6"://新风
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "7"://门磁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "8"://人体感应
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "9"://红外转发
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("vlaue").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "10"://燃气检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "11"://水浸检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "12"://PM2.5Zigbee
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("pm2.5").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "13"://健康检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "14"://蓝牙转发
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("vlaue").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "15"://报警器
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "16"://指纹锁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "17"://人连锁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "50"://普通摄像头
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "51"://全景摄像头
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "52"://智能门铃
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "53"://音乐面板
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "54"://PM2.5WIfi
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("pm2.5").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "100"://手动场景
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
        }
    }


    /**
     * 设备断线状态
     *
     * @param position
     * @param viewHolderContentType
     * @param type
     */
    private void status_bolt(int position, ViewHolderContentType viewHolderContentType, String type) {
        viewHolderContentType.linear_select.setBackgroundResource(
                R.drawable.home_dev_bg);
        switch (type) {
            case "1"://灯
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                break;
            case "2"://调光灯
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "3"://窗帘
                viewHolderContentType.image.setImageResource(R.drawable.icon_chuanglian_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("fatherName").toString());
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "4"://空调
                viewHolderContentType.image.setImageResource(R.drawable.icon_kongtiao_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "5"://地暖
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "6"://新风
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("mode").toString()
                        + "|" + list.get(position).get("temperature").toString()
                        + "|" + list.get(position).get("speed").toString());
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "7"://门磁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "8"://人体感应
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "9"://红外转发
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("vlaue").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "10"://燃气检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "11"://水浸检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "12"://PM2.5Zigbee
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("pm2.5").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "13"://健康检测
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "14"://蓝牙转发
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("vlaue").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "15"://报警器
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "16"://指纹锁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "17"://人连锁
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "50"://普通摄像头
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "51"://全景摄像头
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "52"://智能门铃
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("alarm").toString()
                        + "|" + list.get(position).get("electricity").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "53"://音乐面板
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "54"://PM2.5WIfi
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                viewHolderContentType.device_action_or_father_name.setText(list.get(position).get("pm2.5").toString()
                );
                //viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
            case "100"://手动场景
                viewHolderContentType.image.setImageResource(R.drawable.icon_deng_active);
                // viewHolderContentType.device_name.setText(list.get(position).get("name").toString());
                break;
        }
    }

//    public void setList(List<Map> dataSourceList) {
//        this.list = new ArrayList<>();
//        this.list = dataSourceList;
//        notifyDataSetChanged();
//    }

    class ViewHolderContentType {
        ImageView image;
        TextView title_room;//device_name,device_action_or_father_name
        TextView device_name;
        TextView device_action_or_father_name;
        LinearLayout linear_select;
        ImageView scene_img;//场景图片
        CheckBox scene_checkbox;//场景选中
    }
}
