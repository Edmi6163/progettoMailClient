package com.example.Transmission;

public class InboxRequest {

    String email;
    String max;

    public InboxRequest(String email, String max) {
        this.email = email;
        this.max = max;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

}
