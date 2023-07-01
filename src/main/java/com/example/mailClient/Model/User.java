package com.example.mailClient.Model;

import com.example.Transmission.Email;

import java.util.ArrayList;
import java.util.List;

public class User {
  private String username;
  private List<Email> inbox;
  private List<Email> outbox;

  public User(String username) {
    this.username = username;
    this.inbox = new ArrayList<Email>();
    this.outbox = new ArrayList<Email>();
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<Email> getInbox() {
    return inbox;
  }

  public void setInbox(List<Email> inbox) {
    this.inbox = inbox;
  }

  public List<Email> getOutbox() {
    return outbox;
  }

  public void setOutbox(List<Email> outbox) {
    this.outbox = outbox;
  }
}
