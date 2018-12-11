package com.massky.sraum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.massky.sraum.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by masskywcy on 2017-05-16.
 */

public class RoomListAdapter extends android.widget.BaseAdapter {
    private final Context context;
    private List<Map> list = new ArrayList<>();
    private HomeDeviceItemClickListener homeDeviceItemClickListener;
    // 用来控制CheckBox的选中状况
    private static HashMap<Integer, Boolean> isSelected = new HashMap<>();


    public RoomListAdapter(Context context, List<Map> list, HomeDeviceItemClickListener homeDeviceItemClickListener) {
        this.context = context;
        this.list = list;
        this.homeDeviceItemClickListener = homeDeviceItemClickListener;
        initDate();
    }

    // 初始化isSelected的数据
    private void initDate() {
        for (int i = 0; i < list.size(); i++) {
            getIsSelected().put(i, false);
        }
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
       isSelected = isSelected;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.room_new_item, null);
            viewHolderContentType.title_home_device = (TextView) convertView.findViewById(R.id.title_home_device);
            viewHolderContentType.image_home_device_item = (ImageView) convertView.findViewById(R.id.image_home_device_item);

            convertView.setTag(viewHolderContentType);
        } else {
            viewHolderContentType = (ViewHolderContentType) convertView.getTag();
        }

        viewHolderContentType.title_home_device.setText((String) list.get(position).get("name") + "(" +
                (String) list.get(position).get("count") + ")");

        if (getIsSelected().get(position)) {
//            viewHolderContentType.img_guan_scene.setImageResource(listintwo.get(position));
//            viewHolderContentType.image_home_device_item.setImageResource(R.drawable.icon_l_keting_active);
            viewHolderContentType.title_home_device.setTextColor(context.getResources().getColor(R.color.green));
        } else {
//            viewHolderContentType.img_guan_scene.setImageResource(listint.get(position));
//            viewHolderContentType.image_home_device_item.setImageResource(R.drawable.icon_l_keting);
            viewHolderContentType.title_home_device.setTextColor(context.getResources().getColor(R.color.black));
        }
        final ViewHolderContentType finalViewHolderContentType = viewHolderContentType;
        return convertView;
    }


    public void setList1(List<Map> list1) {
        this.list = list1;
        initDate();
    }

    class ViewHolderContentType {
        ImageView image_home_device_item;
        TextView title_home_device;
    }

    public interface HomeDeviceItemClickListener {
        void homedeviceClick(String number);
    }


}
