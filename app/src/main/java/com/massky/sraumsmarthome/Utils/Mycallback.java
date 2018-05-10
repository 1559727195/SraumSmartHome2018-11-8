package com.massky.sraumsmarthome.Utils;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.gson.GsonBuilder;
import com.massky.sraumsmarthome.User;
import com.zhy.http.okhttp.callback.StringCallback;
import okhttp3.Call;
import static com.massky.sraumsmarthome.Util.AES.Decrypt;


/**
 * Created by masskywcy on 2016-11-04.
 */
public class Mycallback extends StringCallback implements ApiResult {
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

    public Mycallback(Context context) {
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
            String key = "masskysraum-6206";//masskysraum-6206
            // 解密
            String DeString = null;
            try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
                DeString = Decrypt(response, key);
                Log.e("robin debug", "DeString:" + DeString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            User user = new GsonBuilder().registerTypeAdapterFactory(
                    new NullStringToEmptyAdapterFactory()).create().fromJson(DeString, User.class);
            switch (user.result) {
                case "100":
                    onSuccess(user);
                    break;
                case "101":
                    wrongToken();
                    break;
                case "102":
                    wrongProjectCode();
                    break;
                case "103":
                    threeCode();
                    break;
            }
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
    public void onSuccess(User user) {
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
