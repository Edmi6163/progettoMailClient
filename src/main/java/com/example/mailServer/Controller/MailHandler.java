package com.example.mailServer.Controller;

import com.example.Transmission.Email;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MailHandler {
  public synchronized static boolean save(Email mail) {
    Email newMail = null;
    try {
      Date date = new Date();
      long millis = date.getTime();
      String sender = mail.getSender();
      List<String> receivers = mail.getReceivers();

      System.out.println("[MailHandler] sender: " + mail.getSender());

      File senderDir = new File("./src/main/java/com/example/mailServer/file/" + sender + "/out/");
      senderDir.mkdirs(); // Create directories recursively if they don't exist

      File file = new File(senderDir, millis + ".txt");
      System.out.println("[save] file: " + file);

      FileOutputStream fileOutputStream = new FileOutputStream(file);
      System.out.println("[save] fileOutputStream: " + fileOutputStream);

      newMail = new Email(mail.getSender(), mail.getReceivers(), mail.getSubject(), mail.getText());
      System.out.println("[save] newMail: " + newMail);

      fileOutputStream.close();

      for (String r : receivers) {
        // Create the directory for the receiver just in case it doesn't exist
        File receiverDir = new File("src/main/java/com/example/mailServer/file/" + r + "/in/");
        System.out.println("[save] receiverDir: " + receiverDir);
        receiverDir.mkdirs();

        file = new File(receiverDir, millis + ".txt");

        fileOutputStream = new FileOutputStream(file);

        fileOutputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }





  public synchronized static ArrayList<Email> loadOutBox(String user) {
    System.out.println("[loadOutBox] user: " + user);
    ArrayList<Email> out = new ArrayList<>();
    ObjectInputStream output = null;
    FileInputStream files = null;
    System.out.println("[loadOutBox] already raised exc");
    try {
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "out");
      for (File f : Objects.requireNonNull(dir.listFiles())) {
        files = new FileInputStream(f);
        output = new ObjectInputStream(files);
        out.add((Email) output.readObject());
        output.close();
        files.close();
      }
      if (files != null) {
        output.close();
        files.close();
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return out;
  }

/*
  public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] user: " + user);
    ArrayList<Email> inbox = new ArrayList<>();
    try {
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");
      ObjectInputStream input = null;
      FileInputStream file = null;
      */
/*for (File f : Objects.requireNonNull(dir.listFiles())) {
        file = new FileInputStream(f);
        input = new ObjectInputStream(file);
        inbox.add((Email) input.readObject());
        input.close();
        file.close();
      }*//*

      for (File f : Objects.requireNonNull(dir.listFiles())) {
        try (FileInputStream file = new FileInputStream(f);
             ObjectInputStream input = new ObjectInputStream(file)) {
          inbox.add((Email) input.readObject());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      if (file != null) {
        input.close();
        file.close();
      }

    } catch (Exception e) {

      e.printStackTrace();
    }
    return inbox;
  }
*/

  /*
  FIXME inbox related problems
   */
  public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] loading " + user + "'s inbox");
    ArrayList<Email> inbox = new ArrayList<>();
    try {
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");
      for (File f : Objects.requireNonNull(dir.listFiles())) {
        try (FileInputStream file = new FileInputStream(f);
             ObjectInputStream input = new ObjectInputStream(file)) {
          inbox.add((Email)input.readObject());
        } catch (ClassNotFoundException | IOException e) {
          e.printStackTrace();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return inbox;
  }


  /*
    @brief: delete the selected mail in MailContainerController from the file system both in and out folder of the user
    using a regex to remove the brackets from the username
   */
  public static synchronized void delete(String user,Email mail) {
    System.out.println("user arrive as: " + user);

    String userWithoutBracket = user.replace("\\[|\\]", "");
    try {
      Files.delete(Paths.get(
          "src/main/java/com/example/mailServer/file/" + userWithoutBracket + "/out/" + mail.getTimestamp() + ".txt"));
      Files.delete(Paths.get(
          "src/main/java/com/example/mailServer/file/" + userWithoutBracket + "/in/" + mail.getTimestamp() + ".txt"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
