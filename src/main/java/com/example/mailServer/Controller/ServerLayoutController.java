package com.example.mailServer.Controller;

import com.example.mailServer.Model.*;

import com.example.mailServer.ServerMain;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerLayoutController {

	@FXML
	public TextFlow logFlow;

	public LoggerModel logModel;

	public ServerLayoutController() {
		logModel = new LoggerModel();
		logModel.getLog().addListener((observable, oldValue, newValue) -> {
			setLog(newValue);
		});

		Runnable serverThread = new ServerController(logModel);
		new Thread(serverThread).start();

	}

	public void initialize() {
		System.out.println("ServerLayoutController initialized");
		logModel.setLog("server started");
	}

	public void setLog(String log) {
		Text fullLog = new Text("[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]\t ---> "
				+ "  " + log + "\n");
		fullLog.setFill(Color.web("#ffffff"));
		logFlow.getChildren().add(fullLog);
	}
}
