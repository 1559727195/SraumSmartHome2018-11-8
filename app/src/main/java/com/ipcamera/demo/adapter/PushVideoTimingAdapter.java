package com.ipcamera.demo.adapter;

import android.content.Context;

import androidx.annotation.NonNull;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.massky.sraum.R;
import com.mcxtzhang.swipemenulib.SwipeMenuLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PushVideoTimingAdapter extends BaseAdapter {
    private Context mContext;
    //    public ArrayList<Map<Integer, Integer>> movetiming;
    private LayoutInflater inflater;
    private ViewHolder holder;
    private PushVideoTimingListener pushVideoTimingListener;
    private List<Map> list_camera_list = new ArrayList<>();

    public PushVideoTimingAdapter(Context mContext, PushVideoTimingListener pushVideoTimingListener) {
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
//        movetiming = new ArrayList<Map<Integer, Integer>>();
        list_camera_list = new ArrayList<>();
        inflater = LayoutInflater.from(mContext);
        this.pushVideoTimingListener = pushVideoTimingListener;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list_camera_list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position1, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.timing_video_item, null);
            holder = new ViewHolder();
            holder.tv_timing_time = (TextView) convertView.findViewById(R.id.tv_timing_time);
            holder.tv_timing_week = (TextView) convertView.findViewById(R.id.tv_timing_week);
            holder.swipe_context = (RelativeLayout) convertView.findViewById(R.id.swipe_context);
            holder.swipe_right_menu = (LinearLayout) convertView.findViewById(R.id.swipe_right_menu);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        Map<Integer, Integer> item = movetiming.get(position1);
//        int itemplan = item.entrySet().iterator().next().getValue();
//        Log.e("itemplan:*******", "" + itemplan);
        if (list_camera_list.size() != 0) {
            String startTime = (String) list_camera_list.get(position1).get("startTime");
            String endTime = (String) list_camera_list.get(position1).get("endTime");

            String monday = (String) list_camera_list.get(position1).get("monday");
            String tuesday = (String) list_camera_list.get(position1).get("tuesday");
            String wednesday = (String) list_camera_list.get(position1).get("wednesday");
            String thursday = (String) list_camera_list.get(position1).get("thursday");
            String friday = (String) list_camera_list.get(position1).get("friday");
            String saturday = (String) list_camera_list.get(position1).get("saturday");
            String sunday = (String) list_camera_list.get(position1).get("sunday");
            String weekdays = getWeeks(monday, tuesday, wednesday, thursday, friday, saturday, sunday);
            holder.tv_timing_week.setText(weekdays);
//        int bStarttime = itemplan & 0x7ff;
//        int bEndTime = (itemplan >> 12) & 0x7ff;
            holder.tv_timing_time.setText(startTime + "-" + endTime);
        }

//        int plankey = item.entrySet().iterator().next().getKey();
//        int plantime = item.entrySet().iterator().next().getValue();
//        movetiming.get(position1).put(plankey, plantime);
//            ((SwipeMenuLayout) convertView).setAccountType("1");

//            ((SwipeMenuLayout) convertView).setOnMenuClickListener(new SwipeMenuLayout.OnMenuClickListener() {
//                @Override
//                public void onMenuClick(View v, int position) {
//                    //弹出删除框
//                    if (pushVideoTimingListener != null) {
//                        pushVideoTimingListener.delete(position1);
//                    }
//                }
//
//                @Override
//                public void onItemClick(View v, int position) {
//                    if (pushVideoTimingListener != null) {
//                        pushVideoTimingListener.onItemClick(position1);
//                    }
//                }
//
//                @Override
//                public void onInterceptTouch() {
//
//                }
//
//                @Override
//                public void onInterceptTouch_end() {
//
//                }
//            });
        holder.swipe_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pushVideoTimingListener != null) {
                    pushVideoTimingListener.onItemClick(position1);
                }
            }
        });

        holder.swipe_right_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //弹出删除框
                if (pushVideoTimingListener != null) {
                    pushVideoTimingListener.delete(position1);
                }
            }
        });

        return convertView;
    }

    @NonNull
    private String getWeeks(String monday, String tuesday, String wednesday, String thursday, String friday, String saturday, String sunday) {
        String weekdays = "";
        switch (sunday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_seven)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }

        switch (monday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_one)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }

        switch (tuesday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_two)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }

        switch (wednesday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_three)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }

        switch (thursday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_four)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }


        switch (friday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_five)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }


        switch (saturday) {
            case "1":
                weekdays = weekdays
                        + mContext.getResources().getString(R.string.plug_six)
                        + " ";
                break;
            case "0":
                weekdays = weekdays
                        + " ";
                break;
        }
        return weekdays;
    }

    public void setList(List<Map> list_camera_list) {
        this.list_camera_list = list_camera_list;
    }

    private class ViewHolder {
        TextView tv_timing_time;
        TextView tv_timing_week;
        RelativeLayout swipe_context;
        LinearLayout swipe_right_menu;

    }

    private String getWeekPlan(int time) {
        String weekdays = "";
        for (int i = 24; i < 31; i++) {
            int weeks = (time >> i) & 1;
            if (weeks == 1) {
                switch (i) {
                    case 24:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_seven)
                                + " ";
                        break;
                    case 25:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_one)
                                + " ";
                        break;
                    case 26:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_two)
                                + " ";
                        break;
                    case 27:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_three)
                                + " ";
                        break;
                    case 28:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_four)
                                + " ";
                        break;
                    case 29:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_five)
                                + " ";
                        break;
                    case 30:
                        weekdays = weekdays
                                + mContext.getResources().getString(R.string.plug_six)
                                + " ";
                        break;
                    default:
                        break;
                }
            }
        }

        return weekdays;
    }

    private String getTime(int time) {
        if (time < 60) {
            if (time < 10)
                return "00:0" + time;
            return "00:" + time;
        }
        int h = time / 60;
        int m = time - (h * 60);
        if (h < 10 && m < 10) {
            return "0" + h + ":0" + m;
        } else if (h > 9 && m < 10) {
            return h + ":0" + m;
        } else if (h < 10 && m > 9) {
            return "0" + h + ":" + m;
        }

        return h + ":" + m;

    }

//        public void addPlan ( int key, int value){
//            Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//            map.put(key, value);
//            movetiming.add(map);
//            int size = movetiming.size();
//            for (int i = 0; i < size - 1; i++) {
//                for (int j = 1; j < size - i; j++) {
//                    Map<Integer, Integer> maps;
//                    if (movetiming
//                            .get(j - 1)
//                            .entrySet()
//                            .iterator()
//                            .next()
//                            .getKey()
//                            .compareTo(
//                                    movetiming.get(j).entrySet().iterator().next()
//                                            .getKey()) > 0) {
//                        maps = movetiming.get(j - 1);
//                        movetiming.set(j - 1, movetiming.get(j));
//                        movetiming.set(j, maps);
//                    }
//                }
//            }
//
//        }
//
//        public void notify ( int key, int value){
//            int size = movetiming.size();
//            for (int i = 0; i < size; i++) {
//                Map<Integer, Integer> map = movetiming.get(i);
//                if (map.containsKey(key)) {
//                    map.put(key, value);
//                    break;
//                }
//            }
//        }
//
//        public void removePlan ( int key){
//            int size = movetiming.size();
//            for (int i = 0; i < size; i++) {
//                Map<Integer, Integer> map = movetiming.get(i);
//                if (map.containsKey(key)) {
//                    movetiming.remove(i);
//                    break;
//                }
//            }
//        }

    public interface PushVideoTimingListener {
        void delete(int position);

        void onItemClick(int position);
    }
}
