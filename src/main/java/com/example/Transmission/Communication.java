package com.example.Transmission;

import java.io.Serializable;

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

    public void setAction(String action) {
        this.action = action;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}