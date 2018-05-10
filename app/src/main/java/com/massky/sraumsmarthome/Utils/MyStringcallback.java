package com.massky.sraumsmarthome.Utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;


/**
 * Created by masskywcy on 2016-11-04.
 */
public class MyStringcallback extends StringCallback implements ApiStringResult {
    /**
     * 上下文对象
     *
     * @param Context context
     */
    private Context context;
    private int wrongtoken_index = 2;
    /**
     * 加载数据动画展示
     *
     * @param DialogUtil dialogUtil
     */

    private final Activity activity;

    public MyStringcallback(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        remove();
    }

    @Override
    public void onResponse(String response, int id) {
        remove();
        if (TextUtils.isEmpty(response)) {
            emptyResult();
        } else {
//            User user = new GsonBuilder().registerTypeAdapterFactory(
//                    new NullStringToEmptyAdapterFactory()).create().fromJson(response, User.class);
            Log.e("TAG", "response:" + response);
            onSuccess(response);
        }
    }


    //移除dialog动画加载
    private void remove() {

    }

    @Override
    public void emptyResult() {

    }

    @Override
    public void threeCode() {

    }

    @Override
    public void fourCode() {

    }

    @Override
    public void fiveCode() {

    }

    @Override
    public void sixCode() {

    }

    @Override
    public void sevenCode() {
    }

    @Override
    public void defaultCode() {

    }

    @Override
    public void onSuccess(String result) {
    }

    @Override
    public void wrongToken() {

    }

    @Override
    public void pullDataError() {

    }


    @Override
    public void wrongProjectCode() {

    }

}
