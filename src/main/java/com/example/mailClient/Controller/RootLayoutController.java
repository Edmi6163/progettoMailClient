package com.example.mailClient.Controller;

import com.example.mailServer.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import com.example.Transmission.Email;
import com.example.mailClient.ClientMain;

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
        0L,
        ""), "Send new email");
  }

  public RootLayoutController() {

  }
}
