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
import javafx.scene.image.ImageView;

public class DBConnectionURLController implements Initializable{

	@FXML private JFXTextField txtURL;
	@FXML private JFXButton btnConnect;
	@FXML private ImageView imageLoading;
	@FXML private Label labelError;
	private Main main; // To call the login screen
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	private void showErrorMessage(String message) {
		labelError.setVisible(true);
		labelError.setText(message);
		btnConnect.setDisable(false);
	}
	
	private void changeLoadingVisible() {
		boolean visibility = imageLoading.isVisible() ? false : true;
		imageLoading.setVisible(visibility);
	}
	
	private void tryToConnect(ActionEvent event) {
		changeLoadingVisible();
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
				e.printStackTrace();
				Platform.runLater(() -> {
					showErrorMessage("Erro ao se conectar com o banco de dados.");
				});
			} finally {
				changeLoadingVisible();
			}
		});
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