package com.massky.sraum.Util;

import android.content.Context;
import android.content.Intent;

import com.massky.sraum.MyServcie;
import com.massky.sraum.MyServcie1;

/**
 * Created by masskywcy on 2016-08-31.
 */
//用于音乐播放器使用
public class MusicUtil {
    /*用于开启播放音乐*/
    public static void startMusic(Context context, int command, String type) {
        Intent intent = null;
        switch (type) {
            case "doorbell":
                intent = new Intent(context, MyServcie1.class);
                break;
            default:
                intent = new Intent(context, MyServcie.class);
                break;
        }
        intent.putExtra("command", command);
        context.startService(intent);
    }

    /*用于音乐停止播放*/
    public static void stopMusic(Context context, String type) {
        Intent intent = null;
        switch (type) {
            case "doorbell":
                intent = new Intent(context, MyServcie1.class);
                break;
            default:
                intent = new Intent(context, MyServcie.class);
                break;
        }
        context.stopService(intent);
    }
}
