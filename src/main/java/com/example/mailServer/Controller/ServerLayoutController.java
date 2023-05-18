package com.example.mailServer.Controller;

import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.ServerMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ServerLayoutController {

  @FXML
  public TextFlow logFlow;

  private LoggerModel loggerModel;

  public ServerLayoutController() {
    loggerModel = new LoggerModel();
    loggerModel.getLog().addListener((observable, oldValue, newValue) -> {
      setLog(newValue);
    });
  }

  public void initialize() {
    loggerModel.setLog("Server start");
  }
    public void setLog(String log){
      Text fullLog = new Text("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]\t -- " + "  " + log + "\n");
      fullLog.setFill(Color.web("#000000"));
      logFlow.getChildren().add(fullLog);
    }
  }
