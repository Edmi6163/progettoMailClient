package com.example.mailClient.Controller;

import com.example.mailServer.Controller.ServerLayoutController;
import com.example.mailServer.Model.Mail;
import javafx.application.Platform;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.mailClient.ClientMain;

import java.io.*;
import java.net.*;
import java.util.List;

public class ClientController implements Serializable {
  private ClientMain clientMain;
  private transient boolean serverStatus = false;
  private Socket socket;
  private static final String host = "127.0.0.1";

  // private static LoggerModel logger;
  private static ServerLayoutController logger;

  ObjectOutputStream out;
  ObjectInputStream in;

  public ClientController(ClientMain clientMain) {
    this.clientMain = clientMain;
    logger = new ServerLayoutController();
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
      ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
      out.flush();
      ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
      this.serverStatus = true;
    } catch (IOException e) {
      this.serverStatus = false;
    }
    return serverStatus;
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
      try (Socket s = new Socket(host, 8189)) {
        System.out.println("Socket opened"); // TODO debug
        out = new ObjectOutputStream(s.getOutputStream());
        in = new ObjectInputStream(s.getInputStream());
        System.out.println("receiving data from server :)" + s);
        out.writeObject("inbox");
        out.writeObject(clientMain.getUserMail());
        out.writeObject(getMaxTimeStamp(clientMain.getInbox()));
        List<Mail> res = (List<Mail>) in.readObject();
        in.close();
        out.close();
        if (res != null) {
          if (res.size() > 0) {
            clientMain.addInbox(res);
            clientMain.showNewMailPopUp(res.size());
          }
        } else {
          noMailPopUp();
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean requestAll() {
    try {
      try (Socket s = new Socket(host, 8189)) {
        System.out.println("[Client Controller] socket opened :)" + s); // TODO debug
        in = new ObjectInputStream(s.getInputStream());
        out = new ObjectOutputStream(s.getOutputStream());
        String getInput;
        out.writeObject("all");
        out.writeObject(clientMain.getUserMail());
        List<Mail> resIn = (List<Mail>) in.readObject();
        List<Mail> resOut = (List<Mail>) in.readObject();
        in.close();
        out.close();
        if (resIn != null && resOut != null) {
          if (resIn.size() > 0) {
            clientMain.addInbox(resIn);
            clientMain.addOutbox(resOut);
          }
        } else {
          return false;
        }
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

  public static void sendMail(Email mail, ClientMain clientMain) {
    clientMain.setMailSent(false);
    try (Socket s = new Socket(host, 8189)) {
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      System.out.println("action send written to server");
      System.out.println(mail);
      Communication c = new Communication("send", mail);
      out.writeObject(c);
      out.flush();
      System.out.println("[send mail CC] mail written to server\n" + mail.toString());

      System.out.println("in.available() = " + in.available()); // FIXME in.available() is always 0 and the program gets
                                                                // stuck here due to EOF
      // Read the response from the server
      Object response = in.readObject();
      if (response instanceof Mail responseMail) {
        System.out.println("Received response mail: " + responseMail);
        clientMain.setMailSent(true);
        System.out.println("Received response mail: " + responseMail);
        Platform.runLater(() -> clientMain.addOut(responseMail));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void deleteMail(Mail mail, ClientMain clientMain) {
    try (Socket s = new Socket(host, 8189)) {
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      out.writeObject("delete");
      out.writeObject(clientMain.getUserMail());
      out.writeObject(mail);
      Platform.runLater(() -> clientMain.delete(mail));

    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
