package com.example.mailServer.Controller;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
import com.example.Transmission.UserModel;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Model.UserService;
import com.example.mailServer.ServerMain;
import com.example.mailServer.Model.Mail;
import com.example.mailServer.Model.UserList;
import javafx.application.Platform;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler implements Runnable {
  private ServerLayoutController logger = new ServerLayoutController();
  private final Socket incoming;

  public UserService userService ;

  private LoggerModel log;
  ObjectOutputStream out;
  ObjectInputStream in;

  public ServerHandler(Socket incoming, LoggerModel log) {
    this.incoming = incoming;
    this.log = log;
    userService = new UserService();
  }

  public UserList getUserList() {
    UserList userList = new UserList();
    userList.addUser("francesco@javamail.it");
    userList.addUser("mauro@javamail.it");
    userList.addUser("something@javamail.it");
    return userList;
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
   UserList userList = this.getUserList();
    assert userList != null;

    out = new ObjectOutputStream(incoming.getOutputStream());
    in = new ObjectInputStream(incoming.getInputStream());

    System.out.println("in.readObject() = " + in.readObject().toString());
    Communication c = (Communication) in.readObject();

    System.out.println("Action registered: " + c.getAction());
    logger.setLog("Action registered: " + c.getAction());
    switch (c.getAction()) {
      case "login" -> handleLoginAction(((UserModel) c.getBody()).getEmail());
      case "all" -> handleAllAction(in, out, userList);
      case "inbox" -> handleInboxAction(in, out);
      case "send" -> handleSendAction(userList, (Email) c.getBody());
      default -> log("Unrecognized action"); // handle unrecognized action
    }
  }

  private void handleLoginAction(String username) throws IOException {
    Set<String> set = userService.getUsernamesFromDirectory(username);
    if(set.isEmpty())
      userService.createUserFolders(username);

    logger.setLog("User " + username + " logged in");

    ArrayList<Email> inbox = MailHandler.loadInBox(username);

    ArrayList<Email> outbox = MailHandler.loadOutBox(username);

    ArrayList<ArrayList<Email>> emails = new ArrayList<>();
    emails.add(inbox);
    emails.add(outbox);
    LoginRes responseBody = new LoginRes(emails);
    Communication c = new Communication("loginRes", responseBody);
    out.writeObject(c);
  }


  private void handleAllAction(ObjectInputStream in, ObjectOutputStream out, UserList userList)
      throws IOException, ClassNotFoundException {
    String user = (String) in.readObject();
    if (userList.userExist(user)) {
      out.writeObject(MailHandler.loadOutBox(user));
      out.writeObject(MailHandler.loadInBox(user));
    } else {
      out.writeObject(null);
      out.writeObject(null);
    }
  }

  private void handleInboxAction(ObjectInputStream in, ObjectOutputStream out)
      throws IOException, ClassNotFoundException {
    String user = (String) in.readObject();
    String max = (String) in.readObject();
    out.writeObject(MailHandler.getUpdatedList(user, max));
  }


  private void handleSendAction(UserList userList, Email mail) throws IOException, ClassNotFoundException {
    System.out.println("***handleSendAction***");
    // log("***handleSendAction***");

    System.out.println("[handle send action] mail arrived to server:\n " + mail);
    System.out.println("[handle send action] receivers: " + mail.getReceivers());
    Set<String> receivers = new HashSet<>(mail.getReceivers());
    for (String receiver : receivers) {
      if (!userList.userExist(receiver)) {
        Mail wrong = new Mail("System",
          "Wrong email address", mail.getSender(),
          0,
          "It wasn't possible to send this email to " + receiver + ", wrong email  address + " +
            "\n***********************\n" + mail
            + "\n***********************\nTHIS IS AN AUTOMATED MESSAGE, PLEASE, DO NOT REPLY.");
        mail.getReceivers().remove(receiver);
      }
//   log(mail.getSender() + " sent an email to " + mail.getReceiversString());
      logger.setLog(mail.getSender() + " sent an email to " + mail.getReceivers());
      System.out.println(mail.getSender() + " sent an email to " + mail.getReceivers());
      mail.setBin(true);
      out.writeObject(mail);
      MailHandler.save(mail);
//SI SPACCA PERCHÈ MANCA IL WRITEOBJECT DEL SERVER

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
    Platform.runLater(() -> logger.setLog(message));
  }
}
