package com.example.mailServer.Controller;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Model.UserService;
import com.example.mailServer.Model.Mail;
import com.example.mailServer.Model.UserList;

import java.io.EOFException;
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
  private MailHandler mailHandler;

  LoggerModel log;
  ObjectOutputStream outputStream;
  ObjectInputStream inputStream;

  public ServerHandler(Socket incoming, LoggerModel log) {
    this.incoming = incoming;
    this.log = log;
    userService = new UserService();
    try {
      inputStream = new ObjectInputStream(incoming.getInputStream());
      outputStream = new ObjectOutputStream(incoming.getOutputStream());
      this.mailHandler = new MailHandler(inputStream, outputStream);
    } catch (IOException xcpt) {
      xcpt.printStackTrace();
    }
  }

  public UserList getUserList() {
    UserList userList = new UserList();
    userList.addUser("francesco@javamail.it");
    userList.addUser("paolo@javamail.it");
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

        try {
          Communication c = (Communication) inputStream.readObject();
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
        } catch (EOFException xcpt) {
          System.out.println("NULL - end of requests");
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

      mailHandler.delete(user,body);

      Communication response = new Communication("delete_ok", body);

      outputStream.writeObject(response);
      outputStream.flush();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void handleLoginAction(String username) throws IOException, ClassNotFoundException {
    System.out.println("handleLoginAction");

    Set<String> set = userService.getUsernamesFromDirectory(username);
    if (set.isEmpty())
      userService.createUserFolders(username);

    log.setLog("User " + username + " logged in");

//    ArrayList<Email> inbox = MailHandler.loadInBox(username,incoming);
//    log.setLog(username + "'s inbox loaded, size is " + inbox.size());
//    ArrayList<Email> outbox = MailHandler.loadOutBox(username,incoming);
//    log.setLog(username + "'s outbox loaded, size is " + outbox.size());
//    ArrayList<ArrayList<Email>> emails = new ArrayList<>();
//    emails.add(inbox);
//    emails.add(outbox);

//    LoginRes responseBody = new LoginRes(emails);
//    Communication c = new Communication("loginRes", responseBody);

    Communication c = new Communication("loginRes", new LoginRes());

    outputStream.writeObject(c);
//    out.flush();
//    out.reset();
    System.out.println("Communication c: " + c.getAction() + " " + c.getBody().toString());
  }

  /*private void handleInboxAction(String body) throws IOException, ClassNotFoundException {
    System.out.println("[handleInboxAction] body arrived is: " + body);
    Communication response = new Communication("inbox",MailHandler.loadInBox(body)); //FIXME here we should load the inbox note body is the username, but response is empty []
    out.writeObject(response);
  }*/

  private void handleInboxAction(String username) throws IOException, ClassNotFoundException {
    System.out.println("[handleInboxAction] username received: " + username);
    ArrayList<Email> inbox = mailHandler.loadInBox(username);
    //print all the content in inbox
    for (Email email : inbox) {
      System.out.println("inbox contains: "  + email);
    } //FIXME this isn't printed because in loadinbox it raise the exception
    Communication response = new Communication("inbox", inbox);
    outputStream.writeObject(response);
  }

  private void handleOutboxAction(String username) throws IOException, ClassNotFoundException {
    System.out.println("[handleOutboxAction] body arrived is: " + username);
    ArrayList<Email> outbox = mailHandler.loadOutBox(username);
    Communication response = new Communication("outbox",outbox); //FIXME here we should load the outbox, note body is the username
    outputStream.writeObject(response);
  }

  private void handleSendAction(UserList userList, Email mail) throws IOException, ClassNotFoundException {
    System.out.println("***handleSendAction***");

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
      //FIXME: mail.getReceivers() is empty after for loop
      mail.setBin(true);

      if (!mailHandler.save(mail)) {
        Communication response = new Communication("send_not_ok", mail);
        outputStream.writeObject(response);
        return;
      }

      Communication response = new Communication("send_ok", mail);
      outputStream.writeObject(response);
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
