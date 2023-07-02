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
  public synchronized static Email save(Email mail) {
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

      ObjectOutputStream output = new ObjectOutputStream(fileOutputStream);

      newMail = new Email(mail.getSender(), mail.getReceivers(), mail.getSubject(), mail.getText());
      System.out.println("[save] newMail: " + newMail);
      newMail.setBin(false);
      output.writeObject(newMail);
      output.close();
      fileOutputStream.close();

      for (String r : receivers) {
        // Create the directory for the receiver just in case it doesn't exist
        File receiverDir = new File("src/main/java/com/example/mailServer/file/" + r + "/in/");
        System.out.println("[save] receiverDir: " + receiverDir);
        receiverDir.mkdirs();

        file = new File(receiverDir, millis + ".txt");

        fileOutputStream = new FileOutputStream(file);

        output = new ObjectOutputStream(fileOutputStream);

        newMail.setBin(false);
        output.writeObject(newMail);
        output.close();
        fileOutputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return newMail;
  }

  /*public synchronized static List<Email> getUpdatedList(String user, String max) {
    List<Email> updatedList = new ArrayList<>();
    max = max + ".txt";
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in/");
    ObjectOutputStream output = null;
    FileOutputStream files = null;

    for (File f : Objects.requireNonNull(dir.listFiles())) {
      if (f.getName().compareTo(max) > 0) {
        try {
          files = new FileOutputStream(f);
          output = new ObjectOutputStream(files);
          output.close();
          files.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return updatedList;
  }
*/
  public synchronized static List<Email> getUpdatedList(String user, String max) {
    List<Email> updatedList = new ArrayList<>();
    max = max + ".txt";
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in/");
    ObjectOutputStream output = null;
    FileOutputStream files = null;

    for (File f : Objects.requireNonNull(dir.listFiles())) {
      if (f.getName().compareTo(max) > 0) {
        try {
          files = new FileOutputStream(f);
          output = new ObjectOutputStream(files);

          // Assuming the file contains Email data that can be read and deserialized
          Email email = null;
          try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(f))) {
            email = (Email) input.readObject();
          } catch (ClassNotFoundException e) {
            e.printStackTrace();
          }

          // If the email object was successfully deserialized, add it to the updatedList
          if (email != null) {
            updatedList.add(email);
          }

          output.close();
          files.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return updatedList;
  }


  public synchronized static ArrayList<Email> loadOutBox(String user) {
    System.out.println("[loadOutBox] user: " + user);
    ArrayList<Email> out = new ArrayList<>();
    try {
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "out");
      ObjectInputStream output = null;
      FileInputStream files = null;
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

  public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] user: " + user);
    ArrayList<Email> inbox = new ArrayList<>();
    try {
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");
      ObjectInputStream input = null;
      FileInputStream file = null;
      for (File f : Objects.requireNonNull(dir.listFiles())) {
        file = new FileInputStream(f);
        input = new ObjectInputStream(file);
        inbox.add((Email) input.readObject());
        input.close();
        file.close();
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

  public static synchronized void delete(Email mail) {
    try {
        Files.delete(Paths.get("src/main/java/com/example/mailServer/file/" + mail.getReceivers() + "/out/" + mail.getTimestamp() + ".txt"));
        Files.delete(Paths.get("src/main/java/com/example/mailServer/file/" + mail.getSender() + "/in/" + mail.getTimestamp() + ".txt"));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
