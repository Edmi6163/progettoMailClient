package com.example.mailClient.Controller;

import com.example.mailServer.Model.LoggerModel;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;
import java.io.IOException;

public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  private Stage stage;

  /*
   * @brief: using Set data structure for uniqueness of elements, avoiding the
   * need to check each files name
   * 
   * @return: true if the username is in the directory, false otherwise
   * 
   * @note: the directory is hardcoded, it should be changed to a relative path
   */
  @FXML
  private void handleLogin() throws IOException {
    // logger.setLog("username is: " + username.getText());
    String usernameToCheck = this.username.getText() + "@javamail.it";

    ClientController cc = new ClientController(this.username.getText());
    cc.login();
    System.out.println(username.getText() + " logged in ");

    stage.close();
  }
}
