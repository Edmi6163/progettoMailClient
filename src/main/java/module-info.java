module com.example.progettomailclient {
  requires javafx.controls;
  requires javafx.fxml;

  opens com.example.mailServer to javafx.fxml;
  opens com.example.mailServer.Controller to javafx.fxml;

  exports com.example.mailServer;
  }
