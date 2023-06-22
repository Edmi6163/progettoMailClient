package com.example.mailClient.Model;

import com.example.mailServer.Model.Mail;

import java.util.List;

public class User {
  private String email;
//  private String password;
  private List<Mail> inbox;
  private List<Mail> outbox;


  public User(String email, List<Mail> inbox, List<Mail> outbox) {
    this.email = email;
    this.inbox = inbox;
    this.outbox = outbox;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
/*

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
*/

  public List<Mail> getInbox() {
    return inbox;
  }

  public void setInbox(List<Mail> inbox) {
    this.inbox = inbox;
  }

  public List<Mail> getOutbox() {
    return outbox;
  }

  public void setOutbox(List<Mail> outbox) {
    this.outbox = outbox;
  }
}
