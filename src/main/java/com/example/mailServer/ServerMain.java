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
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain extends Application {
  @FXML
  private Stage stage;
  @FXML
  private SplitPane rootLayout;

  private UserList userList;
  private ObservableList<String> logList = FXCollections.observableArrayList();

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
    System.out.println("setUpServer function called"); //TODO debug
    try {
      int thread_counter = 0;
      ServerSocket s = new ServerSocket(8189);
      System.out.println("connected to server socket,the ip is " + Inet4Address.getLocalHost().getHostAddress() + " and the port is 8189"); //TODO when view will start remove this
      addLog("connected to server socket,the ip is " + Inet4Address.getLocalHost().getHostAddress() + " and the port is 8189");
      while (true) {

        Socket incoming = s.accept();
        System.out.println("incoming ip is: " + Inet4Address.getLocalHost().getHostAddress()); //TODO same as the println above
        addLog("incoming ip is: " + Inet4Address.getLocalHost().getHostAddress());
        addLog("thread: " + thread_counter);
        Runnable r = new ServerHandler(this, incoming, new MailHandler());
        new Thread(r).start();
        thread_counter++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void start(Stage topStage) {
    try {
      System.out.println("method: start"); //TODO debug
      FXMLLoader loader = new FXMLLoader(ServerMain.class.getResource("server-layout.fxml"));
      Scene scene = new Scene(loader.load(), 300, 500);
      topStage.setTitle("Server log @javamail");
      topStage.setScene(scene);
      topStage.show();
      scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent event) -> System.exit(1));
      Thread t = new Thread(this::setUpServer);
      t.start();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("starting serverMain....");
    launch();
  }
}
