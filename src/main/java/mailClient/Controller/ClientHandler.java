package mailClient.Controller;
import javafx.application.Platform;
import mailClient.ClientMain;
import mailServer.Model.Mail;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class ClientHandler {
  private ClientMain clientMain;

  private static final String host = "192.168.1377.1";

  public ClientHandler(ClientMain clientMain){
    this.clientMain=clientMain;
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
       System.out.println("Socket opened");
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
          System.out.println("socket opened :)");
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
      out.writeObject(mail);
      Mail m = (Mail)in.readObject();
      if(m!= null){
        clientMain.setMailSent(true);
        Platform.runLater(()->clientMain.addOutbox(m));
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
