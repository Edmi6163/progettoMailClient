package com.example.mailServer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import com.example.mailServer.Controller.MailHandler;
import com.example.mailServer.Controller.ServerHandler;
import com.example.mailServer.Model.UserList;
import com.example.mailServer.Controller.ServerLayoutController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain extends Application {
  @FXML
  private Stage topStage;
  @FXML
  private SplitPane rootLayout;

  private UserList userList;
  private ObservableList<String> logList = FXCollections.observableArrayList();

  public ObservableList<String> getLogList(){
    return logList;
  }
  public void addLog(String log){
    logList.add(log);
  }
  public UserList getUserList(){
    return userList;
  }

  public ServerMain(){
   userList = new UserList();
   userList.addUser("francesco@javamail.it");
   userList.addUser("mauro@javamail.it");
   userList.addUser("something@javamail.it");
  }

  public void initRootLayout(Stage topStage) {
    System.out.println("initRootLayout function called");
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("View/ServerLayout.fxml"));
      System.out.println("loader path is "+loader.getClass().getResource("ServerLayout.fxml"));
      System.out.println("getClass is "+getClass());
      rootLayout = loader.load();
      ServerLayoutController controller = loader.getController();
      controller.setServerMain();

      Scene scene = new Scene(rootLayout);
      topStage.setScene(scene);
      topStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  private void setUpServer(){
    System.out.println("setUpServer function called"); //TODO debug
    try {
      ServerSocket s = new ServerSocket(8189);
      System.out.println("connected to server socket");
      while (true){
        Socket incoming = s.accept();
        Runnable r = new ServerHandler(this,incoming,new MailHandler());
        Thread t = new Thread(r);
        t.start();
      }
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  public void start(Stage topStage){
    this.topStage = topStage;
    this.topStage.setTitle("Server @javamail");
    initRootLayout(topStage);
    Thread t = new Thread(this::setUpServer);
    t.start();
  }
  public static void main(String[] args) {
    System.out.println("starting serverMain....");
    launch();
  }
}
