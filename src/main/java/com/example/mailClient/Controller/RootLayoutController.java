package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import com.example.mailServer.Controller.LoginController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.example.mailClient.ClientMain;

public class RootLayoutController {
  @FXML
  public Label addressLabel;
  @FXML
  public Label userNameLabel;
  private ClientMain clientMain;

  public void setClientMain(ClientMain clientMain) {
    this.clientMain = clientMain;
//    addressLabel.setText(clientMain.getUserMail());
    userNameLabel.setText(clientMain.getUserMail());
  }

  @FXML
  private void handleNew() {
    clientMain.showSendMailDialog(new Mail(
      clientMain.getUserMail(),
      "",
      null,
      0L,
      ""
    ), "Send new email");
  }

  public RootLayoutController() {

  }
}
