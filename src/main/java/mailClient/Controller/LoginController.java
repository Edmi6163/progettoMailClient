package mailClient.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import mailClient.ClientMain;

public class LoginController {
  @FXML
  private TextField username;
  @FXML
  private AnchorPane loginPane;

  private ClientMain clientMain;
  private Stage stage;

  public void setClientMain(ClientMain main,Stage stage){
    this.clientMain=main;
    this.stage=stage;
  }

  @FXML
  private void handleLogin(){
    if(username.getText().length()>0 && username != null){
      clientMain.setUserMail(username.getText()+"@mail.it");
      boolean loginSuccess=clientMain.getClientHandler().requestAll();
      if(loginSuccess){
        stage.close();
      } else {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(stage);
        alert.setTitle("wrong username");
        alert.setHeaderText("Error");
        alert.setContentText("Can't login");
        alert.showAndWait();
        Platform.exit();
      }
    }

  }
  @FXML
  public void closeStage(){
    stage.close();
  }

}
