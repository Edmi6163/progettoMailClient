package com.example.mailClient.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.example.mailClient.ClientMain;
import com.example.mailServer.Model.Mail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
  private ClientMain clientMain;
  private Mail selectedMail;

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
    clientMain.showSendMailDialog(new Mail(clientMain.getUserMail(),
        "[FWD] " + selectedMail.getSubject(),
        " ",
        0L,
        selectedMail.getMessage() + "\n--forwarded from" + selectedMail.getSender()),
        "Forward Email");
  }

  @FXML
  private void reply() {
    if (!selectedMail.getSender().equals(clientMain.getUserMail())) {
      clientMain.showSendMailDialog(new Mail(clientMain.getUserMail(),
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    } else {
      clientMain.showSendMailDialog(new Mail(clientMain.getUserMail(),
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    }
  }

  @FXML
  private void replyAll() {
    if (!selectedMail.getSender().equals(clientMain.getUserMail())) {
      clientMain.showSendMailDialog(new Mail(clientMain.getUserMail(),
          "[RE]" + selectedMail.getSubject(),
          selectedMail.getSender(),
          0L,
          "\n---\n" + selectedMail.getSender() + ":\n\n" + selectedMail.getMessage()),
          "Reply Email");
    } else {
      clientMain.showSendMailDialog(new Mail(clientMain.getUserMail(),
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

  public void setClientMain(ClientMain clientMain) {
    this.clientMain = clientMain;
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
