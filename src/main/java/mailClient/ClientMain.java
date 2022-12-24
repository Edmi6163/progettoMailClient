package mailClient;


import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mailClient.Controller.*;
import mailClient.Model.Mail;

import java.io.IOException;
import java.util.List;

public class ClientMain extends Application {
  private Stage topStage;
  private Stage dialog;
  private BorderPane rootLayout;
  private boolean mailSent;

  public boolean isMailSent(){
    return mailSent;
  }
  public void setMailSent(boolean mailSent){
    this.mailSent = mailSent;
  }
  private ObservableList<Mail> inbox = FXCollections.observableArrayList();
  private ObservableList<Mail> outbox = FXCollections.observableArrayList();

  private String userMail = "";
  private ClientController clientHandler;

  public ClientController getClientHandler() {
    return clientHandler;
  }
  public ClientMain(){
    clientHandler = new ClientController(this);
  }
  public void setUserMail(String userMail){
    this.userMail = userMail;
  }
  public ObservableList<Mail> getInbox(){
    return inbox;
  }
  public ObservableList<Mail> getOutbox(){
    return outbox;
  }
  public void addInbox(List<Mail> in){
    inbox.addAll(in);
  }
  public void addOutbox(List<Mail> out){
    outbox.addAll(out);
  }
  public void addOut(Mail out){
    outbox.add(out);
  }
  public void delete(Mail mail){
    if(mail.isIsSent())
      outbox.remove(mail);
    else
      inbox.remove(mail);
  }
  public String getUserMail(){
    return userMail;
  }
  public void start(Stage topStage){
    this.topStage = topStage;
    this.topStage.setTitle("Client mail window");
    showLoginDialog();
    initRootLayout();
    showMailContainer();

    Thread refresh = new Thread(this::refresh);
    refresh.setDaemon(true);
    refresh.start();
  }


  public void initRootLayout(){
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ClientMain.class.getResource("View/RootLayout.fxml"));
      rootLayout = (BorderPane) loader.load();
      RootLayoutController controller = loader.getController();
      controller.setClientMain(this);
      Scene scene = new Scene(rootLayout);
      topStage.setScene(scene);
      topStage.show();
    } catch (IOException e){
      e.printStackTrace();
    }
  }

  public void showMailContainer(){
    try {
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ClientMain.class.getResource("View/MailContainer.fxml"));
      AnchorPane mailContainer = (AnchorPane) loader.load();
      rootLayout.setCenter(mailContainer);
      MailContainerController controller = loader.getController();
      controller.setClientMain(this);
    } catch (IOException e){
      e.printStackTrace();
    }
  }
  public boolean showSendMailDialog(Mail mail,String title){
    try {
    FXMLLoader loader = new FXMLLoader();
    loader.setLocation(ClientMain.class.getResource("View/NewMessage.fxml"));
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
    return controller.isOkClicked();
    } catch (IOException e){
      e.printStackTrace();
      return false;
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

    popup.showAndWait();
  }

  public void showErrorPopUp(){
    Alert popup=new Alert(Alert.AlertType.INFORMATION);
    popup.initOwner(topStage);
    popup.setTitle("Error server");
    popup.setContentText("Server propably is offline, Please try again later");
    popup.showAndWait();
  }
  private void showLoginDialog(){
    try{
      FXMLLoader loader = new FXMLLoader();
      loader.setLocation(ClientMain.class.getResource("view/Login.fxml"));
      AnchorPane page = (AnchorPane) loader.load();

      dialog = new Stage();
      dialog.setTitle("Login");
      dialog.initModality(Modality.WINDOW_MODAL);
      dialog.initOwner(topStage);
      Scene scene = new Scene(page);
      dialog.setScene(scene);
      dialog.setOnCloseRequest(new EventHandler<WindowEvent>() {
        @Override
        public void handle(WindowEvent windowEvent) {
          Platform.exit();
        }
      });
      LoginController loginController = loader.getController();
      loginController.setClientMain(this,dialog);

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
  public Stage getTopStage(){
    return topStage;
  }


  public static void main(String[] args){
    launch(args);
  }
}
