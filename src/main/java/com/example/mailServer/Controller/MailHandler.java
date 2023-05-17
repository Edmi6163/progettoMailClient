package com.example.mailServer.Controller;

import com.example.mailServer.Model.Mail;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MailHandler {
  public synchronized static Mail save(Mail mail){
    Mail newMail = null;
    try{
      Date date = new Date();
      long millis = date.getTime();
      String sender = mail.getSender();
      List<String> receivers = mail.getReceivers();
      FileOutputStream file = new FileOutputStream("./file"+sender+"/"+"out"+millis+".txt");
      ObjectOutputStream output = new ObjectOutputStream(file);
      newMail=new Mail(mail.getSender(),mail.getSubject(),mail.getReceiversString(),millis,mail.getMessage());
      newMail.setIsSent(true);
      output.writeObject(newMail);
      output.close();
      file.close();
      for(String r: receivers){
        System.out.println(r);
        file = new FileOutputStream("./file"+r+"/"+"in/"+millis+".txt");
        output = new ObjectOutputStream(file);
        newMail.setIsSent(false);
        output.writeObject(newMail);
        output.close();
        file.close();
      }
    } catch (IOException e){
      e.printStackTrace();
    }
    return newMail;
  }

public synchronized static List<Mail> getUpdatedList(String user,String max){
    List<Mail> updatedList= new ArrayList<>();
    max=max+".txt";
    File dir=new File("./file"+user+"/"+"in/");
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
     File dir= new File("./file"+user+"/"+"out");
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
      File dir=new File("./file/"+user+"/"+"in");
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


public synchronized void delete(Mail mail,String user){
    try{
      if(mail.isIsSent())
        Files.delete(Paths.get("./file/"+user+"/out/"+mail.getMillis()+".txt"));
      else
        Files.delete(Paths.get("./file/"+user+"/in/"+mail.getMillis()+".txt"));
    } catch (Exception e){
      e.printStackTrace();
    }
}

}
