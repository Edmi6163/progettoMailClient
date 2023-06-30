package com.example.mailClient.Controller;

import com.example.mailServer.Model.Mail;
import javafx.application.Platform;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController implements Serializable {
  private String username;
  private transient boolean serverStatus = false;
  private static Socket socket;

  Stage topStage;
  private static ObjectOutputStream out = null;
  private static ObjectInputStream in = null;
  public static LoginController loginController = new LoginController();

  public ClientController(String username) {
    this.username = username;
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

  // TODO a lot of debug print to remove
  private static Communication sendCommunicationToServer(Communication c) {
    System.out.println("sending communication to server: " + c.getAction() + " " + c.getBody());
    try {
      if (out == null || in == null) {
        System.out.println("out or in is null");
        return null;
      }
      out.writeObject(c);
      out.flush();
      Communication response = (Communication) in.readObject();
      System.out
          .println("function sendCommunicationToServer returned: " + response.getAction() + " " + response.getBody());
      return response;
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("error in sendCommunicationToServer");
      e.printStackTrace();
      return null;
    }
  }

  public String getMaxTimeStamp(List<Mail> inbox) {
    long maxTimeStamp = 0;
    for (Mail m : inbox) {
      if (m.getMillis() > maxTimeStamp) {
        maxTimeStamp = m.getMillis();
      }
    }
    return " " + maxTimeStamp;
  }

  // public void noMailPopUp() {
  // Platform.runLater(() -> clientMain.noMailPopUp());
  // }

  public void requestInfo() {
    try {
      if (!connectToSocket()) {
        showErrorPopUp();
        return;
      }

      Communication request = new Communication("inbox", username);

      Communication response = sendCommunicationToServer(request);

      ArrayList<Email> res = (ArrayList<Email>) response.getBody();
      // print res
      System.out.println("request info returned: " + res.toString());

      closeSocketConnection();

      // if (res != null) {
      // if (res.size() > 0) {
      // clientMain.addInbox(res);
      // clientMain.showNewMailPopUp(res.size());
      // }
      // } else {
      // noMailPopUp();
      // }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void login() {
    try {
      if (!connectToSocket()) {
        loginController.showErrorPopUp();
        return;
      }

      Communication request = new Communication("login", username + "@javamail.it");
      System.out.println("comunication request: " + request.getAction());
      System.out.println("communication request body: " + request.getBody());

      Communication response = sendCommunicationToServer(request);

      System.out.println("communication response: " + response.getBody());
      System.out.println("communication response body: " + response.getAction());

      LoginRes arrayLists = (LoginRes) response.getBody();

      ArrayList<Email> inbox = arrayLists.getArrayLists().get(0);
      ArrayList<Email> outbox = arrayLists.getArrayLists().get(1);

      closeSocketConnection();

      // TODO: settare inbox e outbox
      // if (resIn != null && resOut != null) {
      // if (resIn.size() > 0) {
      // clientMain.addInbox(resIn);
      // clientMain.addOutbox(resOut);
      // }
      // } else {
      // }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void sendMail(Email mail, LoginController clientMain) {
    // clientMain.setMailSent(false);
    try {
      /*
       * if (!connectToSocket()) {
       * // fai uscire il popup il server è offline
       * loginController.showErrorPopUp();
       * return;
       * }
       */
      System.out.println("action send written to server");
      System.out.println(mail);

      Communication sendMail = new Communication("send", mail);

      Communication response = sendCommunicationToServer(sendMail);

      System.out.println("[send mail CC] mail written to server\n" + mail.toString());

      // TODO: da capire cosa risponde il backend
      if (response.getBody() instanceof Mail responseMail) {
        System.out.println("Received response mail: " + responseMail);
        // clientMain.setMailSent(true);
        System.out.println("Received response mail: " + responseMail);
        Platform.runLater(() -> clientMain.addOut(responseMail));
      }

      closeSocketConnection();

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void deleteMail(Email mail) {
    try {
      if (!connectToSocket()) {
        // fai uscire il popup il server è offline
        showErrorPopUp();
        return;
      }

      Communication delete = new Communication("delete", mail);

      Communication response = sendCommunicationToServer(delete);

      // Platform.runLater(() -> clientMain.delete(mail));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
