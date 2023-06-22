package com.example.mailServer.Controller;

import com.example.mailServer.Model.*;

import com.example.mailServer.ServerMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class ServerLayoutController {
	private ServerMain serverMain;

	@FXML
	private ListView<String> logList = new ListView<>();

	public void setMainApp(ServerMain serverMain) {
		this.serverMain = serverMain;
		logList.setItems(serverMain.getLogList());
	}

	public void initialize() {
		setLog("server started");
	}

	public void setLog(String log) {
		logList.getItems().add(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "  " + log);
	}
}
