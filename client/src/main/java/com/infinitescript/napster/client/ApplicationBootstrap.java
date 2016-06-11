package com.infinitescript.napster.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * The entrance of the application.
 * 
 * @author Haozhe Xie
 */
@SuppressWarnings("restriction")
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
		setupUiComponent(primaryStage);
		primaryStage.show();
	}
	
	private void setupUiComponent(Stage primaryStage) {
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
		Button shareFileButton = new Button("Share a File");
		shareFileButton.setMinWidth(160);
		GridPane.setConstraints(shareFileButton, 2, 2);
		grid.getChildren().add(shareFileButton);
		
		Button receiveFileButton = new Button("Receive Selected File");
		receiveFileButton.setMinWidth(160);
		GridPane.setConstraints(receiveFileButton, 2, 3);
		grid.getChildren().add(receiveFileButton);
		
		Label fileListLabel = new Label("Shared Files: ");
		GridPane.setConstraints(fileListLabel, 0, 2);
		grid.getChildren().add(fileListLabel);
		
		ListView<String> fileListView = new ListView<String>();
		GridPane.setConstraints(fileListView, 0, 3, 2, 2);
		grid.getChildren().add(fileListView);
		
		// Initialize UI
		setupUiComponentAvailability(serverIpTextField, nickNameTextField, 
				connectServerButton, shareFileButton, receiveFileButton, 
				fileListView, isConnected);
		
		// Setup Events Handlers
		connectServerButton.setOnAction((ActionEvent e) -> {
			connectServerButton.setDisable(true);
			
			if ( !isConnected ) {
				String ipAddress = serverIpTextField.getText();
				String nickName = nickNameTextField.getText();
				
				try {
					client.connect(ipAddress, nickName);
					isConnected = true;
				} catch (Exception ex) {
					LOGGER.catching(ex);
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Connection Refused");
					alert.setHeaderText("Failed to connect to Napster server.");
					alert.setContentText("Error message: " + ex.getMessage());
					alert.showAndWait();
				}
			} else {
				client.disconnect();
				isConnected = false;
			}
			
			setupUiComponentAvailability(serverIpTextField, nickNameTextField, 
					connectServerButton, shareFileButton, receiveFileButton, 
					fileListView, isConnected);
			connectServerButton.setDisable(false);
		});
		
		primaryStage.setScene(scene);
	}
	
	/**
	 * Setup the availability of components in UI.
	 * @param serverIpTextField - the text field for server IP
	 * @param nickNameTextField - the text field for nick name
	 * @param connectServerButton - the button for connecting/disconnect to/from server 
	 * @param shareFileButton - the button for sharing files
	 * @param receiveFileButton - the button for receive the selected file
	 * @param fileListView - the list view for display all shared files
	 * @param isConnected - whether is connected to server right now
	 */
	private void setupUiComponentAvailability(
			TextField serverIpTextField, TextField nickNameTextField, 
			Button connectServerButton, Button shareFileButton,
			Button receiveFileButton, ListView<String> fileListView, 
			boolean isConnected) {
		if ( isConnected ) {
			serverIpTextField.setDisable(true);
			nickNameTextField.setDisable(true);
			connectServerButton.setText("Disconnect");
			shareFileButton.setDisable(false);
			receiveFileButton.setDisable(false);
		} else {
			serverIpTextField.setDisable(false);
			nickNameTextField.setDisable(false);
			connectServerButton.setText("Connect");
			shareFileButton.setDisable(true);
			receiveFileButton.setDisable(true);
			fileListView.getItems().clear();
		}
	}
	
	/**
	 * Napster client used for communicating with server.
	 */
	private static final Client client = Client.getInstance();
	
	/**
	 * A variable stores whether the client is connected to server.
	 */
	private boolean isConnected = false;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);
}
