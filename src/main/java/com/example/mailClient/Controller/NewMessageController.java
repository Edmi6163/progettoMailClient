package com.example.mailClient.Controller;

import com.example.Transmission.LoginRes;
import com.example.mailServer.Model.Mail;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.example.Transmission.Email;
import com.example.mailClient.ClientMain;

import java.io.File;
import java.security.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

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
  private ClientController cc;

  public void setController(ClientController cc) {
    this.cc = cc;
  }

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
    LoginController clientMain = new LoginController();
    // boolean mailExist = false;

    String sender = mail.getSender();
    System.out.println("[NMC] sender is: " + mail.getSender());
    mail.setReceivers(receiversField.getText());
    System.out.println("[NMC] receiver field is: " + receiversField.getText());
    mail.setSubject(subjectField.getText());
    mail.setMessage(messageBodyArea.getText());
    ArrayList<String> receivers = new ArrayList<>();

    receivers.addAll(Arrays.asList(receiversField.getText().split(",")));
    // TODO figure out timestamp
    Mail m = new Mail(sender, subjectField.getText(), receiversField.getText(), 0L, messageBodyArea.getText());
    System.out.println("[NewMessageController] handleOk() m: " + m);
    Email e = new Email(sender, receivers, subjectField.getText(), messageBodyArea.getText());
    System.out.println("[NewMessageController] handleOk() e: " + e);
    /*
     * if (checkIfMailExists(m, clientMain)) {
     * mailExist = true;
     * }
     */
    if (isInputOk(m)) {
      // send mail
      cc.sendMail(e, clientMain);
      okClicked = true;
    }
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

  private boolean checkIfMailExists(Mail mail, LoginController clientMain) {
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
    if (mail.getReceivers().size() == 0)
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