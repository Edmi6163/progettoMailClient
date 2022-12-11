package mailServer.Controller;

import javafx.application.Platform;
import mailServer.Model.Mail;
import mailServer.Model.UserList;
import mailServer.ServerMain;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ServerHandler implements Runnable{
  private ServerMain serverMain;
  private Socket incoming;
  private MailHandler mailHandler;

  public ServerHandler(ServerMain serverMain,Socket incoming,MailHandler mailHandler){
    this.serverMain=serverMain;
    this.incoming=incoming;
    this.mailHandler=mailHandler;
  }

  @Override
  public void run(){
    UserList userList=serverMain.getUserList();
    String log = "";
    String user="";
    String max;

    try{
      ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream());
      ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());
      String action =(String)in.readObject();
      //TODO i think this can be replaced with a switch-case
      if(action.equals("all")) {
        user = (String) in.readObject();
        if (userList.userExist(user)) {
          out.writeObject(MailHandler.loadOutBox(user));
          out.writeObject(MailHandler.loadInBox(user));
        } else {
          out.writeObject(null);
          out.writeObject(null);
        }
      } else if (action.equals("inbox")) {
        user = (String) in.readObject();
        max = (String) in.readObject();
        out.writeObject(MailHandler.getUpdatedList(user, max));
      } else if (action.equals("send")) {
        Mail mail = (Mail)in.readObject();
        ArrayList<String> receivers = new ArrayList<>(mail.getReceivers());
        boolean wrongReceiver= false;
        for(String receiver: receivers){
          if(!userList.userExist(receiver)) {
            Mail wrong = new Mail("System", mail.getSender(), 0, "\ncheck mail address, cannot find address" + receiver + mail + "this is an automated message");
            mail.getReceivers().remove(receiver);
          }
          if(wrongReceiver)
            log=mail.getSender()+"email address not found";
          else
            log=mail.getSender()+"sent an email to"+ mail.getReceiversString();

         String finalLog=log;
          Platform.runLater(()->serverMain.addLog(finalLog));
          mail.setIsSent(true);
          Mail toSave = mailHandler.save(mail);
          out.writeObject(toSave);
      }
        in.close();
        out.close();
    }
    } catch (ClassNotFoundException| IOException e){
      e.printStackTrace();
    } finally {
      try {
        incoming.close();
      } catch (IOException e){
        e.printStackTrace();
      }
    }
  }

}
