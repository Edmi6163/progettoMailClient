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
  private final ServerMain mail;
  private ServerLayoutController logger = new ServerLayoutController();
  private final Socket incoming;

  ObjectOutputStream out;
  ObjectInputStream in;

  public ServerHandler(ServerMain serverMain, Socket incoming, MailHandler mailHandler) {
    this.mail = serverMain;
    this.incoming = incoming;
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
    UserList userList = mail.getUserList();
    assert userList != null;

     out = new ObjectOutputStream(incoming.getOutputStream());
     in = new ObjectInputStream(incoming.getInputStream());

    String action = in.readObject().toString();
    System.out.println("Action: " + action);
    switch (action) {
      case "all" -> handleAllAction(in, out, userList);
      case "inbox" -> handleInboxAction(in, out);
      case "send" -> handleSendAction(in, out, userList);
      default -> log("Unrecognized action"); //handle unrecognized action
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
    System.out.println("***handleSendAction***");
    log("***handleSendAction***");

    Mail mail = (Mail) in.readObject(); //FIXME here the program that mail aren't sent
    System.out.println("[handle send action] mail arrived to server:\n " + mail);
    System.out.println("[handle send action] receivers: " + mail.getReceiversString());
    Set<String> receivers = new HashSet<>(mail.getReceivers());
    for (String receiver : receivers) {
      if (!userList.userExist(receiver)) {
        Mail wrong = new Mail("System",
          "Wrong email address", mail.getSender(),
          0,
          "It wasn't possible to send this email to " + receiver + ", wrong email address." +
            "\n***********************\n" + mail + "\n***********************\nTHIS IS AN AUTOMATED MESSAGE, PLEASE, DO NOT REPLY.");
        mail.getReceivers().remove(receiver);
      }

      log(mail.getSender() + " sent an email to " + mail.getReceiversString());
      logger.setLog(mail.getSender() + " sent an email to " + mail.getReceiversString());
      System.out.println(mail.getSender() + " sent an email to " + mail.getReceiversString());

      mail.setIsSent(true);
      Mail toSave = MailHandler.save(mail);
      out.writeObject(toSave);
    }
  }

  private synchronized void closeConnection() {
    try {
      logger.setLog("closing connection");
      incoming.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void log(String message) {
    Platform.runLater(() -> mail.addLog(message));
  }
}
