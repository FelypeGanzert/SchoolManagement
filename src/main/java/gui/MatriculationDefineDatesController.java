package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.DateUtil;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import model.dao.MatriculationDao;
import model.entites.Matriculation;
import model.entites.Parcel;

public class MatriculationDefineDatesController implements Initializable{

	@FXML private Label labelParcels;
	@FXML private JFXTextField textFirstParcelDate;
	@FXML private Spinner<Integer> spinnerParcelsDueDate;
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
		openParcels = openParcels.stream().filter(p -> p.getSituation().equals(ParcelStatusEnum.ABERTA.toString()) && p.getParcelNumber() != 0)
				.collect(Collectors.toList());
		// Show in Label of UI the parcels that are open
		String labelOpenParcels = "|";
		for(Parcel p : openParcels) {
			labelOpenParcels += " " + p.getParcelNumber() + " |";
		}
		this.labelParcels.setText(labelOpenParcels);
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================

	public void handleBtnSave(ActionEvent event) {
		// Check if user as set a valid date
		if (!textFirstParcelDate.validate()) {
			return;
		}
		// Check if date of first parcel is before today, this can't happen
		try {
			if (!textFirstParcelDate.getText().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (DateUtil.compareTwoDates(sdf.parse(textFirstParcelDate.getText()), today) < 0) {
					Alerts.showAlert("Inválido", "Inválido: Data de vencimento da 1ª Parcela.",
							"Não é possível definir mensalidades com data de vencimento no passado",
							AlertType.ERROR, Utils.currentStage(event));
					// stop the method
					return;
				}
			}
		} catch (ParseException e) {
			System.out.println("Erro durante conversão para verificar datas...");
			e.printStackTrace();
		}
			// parcels due date
			Integer parcelsDueDate = spinnerParcelsDueDate.getValue();
			// first parcel date
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date firstParcelDate = null;
			try {
				firstParcelDate = sdf.parse(textFirstParcelDate.getText());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// change date of parcels
			for(Parcel p : openParcels) {
				// date
				p.setDateParcel(firstParcelDate);
				// add a month to first date parcel, and change the day to the user has set if i == 1
				if(firstParcelDate != null) {
					Calendar c = DateUtil.dateToCalendar(firstParcelDate);
					c.add(Calendar.MONTH, 1);
					c.set(Calendar.DAY_OF_MONTH, parcelsDueDate);
					firstParcelDate = DateUtil.calendarToDate(c);
				}
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
		// Create Required and Date validator
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
		requiredValidator.setMessage("Necessário");
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textFirstParcelDate.setValidators(requiredValidator);
		textFirstParcelDate.setValidators(dateValidator);
		// ======= Spinner ============
		// IntegerSpinnerValueFactory(int min, int max, int initialValue, int amountToStepBy)
		spinnerParcelsDueDate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 25, 1, 1));
	}

}