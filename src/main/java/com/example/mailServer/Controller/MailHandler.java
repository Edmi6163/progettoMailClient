package com.example.mailServer.Controller;

import com.example.Transmission.Email;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MailHandler {

  private ObjectOutputStream outputStream;
  private ObjectInputStream inputStream;

  public MailHandler(ObjectInputStream inputStream, ObjectOutputStream outputStream) {
    this.inputStream = inputStream;
    this.outputStream = outputStream;
  }

  public synchronized boolean save(Email mail) {
    try {
      String sender = mail.getSender();
      List<String> receivers = mail.getReceivers();

      System.out.println("[MailHandler] sender: " + mail.getSender());

      File senderDir = new File("./src/main/java/com/example/mailServer/file/" + sender + "/out/");
      senderDir.mkdirs(); // Create directories recursively if they don't exist

      File file = new File(senderDir, mail.getTimestamp() + ".txt");
      System.out.println("[save] file: " + file);

      ObjectOutputStream fileOutputStream = new ObjectOutputStream(new FileOutputStream(file));

      // Convert the email object to a string using toString() and write it to the file
//      String emailContent = mail.toString();
//      fileOutputStream.writeObject(emailContent.getBytes());

      fileOutputStream.writeObject(mail);
      fileOutputStream.close();

      for (String r : receivers) {
        // Create the directory for the receiver just in case it doesn't exist
        File receiverDir = new File("./src/main/java/com/example/mailServer/file/" + r + "/in/");
        System.out.println("[save] receiverDir: " + receiverDir);
        receiverDir.mkdirs();

        file = new File(receiverDir, mail.getTimestamp() + ".txt");

        fileOutputStream = new ObjectOutputStream(new FileOutputStream(file));

        // Write the email content to the file
        fileOutputStream.writeObject(mail);

        fileOutputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }

public synchronized ArrayList<Email> loadOutBox(String user) {

  ArrayList<Email> out = new ArrayList<>();

  File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/out");

  if (dir.exists() && dir.isDirectory()) {
//    try {

      for (File textFile : Objects.requireNonNull(dir.listFiles())) {
        try (ObjectInputStream fileInputStream = new ObjectInputStream(new FileInputStream(textFile))) {
          Email email = (Email) fileInputStream.readObject();
          out.add(email);

          // Send the email object over the socket
//          outputStream.writeObject(email);

          // Receive any acknowledgement or response from the server
//          Object response = inputStream.readObject();
//          System.out.println("[loadOutBox] Server response: " + response);
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }

      // Signal the end of data transmission
      // TODO: reimplement handling the exception client side
//      outputStream.writeObject(null);
//    } catch (IOException e) {
//      e.printStackTrace();
//    }
  }

  return out;
}

  public synchronized ArrayList<Email> loadInBox(String user) {
    ArrayList<Email> allEmails = new ArrayList<>();
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/in");

    if (dir.exists() && dir.isDirectory()) {
      //try {
        for (File textFile : Objects.requireNonNull(dir.listFiles())) {
          try (ObjectInputStream fileInputStream = new ObjectInputStream(new FileInputStream(textFile))) {
            Email email = (Email) fileInputStream.readObject();
            allEmails.add(email);

          } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
          }
        }

        // Signal the end of data transmission
        // TODO: reimplement handling the exception client side
        //outputStream.writeObject(null);

//      } catch (IOException e) {
//        e.printStackTrace();
//      }
    }

    return allEmails;
  }

  /*
    @brief: delete the selected mail in MailContainerController from the file system both in and out folder of the user
    using a regex to remove the brackets from the username
   */
  public synchronized void delete(String user,Email mail) {
//    String userWithoutBracket = user.replace("[", "").replace("]", "");
    try {
      Files.delete(Paths.get(
        "src/main/java/com/example/mailServer/file/" + user + "/in/" + mail.getTimestamp() + ".txt"));
    } catch (Exception e) {
//      e.printStackTrace();
      System.out.println("Can't delete in");
    }

    try {
      Files.delete(Paths.get(
        "src/main/java/com/example/mailServer/file/" + user + "/out/" + mail.getTimestamp() + ".txt"));
    } catch (Exception e) {
//      e.printStackTrace();
      System.out.println("Can't delete out");
    }
  }
}
