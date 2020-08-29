package gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.HibernateException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import application.Main;
import db.DB;
import gui.util.Utils;
import gui.util.Validators;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

public class DBConnectionURLController implements Initializable{

	@FXML private JFXTextField txtURL;
	@FXML private JFXButton btnConnect;
	@FXML private ProgressBar progressConnection;
	@FXML private Label labelError;
	private Main main; // To call the login screen
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		
	}
	
	public void setMain(Main main) {
		this.main = main;
	}

	private void updateProgressConnection(int value) {
		// User will entry with a value beetwen 0 - 100 and we convert to 0 -1
		double progress = Double.valueOf(value) / 100;
		progressConnection.setProgress(progress);
	}
	
	private void showErrorMessage(String message) {
		labelError.setVisible(true);
		labelError.setText(message);
		btnConnect.setDisable(false);
	}
	
	private void tryToConnect(ActionEvent event) {
		DB.setUnits(txtURL.getText());
		// In threads to not freeze the UI
		Thread threadConnection = new Thread(() -> {
			try {
				DB.getFactory(); // Try to connect
				// Close dialog when connect
				Platform.runLater(() -> {
					main.showLoginForm();
					Utils.currentStage(event).close();
				});
			} catch (HibernateException e) {
				Platform.runLater(() -> {
					showErrorMessage("Erro ao se conectar com o banco de dados.");
				});
			}
		});
		Thread threadProgress = new Thread(() -> {
			boolean connectionThreadEnded = false; // Flag
			progressConnection.setVisible(true);
			for (int i = 0; i <= 100; i++) {
				try {
					Thread.sleep(400);
					final int iFinal = i;
					// Update progress bar
					Platform.runLater(() -> {
						updateProgressConnection(iFinal);
					});
					// Set progress bar to the end 
					if (!threadConnection.isAlive() && !connectionThreadEnded) {
						connectionThreadEnded = true;
						i = 98;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		threadProgress.start();
		threadConnection.start();
	}

	public void handleBtnConnect(ActionEvent event) {
		labelError.setVisible(false);
		txtURL.getValidators().add(Validators.getRequiredFieldValidator());
		if (txtURL.validate()) {
			btnConnect.setDisable(true);
			tryToConnect(event);
		}
	}
	
}