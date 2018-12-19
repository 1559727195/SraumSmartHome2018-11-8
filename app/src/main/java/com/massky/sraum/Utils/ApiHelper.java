package com.massky.sraum.Utils;

/**
 * Created by zhu on 2018/3/14.
 */

public class ApiHelper {
    //    public static String api = "http://192.168.0.1/cgi-bin/sraum-test";
    //https://app.sraum.com/SmartHome/api/v2/xxxxx

    //https://test.sraum.com/SmartHome/api/v2/xxxxx接口地址
    public static String api = "https://test.sraum.com/SmartHome/api/v2/";
    //    public static String api = "https://app.sraum.com/SmartHome/api/v2/";
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
    public static String sraum_addRoom = api + "sraum_addRoom";
    public static String sraum_updateRoomName = api + "sraum_updateRoomName";
    public static String sraum_deleteRoom = api + "sraum_deleteRoom";
    public static String sraum_getGatewayInfo = api;
    public static String sraum_isLogin = api + "sraum_isLogin";
    public static String sraum_getAllArea = api + "sraum_getAllArea";
    public static String sraum_changeArea = api + "sraum_changeArea";
    public static String sraum_deviceControl = api + "sraum_deviceControl";
    public static String sraum_getAllDevice = api + "sraum_getAllDevice";
    public static String sraum_getBoxStatus = api + "sraum_getBoxStatus";
    public static String sraum_getAllPanel = api + "sraum_getAllPanel";
    public static String sraum_myAllDevice = api + "sraum_myAllDevice";
    public static String sraum_addBox = api + "sraum_addBox";
    public static String sraum_setGateway = api + "sraum_setGateway";
    public static String sraum_getAllBox = api + "sraum_getAllBox";
    public static String sraum_getPanelDevices = api + "sraum_getPanelDevices";
    public static String sraum_updateDeviceName = api + "sraum_updateDeviceName";
    public static String sraum_updateButtonName = api + "sraum_updateButtonName";
    public static String sraum_findDevice = api + "sraum_findDevice";
    public static String sraum_addArea = api + "sraum_addArea";
    public static String sraum_updateAreaName = api + "sraum_updateAreaName";
    public static String sraum_deleteArea = api + "sraum_deleteArea";
    public static String sraum_getAllAreas = api + "sraum_getAllAreas";
    public static String sraum_findButton = api + "sraum_findButton";
    public static String sraum_deleteDevice = api + "sraum_deleteDevice";
    public static String sraum_deleteWifiCamera = api + "sraum_deleteWifiCamera";
    public static String sraum_deleteWifiApple = api + "sraum_deleteWifiApple";
    public static String sraum_deleteGateway = api + "sraum_deleteGateway";
    public static String sraum_deviceRelatedRoom = api + "sraum_deviceRelatedRoom";
    public static String sraum_getAllScenesCount = api + "sraum_getAllScenesCount";
    public static String sraum_getManuallyScenes = api + "sraum_getManuallyScenes";
    public static String sraum_manualSceneControl = api + "sraum_manualSceneControl";
    public static String sraum_getOneSceneInfo = api + "sraum_getOneSceneInfo";
    public static String sraum_getLinkController = api + "sraum_getLinkController";
    public static String sraum_getWifiAppleDeviceInfos = api + "sraum_getWifiAppleDeviceInfos";
    public static String sraum_getLinkScene = api + "sraum_getLinkScene";
    public static String sraum_deviceLinkInfo = api + "sraum_deviceLinkInfo";
    public static String sraum_getLinkSensor = api + "sraum_getLinkSensor";
    public static String sraum_updateDeviceLink = api + "sraum_updateDeviceLink";
    public static String sraum_setDeviceLink = api + "sraum_setDeviceLink";
    public static String sraum_addManuallyScene = api + "sraum_addManuallyScene";
    public static String sraum_editManuallyScene = api + "sraum_editManuallyScene";
    public static String sraum_panelRelation = api + "sraum_panelRelation";
    public static String sraum_getAutoScenes = api + "sraum_getAutoScenes";
    public static String sraum_reNameManuallyScene = api + "sraum_reNameManuallyScene";
    public static String sraum_deleteManuallyScene = api + "sraum_deleteManuallyScene";
    public static String sraum_updateDeviceLinkName = api + "sraum_updateDeviceLinkName";
    public static String sraum_deleteDeviceLink = api + "sraum_deleteDeviceLink";
    public static String sraum_setDeviceLinkIsUse = api + "sraum_setDeviceLinkIsUse";
    public static String UpdateApkUrl ;
    public static String sraum_getVersion;
}
