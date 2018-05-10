package com.massky.sraumsmarthome.Utils;

/**
 * Created by zhu on 2018/3/14.
 */

public class ApiHelper {
    public static String api = "http://192.168.0.1/cgi-bin/sraum-test";
    public static String Sraum_SearchGateway = "sraum_searchGateway";
    //sraum_setGatewayTime
    public static String Sraum_SetGatewayTime = "sraum_setGatewayTime";
    //sraum_beat
    public static String Sraum_Beat = "sraum_beat";
    //sraum_verifySocket
    public static String Sraum_VerifySocket = "sraum_verifySocket";
    //sraum_login
    public static String Sraum_Login = "sraum_login";


    /**
     * http协议
     */
    public static String sraum_getGatewayName = api;

    public static String sraum_getRoomsInfo = api;
    public static String sraum_getOneRoomInfo = api;
    public static String sraum_getInfos = api;
    public static String sraum_getOneInfo = api;
    public static String sraum_updateRoomInfo = api;
}
