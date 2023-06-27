package com.example.mailServer.Controller;

import com.example.Transmission.Email;
import com.example.mailServer.Model.Mail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MailHandler {
  public synchronized static Email save(Email mail){
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


  public synchronized static List<Mail> getUpdatedList(String user,String max){
    List<Mail> updatedList= new ArrayList<>();
    max=max+".txt";
    File dir=new File("src/main/java/com/example/mailServer/file/"+user+"/"+"in/");
    ObjectOutputStream output=null;
    FileOutputStream files=null;
    for(File f: Objects.requireNonNull(dir.listFiles())){
      if(f.getName().compareTo(max)>0){
        try{
          files=new FileOutputStream(f);
          output= new ObjectOutputStream(files);
          output.close();
          files.close();
        } catch (IOException e){
          e.printStackTrace();
        }
      }
    }
    return updatedList;
}

public synchronized static List<Mail> loadOutBox(String user){
    List<Mail> out = new ArrayList<>();
    try{
     File dir= new File("src/main/java/com/example/mailServer/file/"+user+"/"+"out");
     ObjectInputStream output = null;
     FileInputStream files=null;
     for(File f: Objects.requireNonNull(dir.listFiles())) {
       files = new FileInputStream(f);
       output = new ObjectInputStream(files);
       out.add((Mail) output.readObject());
       output.close();
       files.close();
     }
      if(files!=null) {
        output.close();
        files.close();
      }

    } catch (Exception e){
      e.printStackTrace();
    }
    return out;
}

public synchronized static List<Mail> loadInBox(String user){
    List<Mail> inbox = new ArrayList<>();
    try{
      File dir=new File("src/main/java/com/example/mailServer/file/"+user+"/"+"in");
      ObjectInputStream input = null;
      FileInputStream file = null;
      for(File f: Objects.requireNonNull(dir.listFiles())) {
        file = new FileInputStream(f);
        input = new ObjectInputStream(file);
        inbox.add((Mail) input.readObject());
        input.close();
        file.close();
      }
        if(file!=null){
          input.close();
          file.close();
        }

      } catch (Exception e){
      e.printStackTrace();
    }
    return inbox;
    }


public synchronized void delete(Mail mail, String user){
    try{
      if(mail.isIsSent())
        Files.delete(Paths.get("src/main/java/com/example/mailServer/file/"+user+"/out/"+mail.getMillis()+".txt"));
      else
        Files.delete(Paths.get("src/main/java/com/example/mailServer/file/"+user+"/in/"+mail.getMillis()+".txt"));
    } catch (Exception e){
      e.printStackTrace();
    }
}

}
