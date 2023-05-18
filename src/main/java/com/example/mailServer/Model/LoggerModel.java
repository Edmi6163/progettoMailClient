package com.example.mailServer.Model;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;

public class LoggerModel {
  public SimpleStringProperty log;

  public LoggerModel() {
    log = new SimpleStringProperty("Start server \n");
  }
  public SimpleStringProperty getLog() {
    return log;
  }
  public void setLog(String logs) {
    Platform.runLater(()-> log.set(logs));
  }
}
