package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import com.example.mailClient.ClientMain;

public class RootLayoutController {
  @FXML
  private Label addressLable;
  private ClientMain clientMain;

  public void setClientMain(ClientMain clientMain){
    this.clientMain = clientMain;
    addressLable.setText(clientMain.getUserMail());
  }

  @FXML
  private void handleNew(){
    clientMain.showSendMailDialog(new Mail(
      clientMain.getUserMail(),
      "",
      null,
      0L,
      ""
      ),
      "Send new email");
  }

  public RootLayoutController(){

  }
}
