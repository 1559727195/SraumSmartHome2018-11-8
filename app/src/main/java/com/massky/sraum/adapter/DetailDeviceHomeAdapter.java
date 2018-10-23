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


            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
            int itemWidth =  ((context.getResources().getDisplayMetrics().widthPixels
            ) / 3 * 2 - dip2px(context,6)) / 2;
            // Calculate the height by your scale rate, I just use itemWidth here
            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
            int itemHeight = itemWidth;
            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
            if (params == null) {
                params = new AbsListView.LayoutParams(
                        itemWidth,
                        itemHeight / 10 * 6);
                convertView.setLayoutParams(params);
            } else {
                params.height = itemHeight;//
                params.width = itemWidth;
            }

            viewHolderContentType.title_room = (TextView) convertView.findViewById(R.id.title_room);
            viewHolderContentType.device_name = (TextView) convertView.findViewById(R.id.device_name);
            viewHolderContentType.status_txt = (TextView) convertView.findViewById(R.id.status_txt);
            viewHolderContentType.imageitem_id = (ImageView) convertView.findViewById(R.id.image);//title_room;//device_name,
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
//        String status = (String) list.get(position).get("status");
////        if (type != null) {//
//            switch (status) {
//                case "0":
//                    status_bolt(position, viewHolderContentType, type);
//                    break;
//                case "1":
//                    status_normal(position, viewHolderContentType, type);
//                    break;
//            }
        viewHolderContentType.device_name.setText(list.get(position).get("device_name").toString());
        //roomName
        String roomName = list.get(position).get("title_room").toString();
        if (roomName != null) { //说明是所有下的
            viewHolderContentType.title_room.setText(roomName);
        }

        viewHolderContentType.imageitem_id.setImageResource((Integer) list.get(position).get("item_image"));
//        }

//        ViewHolderContentType  mHolder  = viewHolderContentType;
//        switch (list.get(position).get("type").toString()) {
//            case "1":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.status_txt.setText("开");//#E2C891
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_quankai_40_active);
//                } else {
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_deng_40);
//                }
//                break;
//            case "2":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_tiaoguang_40_active);
//                    mHolder.status_txt.setText("开");//#E2C891
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_tiaoguang_40);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "3":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_kongtiao_40_active);
//                    mHolder.status_txt.setText("开");//#E2C891
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_kongtiao_40);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//
//                break;
//            case "4":
//                String curstatus = list.get(position).get("status").toString();
//                if (curstatus.equals("1") || curstatus.equals("3") || curstatus.equals("4")
//                        || curstatus.equals("4") || curstatus.equals("5") || curstatus.equals("8")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("开");//#E2C891
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_chuanglian_40_active);
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_chuanglian_40);
//                }
//                break;
//            case "5":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("开");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                mHolder.imageitem_id.setImageResource(R.drawable.freshair);
//                break;
//            case "6":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("开");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                mHolder.imageitem_id.setImageResource(R.drawable.floorheating);
//                break;
//
//
//            case "7":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_menci_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("打开");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_menci_40);
//                    mHolder.status_txt.setText("关闭");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "8":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_rentiganying_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("有人");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_rentiganying_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//
//                break;
//            case "9":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_shuijin_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("报警");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_shuijin_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//
//                break;
//            case "10":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_pm25_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
////                    mHolder.status_txt.setText("报警");//#E2C891
//                    mHolder.status_txt.setText(list.get(position).get("dimmer").toString());//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_pm25_40);
////                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setText(list.get(position).get("dimmer").toString());//#E2C891
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "11":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_jinjianniu_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("报警");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_jinjianniu_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "12":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_rucebjq_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("报警");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_rucebjq_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "13":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_yanwubjq_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("报警");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_yanwubjq_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "14":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_ranqibjq_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("报警");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_ranqibjq_40);
//                    mHolder.status_txt.setText("正常");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "15":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_zhinengmensuo_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("打开");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_zhinengmensuo_40);
//                    mHolder.status_txt.setText("关闭");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "16":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_jixieshou_40);
//                    mHolder.status_txt.setText("打开");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                } else {
//                    //                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_jixieshou_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("关闭");//#E2C891
//                }
//                break;
//
//            case "202":
//            case "206":
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_yaokongqi_40_active);
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setText("开");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_yaokongqi_40);
//                    mHolder.status_txt.setText("关");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//            case "101"://wifi摄像头
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_shexiangtou_40);
////                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                    mHolder.status_txt.setText("正常");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_shexiangtou_40);
//                    mHolder.status_txt.setText("断线");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//
//            case "103"://wifi摄像头
//                if (list.get(position).get("status").toString().equals("1")) {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markstarh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_keshimenling_40);
////                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.zongse_color));
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                    mHolder.status_txt.setText("正常");//#E2C891
//                } else {
////                    mHolder.itemrela_id.setBackgroundResource(R.drawable.markh);
//                    mHolder.imageitem_id.setImageResource(R.drawable.icon_keshimenling_40);
//                    mHolder.status_txt.setText("断线");
//                    mHolder.status_txt.setTextColor(context.getResources().getColor(R.color.e30));
//                }
//                break;
//
//            default:
//                mHolder.imageitem_id.setImageResource(R.drawable.marklamph);
//                break;
//        }

        return convertView;
    }
    /**
     * 根据手机分辨率从DP转成PX
     * @param context
     * @param dpValue
     * @return
     */
    public  int dip2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }




//    public void setList(List<Map> dataSourceList) {
//        this.list = new ArrayList<>();
//        this.list = dataSourceList;
//        notifyDataSetChanged();
//    }

    class ViewHolderContentType {
        ImageView imageitem_id;
        TextView title_room;//device_name,device_action_or_father_name
        TextView device_name;
        TextView status_txt;
        LinearLayout linear_select;
        ImageView scene_img;//场景图片
        CheckBox scene_checkbox;//场景选中
    }
}
