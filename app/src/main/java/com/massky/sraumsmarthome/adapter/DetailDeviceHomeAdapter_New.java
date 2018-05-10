package com.massky.sraumsmarthome.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.massky.sraumsmarthome.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class DetailDeviceHomeAdapter_New extends android.widget.BaseAdapter {
    private List<HashMap<String, Object>> list = new ArrayList<>();
    private  Context context;
    public DetailDeviceHomeAdapter_New(Context context, List<HashMap<String, Object>> list) {
        this.list = list;
        this.context = context;
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
//        ViewHolderContentType viewHolderContentType = null;
//        if (null == convertView) {
//            viewHolderContentType = new ViewHolderContentType();
//            convertView = LayoutInflater.from(context).inflate(R.layout.detail_home_device, null);
//            // Calculate the item width by the column number to let total width fill the screen width
//            // 根据列数计算项目宽度，以使总宽度尽量填充屏幕
////            int itemWidth = (int) (context.getResources().getDisplayMetrics().widthPixels - 4 * 10) / 4;
////            // Calculate the height by your scale rate, I just use itemWidth here
////            // 下面根据比例计算您的item的高度，此处只是使用itemWidth
////            int itemHeight = itemWidth;
////            AbsListView.LayoutParams params = (AbsListView.LayoutParams) convertView.getLayoutParams();
////            if (params == null) {
////                params = new AbsListView.LayoutParams(
////                        itemWidth / 10 * 9,
////                        itemHeight / 10 * 9);
////                convertView.setLayoutParams(params);
////            } else {
////                params.height = itemHeight;
////                params.width = itemWidth;
////            }
//
//
//            viewHolderContentType.title_room = (TextView) convertView.findViewById(R.id.title_room);
//            viewHolderContentType.device_name = (TextView) convertView.findViewById(R.id.device_name);
//            viewHolderContentType.device_action = (TextView) convertView.findViewById(R.id.device_action);
//            viewHolderContentType.image = (ImageView) convertView.findViewById(R.id.image);//title_room;//device_name,
//            // device_action
//
//            //linear_select
//            viewHolderContentType.linear_select = (LinearLayout) convertView.findViewById(R.id.linear_select);//title_room;//device_name,
//
//            convertView.setTag(viewHolderContentType);
//        } else {
//            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
//        }
//
//        viewHolderContentType.image.setImageResource((Integer) list.get(position).get("item_image"));
//        viewHolderContentType.title_room.setText(list.get(position).get("title_room").toString());
//        viewHolderContentType.device_name.setText(list.get(position).get("device_name").toString());
//        viewHolderContentType.device_action.setText(list.get(position).get("device_action").toString());
//
//        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
//        final ViewHolderContentType finalViewHolderContentType1 = viewHolderContentType;

        // TODO: 16-3-26 控件的ｂｕｇ 不能使用convertView and holder
        View view = LayoutInflater.from(context).inflate(R.layout.detail_home_device,parent,false);
//        TextView textView = (TextView) view.findViewById(R.id.title);
//        final ProvinceItem item = provinceList.get(position);
//        textView.setText(item.getName()+"");
//        if (dropPosition == position){
//            view.setVisibility(View.GONE);
//        }
//        if (selectItem.getId() == provinceList.get(position).getId()){
//            view.setBackgroundColor(Color.parseColor("#fbfbfb"));
//            textView.setTextColor(Color.parseColor("#ff604f"));
//        }else {
//            view.setBackgroundColor(Color.parseColor("#ffffff"));
//            textView.setTextColor(Color.parseColor("#464646"));
//        }
//
        TextView title_room = (TextView) view.findViewById(R.id.title_room);
        TextView device_name = (TextView) view.findViewById(R.id.device_name);
//        TextView device_action = (TextView) view.findViewById(R.id.device_action);
         ImageView image = (ImageView) view.findViewById(R.id.image);//title_room;//device_name,
         LinearLayout linear_select = (LinearLayout) view.findViewById(R.id.linear_select);//title_room;//device_name,


        image.setImageResource((Integer) list.get(position).get("item_image"));
        title_room.setText(list.get(position).get("title_room").toString());
        device_name.setText(list.get(position).get("device_name").toString());
//        device_action.setText(list.get(position).get("device_action").toString());


//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, SettingRoomActivity.class);
//////                intent.putExtra("id ", (Serializable) list.get(position).get("id").toString());
////                context.startActivity(intent);
//                String device_name = (String) list.get(position).get("device_name");
//                switch (device_name) {//空调,PM检测,客厅窗帘,门磁,主灯
//                    case "空调":
//                        context.startActivity(new Intent(context,
//                                AirControlActivity.class));
//                        break;
//                    case "PM检测":
//                        context.startActivity(new Intent(context,
//                                Pm25Activity.class));
//                        break;
//                    case "客厅窗帘":
//                        context.startActivity(new Intent(context,
//                                CurtainWindowActivity.class));
//                        break;
//                    case "门磁":
//                        context.startActivity(new Intent(context,
//                                DeviceSettingDelRoomActivity.class));
//                        break;
//                    case "主灯"://
////                     image.setImageResource(R.drawable.icon_deng);
////                     linear_select.setBackgroundResource(
////                                R.drawable.home_dev_bg);
//                        break;
//                }
//            }
//        });


        return view;





//        convertView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                Intent intent = new Intent(context, SettingRoomActivity.class);
//////                intent.putExtra("id", (Serializable) list.get(position).get("id").toString());
////                context.startActivity(intent);
//                String device_name = (String) list.get(position).get("device_name");
//                switch (device_name) {//空调,PM检测,客厅窗帘,门磁,主灯
//                    case "空调":
//                        context.startActivity(new Intent(context,
//                                AirControlActivity.class));
//                        break;
//                    case "PM检测":
//              context.startActivity(new Intent(context,
//                        Pm25Activity.class));
//                        break;
//                    case "客厅窗帘":
//                        context.startActivity(new Intent(context,
//                                CurtainWindowActivity.class));
//                        break;
//                    case "门磁":
//                        context.startActivity(new Intent(context,
//                                DeviceSettingDelRoomActivity.class));
//                        break;
//                    case "主灯"://
//                        finalViewHolderContentType1.image.setImageResource(R.drawable.icon_deng);
//                        finalViewHolderContentType1.linear_select.setBackgroundResource(
//                                R.drawable.home_dev_bg);
//                        break;
//                }
//            }
//        });

//        return view;
    }

    public void setList(List<HashMap<String, Object>> dataSourceList) {
        this.list = new ArrayList<>();
        this.list = dataSourceList;
        notifyDataSetChanged();
    }

//    class ViewHolderContentType {
//       ImageView image;
//       TextView  title_room;//device_name,device_action
//        TextView  device_name;
//        TextView  device_action;
//        LinearLayout linear_select;
//    }
}
