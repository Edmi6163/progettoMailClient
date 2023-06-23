package com.example.Transmission;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Email implements Serializable {

    private String id;
    private String sender;
    private ArrayList<String> receivers;
    private String subject;
    private String text;
    private LocalDateTime timestamp;
    private boolean bin;

    public Email(String sender, ArrayList<String> receivers, String subject, String text) {
        this.id = UUID.randomUUID().toString();
        this.sender = sender;
        this.subject = subject;
        this.text = text;
        this.receivers = new ArrayList<>(receivers);
        this.timestamp = LocalDateTime.now();
    }

    public String getSender() {
        return sender;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setReceivers(ArrayList<String> receivers) {
        this.receivers = receivers;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public boolean getBin() {
        return bin;
    }

    public void setBin(boolean bin) {
        this.bin = bin;
    }

    @Override
    public String toString() {
        return "Email{" +
                "id='" + id + '\'' +
                ", sender='" + sender + '\'' +
                ", receivers=" + receivers +
                ", subject='" + subject + '\'' +
                ", text='" + text + '\'' +
                ", timestamp=" + timestamp +
                ", bin=" + bin +
                '}';
    }
}
