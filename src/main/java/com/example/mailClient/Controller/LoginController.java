package com.example.mailClient.Controller;

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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class LoginController {
	@FXML
	private TextField username;
	// @FXML
	// private AnchorPane root;

	@FXML
	public BorderPane root;
	private Stage topStage;
	public String userMail;
	public ClientController clientHandler;
	private ObservableList<Mail> inbox = FXCollections.observableArrayList();
	private ObservableList<Mail> outbox = FXCollections.observableArrayList();

	public ClientController cc;

	public void addOutbox(List<Mail> out) {
		outbox.addAll(out);
	}

	public void addOut(Mail out) {
		System.out.println("adding to outbox");
		outbox.add(out);
	}

	@FXML
	private void handleLogin() throws IOException {
		try {
			userMail = username.getText();

			initRootLayout();
			showMailContainer();

			cc = new ClientController(username.getText());
			cc.login();

			System.out.println(username.getText() + " logged in ");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public ObservableList<Mail> getInbox() {
		return inbox;
	}

	public ObservableList<Mail> getOutbox() {
		return outbox;
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
			controller.setController(cc);
			controller.setDialog(dialog);
			controller.setMail(mail);

			dialog.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void initRootLayout() {
		try {
			topStage = (Stage) root.getScene().getWindow();
			// loading root layout
			FXMLLoader loaderRoot = new FXMLLoader(ClientMain.class.getResource("RootLayout.fxml"));
			root.setTop(loaderRoot.load());

			System.out.println(userMail);
			// QUI
			RootLayoutController controllerRoot = loaderRoot.getController();
			controllerRoot.setClientMain(this, userMail);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void showMailContainer() {
		try {
			// loading mail container
			FXMLLoader loaderContainer = new FXMLLoader(ClientMain.class.getResource("MailContainer.fxml"));
			root.setCenter(loaderContainer.load());

			System.out.println(userMail);
			MailContainerController controller = loaderContainer.getController();
			controller.setClientMain(this, userMail);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
