package com.example.mailServer.Controller;

import com.example.Transmission.Communication;
import com.example.Transmission.Email;
import com.example.Transmission.LoginRes;
import com.example.mailServer.Model.LoggerModel;
import com.example.mailServer.Model.UserService;
import com.example.mailServer.Model.UserList;
import javafx.util.Pair;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;

public class ServerHandler implements Runnable {
  private Socket incoming;
  public UserService userService;
  private MailHandler mailHandler;


  LoggerModel log;
  ObjectOutputStream outputStream;
  ObjectInputStream inputStream;


  UserList userList = new UserList();
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
//    System.out.println("serverhandler constructor called");
  }

  public UserList getUserList() {
    File userFolder = new File("src/main/java/com/example/mailServer/file");
    File[] userFolders = userFolder.listFiles();

    if (userFolders != null) {
      for (File folder : userFolders) {
        if (folder.isDirectory()) {
          String folderName = folder.getName();
          userList.addUser(folderName);
        }
      }
    }

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
        for(String user : userList.getUsers()){
          log.setLog("user list is " + user);
        }
        assert userList != null;

        try {
          Communication c = (Communication) inputStream.readObject();
//          System.out.println("Body registered: " + c.getBody());
//          System.out.println("Action registered: " + c.getAction());
          log.setLog("Action registered: " + c.getAction());
          switch (c.getAction()) {
            case "login" -> handleLoginAction((String) c.getBody());
            case "inbox" -> handleInboxAction((String)((Pair) c.getBody()).getKey(), (List<Email>) ((Pair) c.getBody()).getValue());
            case "send" -> handleSendAction(userList, (Email) c.getBody());
            case "delete" -> handleDeleteAction((String) ((Pair) c.getBody()).getKey(), (Email) ((Pair) c.getBody()).getValue());
            case "outbox" -> handleOutboxAction((String)((Pair) c.getBody()).getKey(), (List<Email>) ((Pair) c.getBody()).getValue());
            default -> log.setLog("Unrecognized action");
          }

        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        } catch (EOFException xcpt) {
//          System.out.println("NULL - end of requests");
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
//      System.out.println("***handleDeleteAction***");

      mailHandler.delete(user,body);

      Communication response = new Communication("delete_ok", body);

      outputStream.writeObject(response);
      outputStream.flush();

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  private void handleLoginAction(String username) throws IOException, ClassNotFoundException {
//    System.out.println("handleLoginAction");
    UserList userList = getUserList();
    if(!userList.userExist(username)){
      userService.createUserFolders(username);
      userList.addUser(username);
    }
    Set<String> set = userService.getUsernamesFromDirectory(username);


    log.setLog("User " + username + " logged in");


    Communication c = new Communication("loginRes", new LoginRes());

    outputStream.writeObject(c);

//    System.out.println("Communication c: " + c.getAction() + " " + c.getBody().toString());
  }

  private void handleInboxAction(String username, List<Email> userInbox) throws IOException, ClassNotFoundException {
//    System.out.println("[handleInboxAction] username received: " + username);
    ArrayList<Email> loadedInbox = mailHandler.loadInBox(username);
    ArrayList<Email> newEmails = new ArrayList<>();
    for (Email email : loadedInbox) {
//      System.out.println("read inbox contains: "  + email);
      if(!userInbox.contains(email)) {
        newEmails.add(email);
//        System.out.println("email sent: " + email);
      }
    }
    Communication response = new Communication("inbox", newEmails);
    outputStream.writeObject(response);
  }

  private void handleOutboxAction(String username, List<Email> userOutbox) throws IOException, ClassNotFoundException {
//    System.out.println("[handleOutboxAction] body arrived is: " + username);
    ArrayList<Email> loadedOutbox = mailHandler.loadOutBox(username);
    ArrayList<Email> newEmails = new ArrayList<>();
    for (Email email : loadedOutbox) {
//      System.out.println("outbox contains: "  + email);
      if(!userOutbox.contains(email)) {
        newEmails.add(email);
//        System.out.println("email sent: " + email);
      }
    }
    Communication response = new Communication("outbox",newEmails);
    outputStream.writeObject(response);
  }

  private void handleSendAction(UserList userList, Email mail) throws IOException, ClassNotFoundException {
//    System.out.println("***handleSendAction***");

//    System.out.println("[handle send action] mail arrived to server:\n " + mail);
//    System.out.println("[handle send action] receivers: " + mail.getReceivers());
    Set<String> receivers = new HashSet<>(mail.getReceivers());
    for (String receiver : receivers) {
      if (!userList.userExist(receiver)) {
        Communication response = new Communication("send_not_ok", mail);
        outputStream.writeObject(response);
        return;
      }
      log.setLog(mail.getSender() + " sent an email to " + mail.getReceivers());
//      System.out.println(mail.getSender() + " sent an email to " + mail.getReceivers());
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
