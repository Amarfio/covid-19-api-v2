package com.work.covid19apiv2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Log {

    private String id;
    private String activity;
    private String status;
    private String device;
    private String ip_address;
    private String time;

    public Log(){

    }
    public Log(String id, String activity, String status, String device, String ip_address, String time){
        this.setId(id);
        this.setActivity(activity);
        this.setStatus(status);
        this.setDevice(device);
        this.setIp_address(ip_address);
        this.setTime(time);
    }

}
