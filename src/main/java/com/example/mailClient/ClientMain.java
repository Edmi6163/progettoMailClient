package com.example.mailClient;


import com.example.mailClient.Controller.*;
import com.example.mailClient.Controller.LoginController;


import com.example.mailServer.Model.Mail;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class ClientMain extends Application {
  private Stage topStage;
  private BorderPane rootLayout;
  private boolean mailSent;

  public boolean isMailSent() {
    return mailSent;
  }

  public void setMailSent(boolean mailSent) {
    this.mailSent = mailSent;
  }

  private ObservableList<Mail> inbox = FXCollections.observableArrayList();
  private ObservableList<Mail> outbox = FXCollections.observableArrayList();

  private String userMail = "";
  private ClientController clientHandler;

  public ClientController getClientHandler() {
    return clientHandler;
  }

  public ClientMain() {
    clientHandler = new ClientController(this);
  }

  public void setUserMail(String userMail) {
    this.userMail = userMail;
  }

  public ObservableList<Mail> getInbox() {
    return inbox;
  }

  public ObservableList<Mail> getOutbox() {
    return outbox;
  }

  public void addInbox(List<Mail> in) {
    inbox.addAll(in);
  }

  public void addOutbox(List<Mail> out) {
    outbox.addAll(out);
  }

  public void addOut(Mail out) {
    System.out.println("adding to outbox");
    outbox.add(out);
  }

  public void delete(Mail mail) {
    if (mail.isIsSent())
      outbox.remove(mail);
    else
      inbox.remove(mail);
  }

  public String getUserMail() {
    return userMail;
  }


  private void showServerUpNotification() {
    Platform.runLater(() -> {
      Alert alert = new Alert(Alert.AlertType.INFORMATION);
      alert.setTitle("Server is up!");
      alert.setHeaderText("Server is up!");
      alert.setContentText("Server is up!");
      alert.show();
    });
  }

  private void stopServerCheckTimer(Timer timer){
    if(timer!=null)
      timer.cancel();
    timer = null;
  }

  private void startServerCheckTimer(){
    Timer timer = new Timer();

    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        if (clientHandler.checkConnection()) {
          showServerUpNotification();
          stopServerCheckTimer(timer);
        }
      }
    }, 0, 10000);
  }

  public void initRootLayout() {
    try {
      FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("RootLayout.fxml"));
      rootLayout = loader.load();
      RootLayoutController controller = loader.getController();
      controller.setClientMain(this);
      Scene scene = new Scene(rootLayout);
      topStage.setScene(scene);
      topStage.show();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (!checkConnection()) {
      showErrorPopUp();
    }
    startServerCheckTimer();
  }

  public void showMailContainer(){
    try {
      FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("MailContainer.fxml"));
      AnchorPane mailContainer = loader.load();
      rootLayout.setCenter(mailContainer);
      MailContainerController controller = loader.getController();
      controller.setClientMain(this);
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  public void showSendMailDialog(Mail mail, String title){
    try {
    FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("NewMessage.fxml"));
    AnchorPane page = (AnchorPane) loader.load();
    Stage dialog = new Stage();
    dialog.setTitle(title);
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.initOwner(topStage);
    Scene scene = new Scene(page);
    dialog.setScene(scene);

    NewMessageController controller = loader.getController();
    controller.setDialog(dialog);
    controller.setMail(mail);

    dialog.showAndWait();
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  public void showNewMailPopUp(int mail){
    Alert popup=new Alert(Alert.AlertType.INFORMATION);
    popup.initOwner(topStage);
    popup.setTitle("Notifications");
    if(mail>1)
      popup.setContentText("You received "+mail+"new messages");
    else
      popup.setContentText("You received a new message");

    popup.show();
  }

  public void showErrorPopUp(){
      Alert popup = new Alert(Alert.AlertType.INFORMATION);
      popup.initOwner(topStage);
      popup.setTitle("Server error");
      popup.setContentText("Server propably is offline or check your internet connection");
      popup.show();
  }
  private void showLoginDialog(){
    try{
      FXMLLoader loader = new FXMLLoader(ClientMain.class.getResource("Login.fxml"));
      AnchorPane page = loader.load();

      Stage dialog = new Stage();
      dialog.setTitle("Login");
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(topStage);
      Scene scene = new Scene(page);
      dialog.setScene(scene);
      dialog.setOnCloseRequest(windowEvent -> Platform.exit());
      LoginController loginController = loader.getController();
      loginController.setClientMain(this, dialog);

      dialog.showAndWait();
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  private void refresh(){
    while(true){
      try {
        Thread.sleep(50000);
        if(userMail.length()>0)
          Platform.runLater(clientHandler::requestInbox);
      } catch (InterruptedException e){
        e.printStackTrace();
      }
    }
  }

  public void noMailPopUp() {
    Alert popup = new Alert(Alert.AlertType.INFORMATION);
    popup.initOwner(topStage);
    popup.setTitle("No mail");
    popup.setContentText("You have no mail");
    popup.show();
  }

  private boolean checkConnection() {
    if(!clientHandler.checkConnection()){
      showErrorPopUp();
      return false;
    }
    return true;
  }
  public Stage getTopStage(){
    return topStage;
  }


  @Override
  public void start(Stage topStage){
    this.topStage = topStage;
    this.topStage.setTitle("Client mail window @javamail");

    Thread refresh = new Thread(this::refresh);
    refresh.setDaemon(true);
    refresh.start();

    showLoginDialog();
    initRootLayout();
    showMailContainer();

  }

  public static void main(String[] args){
    launch(args);
  }


}