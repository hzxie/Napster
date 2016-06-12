package com.infinitescript.napster.client;

import com.google.common.hash.Hashing;
import com.google.common.io.Files;

import java.io.File;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.FileChooser;

/**
 * The entrance of the application.
 * 
 * @author Haozhe Xie
 */
@SuppressWarnings("restriction")
public class ApplicationBootstrap extends Application {
	/**
	 * The entrance of the application. JavaFX application still use the main
	 * method.
	 * 
	 * @param args the arguments passing to the launch method
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

	/**
	 * Setup controls in the UI.
	 * @param primaryStage
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		
		Button unshareFileButton = new Button("Unshare Selected File");
		unshareFileButton.setMinWidth(160);
		GridPane.setConstraints(unshareFileButton, 2, 3);
		grid.getChildren().add(unshareFileButton);

		Button getSharedFilesButton = new Button("Get Shared Files");
		getSharedFilesButton.setMinWidth(160);
		GridPane.setConstraints(getSharedFilesButton, 2, 4);
		grid.getChildren().add(getSharedFilesButton);
		
		Button receiveFileButton = new Button("Receive Selected File");
		receiveFileButton.setMinWidth(160);
		GridPane.setConstraints(receiveFileButton, 2, 5);
		grid.getChildren().add(receiveFileButton);

		Label fileListLabel = new Label("Shared Files: ");
		GridPane.setConstraints(fileListLabel, 0, 2);
		grid.getChildren().add(fileListLabel);

		// TableView control for listing files
		final TableView<SharedFile> fileTableView = new TableView<SharedFile>();
		TableColumn fileNameTableColumn = new TableColumn("File Name");
		fileNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("fileName"));
		fileNameTableColumn.setMinWidth(160);
		TableColumn sourceTableColumn = new TableColumn("Sharer");
		sourceTableColumn.setCellValueFactory(new PropertyValueFactory<>("sharer"));
		sourceTableColumn.setMinWidth(120);
		TableColumn hashTableColumn = new TableColumn("Checksum");
		hashTableColumn.setCellValueFactory(new PropertyValueFactory<>("checksum"));
		hashTableColumn.setMinWidth(160);
		TableColumn sizeTableColumn = new TableColumn("Size (Byte)");
		sizeTableColumn.setCellValueFactory(new PropertyValueFactory<>("size"));
		sizeTableColumn.setMinWidth(120);
		fileTableView.getColumns().addAll(fileNameTableColumn, sourceTableColumn, hashTableColumn, sizeTableColumn);
		
		GridPane.setConstraints(fileTableView, 0, 3, 2, 4);
		grid.getChildren().add(fileTableView);

		// File Chooser
		FileChooser fileChooser = new FileChooser();

		// Initialize UI
		setupUiComponentAvailability(serverIpTextField, nickNameTextField, connectServerButton, 
				shareFileButton, unshareFileButton, getSharedFilesButton, receiveFileButton, 
				fileTableView, isConnected);

		// Setup Events Handlers
		connectServerButton.setOnAction((ActionEvent e) -> {
			connectServerButton.setDisable(true);

			if (!isConnected) {
				String ipAddress = serverIpTextField.getText();
				String nickName = nickNameTextField.getText();

				try {
					fileServer.accept();
					client.connect(ipAddress, nickName);
					fileTableView.setItems(getSharedFiles());

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
				fileServer.close();
				client.disconnect();
				isConnected = false;
			}
			setupUiComponentAvailability(serverIpTextField, nickNameTextField, connectServerButton, 
					shareFileButton, unshareFileButton, getSharedFilesButton, 
					receiveFileButton, fileTableView, isConnected);
			connectServerButton.setDisable(false);
		});
		shareFileButton.setOnAction((ActionEvent e) -> {
			File file = fileChooser.showOpenDialog(primaryStage);
			if ( file != null ) {
				try {
					String fileName = file.getName();
					String filePath = file.getAbsolutePath();
					String sharer = nickNameTextField.getText();
					String checksum = Files.hash(file, Hashing.md5()).toString();
					long size = file.length();
					
					SharedFile sharedFile = new SharedFile(fileName, sharer, checksum, size);
					boolean isFileShared = client.shareNewFile(sharedFile);

					if ( isFileShared ) {
						fileServer.shareNewFile(checksum, filePath);
						fileTableView.setItems(getSharedFiles());
						
						LOGGER.info("File shared: " + sharedFile);
					} else {
						throw new Exception("Something wrong with your network\n or there's a file have the same checksum.");
					}
				} catch (Exception ex) {
					LOGGER.catching(ex);
					
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Share Failed");
					alert.setHeaderText("Failed to share a file to Napster server.");
					alert.setContentText("Error message: " + ex.getMessage());
					alert.showAndWait();
				}
			}
		});
		unshareFileButton.setOnAction((ActionEvent e) -> {
			SharedFile selectedFile = fileTableView.getSelectionModel().getSelectedItem();

			String fileName = selectedFile.getFileName();
			String checksum = selectedFile.getChecksum();
			
			if ( client.unshareFile(fileName, checksum) ) {
				fileServer.unshareFile(checksum);
				fileTableView.setItems(getSharedFiles());

				LOGGER.info("File unshared: " + selectedFile);
			} else {
				LOGGER.error("Failed to unshare a file, because you have no right to unshare this file.");

				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("Share Failed");
				alert.setHeaderText("Failed to unshare a file from Napster server.");
				alert.setContentText("Error message: Permission deined.");
				alert.showAndWait();
			}
		});
		getSharedFilesButton.setOnAction((ActionEvent e) -> {
			fileTableView.setItems(getSharedFiles());
		});
		receiveFileButton.setOnAction((ActionEvent e) -> {
			receiveFileButton.setText("Please wait ...");
			receiveFileButton.setDisable(true);
			SharedFile selectedFile = fileTableView.getSelectionModel().getSelectedItem();

			fileChooser.setInitialFileName(selectedFile.getFileName());
			File file = fileChooser.showSaveDialog(primaryStage);
			if ( file != null ) {
				String checksum = selectedFile.getChecksum();

				try {
					if ( fileServer.contains(checksum) ) {
						throw new Exception("The file is shared by yourself.");
					}

					String ipAddress = client.getFileSharerIp(checksum).substring(1);
					if ( !ipAddress.equals("N/a") ) {
						LOGGER.debug("The IP of sharer: " + ipAddress);

						// Receive files and check if checksum is the same
						fileReceiver.receiveFile(checksum, file.getAbsolutePath(), ipAddress);
						String receivedChecksum = Files.hash(file, Hashing.md5()).toString();

						if ( checksum.equals(receivedChecksum) ) {
							LOGGER.info("File successfully received to: " + file.getAbsolutePath());
						} else {
							throw new Exception("Checksum is not the same, please try again.");
						}
					} else {
						throw new Exception("The file is no longer shared.");
					}
				} catch ( Exception ex ) {
					LOGGER.catching(ex);

					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Receive File Failed");
					alert.setHeaderText("Failed to receive a file from another sharer.");
					alert.setContentText("Error message: " + ex.getMessage());
					alert.showAndWait();
				}
			}
			receiveFileButton.setText("Receive Selected File");
			receiveFileButton.setDisable(false);
		});
		fileTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if ( fileTableView.getSelectionModel().getSelectedItem() != null )  {
				unshareFileButton.setDisable(false);
				receiveFileButton.setDisable(false);
			} else {
				unshareFileButton.setDisable(true);
				receiveFileButton.setDisable(true);
			}
		});

		primaryStage.setScene(scene);
	}

	/**
	 * Setup the availability of components in UI.
	 * 
	 * @param serverIpTextField    the text field for server IP
	 * @param nickNameTextField    the text field for nick name
	 * @param connectServerButton  the button for connecting/disconnect to/from server
	 * @param shareFileButton      the button for sharing files
	 * @param unshareFileButton    the button for unsharing the selected file
	 * @param getSharedFilesButton the button for get shared files
	 * @param receiveFileButton    the button for receive the selected file
	 * @param fileTableView        the TableView for display all shared files
	 * @param isConnected          whether is connected to server right now
	 */
	private void setupUiComponentAvailability(TextField serverIpTextField, TextField nickNameTextField,
			Button connectServerButton, Button shareFileButton, Button unshareFileButton, 
			Button getSharedFilesButton, Button receiveFileButton, TableView<SharedFile> fileTableView, 
			boolean isConnected) {
		if (isConnected) {
			serverIpTextField.setDisable(true);
			nickNameTextField.setDisable(true);
			connectServerButton.setText("Disconnect");
			shareFileButton.setDisable(false);
			getSharedFilesButton.setDisable(false);
		} else {
			serverIpTextField.setDisable(false);
			nickNameTextField.setDisable(false);
			connectServerButton.setText("Connect");
			shareFileButton.setDisable(true);
			unshareFileButton.setDisable(true);
			getSharedFilesButton.setDisable(true);
			receiveFileButton.setDisable(true);
			fileTableView.getItems().clear();
		}
	}

	/**
	 * Get shared files right now from server.
	 * @return a list of shared files
	 */
	private ObservableList<SharedFile> getSharedFiles() {
		List<SharedFile> sharedFiles = client.getSharedFiles();
		return FXCollections.observableArrayList(sharedFiles);
	}

	/**
	 * Napster client used for communicating with server.
	 */
	private static final Client client = Client.getInstance();
	
	/**
	 * FileServer used for receiving commands for sending files.
	 */
	private static final FileServer fileServer = FileServer.getInstance();

	/**
	 * FileReceiver used for receiving file stream.
	 */
	private static final FileReceiver fileReceiver = FileReceiver.getInstance();

	/**
	 * A variable stores whether the client is connected to server.
	 */
	private boolean isConnected = false;

	/**
	 * Logger.
	 */
	private static final Logger LOGGER = LogManager.getLogger(ApplicationBootstrap.class);
}
