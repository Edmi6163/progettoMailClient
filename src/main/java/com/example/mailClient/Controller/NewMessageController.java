package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import com.example.mailClient.Model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import com.example.Transmission.Email;

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
  public User user;

  public MailContainerController mailContainerController;

  public void setController(ClientController cc, User user, MailContainerController mailContainerController) {
    this.cc = cc;
    this.user = user;
    this.mailContainerController = mailContainerController;
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
      this.mail = new Mail("", "", "", null, LocalDateTime.now(), "");
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
  private  void handleOk() throws InterruptedException {
    LoginController clientMain = new LoginController();

    String sender = mail.getSender();
    System.out.println("[NMC] sender is: " + mail.getSender());
    mail.setReceivers(receiversField.getText());
    System.out.println("[NMC] receiver field is: " + receiversField.getText());
    mail.setSubject(subjectField.getText());
    mail.setMessage(messageBodyArea.getText());
    ArrayList<String> receivers = new ArrayList<>();

    LocalDateTime now = LocalDateTime.now();

    receivers.addAll(Arrays.asList(receiversField.getText().split("; ")));
    Mail m = new Mail("", sender, subjectField.getText(), receiversField.getText(), now,
        messageBodyArea.getText());
    System.out.println("[NewMessageController] handleOk() m: " + m);
    Email e = new Email(sender, receivers, subjectField.getText(), messageBodyArea.getText(), now);

    System.out.println("[NewMessageController] handleOk() e: " + e);

    if (isInputOk(m)) {
      // send mail
      mailSendedFeedback();
      boolean response = cc.sendMail(e, clientMain);
      if (response) {
        this.mailContainerController.updateOutboxEmails(e);
        okClicked = true;

      }
    }


  }

  public void mailSendedFeedback() {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Mail is going to be sent");
    alert.setHeaderText("Mail will be sent to " + receiversField.getText());
    alert.setContentText("Mail will be  sent to " + receiversField.getText());
    alert.showAndWait();
  }

  public boolean isInputOk(Mail mail) {
    String error = "";
    if (receiversField.getText() == null || receiversField.getText().length() == 0)
      error += "Missing receiver\n";

    else if (!receiversField.getText().contains("@javamail.it"))
      error += "Invalid receiver email format\n";

    if (mail.getReceivers().size() == 0)
      error += "Wrong email format\n";

    if (subjectField.getText() == null || subjectField.getText().length() == 0)
      error += "Missing subject\n";

    if (messageBodyArea.getText() == null || messageBodyArea.getText().length() == 0)
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