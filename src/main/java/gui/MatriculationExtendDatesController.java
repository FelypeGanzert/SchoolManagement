package gui;

import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.DateUtil;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Alert.AlertType;
import model.dao.MatriculationDao;
import model.entites.Matriculation;
import model.entites.Parcel;

public class MatriculationExtendDatesController implements Initializable{

	@FXML private Spinner<Integer> spinnerNumberOfMonths;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController; 
	private List<Parcel> openParcels;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize fields
		initializeFields();
	}

	// =========================
	// ====== DEPENDENCES ======
	// =========================
	
	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
		// get all parcels from matriculation
		openParcels = matriculation.getParcels();
		// filter to get parcels with situation = ABERTA
		openParcels = openParcels.stream().filter(p -> p.getSituation().equals(ParcelStatusEnum.ABERTA.toString()))
				.collect(Collectors.toList());
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================

	public void handleBtnSave(ActionEvent event) {
		System.out.println("Clicked to save");
		// Get on much months should add
		int monthsToAdd = spinnerNumberOfMonths.getValue();
		for(Parcel p : openParcels) {
			// Get the data of parcel, add the monthsToAdd and set back to parcel
			Date parcelDate = p.getDateParcel();
			Calendar c = DateUtil.dateToCalendar(parcelDate);
			c.add(Calendar.MONTH, monthsToAdd);
			parcelDate = DateUtil.calendarToDate(c);
			p.setDateParcel(parcelDate);
		}
		// Save in DB
		try {
			MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
			// Update matriculation with the updated parcels
			if (matriculation.getCode() != null) {
				matriculationDao.update(matriculation);
			}
			// Update screen of matriculation info and close this form
			if (matriculationInfoController != null) {
				matriculationInfoController.onDataChanged();
			}
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(), AlertType.ERROR,
					Utils.currentStage(event));
			e.printStackTrace();
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	// ==========================
	// ===== END BUTTONS ========
	// ==========================

	// ==========================
	// === INITIALIZE METHODS  ==
	// ==========================
	
	private void initializeFields() {
		// ======= Spinner ============
		// IntegerSpinnerValueFactory(int min, int max, int initialValue, int
		// amountToStepBy)
		spinnerNumberOfMonths.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1, 1));
	}

}