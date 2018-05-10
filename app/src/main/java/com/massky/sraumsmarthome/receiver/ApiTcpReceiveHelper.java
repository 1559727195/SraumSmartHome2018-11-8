package com.massky.sraumsmarthome.receiver;

/**
 * Created by zhu on 2018/4/24.
 */

public class ApiTcpReceiveHelper {
    private static String api = "com.massky.sraumsmarthome.";
    public static String Sraum_PushGateWayName = api + "sraum_pushGatewayName";//
    public static String Sraum_PushGatewayPassword = api + "sraum_pushGatewayPassword";
    public static String Sraum_PushAddDevice = api + "sraum_pushAddDevice";
    public static String Sraum_Control_Button = api + "sraum_control_button";
    public static String TIAO_GUANG_RECEIVE_ACTION = api + "tiaoguang_receiver_action";
    public static String CURTAIN_RECEIVE_ACTION = api + "curtain_receiver_action";
    public static String AIRCONTROL_RECEIVE_ACTION = api + "aircontrol_receiver_action";
}
