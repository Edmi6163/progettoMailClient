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

      // Convert the email object to a string using toString() and write it to the file
      String emailContent = mail.toString();
      fileOutputStream.write(emailContent.getBytes());

      fileOutputStream.close();

      for (String r : receivers) {
        // Create the directory for the receiver just in case it doesn't exist
        File receiverDir = new File("src/main/java/com/example/mailServer/file/" + r + "/in/");
        System.out.println("[save] receiverDir: " + receiverDir);
        receiverDir.mkdirs();

        file = new File(receiverDir, millis + ".txt");

        fileOutputStream = new FileOutputStream(file);

        // Write the email content to the file
        fileOutputStream.write(emailContent.getBytes());

        fileOutputStream.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }





/*
  public synchronized static ArrayList<Email> loadOutBox(String user) {
    System.out.println("[loadOutBox] user: " + user);
    ArrayList<Email> out = new ArrayList<>();
    ObjectInputStream output = null;
    FileInputStream files = null;
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

*/
public synchronized ArrayList<Email> loadOutBox(String user, Socket socket) {
  /*System.out.println("[loadOutBox] user: " + user);
  ArrayList<Email> out = new ArrayList<>();

  File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/out");

  if (dir.exists() && dir.isDirectory()) {
    System.out.println("Directory exists and is a directory: " + dir);

    try {
      ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

      for (File textFile : Objects.requireNonNull(dir.listFiles())) {
        System.out.println("[loadOutBox] processing file: " + textFile.getName());

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
          String line;
          StringBuilder content = new StringBuilder();

          while ((line = reader.readLine()) != null) {
            content.append(line);
          }

          System.out.println("[loadOutBox] file : " + content.toString()); // Debug output
          Email email = (Email) new ObjectInputStream(new ByteArrayInputStream(content.toString().getBytes())).readObject();
          System.out.println("[loadOutBox] email: " + email);
          out.add(email);

          // Serialize and send the content over the socket
          outputStream.writeObject(content.toString());
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }
    } catch (EOFException e) {
      System.out.println("[loadOutBox] Reached end of file unexpectedly: " + e.getMessage());
    } catch (IOException e) {
			e.printStackTrace();
		}
	} else {
    System.out.println("Directory does not exist or is not a directory: " + dir);
  }*/

  // Note: Sending completion signal, assuming Email objects are all that's sent
  /*try {
    ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
    outputStream.writeObject(null);
  } catch (IOException e) {
    e.printStackTrace();
  }*/

//  return out;
  ArrayList<Email> out = new ArrayList<>();

  File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/out");

  if (dir.exists() && dir.isDirectory()) {
    try {

      for (File textFile : Objects.requireNonNull(dir.listFiles())) {
        try (ObjectInputStream fileInputStream = new ObjectInputStream(new FileInputStream(textFile))) {
          Email email = (Email) fileInputStream.readObject();
          out.add(email);

          // Send the email object over the socket
          outputStream.writeObject(email);

          // Receive any acknowledgement or response from the server
          Object response = inputStream.readObject();
          System.out.println("[loadOutBox] Server response: " + response);
        } catch (IOException | ClassNotFoundException e) {
          e.printStackTrace();
        }
      }

      // Signal the end of data transmission
      outputStream.writeObject(null);
    } catch (IOException e) {
      e.printStackTrace();
    }
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
      }*/
/*

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
  FIXME inbox related problems, the inbox returned is always empty so the response for client is always empty
   */
/*
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
*/
/*
  public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] loading " + user + "'s inbox");
    ArrayList<Email> inbox = new ArrayList<>();
    System.out.println("just before try");
    try {
      System.out.println("just after try");
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");
      if (dir.exists() && dir.isDirectory()) {
        System.out.println("Directory exists and is a directory: " + dir);
        for (File f : Objects.requireNonNull(dir.listFiles())) {
          try (FileInputStream file = new FileInputStream(f);
               ObjectInputStream input = new ObjectInputStream(file)) {
            Email email = (Email) input.readObject();
            inbox.add(email);
            System.out.println("[loadInBox] inbox with add " + inbox);
          } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
          }
        }
      } else {
        System.out.println("Directory does not exist or is not a directory: " + dir);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return inbox;
  }
*/

 /* public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] loading " + user + "'s inbox");
    ArrayList<Email> inbox = new ArrayList<>();
    System.out.println("just before try");
    try {
      System.out.println("just after try");
      File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");
      if (dir.exists() && dir.isDirectory()) {
        System.out.println("Directory exists and is a directory: " + dir);
        for (File f : Objects.requireNonNull(dir.listFiles())) {
          try (FileInputStream file = new FileInputStream(f);
               ObjectInputStream input = new ObjectInputStream(file)) {
            try {
              Email email = (Email) input.readObject();
              inbox.add(email);
              System.out.println("[loadInBox] inbox with add " + inbox);
            } catch (EOFException e) {
              // Handle EOFException here (print, log, or take appropriate action)
              System.out.println("[loadInBox] Reached end of file unexpectedly: " + e.getMessage());
            } catch (ClassNotFoundException | IOException e) {
              e.printStackTrace();
            }
          }
        }
      } else {
        System.out.println("Directory does not exist or is not a directory: " + dir);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return inbox;
  }
*/

  /*public synchronized static ArrayList<Email> loadInBox(String user) {
    System.out.println("[loadInBox] loading " + user + "'s inbox");
    ArrayList<Email> inbox = new ArrayList<>();
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");

    if (dir.exists() && dir.isDirectory()) {
      System.out.println("Directory exists and is a directory: " + dir);

      for (File textFile : Objects.requireNonNull(dir.listFiles())) {
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
          String line;
          StringBuilder content = new StringBuilder();

          while ((line = reader.readLine()) != null) {
            content.append(line);
          }

          Email email = (Email) new ObjectInputStream(new ByteArrayInputStream(content.toString().getBytes())).readObject();
          System.out.println("[loadInBox] email: " + email);
          inbox.add(email);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
    } else {
      System.out.println("Directory does not exist or is not a directory: " + dir);
    }

    return inbox;
  }*/
/*
  public synchronized static ArrayList<Email> loadInBox(String user)  {
    System.out.println("[loadInbox] loading all emails for user: " + user);
    ArrayList<Email> allEmails = new ArrayList<>();
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");

    if (dir.exists() && dir.isDirectory()) {
      System.out.println("Directory exists and is a directory: " + dir);

      for (File textFile : Objects.requireNonNull(dir.listFiles())) {
        System.out.println("[loadInbox] already raised exc");

        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
          String line;
          StringBuilder content = new StringBuilder();

          while ((line = reader.readLine()) != null) {
            content.append(line);
          }

          System.out.println("[loadInbox] content read from file: " + content.toString()); // Debug output
          Email email = (Email) new ObjectInputStream(new ByteArrayInputStream(content.toString().getBytes())).readObject();
          System.out.println("[loadInbox] email: " + email);
          allEmails.add(email);
        } catch (IOException e) {
          e.printStackTrace();
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
    } else {
      System.out.println("Directory does not exist or is not a directory: " + dir);
    }
    for (Email email : allEmails) {
      System.out.println("allEmails contains: "  + email);
    }
    return allEmails;
  }
*/

  public synchronized ArrayList<Email> loadInBox(String user,Socket socket) {
    /*System.out.println("[loadInBox] loading all emails for user: " + user);
    ArrayList<Email> allEmails = new ArrayList<>();
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/" + "in");

    if (dir.exists() && dir.isDirectory()) {
      System.out.println("Directory exists and is a directory: " + dir);

      try {
        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());

        for (File textFile : Objects.requireNonNull(dir.listFiles())) {
          System.out.println("[loadInBox] processing file: " + textFile.getName());

          try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            String line;
            StringBuilder content = new StringBuilder();

            while ((line = reader.readLine()) != null) {
              content.append(line);

              System.out.println("[loadInBox] content read from file: " + content.toString()); // Debug output
            }

            Email email = (Email) new ObjectInputStream(new ByteArrayInputStream(content.toString().getBytes())).readObject();
            System.out.println("[loadInBox] email: " + email);
            allEmails.add(email);

            // Serialize and send the content over the socket
            outputStream.writeObject(content.toString());
          } catch (EOFException e) {
            System.out.println("[loadInBox] Reached end of file unexpectedly: " + e.getMessage());
          } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("Directory does not exist or is not a directory: " + dir);
    }

    // Note: Sending completion signal, assuming Email objects are all that's sent
    try {
      ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
      outputStream.writeObject(null);
    } catch (IOException e) {
      e.printStackTrace();
    }

    for (Email email : allEmails) {
      System.out.println("allEmails contains: " + email);
    }
    return allEmails;*/
    ArrayList<Email> allEmails = new ArrayList<>();
    File dir = new File("src/main/java/com/example/mailServer/file/" + user + "/in");

    if (dir.exists() && dir.isDirectory()) {
      try {

        for (File textFile : Objects.requireNonNull(dir.listFiles())) {
          try (ObjectInputStream fileInputStream = new ObjectInputStream(new FileInputStream(textFile))) {
            Email email = (Email) fileInputStream.readObject();
            allEmails.add(email);

            // Send the email object over the socket
            outputStream.writeObject(email);

            // Receive any acknowledgement or response from the server
            Object response = inputStream.readObject();
            System.out.println("[loadInBox] Server response: " + response);
          } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
          }
        }

        // Signal the end of data transmission
        outputStream.writeObject(null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return allEmails;
  }



  /*
    @brief: delete the selected mail in MailContainerController from the file system both in and out folder of the user
    using a regex to remove the brackets from the username
   */
  public synchronized void delete(String user,Email mail) {
    String userWithoutBracket = user.replace("[", "").replace("]", "");
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
