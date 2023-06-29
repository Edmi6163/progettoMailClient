package com.example.mailServer.Model;

import com.example.mailServer.Controller.ServerLayoutController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

public class LoggerModel {
  public SimpleStringProperty log;

  public LoggerModel() {
    log = new SimpleStringProperty("Start server mod \n");
  }
  public SimpleStringProperty getLog() {
    return log;
  }
 public void setLog(String logs) {
    Platform.runLater(()->log.set(logs));
  }
}
