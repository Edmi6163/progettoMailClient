package com.example.mailClient.Controller;

import com.example.mailServer.Controller.ServerLayoutController;
import com.example.mailServer.Model.Mail;
import javafx.application.Platform;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
import com.example.mailClient.ClientMain;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ClientController implements Serializable {
  private ClientMain clientMain;
  private transient boolean serverStatus = false;
  private Socket socket;

  private ObjectOutputStream out = null;
  private ObjectInputStream in = null;

  public ClientController(ClientMain clientMain) {
    this.clientMain = clientMain;
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

  private void closeSocketConnection() throws IOException {
    if (socket != null) {
      out.close();
      in.close();
      socket.close();
    }
  }

  private Communication sendCommunicationToServer(Communication c) {
    try {
      if (out == null || in == null) {
        return null;
      }
      out.writeObject(c);
      return (Communication) in.readObject();
    } catch (IOException | ClassNotFoundException e) {
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

  public void noMailPopUp() {
    Platform.runLater(() -> clientMain.noMailPopUp());
  }

  public void requestInbox() {
    try {
      if (!connectToSocket()) {
        // fai uscire il popup il server è offline
        return;
      }

      System.out.println("Socket opened"); // TODO debug
      System.out.println("receiving data from server :)");

      Communication request = new Communication("inbox", clientMain.getUserMail());

      Communication response = sendCommunicationToServer(request);

      ArrayList<Email> res = (ArrayList<Email>) response.getBody();

      closeSocketConnection();

      if (res != null) {
        if (res.size() > 0) {
          clientMain.addInbox(res);
          clientMain.showNewMailPopUp(res.size());
        }
      } else {
        noMailPopUp();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  public void login() {
    try {
      if (!connectToSocket()) {
        // fai uscire il popup il server è offline
        return;
      }

      Communication request = new Communication("login", clientMain.getUserMail());

      Communication response = sendCommunicationToServer(request);

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

  public void sendMail(Email mail, ClientMain clientMain) {
    clientMain.setMailSent(false);
    try {
      if (!connectToSocket()) {
        // fai uscire il popup il server è offline
        return;
      }
      if (!connectToSocket()) {
        // fai uscire il popup il server è offline
        return;
      }

      System.out.println("action send written to server");
      System.out.println(mail);

      Communication sendMail = new Communication("send", mail);

      Communication response = sendCommunicationToServer(sendMail);

      System.out.println("[send mail CC] mail written to server\n" + mail.toString());

      // TODO: da capire cosa risponde il backend
      if (response.getBody() instanceof Mail responseMail) {
        System.out.println("Received response mail: " + responseMail);
        clientMain.setMailSent(true);
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
        return;
      }

      Communication delete = new Communication("delete", mail);

      Communication response = sendCommunicationToServer(delete);

      Platform.runLater(() -> clientMain.delete(mail));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
