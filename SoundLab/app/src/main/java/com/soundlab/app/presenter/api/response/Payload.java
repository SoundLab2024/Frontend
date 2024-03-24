package com.soundlab.app.presenter.api.response;

public class Payload {

    private int statusCode;
    private String msg;

    public Payload(int statusCode, String msg) {
        this.statusCode = statusCode;
        this.msg = msg;
    }

    public Payload() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
