package com.example.mailClient.Controller;

import com.example.mailClient.Model.User;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Model.UserList;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.SubScene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;
import com.example.mailServer.*;

import java.io.File;

public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  LoggerModel logger = new LoggerModel();
  private ClientMain clientMain;
  private Stage stage;

  public void setClientMain(ClientMain main,Stage stage){
    this.clientMain=main;
    this.stage=stage;
  }




  @FXML
  private boolean handleLogin(){
    System.out.println("username is: " + username.getText());// TODO debug
    File directory = new File("/home/francesco/Documents/Universita/3 Anno/programmazione3/progettoMailClient/src/main/java/com/example/mailServer/files");
    String usernameToCheck = this.username.getText() + "@javamail.it";
    File[] files = directory.listFiles();
    boolean loginSuccess = false;
    assert files != null;
    for(File file : files){
      System.out.println("file name is " + file.getName());
      System.out.println("login success status: " + loginSuccess);
      if(file.getName().equals(usernameToCheck)){
        loginSuccess = true;
        logger.setLog(username.getText() + " logged in");
        System.out.println(username.getText() + " logged in"); //TODO debug, remove
        System.out.println("login success status: " + loginSuccess); //TODO debug, remove
        stage.close();
        break;
     } else {
//        System.out.println(username.getText() + " tried to login, but wasn't registered");
        logger.setLog(username.getText() + " tried to login, but wasn't registered");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("wrong username");
        alert.setHeaderText("An error occurred");
        alert.setContentText("Can't login");
        alert.showAndWait();
        Platform.exit();
      }
    }
    return loginSuccess;
  }

}
