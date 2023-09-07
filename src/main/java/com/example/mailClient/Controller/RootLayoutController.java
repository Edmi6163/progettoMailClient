package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.time.LocalDateTime;

public class RootLayoutController {

  @FXML
  public Label userNameLabel;
  @FXML
  public Label serverStatusLabel;
  private boolean serverStatus;
  private LoginController loginController;
  private String username;

  public void setClientMain(LoginController loginController, boolean serverStatus, String username) {
    this.loginController = loginController;
    this.serverStatus = serverStatus;
    this.username = username;
    userNameLabel.setText(username);
  }

  public boolean setServerStatusLabel(boolean status) {
    if(status != serverStatus) {
      serverStatus = status;
      if (status) {
        serverStatusLabel.setText("Connected to server");
        return true;
      } else {
        serverStatusLabel.setText("Connection lost");
      }
    }
    return false;
  }

  @FXML
  private void handleNew() {
    loginController.showSendMailDialog(new Mail("",
        username,
        "",
        null,
        LocalDateTime.now(),
        ""), "Send new email");
  }

  public RootLayoutController() {

  }
}
