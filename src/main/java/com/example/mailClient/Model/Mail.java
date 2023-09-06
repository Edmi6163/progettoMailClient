package com.example.mailClient.Model;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable; //this make object persistent
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mail implements Serializable {
  private String id;
  private StringProperty sender;
  private StringProperty subject;
  private ListProperty<String> receivers;
  private ObjectProperty<LocalDateTime> date;
  private StringProperty message;
  private BooleanProperty isSent;

  public Mail(String id, String sender, String subject, String receivers, LocalDateTime localDateTime, String message) {
    this.id = id;
    this.sender = new SimpleStringProperty(sender);
    this.subject = new SimpleStringProperty(subject);
    this.receivers = new SimpleListProperty<>();
    if (receivers != null)
      setReceivers(receivers);
    this.date = new SimpleObjectProperty<>(localDateTime);
    this.message = new SimpleStringProperty(message);
    this.isSent = new SimpleBooleanProperty(false);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSender() {
    return sender.get();
  }

  public StringProperty senderProperty() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender.set(sender);
  }

  public String getSubject() {
    return subject.get();
  }

  public StringProperty subjectProperty() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject.set(subject);
  }

  public ObservableList<String> getReceivers() {
    return receivers.get();
  }

  /*
  * @brief: parser to use the receivers string as a list of receivers, use isValidEmail to check if the email is valid using regex
  * */
  public void setReceivers(String r) {
    ArrayList<String> list = new ArrayList<>();

    String[] receiverArray = r.split("/ ");
    for (String receiver : receiverArray) {
      String trimmedReceiver = receiver.trim();
      if (isValidEmail(trimmedReceiver)) {
        list.add(trimmedReceiver);
      }
    }

    receivers.set(FXCollections.observableArrayList(list));
  }

  /*
  * @brief: check if the email is valid using regex
   */
  private boolean isValidEmail(String email) {
    Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9_.+-]+\\.[a-zA-Z0-9-.]+").matcher(email);
    return m.matches();
  }

  public StringProperty receiversStringProperty() {
    if (receivers == null) {
      return new SimpleStringProperty("");
    }
    StringBuilder str = new StringBuilder();
    for (String s : receivers) {
      str.append(s).append(";");
    }
    return new SimpleStringProperty(str.toString());
  }

  public String getReceiversString() {
    return receiversStringProperty().get();
  }

  public LocalDateTime getDate() {
    return date.get();
  }

  public synchronized String getFormattedDate() {
    return date.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
  }



  public void setDate(LocalDateTime date) {
    this.date.set(date);
  }

  public String getMessage() {
    return message.get();
  }

  public void setMessage(String message) {
    this.message.set(message);
  }





  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("sender: ").append(sender).append("\n");
    builder.append("subject: ").append(subject).append("\n");
    builder.append("receivers: ").append(receivers).append("\n");
    if (date != null) {
      builder.append("date: ").append(getFormattedDate());
    } else {
      builder.append("date: null");
    }
    return builder.toString();
  }

}