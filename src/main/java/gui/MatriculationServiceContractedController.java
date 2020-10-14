package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.dao.MatriculationDao;
import model.entites.Matriculation;

public class MatriculationServiceContractedController implements Initializable{

	@FXML private JFXTextArea textServiceContracted;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		textServiceContracted.setValidators(Validators.getRequiredFieldValidator());
	}
	
	// DEPENDENCES
	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
		textServiceContracted.setText(matriculation.getServiceContracted());
	}
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	public void handleSaveBtn(ActionEvent event) {
		if (textServiceContracted.validate()) {
			matriculation.setServiceContracted(textServiceContracted.getText());
			// try to save updated matriculation in db
			try {
				MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
				matriculationDao.update(matriculation);
				// Update MatriculationInfoController and then close this form
				matriculationInfoController.onDataChanged();
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),
						AlertType.ERROR, Utils.currentStage(event));
				e.printStackTrace();
			} 
		}
	}
	
	public void handleCancelBtn(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
}