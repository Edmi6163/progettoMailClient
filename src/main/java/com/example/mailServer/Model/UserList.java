package com.example.mailServer.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserList implements Serializable {
  private ObservableList<String> users;
  public UserList(List<String> users){
    this.users= FXCollections.observableArrayList(users);
  }
  public UserList(){
    users = FXCollections.observableArrayList();
  }
  public void addUser(String user){
      users.add(user);
  }

  public boolean userExist(String user){
    return users.contains(user);
  }

  public ObservableList<String> getUsers(){
    return users;
  }

  public List<Mail> getUserInbox(String u){
    List<Mail> inbox= new ArrayList<>();
    inbox.add(new Mail()); //TODO see this part better
    inbox.add(new Mail());
    inbox.add(new Mail());
    return inbox;
  }
  public List<Mail> getUserOutBox(String u){
    List<Mail> outbox = new ArrayList<>();
    outbox.add(new Mail());
    outbox.add(new Mail());
    outbox.add(new Mail());
    return outbox;
  }

}
