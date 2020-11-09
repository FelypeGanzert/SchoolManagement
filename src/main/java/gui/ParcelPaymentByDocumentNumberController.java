package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import animatefx.animation.Shake;
import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FXMLPath;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import model.dao.ParcelDao;
import model.entites.Parcel;

public class ParcelPaymentByDocumentNumberController implements Initializable {

	@FXML private JFXTextField textDocumentNumber;
	@FXML private Label labelError;
	@FXML private JFXButton btnFind;
	
	private ParcelDao parcelDao;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// hidden error message
		labelError.setVisible(false);
		// only allows number in text field to searc
		Constraints.setTextFieldInteger(textDocumentNumber);
		Constraints.setTextFieldMaxLength(textDocumentNumber, 9);
		//  init parcelDao
		parcelDao = new ParcelDao(DBFactory.getConnection());
	}
	
	public void handleBtnFind(ActionEvent event) {
		labelError.setVisible(false);
		if(textDocumentNumber.getText().isEmpty()) {
			showError("Nenhum número inserido");
			return;
		}
		Integer documentNumber = Utils.tryParseToInt(textDocumentNumber.getText());
		if(documentNumber == null) {
			showError("Há algo errado no número " + documentNumber);
			return;
		}
		try {
			btnFind.setDisable(true);
			Parcel parcel = parcelDao.findById(documentNumber);
			if (parcel != null) {
				// check if parcel is paid
				if (parcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.PAGA.toString())) {
					showError("A parcela #" + documentNumber + " já está PAGA.");
				} else if (parcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.ACORDO.toString())) {
					showError("A parcela #" + documentNumber + " está em ACORDO.");
				} else if (parcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.CANCELADA.toString())) {
					showError("A parcela #" + documentNumber + " está CANCELADA.");
				} else {
					// Open screen to pay parcel
					Utils.loadView(this, true, FXMLPath.MATRICULATION_PARCEL_PAYMENT, Utils.currentStage(event),
							"Baixar Pagamento", false, (MatriculationParcelPaymentController controller) -> {
								controller.setParcel(parcel);
							});

				}
				textDocumentNumber.clear();
				textDocumentNumber.requestFocus();
			} else {
				showError("Não encontrado parcela com número: " + documentNumber);
			}
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("ERRO", "DBException", e.getMessage(), AlertType.ERROR, Utils.currentStage(event));
		} finally {
			btnFind.setDisable(false);
		}
	}

	private void showError(String message) {
		labelError.setText(message);
		labelError.setVisible(true);
		new Shake(labelError).play();
		
	}
	
	
}
