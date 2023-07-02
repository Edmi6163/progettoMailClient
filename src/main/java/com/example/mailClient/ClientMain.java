package com.example.mailClient;

import com.example.mailClient.Controller.LoginController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class ClientMain extends Application {



  @Override
  public void start(Stage topStage) throws IOException {
    topStage.setTitle("Client mail window @javamail");

    FXMLLoader loaderLogin = new FXMLLoader(getClass().getResource("Login.fxml"));
    Scene sceneLogin = new Scene(loaderLogin.load());
    // loading login dialog

    topStage.setTitle("Login");
    topStage.setScene(sceneLogin);

    topStage.setOnCloseRequest(windowEvent -> Platform.exit());
    sceneLogin.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, (WindowEvent e) -> System.exit(1));

    topStage.show();
  }

  public static void main(String[] args) {
    launch();
  }

}