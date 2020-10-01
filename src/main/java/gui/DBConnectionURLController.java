package gui;

import java.net.URL;
import java.util.ResourceBundle;

import org.hibernate.HibernateException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import gui.util.Utils;
import gui.util.Validators;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class DBConnectionURLController implements Initializable {

	@FXML private JFXTextField txtURL;
	@FXML private JFXButton btnConnect;
	@FXML private ImageView imageLoading;
	@FXML private Label labelError;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	private void changeLoadingVisibility() {
		imageLoading.setVisible(!imageLoading.isVisible());
	}

	public void handleBtnConnect(ActionEvent event) {
		labelError.setVisible(false);
		if (txtURL.validate()) {
			// We disable the button to user doenst continues clicking
			btnConnect.setDisable(true);
			tryToConnect(event);
		}
	}

	private void tryToConnect(ActionEvent event) {
		changeLoadingVisibility();
		DBFactory.setUnits(txtURL.getText());
		// Try to connect in a different thread to not freeze the UI
		Thread threadConnection = new Thread(() -> {
			try {
				DBFactory.getConnection();
				Platform.runLater(() -> {
					// If connection is established, them close DBConnection dialogStage
					Utils.currentStage(event).close();
					// show LoginForm
					Roots.loginForm(this);
				});
			} catch (HibernateException e) {
				e.printStackTrace();
				Platform.runLater(() -> {
					showErrorMessage("Erro ao se conectar com o banco de dados.");
				});
			} finally {
				changeLoadingVisibility();
			}
		});
		threadConnection.start();
	}

	private void showErrorMessage(String message) {
		labelError.setVisible(true);
		labelError.setText(message);
		btnConnect.setDisable(false);
		txtURL.getValidators().add(Validators.getRequiredFieldValidator());
	}
}