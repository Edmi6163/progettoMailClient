package com.example.mailClient.Controller;

import com.example.mailServer.Controller.MailHandler;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.mailServer.Model.Mail;


import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.nio.charset.MalformedInputException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

  private Mail selectedMail;
  private String username = "francesco@javamail.it"; //TODO replace
  LoginController loginController = new LoginController();
  ObservableList<Mail> inboxMailsList = loginController.getInbox();
  ObservableList<Mail> outboxMailsList = loginController.getOutbox();

  MailHandler mailHandler = new MailHandler();


  @FXML
  private void initialize() {
    System.out.println("[MCC] initialize");
    loadMailsFromFile();
    System.out.println("inboxMailList size: " + inboxMailsList.size());
    System.out.println("outboxMailList size: " + outboxMailsList.size());

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
      System.out.println("[MCC] inTable selection listener");
      if (newSelection != null) {
        showMailDetails(newSelection);
      }
    });

    outTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
      System.out.println("[MCC] outTable selection listener");
      if (newSelection != null) {
        showMailDetails(newSelection);
      }
    });
  }

  private void showMailDetails(Mail mail) {
    System.out.println("[MCC] showMailDetails");
    subjectLabel.setText(mail.getSubject());
    senderLabel.setText("From: " + mail.getSender());
    dateLabel.setText("Date: " + mail.getFormattedDate());
    receiverLabel.setText("To: " + mail.getReceiversString());
    bodyTextArea.setText(mail.getMessage());
    buttonReply.setDisable(false);
    buttonReplyAll.setDisable(false);
    buttonForward.setDisable(false);
    buttonDelete.setDisable(false);
  }

  @FXML
  private void forward() {
    loginController.showSendMailDialog(new Mail(username,
        "[FWD] " + selectedMail.getSubject(),
        " ",
        0L,
        selectedMail.getMessage() + "\n--forwarded from" + selectedMail.getSender()),
      "Forward Email");
  }

  @FXML
  private void reply() {
    if (!selectedMail.getSender().equals(username)) {
      loginController.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
        "Reply Email");
    } else {
      loginController.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
        "Reply Email");
    }
  }

  @FXML
  private void replyAll() {
    if (!selectedMail.getSender().equals(username)) {
      loginController.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
        "Reply Email");
    } else {
      loginController.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
        "Reply Email");
    }
  }

  @FXML
  private void delete() {
    // new Thread(() -> ClientController.deleteMail(selectedMail,
    // clientMain)).start();
  }

  public void setClientMain(LoginController loginController, String username) {
    this.loginController = loginController;
    this.username = username;
    inTable.setItems(loginController.getInbox());
    outTable.setItems(loginController.getOutbox());
  }

  public static List<Mail> convertLinesToMails(List<String> lines) {
    List<Mail> mails = new ArrayList<>();

    for (String line : lines) {
      String[] parts = line.split(", ");

      if (parts.length >= 5) {
        String sender = parts[0];
        String subject = parts[1];
        String receiversString = parts[2];
        long timestamp = Long.parseLong(parts[3]);
        String message = parts[4];

        Mail mail = new Mail(sender, subject, receiversString, timestamp, message);
        mails.add(mail);
      }
    }

    return mails;
  }


  private List<Mail> loadMailsFromFile() {
    try {
      String folderPath = "src/main/java/com/example/mailServer/file/" + username;
      String inboxPath = folderPath + "/in/";
      String outboxPath = folderPath + "/out/";

      System.out.println("folderPath is: " + inboxPath);

      System.out.println("loading mails from file");
      // Create the folder structure if it doesn't exist
      if (!Files.exists(Paths.get(inboxPath))) {
        Files.createDirectories(Paths.get(inboxPath));
      }
      if (!Files.exists(Paths.get(outboxPath))) {
        Files.createDirectories(Paths.get(outboxPath));
      }

      // Read all text files in the inbox directory
      List<Mail> inboxMails = new ArrayList<>();
      try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(inboxPath), "*.txt")) {
        for (Path filePath : stream) {
          List<String> lines = Files.readAllLines(filePath);
          List<Mail> mails = convertLinesToMails(lines);
          inboxMails.addAll(mails);
        }
      }

      // Read the outbox file
      String outboxFilePath = outboxPath + "/" + username + "_outbox.txt";
      List<Mail> outboxMails = new ArrayList<>();
      if (Files.exists(Paths.get(outboxFilePath))) {
        List<String> outboxLines = Files.readAllLines(Paths.get(outboxFilePath));
        outboxMails = convertLinesToMails(outboxLines);
      }

      // Update the UI with inbox mails
      Platform.runLater(() -> {
        // Clear existing inbox mails and add the loaded mails
//        inboxMailsList.clear();
        inboxMailsList.addAll(inboxMails);
      });

      // Update the UI with outbox mails
      List<Mail> finalOutboxMails = outboxMails;
      Platform.runLater(() -> {
        // Clear existing outbox mails and add the loaded mails
//        outboxMailsList.clear();
        outboxMailsList.addAll(finalOutboxMails);
      });

      // Combine and return the inbox and outbox mails
      List<Mail> allMails = new ArrayList<>();
      allMails.addAll(inboxMails);
      allMails.addAll(outboxMails);

      return allMails;
    } catch (IOException e) {
      System.out.println("MalformedInputException");
//      e.printStackTrace();
      return null;
    }
  }
}


/*
public class MailContainerController {
  @FXML
  private TabPane tabPane;
  @FXML
  private TableView<Mail> inTable;
  @FXML
  private TableView<Mail> outTable;
  @FXML
  private TableColumn<Mail, String> inSenderColumn;
  @FXML
  private TableColumn<Mail, String> inSubjectColumn;
  @FXML
  private TableColumn<Mail, LocalDateTime> inDateColumn;
  @FXML
  private TableColumn<Mail, String> outReceiverColumn;
  @FXML
  private TableColumn<Mail, String> outSubjectColumn;
  @FXML
  private TableColumn<Mail, LocalDateTime> outDateColumn;
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
  private LoginController clientMain;
  private Mail selectedMail;
  private String username;

  public MailContainerController() {

  }

  public void setDate(TableColumn<Mail, LocalDateTime> d) {
    d.setCellValueFactory(cellData -> cellData.getValue().dateProperty());
    d.setCellFactory(col -> new TableCell<>() {
      @Override
      protected void updateItem(LocalDateTime item, boolean empty) {
        super.updateItem(item, empty);
        if (empty)
          setText(null);
        else
          setText(item.format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH-mm")));
      }
    });
  }

  @FXML
  private void initialize() {
    inSenderColumn.setCellValueFactory(cellData -> cellData.getValue().senderProperty());
    inSubjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
    setDate(inDateColumn);
    outReceiverColumn.setCellValueFactory(cellData -> cellData.getValue().receiversStringProperty());
    outSubjectColumn.setCellValueFactory(cellData -> cellData.getValue().subjectProperty());
    setDate(outDateColumn);

    showMailDetails(null);
    inTable.getSelectionModel().selectedItemProperty()
        .addListener(((observable, oldValue, newValue) -> showMailDetails(newValue)));
    outTable.getSelectionModel().selectedItemProperty()
        .addListener(((observable, oldValue, newValue) -> showMailDetails(newValue)));
  }

  @FXML
  private void forward() {
    clientMain.showSendMailDialog(new Mail(username,
        "[FWD] " + selectedMail.getSubject(),
        " ",
        0L,
        selectedMail.getMessage() + "\n--forwarded from" + selectedMail.getSender()),
        "Forward Email");
  }

  @FXML
  private void reply() {
    if (!selectedMail.getSender().equals(username)) {
      clientMain.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    } else {
      clientMain.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    }
  }

  @FXML
  private void replyAll() {
    if (!selectedMail.getSender().equals(username)) {
      clientMain.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    } else {
      clientMain.showSendMailDialog(new Mail(username,
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    }
  }

  @FXML
  private void delete() {
    // new Thread(() -> ClientController.deleteMail(selectedMail,
    // clientMain)).start();
  }

  public void setClientMain(LoginController loginController, String username) {
    this.clientMain = loginController;
    this.username = username;
    inTable.setItems(clientMain.getInbox());
    outTable.setItems(clientMain.getOutbox());
  }

  private void showMailDetails(Mail mail) {
    if (mail != null) {
      this.selectedMail = mail;
      subjectLabel.setText(mail.getSubject());
      senderLabel.setText("from: " + mail.getSender());
      dateLabel.setText(mail.getFormattedDate());
      receiverLabel.setText("to: " + mail.getReceiversString());
      bodyTextArea.setText("to: " + mail.getReceiversString());
      buttonDelete.setDisable(false);
      buttonReplyAll.setDisable(false);
      buttonReply.setDisable(false);
    } else {
      subjectLabel.setText(" ");
      senderLabel.setText(" ");
      dateLabel.setText(" ");
      receiverLabel.setText(" ");
      bodyTextArea.setText("Please select a message");
    }
  }

}
*/
