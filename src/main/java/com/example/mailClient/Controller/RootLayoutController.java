package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.LocalDateTime;

public class RootLayoutController {

  @FXML
  public Label userNameLabel;
  private LoginController loginController;
  private String username;

  public void setClientMain(LoginController loginController, String username) {
    this.loginController = loginController;
    this.username = username;
    userNameLabel.setText(username);
  }

  @FXML
  private void handleNew() {
    loginController.showSendMailDialog(new Mail(
        username,
        "",
        null,
        LocalDateTime.now(),
        ""), "Send new email");
  }

  public RootLayoutController() {

  }
}
