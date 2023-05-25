package com.example.mailClient.Controller;
import com.example.mailClient.Model.Mail;
import com.example.mailServer.Model.LoggerModel;
import javafx.application.Platform;
import com.example.mailClient.ClientMain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class ClientController implements Serializable {
  private ClientMain clientMain;
  private boolean serverStatus = false;
  private Socket socket;
  private static final String host = "127.0.1.1";

  private static LoggerModel logger;

  public ClientController(ClientMain clientMain){
    this.clientMain=clientMain;
    logger = new LoggerModel();
  }


  private void connectToSocket() throws IOException {
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
 public void requestInbox() {
   try {
     try (Socket s = new Socket(host, 8189)) {
       System.out.println("Socket opened"); //TODO debug
       ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
       ObjectInputStream in = new ObjectInputStream(s.getInputStream());
       System.out.println("receiving data from server :)");
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
          if(s == null){
            System.out.println("[Client Controller] socket is null");
            return false;
          }
          System.out.println("[Client Controller] socket opened :)"); //TODO dubug
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
   public static void sendMail(Mail mail, ClientMain clientMain){
    clientMain.setMailSent(false);
    try(Socket s = new Socket(host,8189)) {
      ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(s.getInputStream());
      out.writeObject("send");
      System.out.println("mail is being sent from " + mail.getSender() + "to: "+ mail.getReceiversString()  + mail.getSubject());
      logger.setLog("sent an email: " + mail.getReceiversString() + mail.getSubject());
      out.writeObject(mail);
      if(in.available() > 0) {
        Mail m = (Mail) in.readObject();
        if (m != null) {
          clientMain.setMailSent(true);
          Platform.runLater(() -> clientMain.addOut(m));
        }
      }
    } catch (Exception e){
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
