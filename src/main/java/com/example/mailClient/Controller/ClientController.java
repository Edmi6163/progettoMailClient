package com.example.mailClient.Controller;

import com.example.mailClient.Model.Mail;
import javafx.application.Platform;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.InboxRequest;
import com.example.Transmission.LoginRes;
import com.example.mailClient.Model.User;

import java.awt.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
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
      out.flush();


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

  /*
  public String getMaxTimeStamp(List<Mail> inbox) {
    long maxTimeStamp = 0;
    for (Mail m : inbox) {
      if (m.getMillis() > maxTimeStamp) {
        maxTimeStamp = m.getMillis();
      }
    }
    return " " + maxTimeStamp;
  }
*/

  // public void noMailPopUp() {
  // Platform.runLater(() -> loginController.noMailPopUp());
  // }


  /*
    * @brief: using the notification manager of the os to notify the user when a new mail arrives
   */

  private void notificationManager(String title, String message){
    if(SystemTray.isSupported()){
     SystemTray tray = SystemTray.getSystemTray();
     trayIcon = new TrayIcon(Toolkit.getDefaultToolkit().getImage(" "), "Mail client");
     try {
       tray.add(trayIcon);
     } catch (Exception e) {
       e.printStackTrace();
     }

     trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
    }
  }

  /*
   * @brief: request inbox to server
   * FIXME inbox is not updated
   */
  public void requestInfo() {
    System.out.println("[requestInfo] request info called");
    try {
      if (!connectToSocket()) {
        loginController.showErrorPopUp();
        return;
      }
      Communication request = new Communication("inbox", username);

      System.out.println("[requestInfo] communication request: " + request.getAction() + " " + request.getBody());
      Communication response = (Communication) sendCommunicationToServer(request);

      if (response == null) {
        System.out.println("response is null");
        return;
      }
      System.out.println("[requestInfo] communication response: " + response.getAction() + " " + response.getBody()); //FIXME here the response is null, so inbox isn't updated
      Object body = response.getBody();
      if (!(body instanceof ArrayList)) {
        System.out.println("response body is not an ArrayList");
        return;
      }

      ArrayList<Email> res = (ArrayList<Email>) body;
      ObservableList<Email> resList = FXCollections.observableList(res);

      if(!res.isEmpty()){
        notificationManager("New mail arrived","You have new mail");
      }

      System.out.println("[requestInfo] res dimension is " + res.size());
      System.out.println("[requestInfo] res is " + res.getClass());
      System.out.println("[requestInfo] request info returned: " + res);



//      this.userModel.setInbox(resList);




      closeSocketConnection();
    } catch (IOException e) {
			throw new RuntimeException(e);
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

      LoginRes arrayLists = (LoginRes) response.getBody();

      ArrayList<Email> inbox = arrayLists.getArrayLists().get(0);
      ArrayList<Email> outbox = arrayLists.getArrayLists().get(1);

      this.userModel.setInbox(inbox);
      this.userModel.setOutbox(outbox);

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
        showErrorPopUp();
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
  public void deleteMail(Mail mail) {
    try {
      if (!connectToSocket()) {
        showErrorPopUp();
        return;
      }

      ArrayList<String> receivers = (ArrayList<String>) mail.getReceivers().stream().map(receiver -> receiver)
          .collect(Collectors.toList());

      Email e = new Email(mail.getSender(), receivers, mail.getSubject(), mail.getMessage());

      Communication delete = new Communication("delete", e);

      Communication response = (Communication) sendCommunicationToServer(delete);

      System.out.println(response);
      closeSocketConnection();

    } catch (Exception e) {
      e.printStackTrace();
    }
  }


}
