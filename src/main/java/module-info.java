module com.example.progettomailclient {
  requires javafx.controls;
  requires javafx.fxml;
  requires me.xdrop.fuzzywuzzy;

  opens com.example.mailServer to javafx.fxml;
  opens com.example.mailServer.Controller to javafx.fxml;
  opens  com.example.mailClient.Controller to javafx.fxml;
  exports com.example.mailServer;
  exports com.example.mailClient;
  exports com.example.mailClient.Controller;
  exports com.example.mailServer.Controller;
  exports com.example.mailClient.Model;
}
