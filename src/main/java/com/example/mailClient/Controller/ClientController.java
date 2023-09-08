package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;


import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.mailClient.Model.User;

import java.awt.*;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ClientController implements Serializable {
  private String username;
  private  boolean serverStatus = false;
  private static Socket socket;

  private User userModel;
  Stage topStage;
  private static ObjectOutputStream out = null;
  private static ObjectInputStream in = null;
  public static LoginController loginController = new LoginController();

  public ClientController(User userModel) {
    this.username = userModel.getUsername();
    this.userModel = userModel;
  }

  /*
   * @brief: This method checks if the server is online
   */
  public boolean checkConnection() {
    if(serverStatus)
      return true;
    return connectToSocket();
  }

  private boolean connectToSocket() {
    try {
      String hostName = InetAddress.getLocalHost().getHostName();
      socket = new Socket(hostName, 8189);
      out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      in = new ObjectInputStream(socket.getInputStream());
      this.serverStatus = true;
    } catch (IOException e) {
      if(serverStatus)
        showServerDownNotification();
      this.serverStatus = false;
    }
    return serverStatus;
  }

  private static void closeSocketConnection() throws IOException {
    if (socket != null) {
      out.close();
      in.close();
      socket.close();
    }
  }

  public void showServerDownNotification() {
    Alert popup = new Alert(Alert.AlertType.INFORMATION);
    popup.initOwner(topStage);
    popup.setTitle("Server error");
    popup.setContentText("Server unreachable, check your internet connection");
    popup.show();
  }

  public void showServerUpNotification() {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Server is up!");
      alert.setHeaderText("Server is up!");
      alert.setContentText("Server is up!");
      alert.show();
    });
  }

  public void mailNotExist() {
    Alert popup = new Alert(Alert.AlertType.ERROR);
    popup.initOwner(topStage);
    popup.setTitle("Mail error");
    popup.setContentText("Mail doesn't exist :(");
    popup.show();
  }
  /*
    * @brief: send information to server through Communication object and socket
   */
  private static Communication sendCommunicationToServer(Communication c) {
    try {
      if (out == null || in == null) {
        return null;
      }
      out.writeObject(c);

      Communication response = (Communication) in.readObject();


      return response;

    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
      return null;
    }
  }

  public int requestInbox() {

    try {
      if (!connectToSocket()) {
        return -1;
      }
      Communication request = new Communication("inbox", new Pair<>(username,(ArrayList)userModel.getInbox()));

      Communication response = sendCommunicationToServer(request);

      if (response == null) {
        return -1;
      }
      Object body = response.getBody();
      if (!(body instanceof ArrayList)) {
        return -1;
      }

      ArrayList<Email> res = (ArrayList<Email>) body;

      if(!res.isEmpty()) {
        ObservableList<Email> resList = FXCollections.observableList(res);

        notificationManager();

        this.userModel.addToInbox(resList);
      }

      closeSocketConnection();
      return res.size();
    } catch (IOException e) {
        throw new RuntimeException(e);
    }
  }

  public int requestOutbox() {

    try {
      if (!connectToSocket()) {
        showServerDownNotification();
        return -1;
      }
      Communication request = new Communication("outbox", new Pair<>(username,(ArrayList)userModel.getOutbox()));

      Communication response = sendCommunicationToServer(request);

      if (response == null) {
        return -1;
      }
      Object body = response.getBody();
      if (!(body instanceof ArrayList)) {
        return -1;
      }

      ArrayList<Email> res = (ArrayList<Email>) body;

      if(!res.isEmpty()) {
        ObservableList<Email> resList = FXCollections.observableList(res);


        this.userModel.addToOutbox(resList);
      }

      closeSocketConnection();

      return res.size();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * @brief: send a notification using jna library to use notify-send command on linux
   */
  public void notificationManager(){
    String[] command = {"notify-send",this.username + " received a new mail", "check your inbox"};

    try {
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      Process process = processBuilder.start();
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
  /*
   * @brief: send information to server through Communication object and socket
   */
  public void login() {
    try {
      if (!connectToSocket()) {
        showServerDownNotification();
        return;
      }

      Communication request = new Communication("login", username);

      Communication response = sendCommunicationToServer(request);

      closeSocketConnection();

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
   * @brief: send email to server
   */
  public boolean sendMail(Email mail, LoginController clientMain) {
    try {
      if (!connectToSocket()) {
        showServerDownNotification();
        return false;
      }
      Communication sendMail = new Communication("send", mail);
      Communication response = (Communication) sendCommunicationToServer(sendMail);


      if (response.getAction().equals("send_not_ok")) {
        mailNotExist();
        closeSocketConnection();
        return false;
      }

      closeSocketConnection();

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  /*
   * @brief: delete email from server
   */
  public boolean deleteMail(Mail mail) {
    try {
      if (!connectToSocket()) {
        showServerDownNotification();
        return false;
      }

      ArrayList<String> receivers = (ArrayList<String>) mail.getReceivers().stream().map(receiver -> receiver)
          .collect(Collectors.toList());

      Email e = new Email(mail.getSender(), receivers, mail.getSubject(), mail.getMessage(), mail.getDate());

      Pair<String, Email> pair = new Pair(this.username, e);
      Communication delete = new Communication("delete", pair);

      Communication response = sendCommunicationToServer(delete);

      closeSocketConnection();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

}
