package mailServer;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.stage.Stage;
import mailServer.Controller.MailHandler;
import mailServer.Model.UserList;
import mailServer.View.ServerLayout;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain extends Application {
  private Stage topStage;
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

  public void initRootLayout() {
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ServerMain.class.getResource("View/ServerLayout.fxml"));
      topStage = loader.load();
      ServerLayout controller = loader.getController();
      controller.setServerMain();

      Scene scene = new Scene(rootLayout);
      topStage.setScene(scene);
      topStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  public void start(Stage topStage){
    this.topStage = topStage;
    this.topStage.setTitle("Server @javamail");
    initRootLayout();
    Thread t = new Thread(this::setUpServer);
  }


  private void setUpServer(){
    try {
      ServerSocket s = new ServerSocket(8189);
      System.out.println("connected to server socket");
      while (true){
        Socket incoming = s.accept();
        Runnable r = new ServerHandler(this,incoming,new MailHandler());
        Thread t = new Thread(r);
      }
    } catch (IOException e){
      e.printStackTrace();
    }
  }
}
