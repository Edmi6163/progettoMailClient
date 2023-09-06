package com.example.Transmission;

import java.io.Serializable;

/*
* @brief: this class handle the communication between client and server
* @param: action: the action that the client want to do, like login, request inbox, send email, etc.
* @param: body: the body of the action, like the email that the client want to send, the user that want to login, etc.
* */

public class Communication implements Serializable {
    private String action;

    private Object body;

    public Communication(String action, Object body) {
        this.action = action;
        this.body = body;
    }

    public String getAction() {
        return action;
    }



    public Object getBody() {
        return body;
    }

}