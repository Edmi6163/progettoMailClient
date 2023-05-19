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
//  @FXML
//  private Stage topStage;
  @FXML
  private SplitPane rootLayout;

  private final UserList userList;
  private ObservableList<String> logList = FXCollections.observableArrayList();

  ServerLayoutController controller = new ServerLayoutController();
  public ObservableList<String> getLogList() {
    return logList;
  }

  public void addLog(String log) {
    logList.add(log);
  }

  public UserList getUserList() {
    return userList;
  }


  public ServerMain() {
    userList = new UserList();
    userList.addUser("francesco@javamail.it");
    userList.addUser("mauro@javamail.it");
    userList.addUser("something@javamail.it");
  }




  private void setUpServer(){
    LoggerModel logger = new LoggerModel();
    try {
      int thread_counter = 0;
      ServerSocket s = new ServerSocket(8189);
      logger.setLog("connected to server socket,the ip is " + Inet4Address.getLocalHost().getHostAddress() + " and the port is 8189"); //TODO when view will start remove this
      System.out.println("connected to server socket,the ip is " + Inet4Address.getLocalHost().getHostAddress() + " and the port is 8189");
      while (true) {
        Socket incoming = s.accept();
        logger.setLog("incoming ip is: " + Inet4Address.getLocalHost().getHostAddress()); //TODO same as the println above
        System.out.println("incoming ip is: " + Inet4Address.getLocalHost().getHostAddress()); //TODO same as the println above
        addLog("thread: " + thread_counter);
        Runnable r = new ServerHandler(this, incoming, new MailHandler());
        new Thread(r).start();
        thread_counter++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage stage) throws Exception {
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("ServerLayout.fxml"));
      Scene scene = new Scene(loader.load(), 770, 500);
      stage.setTitle("Server log @javamail");
      stage.setScene(scene);
      stage.show();
      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> System.exit(1));
      Thread t = new Thread(this::setUpServer);
      t.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    launch();
  }
}
