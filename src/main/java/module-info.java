module com.example.progettomailclient {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.example.progettomailclient to javafx.fxml;
  exports com.example.progettomailclient;
  exports com.example.mailServer;
}