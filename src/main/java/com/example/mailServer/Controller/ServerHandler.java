package com.example.mailServer.Controller;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.InboxRequest;
import com.example.Transmission.LoginRes;
import com.example.Transmission.UserModel;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Model.UserService;
import com.example.mailServer.Model.Mail;
import com.example.mailServer.Model.UserList;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ServerHandler implements Runnable {
  private Socket incoming;
  public UserService userService;

  LoggerModel log;
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
      try {
        UserList userList = this.getUserList();
        assert userList != null;

        in = new ObjectInputStream(incoming.getInputStream());
        out = new ObjectOutputStream(incoming.getOutputStream());

        try {
          Communication c = (Communication) in.readObject();
          // System.out.println("in.readObject() = " + in.readObject().toString());
          System.out.println("Action registered: " + c.getAction());
          log.setLog("Action registered: " + c.getAction());
          // log.setLog(c.getBody().toString());
          switch (c.getAction()) {
            case "login" -> handleLoginAction((String) c.getBody());
            case "all" -> handleAllAction(in, out, userList);
            case "inbox" -> handleInboxAction((InboxRequest) c.getBody());
            case "send" -> handleSendAction(userList, (Email) c.getBody());
case "delete" -> handleDeleteAction(userList, (Email) c.getBody());

            default -> log.setLog("Unrecognized action");
          }

        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }

      } finally {
        // System.out.println("FINITO");
        log.setLog("Client disconnected");
        incoming.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleDeleteAction(UserList userList, Email body) {
    System.out.println("handleDeleteAction");
    String username = body.getSender();
    ArrayList<String> receiver = body.getReceivers();
    String subject = body.getSubject();
    LocalDateTime date = LocalDateTime.now();
    String content = body.getText();
    Email mail = new Email(username, receiver, subject,content);
    MailHandler.delete(mail);
  }

  private void handleLoginAction(String username) throws IOException {
    System.out.println("handleLoginAction");

    Set<String> set = userService.getUsernamesFromDirectory(username);
    if (set.isEmpty())
      userService.createUserFolders(username);

    log.setLog("User " + username + " logged in");

    ArrayList<Email> inbox = MailHandler.loadInBox(username);

    log.setLog(username + "'s inbox loaded, size is " + inbox.size());
    ArrayList<Email> outbox = MailHandler.loadOutBox(username);
    log.setLog(username + "'s outbox loaded, size is " + outbox.size());
    ArrayList<ArrayList<Email>> emails = new ArrayList<>();
    emails.add(inbox);
    emails.add(outbox);

    LoginRes responseBody = new LoginRes(emails);
    Communication c = new Communication("loginRes", responseBody);

    out.writeObject(c);
    out.flush();
    System.out.println("Communication c: " + c.getAction() + " " + c.getBody().toString());
  }

  private void handleAllAction(ObjectInputStream in, ObjectOutputStream out, UserList userList)
      throws IOException, ClassNotFoundException {
    System.out.println("***handleAllAction***");
    String user = (String) in.readObject();
    if (userList.userExist(user)) {
      out.writeObject(MailHandler.loadOutBox(user));
      out.writeObject(MailHandler.loadInBox(user));
      // System.out.println("outbox loaded: " + MailHandler.loadOutBox(user));
    } else {
      out.writeObject(null);
      out.writeObject(null);
    }
  }

  private void handleInboxAction(InboxRequest body) throws IOException, ClassNotFoundException {
    System.out.println("***handleInboxAction***");
    out.writeObject(MailHandler.getUpdatedList(body.getEmail(), body.getMax()));
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
            LocalDateTime.now(),
            "It wasn't possible to send this email to " + receiver + ", wrong email  address + " +
                "\n***********************\n" + mail
                + "\n***********************\nTHIS IS AN AUTOMATED MESSAGE, PLEASE, DO NOT REPLY.");
        mail.getReceivers().remove(receiver);
      }
      // log(mail.getSender() + " sent an email to " + mail.getReceiversString());
      log.setLog(mail.getSender() + " sent an email to " + mail.getReceivers());
      System.out.println(mail.getSender() + " sent an email to " + mail.getReceivers());
      mail.setBin(true);
      MailHandler.save(mail);
      out.writeObject(mail);
      // SI SPACCA PERCHÈ MANCA IL WRITEOBJECT DEL SERVER

    }
  }

  private synchronized void closeConnection() {
    try {
      log.setLog("closing connection");
      incoming.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
