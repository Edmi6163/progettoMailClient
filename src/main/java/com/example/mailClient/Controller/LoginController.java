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
import java.util.HashSet;
import java.util.Set;

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





  /*
  * @brief: using Set datastructure for uniqness of elements, avoiding the need to check each files name
  * @return: true if the username is in the directory, false otherwise
  * @note: the username is the name of the file in the directory
  * */
  @FXML
  private boolean handleLogin() {
    System.out.println("username is: " + username.getText()); // TODO debug
    String usernameToCheck = this.username.getText() + "@javamail.it";

    Set<String> usernames = getUsernamesFromDirectory();

    boolean loginSuccess = usernames.contains(usernameToCheck);

    if (loginSuccess) {
      logger.setLog(username.getText() + " logged in ");
      System.out.println(username.getText() + " logged in ");
      stage.close();
    } else {
      logger.setLog(username.getText() + " tried to login, but wasn't registered");
      Alert alert = new Alert(Alert.AlertType.ERROR);
      alert.initOwner(stage);
      alert.setTitle("wrong username");
      alert.setHeaderText("An error occurred");
      alert.setContentText("Can't login");
      alert.showAndWait();
      Platform.exit();
    }

    return loginSuccess;
  }

  private Set<String> getUsernamesFromDirectory() {
    File directory = new File("/home/francesco/Documents/Università/3_Anno/Prog3/progettoMailClient/src/main/java/com/example/mailServer/file");
    File[] files = directory.listFiles();
    Set<String> usernames = new HashSet<>();

    assert files != null;
    for (File file : files) {
      usernames.add(file.getName());
    }

    return usernames;
  }



}
