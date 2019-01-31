package com.massky.sraum.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.zlm.libs.preferences.PreferencesProviderUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
//用于设置SharedPreferences进行封装

/**
 * Created by masskywcy on 2016-08-12.
 */
public class SharedPreferencesUtil {
    //存储的sharedpreferences文件名
    private static final String FILE_NAME = "sraum_smart_home";

    /**
     * 保存数据到文件
     *
     * @param context
     * @param key
     * @param data
     */
    public static void saveData(Context context, String key, Object data) {

        String type = data.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context
                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) data);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) data);
        } else if ("String".equals(type)) {
            editor.putString(key, (String) data);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) data);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) data);
        }

        editor.commit();
    }
    /**
     * 保存数据到文件
     *
     * @param context
     * @param key
     * @param data
     */
    public static void saveData_second(Context context, String key, Object data) {

        String type = data.getClass().getSimpleName();
        if ("Integer".equals(type)) {
//            editor.putInt(key, (Integer) data);
            PreferencesProviderUtils.putInt(context, FILE_NAME, key, (Integer) data);
        } else if ("Boolean".equals(type)) {
//            editor.putBoolean(key, (Boolean) data);
            PreferencesProviderUtils.putBoolean(context, FILE_NAME, key, (Boolean) data);
        } else if ("String".equals(type)) {
//            editor.putString(key, (String) data);
            PreferencesProviderUtils.putString(context, FILE_NAME, key, (String) data);
        } else if ("Float".equals(type)) {
//            editor.putFloat(key, (Float) data);
            PreferencesProviderUtils.putFloat(context, FILE_NAME, key, (Float) data);
        } else if ("Long".equals(type)) {
//            editor.putLong(key, (Long) data);
            PreferencesProviderUtils.putLong(context, FILE_NAME, key, (Long) data);
        }
    }


    /**
     * 从文件中读取数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData(Context context, String key, Object defValue) {

        String type = defValue.getClass().getSimpleName();
        SharedPreferences sharedPreferences = context.getSharedPreferences
                (FILE_NAME, Context.MODE_PRIVATE);

        //defValue为为默认值，如果当前获取不到数据就返回它
        if ("Integer".equals(type)) {
            return sharedPreferences.getInt(key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
            return sharedPreferences.getBoolean(key, (Boolean) defValue);
        } else if ("String".equals(type)) {
            return sharedPreferences.getString(key, (String) defValue);
        } else if ("Float".equals(type)) {
            return sharedPreferences.getFloat(key, (Float) defValue);
        } else if ("Long".equals(type)) {
            return sharedPreferences.getLong(key, (Long) defValue);
        }
        return null;
    }

    /**
     * 从文件中读取数据
     *
     * @param context
     * @param key
     * @param defValue
     * @return
     */
    public static Object getData_second(Context context, String key, Object defValue) {

        String type = defValue.getClass().getSimpleName();

        if ("Integer".equals(type)) {
//            return sharedPreferences.getInt(key, (Integer) defValue);
            return PreferencesProviderUtils.getInt(context, FILE_NAME, key, (Integer) defValue);
        } else if ("Boolean".equals(type)) {
//            return sharedPreferences.getBoolean(key, (Boolean) defValue);
            return PreferencesProviderUtils.getBoolean(context, FILE_NAME, key, (Boolean) defValue);
        } else if ("String".equals(type)) {
//            return sharedPreferences.getString(key, (String) defValue);
            return PreferencesProviderUtils.getString(context, FILE_NAME, key, (String) defValue);
        } else if ("Float".equals(type)) {
//            return sharedPreferences.getFloat(key, (Float) defValue);
            return PreferencesProviderUtils.getFloat(context, FILE_NAME, key, (Float) defValue);
        } else if ("Long".equals(type)) {
//            return sharedPreferences.getLong(key, (Long) defValue);
            return PreferencesProviderUtils.getLong(context, FILE_NAME, key, (Long) defValue);
        }

        return null;
    }

    /**
     * 获取List<Map></>
     *
     * @param context
     * @param key
     * @return
     */
    public static void remove_current_index(Context context, String key, int position) {
        List<Map> datas = new ArrayList<>();
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String result = (String) getData(context, key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {

        }
        if (datas.size() != 0) {
            datas.remove(position);
            saveInfo_List(context, key, datas);
        }
    }


    /**
     * 获取List<Map></>
     *
     * @param context
     * @param key
     * @return
     */
    public static List<Map> getInfo_List(Context context, String key) {
        List<Map> datas = new ArrayList<>();
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String result = (String) getData(context, key, "");
//        ToastUtil.showToast(context, "ss:" + result);
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        Object value = itemObject.get(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {

        }
        return datas;
    }

    /**
     * 获取List<Map></>跨进程用
     *
     * @param context
     * @param key
     * @return
     */
    public static List<Map> getInfo_Second_List(Context context, String key) {
        List<Map> datas = new ArrayList<>();
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String result = (String) getData_second(context, key, "");
//        ToastUtil.showToast(context, "ss:" + result);
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        Object value = itemObject.get(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {

        }
        return datas;
    }


    /**
     * 保存List<Map></>
     *
     * @param context
     * @param key
     * @param datas
     */
    public static void saveInfo_List(Context context, String key, List<Map> datas) {

//        SharedPreferences sharedPreferences = context
//                .getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();

        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            Map itemMap = datas.get(i);
            Iterator<Map.Entry> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                try {

                    String type = entry.getValue().getClass().getSimpleName();
                    if ("Integer".equals(type)) {
                        object.put((String) entry.getKey(), (Integer) entry.getValue());
                    } else if ("Boolean".equals(type)) {
                        object.put((String) entry.getKey(), (Boolean) entry.getValue());
                    } else if ("String".equals(type)) {
                        object.put((String) entry.getKey(), (String) entry.getValue());
                    } else if ("Float".equals(type)) {
                        object.put((String) entry.getKey(), (Float) entry.getValue());
                    } else if ("Long".equals(type)) {
                        object.put((String) entry.getKey(), (Long) entry.getValue());
                    }
                } catch (JSONException e) {

                }
            }
            mJsonArray.put(object);
        }

//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();

//        editor.putString(key, (String)"34593erufogjsjhgrtu");
////        editor.putString(key, mJsonArray.toString());
//        editor.commit();
        saveData(context, key, mJsonArray.toString());
    }


    /**
     * 保存List<Map></>跨进程操作
     *
     * @param context
     * @param key
     * @param datas
     */
    public static void saveInfo_Second_List(Context context, String key, List<Map> datas) {

        JSONArray mJsonArray = new JSONArray();
        for (int i = 0; i < datas.size(); i++) {
            Map itemMap = datas.get(i);
            Iterator<Map.Entry> iterator = itemMap.entrySet().iterator();

            JSONObject object = new JSONObject();

            while (iterator.hasNext()) {
                Map.Entry entry = iterator.next();
                try {

                    String type = entry.getValue().getClass().getSimpleName();
                    if ("Integer".equals(type)) {
                        object.put((String) entry.getKey(), (Integer) entry.getValue());
                    } else if ("Boolean".equals(type)) {
                        object.put((String) entry.getKey(), (Boolean) entry.getValue());
                    } else if ("String".equals(type)) {
                        object.put((String) entry.getKey(), (String) entry.getValue());
                    } else if ("Float".equals(type)) {
                        object.put((String) entry.getKey(), (Float) entry.getValue());
                    } else if ("Long".equals(type)) {
                        object.put((String) entry.getKey(), (Long) entry.getValue());
                    }
                } catch (JSONException e) {

                }
            }
            mJsonArray.put(object);
        }
       saveData_second(context, key, mJsonArray.toString());
    }


    /**
     * 获取List<Map></>
     *
     * @param context
     * @param key
     * @return
     */
    public static void remove_current_values(Context context, String key, String values, String names_1) {
        List<Map> datas = new ArrayList<>();
//        SharedPreferences sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        String result = (String) getData(context, key, "");
        try {
            JSONArray array = new JSONArray(result);
            for (int i = 0; i < array.length(); i++) {
                JSONObject itemObject = array.getJSONObject(i);
                Map itemMap = new HashMap<>();
                JSONArray names = itemObject.names();
                if (names != null) {
                    for (int j = 0; j < names.length(); j++) {
                        String name = names.getString(j);
                        String value = itemObject.getString(name);
                        itemMap.put(name, value);
                    }
                }
                datas.add(itemMap);
            }
        } catch (JSONException e) {

        }

        for (int i = 0; i < datas.size(); i++) {
            if (datas.get(i).get(names_1).equals(values)) {
                datas.remove(i);
            }
        }
        saveInfo_List(context, key, datas);
    }




}
