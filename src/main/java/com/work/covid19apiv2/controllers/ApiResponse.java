package com.work.covid19apiv2.controllers;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse {
    private String statusCode;
    private String message;
    private Object data;

    public ApiResponse(String statusCode, String message, Object data){
        this.setStatusCode(statusCode);
        this.setMessage(message);
        this.setData(data);
    }
}
