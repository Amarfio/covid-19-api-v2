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
    private String countryName;
    private String time;

    public Log(){

    }
    public Log(String id, String activity, String status, String device, String ip_address, String countryName, String time){
        this.setId(id);
        this.setActivity(activity);
        this.setStatus(status);
        this.setDevice(device);
        this.setIp_address(ip_address);
        this.setCountryName(countryName);
        this.setTime(time);
    }

}
