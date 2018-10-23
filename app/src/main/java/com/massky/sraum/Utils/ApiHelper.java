package com.massky.sraum.Utils;

/**
 * Created by zhu on 2018/3/14.
 */

public class ApiHelper {
//    public static String api = "http://192.168.0.1/cgi-bin/sraum-test";
    //https://app.sraum.com/SmartHome/api/v2/xxxxx
    public static String api = "https://app.sraum.com/SmartHome/api/v2/";

    public static String sraum_register = api + "sraum_register";
    public static String sraum_getToken = api + "sraum_getToken";
    public static String sraum_checkMobilePhone = api + "sraum_checkMobilePhone";
    public static String sraum_login = api + "sraum_login";

    public static String Sraum_SearchGateway = "sraum_searchGateway";
    //sraum_setGatewayTime
    public static String Sraum_SetGatewayTime = "sraum_setGatewayTime";
    //sraum_beat
    public static String Sraum_Beat = "sraum_beat";
    //sraum_verifySocket
    public static String Sraum_VerifySocket = "sraum_verifySocket";
    //sraum_login
    public static String Sraum_Login = api + "sraum_login";

    /**
     * http协议
     */
    public static String sraum_getGatewayName = api;
    public static String sraum_getRoomsInfo = api + "sraum_getRoomsInfo";
    public static String sraum_getOneRoomInfo = api + "sraum_getOneRoomInfo";
    public static String sraum_getInfos = api;
    public static String sraum_getOneInfo = api;
    public static String sraum_updateRoomInfo = api + "sraum_updateRoomInfo";
    public static String sraum_addRoomInfo = api;
    public static String sraum_updateRoomName = api;
    public static String sraum_deleteRoom = api;
    public static String sraum_getGatewayInfo = api;
    public static String sraum_isLogin = api + "sraum_isLogin";
    public static String sraum_getAllArea = api + "sraum_getAllArea";
    public static String sraum_changeArea = api + "sraum_changeArea";
    public static String sraum_deviceControl = api + "sraum_deviceControl";
}
