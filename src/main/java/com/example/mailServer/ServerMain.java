package com.example.mailServer;

import com.example.mailServer.Model.LoggerModel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import com.example.mailServer.Controller.MailHandler;
import com.example.mailServer.Controller.ServerHandler;
import com.example.mailServer.Model.UserList;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;

import com.example.mailServer.Controller.*;
public class ServerMain extends Application {



  @Override
  public void start(Stage stage) throws Exception {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerLayout.fxml"));
      Scene scene = new Scene(loader.load(), 800, 700);
      stage.setTitle("Server log @javamail");
      stage.setScene(scene);
      stage.show();
      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> System.exit(1));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
