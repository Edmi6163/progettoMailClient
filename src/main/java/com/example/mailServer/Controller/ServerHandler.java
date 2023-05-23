/*
package com.example.mailServer.Controller;

import com.example.mailServer.ServerMain;
import javafx.application.Platform;
import com.example.mailServer.Model.Mail;
import com.example.mailServer.Model.UserList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler implements Runnable{
  private ServerMain serverMain;
  private Socket incoming;
  private final MailHandler mailHandler;

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

    System.out.println("ServerHandler started");
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
        System.out.println("mail is : "+mail.getClass());
        Set<String> receivers = new HashSet<>(mail.getReceivers());
        boolean wrongReceiver= false;
        for(String receiver: receivers){
          if(!userList.userExist(receiver)) {
            Mail wrong = new Mail("System",
              "Wrong email address",mail.getSender(),
              0,
              "It wasn't possible to send this email to "+receiver+", wrong email address." +
                "\n***********************\n"+mail+"\n***********************\nTHIS IS AN AUTOMATED MESSAGE, PLEASE, DO NOT REPLY.");
            mail.getReceivers().remove(receiver);
          }
          if(wrongReceiver)
            log=mail.getSender()+"email address not found";
          else
            log=mail.getSender()+"sent an email to"+ mail.getReceiversString();

         String finalLog=log;
          Platform.runLater(()->serverMain.addLog(finalLog));
          mail.setIsSent(true);
          Mail toSave = MailHandler.save(mail);
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
*/
package com.example.mailServer.Controller;

import com.example.mailServer.ServerMain;
import com.example.mailServer.Model.Mail;
import com.example.mailServer.Model.UserList;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler implements Runnable {
  private final ServerMain serverMain;
  private final Socket incoming;
  private final MailHandler mailHandler;

  public ServerHandler(ServerMain serverMain, Socket incoming, MailHandler mailHandler) {
    this.serverMain = serverMain;
    this.incoming = incoming;
    this.mailHandler = mailHandler;
  }

  @Override
  public void run() {
    try {
      handleRequest();
    } catch (IOException | ClassNotFoundException e) {
      e.printStackTrace();
    } finally {
      closeConnection();
    }
  }

  private void handleRequest() throws IOException, ClassNotFoundException {
    UserList userList = serverMain.getUserList();
    ObjectOutputStream out = new ObjectOutputStream(incoming.getOutputStream());
    ObjectInputStream in = new ObjectInputStream(incoming.getInputStream());

    String action = (String) in.readObject();
    switch (action) {
      case "all":
        handleAllAction(in, out, userList);
        break;
      case "inbox":
        handleInboxAction(in, out);
        break;
      case "send":
        handleSendAction(in, out, userList);
        break;
      default:
        // Handle unrecognized action
        break;
    }
  }

  private void handleAllAction(ObjectInputStream in, ObjectOutputStream out, UserList userList) throws IOException, ClassNotFoundException {
    String user = (String) in.readObject();
    if (userList.userExist(user)) {
      out.writeObject(MailHandler.loadOutBox(user));
      out.writeObject(MailHandler.loadInBox(user));
    } else {
      out.writeObject(null);
      out.writeObject(null);
    }
  }

  private void handleInboxAction(ObjectInputStream in, ObjectOutputStream out) throws IOException, ClassNotFoundException {
    String user = (String) in.readObject();
    String max = (String) in.readObject();
    out.writeObject(MailHandler.getUpdatedList(user, max));
  }

  private void handleSendAction(ObjectInputStream in, ObjectOutputStream out, UserList userList) throws IOException, ClassNotFoundException {
    Mail mail = (Mail) in.readObject();
    Set<String> receivers = new HashSet<>(mail.getReceivers());
    boolean wrongReceiver = false;
    for (String receiver : receivers) {
      if (!userList.userExist(receiver)) {
        Mail wrong = new Mail("System",
          "Wrong email address", mail.getSender(),
          0,
          "It wasn't possible to send this email to " + receiver + ", wrong email address." +
            "\n***********************\n" + mail + "\n***********************\nTHIS IS AN AUTOMATED MESSAGE, PLEASE, DO NOT REPLY.");
        mail.getReceivers().remove(receiver);
      }
      if (wrongReceiver) {
        log(mail.getSender() + " email address not found");
      } else {
        log(mail.getSender() + " sent an email to " + mail.getReceiversString());
      }

      mail.setIsSent(true);
      Mail toSave = MailHandler.save(mail);
      out.writeObject(toSave);
    }
  }

  private void closeConnection() {
    try {
      incoming.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void log(String message) {
    Platform.runLater(() -> serverMain.addLog(message));
  }
}
