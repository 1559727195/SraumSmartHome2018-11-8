package com.massky.sraumsmarthome.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zhu on 2018/3/14.
 */

@Entity
public class GateWayInfoBean {
//     “number”:” 11223344556a”,
//     “MAC”:”11:22:33:44:55:6a”,
//     “name”:”我的网关1”,
//     “proto”:”dhcp”,
//     “ip”:”192.168.0.1”,
//     “status”:”1”,
//     “timeStamp”:”1509676036”
    private String number;
    private String MAC;
    private String name;
    private String proto;
    private String ip;
    private String status;
    private String timeStamp;
    @Generated(hash = 565108461)
    public GateWayInfoBean(String number, String MAC, String name, String proto,
            String ip, String status, String timeStamp) {
        this.number = number;
        this.MAC = MAC;
        this.name = name;
        this.proto = proto;
        this.ip = ip;
        this.status = status;
        this.timeStamp = timeStamp;
    }
    @Generated(hash = 1057078820)
    public GateWayInfoBean() {
    }
    public String getNumber() {
        return this.number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getMAC() {
        return this.MAC;
    }
    public void setMAC(String MAC) {
        this.MAC = MAC;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getProto() {
        return this.proto;
    }
    public void setProto(String proto) {
        this.proto = proto;
    }
    public String getIp() {
        return this.ip;
    }
    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getTimeStamp() {
        return this.timeStamp;
    }
    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}
