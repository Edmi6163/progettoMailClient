package mailServer.View;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import mailServer.ServerMain;

public class ServerLayout {
  private ServerMain serverMain;
  @FXML private ListView<String> userList;
  @FXML private ListView<String> log= new ListView<>();


  public void setServerMain(){
    this.serverMain=serverMain;
    userList.setItems(serverMain.getUserList().getUsers());
    log.setItems(serverMain.getLogList());
  }

  public ServerLayout(){

  }

  @FXML
  public void initialize(){

  }

  @FXML
  public void addItemToLogList(String item){
    log.getItems().add(item);
  }
}
