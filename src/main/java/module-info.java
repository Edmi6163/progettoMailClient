module com.example.progettomailclient {
  requires javafx.controls;
  requires javafx.fxml;


  exports com.example.progettomailclient;
  exports com.example.mailServer;

  opens com.example.mailServer to javafx.fxml;
}