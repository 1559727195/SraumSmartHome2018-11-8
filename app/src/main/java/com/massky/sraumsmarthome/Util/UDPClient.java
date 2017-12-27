package com.massky.sraumsmarthome.Util;
import com.massky.sraumsmarthome.thread.BroadCastUdp;

import static com.massky.sraumsmarthome.Util.AES.Encrypt;

/**
 * Created by zhu on 2017/11/16.
 */

public class UDPClient {
    public static boolean udp_client_destroy;
    public static void initUdp(String udp_content, String command,ICallback iCallback) {
        String key = "masskysraum-6206";//masskysraum-6206
        // 解密
        String DeString = null;
        try {
//                    content = "0a4ab23ad13aac565069283aac3882e5";
            DeString = Encrypt(udp_content,key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new BroadCastUdp(DeString, command,iCallback).start();
    }

    public static void activity_destroy (boolean destoy) {
        udp_client_destroy = destoy;
    }
}
