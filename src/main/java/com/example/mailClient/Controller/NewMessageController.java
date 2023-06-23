package com.example.mailClient.Controller;

import com.example.mailServer.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.example.Transmission.Email;
import com.example.mailClient.ClientMain;

import java.io.File;
import java.util.ArrayList;

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

  @FXML
  private void initialize() {

  }

  public void setDialog(Stage dialog) {
    this.dialog = dialog;
  }

  public void setMail(Mail mail) {
    this.mail = mail;

    if (mail == null) {
      this.mail = new Mail("", "", null, 0L, "");
    }
    receiversField.setText(this.mail.getReceiversString());
    subjectField.setText(this.mail.getSubject());
    messageBodyArea.setText(this.mail.getMessage());
  }

  public boolean isOkClicked() {
    return okClicked;
  }

  /*
   * @brief: when the user click "send", check if the fields are filled and valid,
   * if not, display an error message
   * 
   * @throws InterruptedException
   */
  @FXML
  private void handleOk() throws InterruptedException {
    ClientMain clientMain = new ClientMain();
    boolean mailExist = false;

    mail.setReceivers(receiversField.getText());
    mail.setSubject(subjectField.getText());
    mail.setMessage(messageBodyArea.getText());
    ArrayList<String> receivers = new ArrayList<>();

    receivers.add("francesco@javamail.it");

    Email m = new Email("francesco@javamail.it", receivers, subjectField.getText(), messageBodyArea.getText());
    // if (checkIfMailExists(m, clientMain)) {
    // mailExist = true;
    // }
    // if (isInputOk(m) && mailExist) {
    // send mail
    ClientController.sendMail(m, clientMain);
    okClicked = true;
    // }
  }

  /*
   * @brief: Check if the receiversField is a valid email address it's a subfolder
   * name in folder "src/com/examlpe/mailServer/file, if not, display an error
   * messager
   * 
   * @param mail
   * 
   * @param clientMain
   */

  private boolean checkIfMailExists(Mail mail, ClientMain clientMain) {
    String error = "";

    File[] files = new File("src/main/java/com/example/mailServer/file").listFiles();
    if (files != null) {
      for (File file : files) {
        if (file.getName().equals(receiversField.getText())) {
          return true;
        }
      }
    }

    Alert alert = new Alert(Alert.AlertType.WARNING);
    alert.initOwner(dialog);
    alert.setTitle("Mail not exists");
    alert.setHeaderText("Sorry,receiver doesn't exist or the email address is not valid");
    alert.setContentText(error);
    alert.showAndWait();

    return false;
  }

  public boolean isInputOk(Mail mail) {
    String error = "";
    if (receiversField.getText() == null || receiversField.getText().length() == 0)
      error += "Missing receiver\n";
    if (mail.getReceiversString().length() == 0)
      error += "Wrong email format\n";
    if (subjectField.getText() == null || subjectField.getText().length() == 0)
      error += "Missing subject\n";
    if (receiversField.getText() == null || receiversField.getText().length() == 0)
      error += "Empty message body\n";
    if (error.length() == 0) {

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