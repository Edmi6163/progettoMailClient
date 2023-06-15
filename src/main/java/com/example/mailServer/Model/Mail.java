package com.example.mailServer.Model;

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
  //OjectProopery<LocalDateTime> is not serializable, so must be transient
  private StringProperty sender;
  private StringProperty subject;
  private ListProperty<String> receivers;
  private transient ObjectProperty<LocalDateTime> date; //needed transient beacause ObjectProperty<LocalDateTime> is not serializable
  private StringProperty message;
  private BooleanProperty isSent;


  public Mail(String sender,String subject,String receivers,long timestamp,String message) {
    this.sender = new SimpleStringProperty(sender);
    this.subject = new SimpleStringProperty(subject);
    this.receivers = new SimpleListProperty<>();
    if(receivers!=null) setReceivers(receivers);
    this.date = new SimpleObjectProperty<>(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), TimeZone.getDefault().toZoneId()));
    this.message = new SimpleStringProperty(message);
    this.isSent = new SimpleBooleanProperty(false);
  }


  public Mail(){
    init();
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

  public ListProperty<String> receiversProperty() {
    return receivers;
  }

  public void setReceivers(String r) {
    ArrayList<String> list = new ArrayList<>();
    Matcher m = Pattern.compile("[a-zA-Z0-9_.+-]+@[a-zA-Z0-9_.+-]+\\.[a-z-A-Z0-9-.]+").matcher(r);
    while(m.find())
      list.add(m.group());
    receivers.set(FXCollections.observableArrayList(list));
  }
  public StringProperty receiversStringProperty(){
    if(receivers==null){
      return new SimpleStringProperty("");
    }
    StringBuilder str=new StringBuilder();
      for(String s: receivers){
        str.append(s).append(";");
      }
      return new SimpleStringProperty(str.toString());
  }
  public String getReceiversString(){
    return receiversStringProperty().get();
  }
  public LocalDateTime getDate() {
    return date.get();
  }
  public String getFormattedDate(){
    return date.get().format(DateTimeFormatter.ofPattern("dd/mm/yyyy-HH:mm"));
  }

  public ObjectProperty<LocalDateTime> dateProperty() {
    return date;
  }

  public void setDate(LocalDateTime date) {
    this.date.set(date);
  }

  public String getMessage() {
    return message.get();
  }

  public StringProperty messageProperty() {
    return message;
  }

  public void setMessage(String message) {
    this.message.set(message);
  }

  public long getMillis(){
    return getDate().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
  }

  public boolean isIsSent() {
    return isSent.get();
  }

  public BooleanProperty isSentProperty() {
    return isSent;
  }

  public void setIsSent(boolean isSent) {
    this.isSent.set(isSent);
  }

  public void init(){
    this.sender = new SimpleStringProperty();
    this.subject = new SimpleStringProperty();
    this.receivers = new SimpleListProperty<>();
    this.date = new SimpleObjectProperty<>();
    this.isSent = new SimpleBooleanProperty();
  }

  private void write(ObjectOutputStream s) throws IOException{
    long millis = getDate().atZone(TimeZone.getDefault().toZoneId()).toInstant().toEpochMilli();
    s.defaultWriteObject();
    s.writeUTF(getSender());
    s.writeUTF(getSubject());
    s.writeUTF(getReceiversString());
    s.writeLong(millis);
    s.writeUTF(getMessage());
    s.writeBoolean(isIsSent());
  }

  public void read(ObjectInputStream s) throws IOException, ClassNotFoundException{
    init();
    setSender(s.readUTF());
    setSubject(s.readUTF());
    setReceivers(s.readUTF());
    setDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(s.readLong()),TimeZone.getDefault().toZoneId()));
    setIsSent(s.readBoolean());
  }
  @Override
  public String toString(){
    return "sender: "+sender+"\nsubject: "+subject+"\nreceivers: "+receivers+"\ndate: "+ getFormattedDate();
  }
}
