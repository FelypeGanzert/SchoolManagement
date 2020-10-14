package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.enums.MatriculationStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.dao.MatriculationDao;
import model.entites.Matriculation;

public class MatriculationStatusFormController implements Initializable {

	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
	}

	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;		
	}
	
	// STILL HAVE TO IMPLEMENT SOME BUSINESS LOGIG:
	// - just allow to change to CONCLUIDA if every parcel is paid
	// - if the user change to CANCELADA, get all open parcels (parcels with status ABERTA) and set status to CANCELADA

	public void handleBtnClick(ActionEvent event) {
		if (matriculation == null) {
			throw new IllegalStateException("Matriculation is null");
		}
		MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
		// Try to update matriculation status
		try {
			// Get the status clicked and set in matriculation
			String btnText = ((JFXButton) event.getSource()).getText();
			matriculation.setStatus(MatriculationStatusEnum.fromString(btnText).toString());
			// Update matriculation in db
			matriculationDao.update(matriculation);
			// Finally, if everything occurs fine, we update MatriculationInfo and close this form
			this.matriculationInfoController.onDataChanged();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(),
					AlertType.ERROR, Utils.currentStage(event));
		}
	}

}
