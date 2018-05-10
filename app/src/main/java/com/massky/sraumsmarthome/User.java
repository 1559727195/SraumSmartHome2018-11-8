package com.massky.sraumsmarthome;

import java.util.List;

/**
 * Created by zhu on 2017/11/14.
 */

public class User {
    /**
     * 搜索网关信息(APP -> 网关)
     */
    public String MAC;
    public String name;
    public String proto;
    public String ip;
    public String status;
    public String result;
    public String command;

    //推送修改网关名称（网关->APP）
//    public String  newName;
    //推送添加设备（网关->APP）
    public String number;

    //推送修改设备按钮名称（网关->APP）
//    deviceNumber：设备编号
//    buttonNumber：按钮编号
//    type：按钮类型，1-灯，2-调光，3-窗帘，4-空调，5-地暖，6-新风 详见附件1
//    newName：按钮名称
//    newName1：按钮名称，按钮为窗帘时存在该字段
//    newName2：按钮名称，按钮为窗帘时存在该字段
    public String deviceNumber;
    public String buttonNumber;
    public String type;
    public String newName;
    public String newName1;
    public String newName2;

    //推送设备按钮状态变化（网关->APP）

    //    deviceNumber：设备编号
//    buttonNumber：按钮编号
//    type：按钮类型，1-灯，2-调光，3-窗帘，4-空调，5-地暖，6-新风 详见附件1
//    status：状态，1-开，0-关（若为窗帘，则1-全开，0-全关，2-暂停，3-组1开组2关，4-组1开组2暂停，5-组1关组2开，6-组1关组2暂停，7-组1暂停组2关，8-组1暂停组2开）
//    以下参数根据不同的设备类型调整，详见附件1对照表
//    dimmer：调光值，范围0-100
//    mode：空调模式，1-制冷，2-制热，3-除湿，4-自动，5-通风
//    temperature：空调或新风或地暖的温度，范围16-30
//    speed：空调或新风或地暖的风速，1-低风，2-中风，3-高风，4-强力，5-送风，6-自动
//    public String status;
    public String dimmer;
    public String timeStamp;

    //获取所有房间信息（APP->网关）
    /**
     * 首页->获取所有房间信息
     */
    public List<roomList> roomList;

    public static class roomList {
        public String number;
        public String name;
        public String count;
    }

    /**
     * 获取单个房间关联信息（APP->网关）
     */
    public String count;
    public List<list> list;

    public static class list {

        public String type;
        public String online;
        public String number;
        public String name;
        public String status;
        public String fatherName;
        public String mode;
        public String temperature;
        public String speed;
        public String electricity;
        public String vlaue;
        public String pm25;
        public String pm1;
        public String pm10;
        public String humidity;
        public String alarm;

        public String roomNumber;

        public String dimmer;

        public String roomName;

    }

    /**
     * 获取全部信息（APP->网关）
     */

    /**
     * 推送修改网关名称（网关->APP）
     */

    /**
     * 推送修改网关密码（网关->APP）
     */
    public String newPassword;

    /**
     * 窗帘
     */
    public String name1;
    public String name2;

    /**
     * 空调
     */
    public String mode;
    public String temperature;
    public String speed;

}
