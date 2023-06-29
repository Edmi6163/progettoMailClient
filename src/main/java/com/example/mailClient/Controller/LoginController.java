package com.example.mailClient.Controller;

import com.example.Transmission.Communication;
import com.example.mailServer.Controller.ServerController;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Controller.ServerLayoutController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  public LoggerModel logger = new LoggerModel();
  private ClientMain clientMain;
  private Stage stage;
  private static final String host = "127.0.0.1";

  
  public LoginController() throws IOException {
  }


  public void setClientMain(ClientMain main,Stage stage){
    this.clientMain=main;
    this.stage=stage;
  }



  /*
  * @brief: using Set data structure for uniqueness of elements, avoiding the need to check each files name
  * @return: true if the username is in the directory, false otherwise
  * @note: the directory is hardcoded, it should be changed to a relative path
  */
  @FXML
  private void handleLogin() throws IOException {
//    logger.setLog("username is: " + username.getText());
    String usernameToCheck = this.username.getText() + "@javamail.it";
//    try (Socket s = new Socket(host ,8189)) {
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      Communication communication = new Communication("login", usernameToCheck);
      System.out.println("[LoignController] communication: " + communication.getBody() + " " + communication.getAction());
      out.writeObject(communication);
      out.flush();
//    }
    logger.setLog(username.getText() + " logged in successfully");

    System.out.println(username.getText() + " logged in ");
    clientMain.setUserMail(usernameToCheck);
    stage.close();
      /*
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
  }*/

  }
}
