package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;


import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.mailClient.Model.User;

import java.awt.*;

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
  private TrayIcon trayIcon;
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

  public void showErrorPopUp() {
    Alert popup = new Alert(Alert.AlertType.INFORMATION);
    popup.initOwner(topStage);
    popup.setTitle("Server error");
    popup.setContentText("Server propably is offline or check your internet connection");
    popup.show();
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
    * FIXME plz
   */
  private static Communication sendCommunicationToServer(Communication c) {
    System.out.println("sending communication to server: " + c.getAction() + " " + c.getBody());
    try {
      if (out == null || in == null) {
        System.out.println("out or in is null");
        return null;
      }
      out.writeObject(c);

      Communication response = (Communication) in.readObject();

      System.out.println("function sendCommunicationToServer returned: " + response.getClass().getSimpleName() + " " + response);

      System.out.println("[sendCommunicationToServer] response: " + response.getAction() + " " + response.getBody()); //FIXME here the response is null, so inbox isn't updated
      return response;

    } catch (IOException | ClassNotFoundException e) {
      System.out.println("error in sendCommunicationToServer");
      e.printStackTrace();
      return null;
    }
  }

  public int requestInbox() {

    System.out.println("[requestInbox] request inbox called");
    try {
      if (!connectToSocket()) {
        loginController.showErrorPopUp();
        return -1;
      }
      Communication request = new Communication("inbox", new Pair<>(username,(ArrayList)userModel.getInbox()));

      System.out.println("[requestInbox] communication request: " + request.getAction() + " " + request.getBody());
      Communication response = sendCommunicationToServer(request);

      if (response == null) {
        System.out.println("response is null");
        return -1;
      }
      System.out.println("[requestInbox] communication response: " + response.getAction() + " " + response.getBody()); //FIXME here the response is null, so inbox isn't updated
      Object body = response.getBody();
      if (!(body instanceof ArrayList)) {
        System.out.println("response body is not an ArrayList");
        return -1;
      }

      ArrayList<Email> res = (ArrayList<Email>) body;

      if(res.size() > 0) {
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

    System.out.println("[requestInfo] request outbox called");
    try {
      if (!connectToSocket()) {
        loginController.showErrorPopUp();
        return -1;
      }
      Communication request = new Communication("outbox", new Pair<>(username,(ArrayList)userModel.getOutbox()));

      System.out.println("[requestOutbox] communication request: " + request.getAction() + " " + request.getBody());
      Communication response = sendCommunicationToServer(request);

      if (response == null) {
        System.out.println("response is null");
        return -1;
      }
      System.out.println("[requestOutbox] communication response: " + response.getAction() + " " + response.getBody()); //FIXME here the response is null, so inbox isn't updated
      Object body = response.getBody();
      if (!(body instanceof ArrayList)) {
        System.out.println("response body is not an ArrayList");
        return -1;
      }

      ArrayList<Email> res = (ArrayList<Email>) body;

      if(res.size() >0) {
        ObservableList<Email> resList = FXCollections.observableList(res);

        //      System.out.println("[requestInfo] res is " + res.getClass());

        this.userModel.addToOutbox(resList);
      }

      closeSocketConnection();

      return res.size();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /*
  @brief: send a notification using jna library on linux
   */
  public void notificationManager(){
    String[] command = {"notify-send", "new mail received", "check your inbox"};

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
        loginController.showErrorPopUp();
        return;
      }

      Communication request = new Communication("login", username);
      System.out.println("[login] communication request: " + request.getAction());
      System.out.println("[login] communication request body: " + request.getBody());



      Communication response = sendCommunicationToServer(request);

      System.out.println("[login] communication response: " + response.getBody());
      System.out.println("[login] communication response body: " + response.getAction());

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
        loginController.showErrorPopUp();
        return false;
      }
      System.out.println("action send written to server");
      System.out.println(mail);

      Communication sendMail = new Communication("send", mail);
      Communication response = (Communication) sendCommunicationToServer(sendMail);

      System.out.println("[send mail CC] mail written to server\n" + mail.toString());

      if (response.getAction().equals("send_not_ok")) {
        mailNotExist();
        closeSocketConnection();
        return false;
      }

      System.out.println("Received response action: " + response.getAction());
      System.out.println("Received response body: " + response.getBody());

      closeSocketConnection();

    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  /*
   * @brief: delete email from server
   * FIXME mail are not deleted from server
   */
  public boolean deleteMail(Mail mail) {
    try {
      if (!connectToSocket()) {
        showErrorPopUp();
        return false;
      }

      ArrayList<String> receivers = (ArrayList<String>) mail.getReceivers().stream().map(receiver -> receiver)
          .collect(Collectors.toList());

      Email e = new Email(mail.getSender(), receivers, mail.getSubject(), mail.getMessage(), mail.getDate());

      Pair<String, Email> pair = new Pair(this.username, e);
      Communication delete = new Communication("delete", pair);

      Communication response = (Communication) sendCommunicationToServer(delete);

      System.out.println(response);
      closeSocketConnection();

    } catch (Exception e) {
      e.printStackTrace();
    }
    return true;
  }

}
