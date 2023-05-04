package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;

public class NewMessageController {
  @FXML
  private TextField receiversField;
  @FXML
  private TextField subjectField;
  @FXML
  private TextArea messageBodyArea;

  private Stage dialog;
  private Mail mail;
  private boolean okClicked = false;
  private ClientMain clientMain;

  @FXML
  private void initialize(){

  }
  public void setDialog(Stage dialog){
    this.dialog=dialog;
  }

  public void setMail(Mail mail){
    this.mail = mail;

    if (mail == null) {
      this.mail = new Mail("", "", null, 0L, "");
    }
    receiversField.setText(this.mail.getReceiversString());
    subjectField.setText(this.mail.getMessage());
    messageBodyArea.setText(this.mail.getMessage());
  }

  public boolean isOkClicked(){
    return okClicked;
  }

  @FXML
  private void handleOk() throws InterruptedException{
    mail.setReceivers(receiversField.getText());
    mail.setSubject(subjectField.getText());
    mail.setMessage(messageBodyArea.getText());
    if(isInputOk(mail)){
      okClicked=true;

    }
  }

  public boolean isInputOk(Mail mail){
    String error= "";
    if(receiversField.getText()==null||receiversField.getText().length()==0)
      error+="Missing receiver\n";
   if(mail.getReceiversString().length()==0)
     error+="Wrong email format\n";
   if(subjectField.getText()==null||subjectField.getText().length()==0)
     error+="Missing subject\n";
   if(receiversField.getText()==null||receiversField.getText().length()==0)
     error+= "Empty message body\n";
   if(error.length()==0){
     return true;
   } else {
     Alert alert = new Alert(Alert.AlertType.ERROR);
     alert.initOwner(dialog);
     alert.setTitle("Invalid fields");
     alert.setHeaderText("Errors detected in the following fields");
     alert.setContentText(error);
     alert.showAndWait();

     return false;
   }
  }
}
