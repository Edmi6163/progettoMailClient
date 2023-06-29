package com.example.mailClient.Controller;

import com.example.mailServer.Model.LoggerModel;
import com.example.mailClient.Controller.ClientController;
import com.example.mailServer.Model.Mail;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.example.mailClient.ClientMain;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {
	@FXML
	private TextField username;
	@FXML
	private AnchorPane loginPane;

	private Stage stage;
	private Stage topStage;
	private BorderPane rootLayout;

	private String userMail = "";
	public ClientController clientHandler;

	private ObservableList<Mail> inbox = FXCollections.observableArrayList();
	private ObservableList<Mail> outbox = FXCollections.observableArrayList();
	public ClientController getClientHandler() {
		return clientHandler;
	}


	public Stage getTopStage(Stage topStage) {
		return topStage;
	}


	@FXML
	private void handleLogin() throws IOException {
		// logger.setLog("username is: " + username.getText());
		String usernameToCheck = this.username.getText() + "@javamail.it";

		ClientController cc = new ClientController(this.username.getText());
		cc.login();
		System.out.println(username.getText() + " logged in ");


//    stage.close();
	}

	public ObservableList<Mail> getInbox() {
		return inbox;
	}

	public ObservableList<Mail> getOutbox() {
		return outbox;
	}


	public String getUserMail() {
		return userMail;
	}


	public void showErrorPopUp() {
		Alert popup = new Alert(Alert.AlertType.INFORMATION);
		popup.initOwner(topStage);
		popup.setTitle("Server error");
		popup.setContentText("Server propably is offline or check your internet connection");
		popup.show();
	}

	private boolean checkConnection() {
		if (!clientHandler.checkConnection()) {
			showErrorPopUp();
			return false;
		}
		return true;
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

	private void stopServerCheckTimer(Timer timer) {
		if (timer != null)
			timer.cancel();
		timer = null;
	}

	/**/
	private void startServerCheckTimer() {
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

	public void showSendMailDialog(Mail mail, String title) {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void loadController() throws IOException {

			FXMLLoader loaderLogin = new FXMLLoader(ClientMain.class.getResource("Login.fxml"));
			AnchorPane page = loaderLogin.load();

			//loading login dialog
			Stage dialog = new Stage();
			dialog.setTitle("Login");
			dialog.initModality(Modality.WINDOW_MODAL);
			dialog.initOwner(topStage);
			Scene sceneLogin = new Scene(page);
			dialog.setScene(sceneLogin);
			dialog.setOnCloseRequest(windowEvent -> Platform.exit());
			LoginController loginController = loaderLogin.getController();
			// loginController.setClientMain(this, dialog);

			dialog.showAndWait();


			//loading root layout

			FXMLLoader loaderRoot = new FXMLLoader(ClientMain.class.getResource("RootLayout.fxml"));
			rootLayout = loaderRoot.load();
			RootLayoutController controllerRoot = loaderRoot.getController();
			controllerRoot.setClientMain(this);
			Scene sceneRoot = new Scene(rootLayout);
			topStage.setScene(sceneRoot);
			topStage.show();


			//loading mail container
			FXMLLoader loaderContainer = new FXMLLoader(ClientMain.class.getResource("MailContainer.fxml"));
			AnchorPane mailContainer = loaderContainer.load();
			rootLayout.setCenter(mailContainer);
			MailContainerController controller = loaderContainer.getController();
			controller.setClientMain(this);


			if (!checkConnection()) {
				showErrorPopUp();
			}
			startServerCheckTimer();
	}
}
