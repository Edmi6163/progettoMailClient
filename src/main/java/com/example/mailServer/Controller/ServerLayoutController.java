package com.example.mailServer.Controller;

import com.example.mailServer.ServerMain;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ServerLayoutController {
  private ServerMain serverMain;
  @FXML private ListView<String> userList;
  @FXML private ListView<String> logList = new ListView<>();


  public void setServerMain(){
    userList.setItems(serverMain.getUserList().getUsers());
    logList.setItems(serverMain.getLogList());

  }

  public ServerLayoutController(){

  }

  @FXML
  public void initialize(){

  }

  /*print item in Listview<String> logList
    @param item: String
    @return void
   */
  @FXML
  public void addItemToLogList(String item){
    Text fullLog= new Text("["+LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) +"]\t -- " + "  " + item+"\n");
    fullLog.setFill(Color.web("#ffffff"));
    logList.getItems().add(fullLog.getText());
  }
}
