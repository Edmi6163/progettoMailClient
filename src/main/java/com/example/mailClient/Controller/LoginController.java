package com.example.mailClient.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;

public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  private ClientMain clientMain;
  private Stage stage;

  public void setClientMain(ClientMain main,Stage stage){
    this.clientMain=main;
    this.stage=stage;
  }

  @FXML
  private void handleLogin(){
    System.out.println("username is: " + username);
    if(username != null && username.getText().length()>0 ){
      System.out.println("username after if is: " + username);
      clientMain.setUserMail(username.getText()+"@javamail.it");
      boolean loginSuccess=clientMain.getClientHandler().requestAll();
      if(loginSuccess){
        stage.close();
      } else {
        System.out.println("in else branch: " + username);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("wrong username");
        alert.setHeaderText("Error");
        alert.setContentText("Can't login");
        alert.showAndWait();
        Platform.exit();
      }
    }
    System.out.println("end of function :(");
  }
  /*
  @FXML
  public void closeStage(){
    stage.close();
  }

   */

}
