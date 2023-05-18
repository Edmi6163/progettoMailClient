package com.example.mailClient.Controller;

import com.example.mailServer.Controller.ServerLayoutController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;
import com.example.mailServer.*;
public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  ServerLayoutController controller = new ServerLayoutController();
  private ClientMain clientMain;
  private Stage stage;

  public void setClientMain(ClientMain main,Stage stage){
    this.clientMain=main;
    this.stage=stage;
  }

  @FXML
  private void handleLogin(){
    System.out.println("username is: " + username.getText());
    if(username != null){
      clientMain.setUserMail(username.getText()+"@javamail.it");
      boolean loginSuccess=clientMain.getClientHandler().requestAll();
      if(loginSuccess){
        stage.close();
      } else {
//        System.out.println(username.getText() + " tried to login, but wasn't registered");
        controller.addItemToLogList(username.getText() + " tried to login, but wasn't registered");
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("wrong username");
        alert.setHeaderText("An error occurred");
        alert.setContentText("Can't login");
        alert.showAndWait();
        Platform.exit();
      }
    }
  }
  /*
  @FXML
  public void closeStage(){
    stage.close();
  }

   */

}
