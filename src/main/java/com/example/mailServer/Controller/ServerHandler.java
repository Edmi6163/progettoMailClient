package com.example.mailServer.Controller;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
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

  /*
   * @brief: method run, is the first things called, so here in base of the
   * request we call the right method
   */
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
          System.out.println("Body registered: " + c.getBody());
          System.out.println("Action registered: " + c.getAction());
          log.setLog("Action registered: " + c.getAction());
          switch (c.getAction()) {
            case "login" -> handleLoginAction((String) c.getBody());
            case "inbox" -> handleInboxAction((String) c.getBody());
            case "send" -> handleSendAction(userList, (Email) c.getBody());
            case "delete" -> handleDeleteAction(c.getBody().toString(), (Email) c.getBody());
            case "outbox" -> handleOutboxAction((String) c.getBody());
            default -> log.setLog("Unrecognized action");
          }

        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }

      } finally {
        log.setLog("Client disconnected");
        incoming.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void handleDeleteAction(String user, Email body) {
    try {
      System.out.println("***handleDeleteAction***");

      MailHandler.delete(user,body);

      Communication response = new Communication("delete_ok", body);

      out.writeObject(response);
      out.flush();

    } catch (Exception e) {
      e.printStackTrace();
    }

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

  private void handleInboxAction(String body) throws IOException, ClassNotFoundException {
    System.out.println("[handleInboxAction] body arrived is: " + body);
    Communication response = new Communication("inbox",MailHandler.loadInBox(body)); //FIXME here we should load the inbox note body is the username
    out.writeObject(response);
  }

  private void handleOutboxAction(String body) throws IOException, ClassNotFoundException {
    System.out.println("[handleOutboxAction] body arrived is: " + body);
    Communication response = new Communication("outbox",MailHandler.loadOutBox(body)); //FIXME here we should load the outbox, note body is the username

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

      if (!MailHandler.save(mail)) {
        Communication response = new Communication("send_not_ok", mail);
        out.writeObject(response);
        return;
      }

      Communication response = new Communication("send_ok", mail);
      out.writeObject(response);
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
