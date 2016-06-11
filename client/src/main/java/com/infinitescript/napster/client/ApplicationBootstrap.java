package com.infinitescript.napster.client;

import java.io.IOException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The entrance of the application.
 * 
 * @author Haozhe Xie
 */
public class ApplicationBootstrap extends Application {
	/**
	 * The entrance of the application.
	 * JavaFX application still use the main method.
	 * 
	 * 
	 * @param args - the arguments passing to the launch method
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(20));
		grid.setVgap(10);
		grid.setHgap(10);
		
		Scene scene = new Scene(grid);
		primaryStage.setTitle("Napster Client");
		primaryStage.setResizable(false);
		
		// Controls in the first row 
		Label serverIpLabel = new Label("Server IP: ");
		GridPane.setConstraints(serverIpLabel, 0, 0);
		grid.getChildren().add(serverIpLabel);
		
		TextField serverIpTextField = new TextField("127.0.0.1");
		GridPane.setConstraints(serverIpTextField, 1, 0);
		grid.getChildren().add(serverIpTextField);
		
		Button connectServerButton = new Button("Connect");
		connectServerButton.setMinWidth(160);
		GridPane.setConstraints(connectServerButton, 2, 0);
		grid.getChildren().add(connectServerButton);
		
		// Controls in the second row 
		Label nickNameLabel = new Label("NickName: ");
		GridPane.setConstraints(nickNameLabel, 0, 1);
		grid.getChildren().add(nickNameLabel);
		
		TextField nickNameTextField = new TextField("Anonymous");
		GridPane.setConstraints(nickNameTextField, 1, 1);
		grid.getChildren().add(nickNameTextField);
		
		// Controls in the next few rows
		Button shareFileButton = new Button("Share File");
		shareFileButton.setMinWidth(160);
		shareFileButton.setDisable(true);
		GridPane.setConstraints(shareFileButton, 2, 2);
		grid.getChildren().add(shareFileButton);
		
		Button receiveFileButton = new Button("Receive Selected File");
		receiveFileButton.setMinWidth(160);
		receiveFileButton.setDisable(true);
		GridPane.setConstraints(receiveFileButton, 2, 3);
		grid.getChildren().add(receiveFileButton);
		
		ListView<String> fileListView = new ListView<String>();
		GridPane.setConstraints(fileListView, 0, 2, 2, 3);
		grid.getChildren().add(fileListView);
		
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);
}
