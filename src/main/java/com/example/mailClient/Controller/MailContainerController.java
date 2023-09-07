package com.example.mailClient.Controller;

import com.example.Transmission.Email;
import com.example.mailClient.Model.User;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.mailClient.Model.Mail;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class MailContainerController {
  @FXML
  private TableView<Mail> inTable;

  @FXML
  private TableColumn<Mail, String> inSenderColumn;

  @FXML
  private TableColumn<Mail, String> inSubjectColumn;

  @FXML
  private TableColumn<Mail, String> inDateColumn;

  @FXML
  private TableView<Mail> outTable;

  @FXML
  private TableColumn<Mail, String> outReceiverColumn;

  @FXML
  private TableColumn<Mail, String> outSubjectColumn;

  @FXML
  private TableColumn<Mail, String> outDateColumn;

  @FXML
  private Label subjectLabel;

  @FXML
  private Label senderLabel;

  @FXML
  private Label dateLabel;

  @FXML
  private Label receiverLabel;

  @FXML
  private TextArea bodyTextArea;

  @FXML
  private Button buttonReply;

  @FXML
  private Button buttonReplyAll;

  @FXML
  private Button buttonForward;

  @FXML
  private Button buttonDelete;

  @FXML
  private TabPane tabPane;

  private Stage topStage;

  public Mail selectedMail;
  private String username;
  LoginController loginController = new LoginController();
  ObservableList<Mail> inboxMailsList;
  ObservableList<Mail> outboxMailsList;

  private User userModel;
  //MailHandler mailHandler = new MailHandler();

  private ExecutorService mailUpdater;

  private ExecutorService emailUpdater;

  private ClientController cc;
  private final Object lock = new Object();

  public void setClientMain(LoginController loginController, User userModel, ClientController cc) {
    this.loginController = loginController;
    this.userModel = userModel;
    this.username = this.userModel.getUsername();
    this.cc = cc;
    this.updateAllEmails();
    startMailUpdater();
  }

  public void setTopStage(Stage topStage) {
    this.topStage = topStage;
  }

  private void startMailUpdater() {
    mailUpdater = Executors.newSingleThreadExecutor();
    mailUpdater.execute(() -> {
      while (true) {
        try {
          Thread.sleep(5000);
//          Platform.runLater(() -> this.cc.requestInbox());
//          Platform.runLater(() -> this.cc.requestOutbox());
//          Platform.runLater(this::updateAllEmails);
          Platform.runLater(this::updateInbox);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

  }

  public void showNewMailPopUp(int mailCount) {
    Platform.runLater(() -> {
      Alert popup = new Alert(Alert.AlertType.INFORMATION);
      popup.initOwner(topStage);
      popup.setTitle("Notifications");

      if (mailCount > 1)
        popup.setContentText("You received " + mailCount + " new messages");
      else
        popup.setContentText("You received a new message");

      popup.show();
    });
  }

  public void updateOutboxEmails(Email newEmail) {
    // prendo la lista delle email e aggiungo quella nuova
//    List<Email> emails = this.userModel.getOutbox();
//    emails.add(0, newEmail);
//    this.userModel.setOutbox(emails);
    this.updateOutbox();
    this.updateInbox();
  }

  /*
  FIXME try to use the parametes inboxMailList
   */
  private void updateInboxEmails() {
//    System.out.println("[upateInboxEmails] refreshing inbox");
    inTable.getItems().clear();

    this.userModel.getInbox().stream().forEach((inboxEmail) -> {
      emailUpdater.submit(() -> {
        String receivers = inboxEmail.getReceivers().stream().map(Object::toString).collect(Collectors.joining("; "));
        Mail m = new Mail(inboxEmail.getId(), inboxEmail.getSender(), inboxEmail.getSubject(), receivers, inboxEmail.getTimestamp(),
            inboxEmail.getText());
        synchronized (lock) {
          Platform.runLater(() -> inTable.getItems().add(m));
        }
      });
    });
  }

  private void updateOutboxEmails() {
//    System.out.println("[updateOutboxEmails] refreshing outbox");
    outTable.getItems().clear();

    this.userModel.getOutbox().stream().forEach((outboxEmail) -> {
      emailUpdater.submit(() -> {
        String receivers = outboxEmail.getReceivers().stream().map(Object::toString).collect(Collectors.joining("; "));

        Mail m = new Mail(outboxEmail.getId(), outboxEmail.getSender(), outboxEmail.getSubject(), receivers, outboxEmail.getTimestamp(),
            outboxEmail.getText());
        synchronized (lock) {
          Platform.runLater(() -> outTable.getItems().add(m));
        }
      });
    });
  }

  public void updateAllEmails() {
//    System.out.println("refreshing gui");

    emailUpdater = Executors.newFixedThreadPool(10);

    this.updateInbox();
    this.updateOutbox();
  }

  public void updateInbox() {
    int size = this.cc.requestInbox();
    if(size == -1)
      System.out.println("Error requesting inbox");
    if(size > 0)
      this.updateInboxEmails();
  }
  public void updateOutbox() {
    int size = this.cc.requestOutbox();
    if(size == -1)
      System.out.println("Error requesting outbox");
    if(size > 0)
      this.updateOutboxEmails();
  }
  @FXML
  private void initialize() {

    // Set up the columns in the inbox table
    inSenderColumn.setCellValueFactory(new PropertyValueFactory<>("sender"));
    inSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
    inDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

    // Set up the columns in the outbox table
    outReceiverColumn.setCellValueFactory(new PropertyValueFactory<>("receiversString"));
    outSubjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
    outDateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));

    // Set up the selection listeners for the inbox and outbox tables
    inTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (newSelection != null) {
        showMailDetails(newSelection);
        this.selectedMail = newSelection;
      }
    });

    outTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      if (newSelection != null) {
        showMailDetails(newSelection);
        this.selectedMail = newSelection;
      }
    });
  }

  private void showMailDetails(Mail mail) {
//    System.out.println("[MCC] showMailDetails");
    subjectLabel.setText(mail.getSubject());
    senderLabel.setText("From: " + mail.getSender());
    dateLabel.setText("Date: " + mail.getFormattedDate());
    receiverLabel.setText("To: " + mail.getReceivers());
    bodyTextArea.setText(mail.getMessage());
    buttonReply.setDisable(false);
    buttonReplyAll.setDisable(false);
    buttonForward.setDisable(false);
    buttonDelete.setDisable(false);
  }

  @FXML
  public void forward() {
    loginController.showSendMailDialog(new Mail(this.selectedMail.getId(),
                    this.userModel.getUsername(),
        "[FWD] " + this.selectedMail.getSubject(),
        " ",
        LocalDateTime.now(),
        this.selectedMail.getMessage() + "\n--forwarded from" + this.selectedMail.getSender()),
        "Forward Email");
  }

  @FXML
  public void reply() {
    // if (!selectedMail.getSender().equals(username)) {
    loginController.showSendMailDialog(new Mail(this.selectedMail.getId(),
                    this.userModel.getUsername(),
        "[RE]" + selectedMail.getSubject(),
        selectedMail.getSender().toString(),
        LocalDateTime.now(),
        "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
        "Reply Email");
  }

//  @FXML
//  public void replyAll() {
//    if (!selectedMail.getSender().equals(username)) {
//      String temp;
////      temp = selectedMail.getReceiversString().replace(username + "; ", username);
//      temp = selectedMail.getReceiversString().replace(username, "");
//      temp.replace(username, "");
//      loginController.showSendMailDialog(new Mail(this.selectedMail.getId(),
//                      username,
//          "[RE]" + selectedMail.getSubject(),
//          selectedMail.getSender() + "; " + temp,
//          LocalDateTime.now(),
//          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
//          "Reply Email");
//    } else {
//      loginController.showSendMailDialog(new Mail(this.selectedMail.getId(),
//                      username,
//          "[RE]" + selectedMail.getSubject(),
//          selectedMail.getSender(),
//          LocalDateTime.now(),
//          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
//          "Reply Email");
//    }
//  }

  @FXML
  public void replyAll() {
    String sender = selectedMail.getSender() + "; " + selectedMail.getReceiversString();
    sender.replace(username + "; ", "");
    String subject = "[RE]" + selectedMail.getSubject();
    String message = "\n---\n" + sender + ":\n\n" + selectedMail.getMessage();
    LocalDateTime now = LocalDateTime.now();

    Mail replyMail = new Mail(selectedMail.getId(), username, subject, sender, now, message);

    loginController.showSendMailDialog(replyMail, "Reply mail");
  }



  @FXML
  public void delete() {
    boolean success = cc.deleteMail(selectedMail);
    showMailDetails(new Mail("",
            "",
                    "",
                    "",
                    LocalDateTime.now(),
                    ""));
    if(success) {
      try {
        ArrayList<String> receivers = new ArrayList<>();
        for (String receiver : selectedMail.getReceivers())
          receivers.add(receiver);
        userModel.removeFromInbox(new Email(selectedMail.getId(), selectedMail.getSender(), receivers, selectedMail.getSubject(), selectedMail.getMessage(), selectedMail.getDate()));
        this.updateInboxEmails();
      } catch (Exception xcpt) {
//        System.out.println("Couldn't delete mail from inbox");
      }
      try {
        ArrayList<String> receivers = new ArrayList<>();
        for (String receiver : selectedMail.getReceivers())
          receivers.add(receiver);
        userModel.removeFromOutbox(new Email(selectedMail.getId(), selectedMail.getSender(), receivers, selectedMail.getSubject(), selectedMail.getMessage(), selectedMail.getDate()));
        this.updateOutboxEmails();
      } catch (Exception xcpt) {
//        System.out.println("Couldn't delete mail from outbox");
      }
    }
  }
}
