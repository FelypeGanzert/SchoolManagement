package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.enums.MatriculationStatusEnum;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.dao.MatriculationDao;
import model.entites.Matriculation;
import model.entites.Parcel;

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
			// Get the status clicked
			String statusSelected = ((JFXButton) event.getSource()).getText();
			// Status complete: all parcels need to be paid (PAGA)
			if(statusSelected.equalsIgnoreCase(MatriculationStatusEnum.CONCLUIDA.toString())) {
				for(Parcel p : matriculation.getParcels()) {
					if(!p.getSituation().equalsIgnoreCase(ParcelStatusEnum.PAGA.toString())) {
						// if has any parcel that is not paid, we show an alert and stop the method
						Alerts.showAlert("Erro", "Existem parcelas que não estão com o status PAGA",
								"Não é possível alterar o status para CONCLUÍDA com parcelas pendentes",
								AlertType.ERROR, Utils.currentStage(event));
						return;
					}
				}
			}
			// Status canceled: all open parcels will change to canceled
			if(statusSelected.equalsIgnoreCase(MatriculationStatusEnum.CANCELADA.toString())) {
				for(Parcel p : matriculation.getParcels()) {
					if(p.getSituation().equalsIgnoreCase(ParcelStatusEnum.ABERTA.toString())) {
						p.setSituation(ParcelStatusEnum.CANCELADA.toString());
					}
				}
			}
			// Status open: all canceled parcels will change to open
			if(statusSelected.equalsIgnoreCase(MatriculationStatusEnum.ABERTA.toString())) {
				for(Parcel p : matriculation.getParcels()) {
					if(p.getSituation().equalsIgnoreCase(ParcelStatusEnum.CANCELADA.toString())) {
						p.setSituation(ParcelStatusEnum.ABERTA.toString());
					}
				}
			}
			matriculation.setStatus(MatriculationStatusEnum.fromString(statusSelected).toString());
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
