package com.example.mailClient.Controller;
import com.example.mailServer.Controller.ServerLayoutController;
import com.example.mailServer.Model.Mail;
import javafx.application.Platform;
import com.example.mailClient.ClientMain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.*;
import java.util.List;

public class ClientController implements Serializable {
  private ClientMain clientMain;
  private transient boolean serverStatus = false;
  private Socket socket;
  private static final String host = "127.0.1.1";


//  private static LoggerModel logger;
  private static ServerLayoutController logger;

  public ClientController(ClientMain clientMain){
    this.clientMain=clientMain;
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
  public String getMaxTimeStamp(List<Mail> inbox){
    long maxTimeStamp= 0;
    for(Mail m:inbox){
      if(m.getMillis()>maxTimeStamp){
        maxTimeStamp = m.getMillis();
      }
    }
    return " "+ maxTimeStamp;
  }

  public void noMailPopUp() {
    Platform.runLater(() -> clientMain.noMailPopUp());
  }
 public void requestInbox() {
   try {
     try (Socket s = new Socket(host, 8189)) {
       System.out.println("Socket opened"); //TODO debug
       ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
       ObjectInputStream in = new ObjectInputStream(s.getInputStream());
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

    public boolean requestAll(){
      try {
        try(Socket s = new Socket(host,8189)){
          System.out.println("[Client Controller] socket opened :)"+ s); //TODO debug
          ObjectInputStream in= new ObjectInputStream(s.getInputStream());
          ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
          out.writeObject("all");
          out.writeObject(clientMain.getUserMail());
          List<Mail> resIn = (List<Mail>) in.readObject();
          List<Mail> resOut = (List<Mail>) in.readObject();
          in.close();
          out.close();
          if(resIn != null && resOut != null){
            if(resIn.size() >0) {
              clientMain.addInbox(resIn);
              clientMain.addOutbox(resOut);
            }
            } else {
              return false;
          }
        } catch (ClassNotFoundException e){
          e.printStackTrace();
          return false;
        }
      } catch (IOException e){
        e.printStackTrace();
        return false;
      }
      return true;
   }
 /*  public static void sendMail(Mail mail, ClientMain clientMain){
    clientMain.setMailSent(false);
    try(Socket s = new Socket(host,8189)) {
      //serialization of the mail object
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      System.out.println("in mails: " + clientMain.getInbox());
      System.out.println("out mails: " + clientMain.getOutbox()); //TODO remove this
      //print writeObject("send") in server log
      logger.setLog("out.writeObject(send)");
      out.writeObject("send"); //this is the command for the server to send the mail,see ServerHandler class
      out.writeObject(mail);
      out.flush();
      System.out.println("[send mail CC] mail written to server \n" + mail);
      System.out.println("in.available() = " + in.available());
      if(in.available() > 0) {
        System.out.println("in.available() > 0" + in.available()); //FIXME in.available = 0
        //TODO debug
//        Mail m = (Mail) in.readObject();
        if (mail != null) {
          clientMain.setMailSent(true);
          System.out.println("mail before addOut: " + mail);
          Platform.runLater(() -> clientMain.addOut(mail));
        }
      }
    } catch (Exception e){
      e.printStackTrace();
    }
   }*/

  public static void sendMail(Mail mail, ClientMain clientMain) {
    clientMain.setMailSent(false);
    try (Socket s = new Socket(host, 8189)) {
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      out.writeObject("send");
      out.writeObject(mail);
      out.flush();
      System.out.println("[send mail CC] mail written to server\n" + mail);

      System.out.println("in.available() = " + in.available());
      // Read the response from the server
      Object response = in.readObject();
      if(response == null){
        System.out.println("response is null");
      } else {
        System.out.println("response is not null");
      }
      if (response instanceof Mail) {
        Mail responseMail = (Mail) response;
        System.out.println("Received response mail: " + responseMail);
        clientMain.setMailSent(true);
        System.out.println("Received response mail: " + responseMail);
        Platform.runLater(() -> clientMain.addOut(responseMail));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static  void deleteMail(Mail mail,ClientMain clientMain){
    try (Socket s = new Socket(host,8189)){
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      out.writeObject("delete");
      out.writeObject(clientMain.getUserMail());
      out.writeObject(mail);
      Platform.runLater(()->clientMain.delete(mail));

    } catch (Exception e){
      e.printStackTrace();
    }
   }
 }
