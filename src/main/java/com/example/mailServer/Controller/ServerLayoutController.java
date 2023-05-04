package com.example.mailServer.Controller;

import com.example.mailServer.ServerMain;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ServerLayoutController {
  private ServerMain serverMain;
  @FXML private ListView<String> userList;
  @FXML private ListView<String> log= new ListView<>();


  public void setServerMain(){
    this.serverMain=serverMain;
    userList.setItems(serverMain.getUserList().getUsers());
    log.setItems(serverMain.getLogList());
  }

  public ServerLayoutController(){

  }

  @FXML
  public void initialize(){

  }

  @FXML
  public void addItemToLogList(String item){
    log.getItems().add(item);
  }
}
