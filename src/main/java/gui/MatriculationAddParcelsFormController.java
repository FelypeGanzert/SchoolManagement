package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.DateUtil;
import gui.util.Utils;
import gui.util.Validators;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import model.dao.MatriculationDao;
import model.entites.Matriculation;
import model.entites.Parcel;

public class MatriculationAddParcelsFormController implements Initializable{

	// Normal Value
	@FXML private Spinner<Integer> spinnerNumberOfParcels;
	@FXML private TextField textParcelValue;
	@FXML private TextField textTotalValue;
	// Fine Delay informations
	@FXML private TextField textValueFineDelay;
	@FXML private TextField textPercentValueFineDelay;
	@FXML private Spinner<Integer> spinnerDaysFineDelay;
	@FXML private TextField textParcelValueWithFineDelay;
	@FXML private TextField textTotalValueWithFineDelay;
	// Payment's date
	@FXML private JFXTextField textFirstParcelDate;
	@FXML private Spinner<Integer> spinnerParcelsDueDate;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
		
	private String lastValueChanged = "parcelValue";
	private String lastValueFineDelayChanged = "valueFineDelay";
	
	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController; 
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Set requiredFields and Constraints
		initializeFields();
		// Set fields listeners
		setListeners();
		// Set default values
		setDefaultValuesToFields();
	}
	
	// =========================
	// ====== DEPENDENCES ======
	// =========================
	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================
	
	public void handleBtnSave(ActionEvent event) {
		try {
			// check if fields is valid, we have theses situations to stop this method:
			// 1- firstParcelDate is empty or not a valid date
			if (!textFirstParcelDate.validate()) {
				return;
			}
			// today is before the first date
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (today.compareTo(sdf.parse(textFirstParcelDate.getText())) >  0) {
					Alerts.showAlert("Inválido", "Data de pagamento inválida.",
							"Só é possível adicionar uma parcela com vencimento a partir de um dia útil depois de hoje.",
							AlertType.ERROR, Utils.currentStage(event));
					// stop the method
					return;
				}
			} catch (ParseException e) {
				System.out.println("Erro durante conversão para verificar datas...");
				e.printStackTrace();
			}

			// Check if there is a parcel value seted
			if(textParcelValue.getText().isEmpty()) {
				Alerts.showAlert("Inválido", "Sem valor inserido.",
						"É necessário definir um valor para as parcelas.", AlertType.ERROR, Utils.currentStage(event));
				// stop the method
				return;
			}
			// Get data in form and generate a matriculation
			getFormData();
			// Save in DB
			try {
				MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
				// Update matriculation with the news parcels
				if (matriculation.getCode() != null) {
					matriculationDao.update(matriculation);
				}
				// Update screen of matriculation info and close this form
				if(matriculationInfoController != null) {
					matriculationInfoController.onDataChanged();
				}
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),
						AlertType.ERROR, Utils.currentStage(event));
				e.printStackTrace();
			} 
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR,
					Utils.currentStage(event));
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	// ==========================
	// ===== END BUTTONS ========
	// ==========================
	
	
	// ===== AUXILIAR METHODS =====
	
	// set all informations in the form to matriculation
	private void getFormData() {
		// ========== PARCELS ==============
		if(spinnerNumberOfParcels.getValue() > 0 && !textParcelValue.getText().isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			// parcel value
			Double parcelValue = textToDouble(textParcelValue.getText());
			// fine delay value
			Double valueFineDelay = 0.0;
			if(!textValueFineDelay.getText().isEmpty()) {
				valueFineDelay = textToDouble(textValueFineDelay.getText());
			}
			// days fine delay
			Integer daysFineDelay = spinnerDaysFineDelay.getValue();
			// number of parcels
			Integer numberOfParcels = spinnerNumberOfParcels.getValue();
			// first parcel date
			Date firstParcelDate = null;
			if(!textFirstParcelDate.getText().isEmpty()) {
				try {
					firstParcelDate = sdf.parse(textFirstParcelDate.getText());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// parcels due date
			Integer parcelsDueDate = spinnerParcelsDueDate.getValue();
			// number of the last parcel already in matriculation
			Integer lastNumber = 0;
			Parcel lastParcel = matriculation.getParcels().get(matriculation.getParcels().size() -1);
			if(lastParcel != null) {
				lastNumber = lastParcel.getParcelNumber();
			};
			// create parcels and add to matriculation
			for(int i = (lastNumber + 1); i < (lastNumber + 1 + numberOfParcels); i++) {
				Parcel parcel = new Parcel();
				parcel.setMatriculation(matriculation);
				matriculation.getParcels().add(parcel);
				// number
				parcel.setParcelNumber(i);
				// date
				parcel.setDateParcel(firstParcelDate);
				// add a month to first date parcel, and change the day to the user has set if i == 1
				if(firstParcelDate != null) {
					Calendar c = DateUtil.dateToCalendar(firstParcelDate);
					c.add(Calendar.MONTH, 1);
					if(i == (lastNumber + 1)) {
						c.set(Calendar.DAY_OF_MONTH, parcelsDueDate);
					}
					firstParcelDate = DateUtil.calendarToDate(c);
				}
				// value
				parcel.setValue(parcelValue);
				parcel.setDaysFineDelay(daysFineDelay);
				parcel.setValueFineDelay(valueFineDelay);
				parcel.setSituation(ParcelStatusEnum.ABERTA.toString());
			}			
		}		
	}
	
	// ==========================
	// === INITIALIZE METHODS  ==
	// ==========================
	
	private void initializeFields() {
		// Disable some fieds
		textPercentValueFineDelay.setDisable(true);
		spinnerDaysFineDelay.setDisable(true);
		spinnerParcelsDueDate.setDisable(true);
		// === Constraints and validators
		// Create Required and Date validator
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// First Parcel: required and have to be a valid date
		textFirstParcelDate.setValidators(requiredValidator);
		textFirstParcelDate.setValidators(dateValidator);
		// ParcelValue, TotalValue, ValueFineDelay, TotalValueWithFineDelay  : only number with ,
		Constraints.setTextFieldDoubleMoney(textParcelValue);
		Constraints.setTextFieldDoubleMoney(textTotalValue);
		Constraints.setTextFieldDoubleMoney(textValueFineDelay);
		Constraints.setTextFieldDoubleMoney(textParcelValueWithFineDelay);
		Constraints.setTextFieldDoubleMoney(textTotalValueWithFineDelay);
		// First Parcel Date
		Constraints.setTextFieldMaxLength(textFirstParcelDate, 10);
		// ======= Spinners ============
		// IntegerSpinnerValueFactory(int min, int max, int initialValue, int
		// amountToStepBy)
		spinnerNumberOfParcels.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 40, 1, 1));
		spinnerDaysFineDelay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 15, 0, 1));
		spinnerParcelsDueDate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 25, 1, 1));
	}

	private void setListeners() {
		// ============ NUMBER OF PARCELS ========
		spinnerNumberOfParcels.valueProperty().addListener((obs, oldValue, newValue) -> {
			// if is <= 1: hidden parcels due date
			if (newValue <= 1) {
				spinnerParcelsDueDate.setDisable(true);
			} else {
				spinnerParcelsDueDate.setDisable(false);
			}
			// Calculate normal total value if value of parcel is not empty
			String textParcelValue = this.textParcelValue.getText();
			if (!textParcelValue.isEmpty()) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValue);
				value = value * newValue;
				this.textTotalValue.setText(doubleToTextField(value));
			} else {
				this.textTotalValue.setText("");
			}
			// Calculate total value with fine delay
			// if value of parcel with fine delay is not empty
			String textParcelValueWithFineDelay = this.textParcelValueWithFineDelay.getText();
			if (!textParcelValueWithFineDelay.isEmpty()) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValueWithFineDelay);
				value = value * newValue;
				this.textTotalValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				this.textTotalValueWithFineDelay.setText("");
			}
			
		});
		// ======== PARCEL VALUE ==========
		textParcelValue.textProperty().addListener((obs, oldValue, newValue) -> {
			String textParcelValue = newValue;
			if (this.textParcelValue.isFocused()) {
				lastValueChanged = "parcelValue";	
			}
			// disable  % of fine delay if number of parcels = 0
			if (newValue.isEmpty() && !lastValueFineDelayChanged.equals("percentValueFineDelay")) {
				textPercentValueFineDelay.setText("");
				textPercentValueFineDelay.setDisable(true);
			} else {
				textPercentValueFineDelay.setDisable(false);
			}
			// Calculate percent value OR fine delay value
			if (!textValueFineDelay.getText().isEmpty() && lastValueFineDelayChanged.equals("valueFineDelay")) {
				if (!textParcelValue.isEmpty()) {
					// prevent division by zero
					if(textToDouble(textParcelValue) != 0.0) {
						Double percent = textToDouble(textValueFineDelay.getText()) / textToDouble(textParcelValue) * 100;
						textPercentValueFineDelay.setText(String.format("%.4f", percent).replace(".", ","));
					} else {
						textPercentValueFineDelay.setText("");
					}
				}
			} else if (lastValueFineDelayChanged.equals("valueFineDelay")) {
				textPercentValueFineDelay.setText("");
			}
			if (!textPercentValueFineDelay.getText().isEmpty() && lastValueFineDelayChanged.equals("percentValueFineDelay")) {
				if (!textParcelValue.isEmpty()) {
					Double valueFineDelay = textToDouble(newValue) * textToDouble(textPercentValueFineDelay.getText()) / 100;
					textValueFineDelay.setText(String.format("%.2f", valueFineDelay).replace(".", ","));
				} else {
					textValueFineDelay.setText("");
				}
			} else if (lastValueFineDelayChanged.equals("percentValueFineDelay")) {
				textValueFineDelay.setText("");
			}
			// ======= Values with fine delay =====
			Double valueFineDelay = 0.0;
			// get value of fine delay
			if(!textValueFineDelay.getText().isEmpty()) {
				valueFineDelay = textToDouble(textValueFineDelay.getText());
			}
			// Update value with fine delay
			if(!textParcelValue.isEmpty()) {
				Double value = textToDouble(textParcelValue);
				value = value + valueFineDelay;
				textParcelValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				textParcelValueWithFineDelay.setText("");
			}
			// Calculate normal total value if value of parcel is not empty
			if (!textParcelValue.isEmpty() && lastValueChanged.equals("parcelValue")) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValue);
				value = value * ((double) spinnerNumberOfParcels.getValue());
				this.textTotalValue.setText(doubleToTextField(value));
			} else if(lastValueChanged.equals("parcelValue")){
				this.textTotalValue.setText("");
			}
			// Calculate total value with fine delay
			// if value of parcel with fine delay is not empty
			String textParcelValueWithFineDelay = this.textParcelValueWithFineDelay.getText();
			if (!textParcelValueWithFineDelay.isEmpty()) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValueWithFineDelay);
				value = value * ((double) spinnerNumberOfParcels.getValue());
				this.textTotalValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				this.textTotalValueWithFineDelay.setText("");
			}
		});
		// =========== TOTAL VALUE ==========
		textTotalValue.textProperty().addListener((obs, oldValue, newValue) -> {
			if(textTotalValue.isFocused()) {
				lastValueChanged = "totalValue";	
			}
			String textTotalValue = newValue;
			// Calculate normal parcel value
			if (!textTotalValue.isEmpty() && lastValueChanged.equals("totalValue")) {
				// parcel = total / number of parcels
				Double value = textToDouble(textTotalValue);
				value = value / ((double) spinnerNumberOfParcels.getValue());
				this.textParcelValue.setText(doubleToTextField(value));
			} else if(lastValueChanged.equals("totalValue")){
				this.textParcelValue.setText("");
			}
			// ======= Values with fine delay =====
			Double valueFineDelay = 0.0;
			// get value of fine delay
			if (!textValueFineDelay.getText().isEmpty()) {
				valueFineDelay = textToDouble(textValueFineDelay.getText());
			}
			// Update value with fine delay
			if (!textParcelValue.getText().isEmpty()) {
				Double value = textToDouble(textParcelValue.getText());
				value = value + valueFineDelay;
				textParcelValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				textParcelValueWithFineDelay.setText("");
			}
			// Calculate total value with fine delay
			// if value of parcel with fine delay is not empty
			String textParcelValueWithFineDelay = this.textParcelValueWithFineDelay.getText();
			if (!textParcelValueWithFineDelay.isEmpty()) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValueWithFineDelay);
				value = value * ((double) spinnerNumberOfParcels.getValue());
				;
				this.textTotalValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				this.textTotalValueWithFineDelay.setText("");
			}
		});
		// ========= FINE DELAY VALUE =========
		textValueFineDelay.textProperty().addListener((obs, oldValue, newValue) -> {
			if(textValueFineDelay.isFocused()) {
				lastValueFineDelayChanged = "valueFineDelay";	
			}
			// disable days of fine delay
			if (newValue.isEmpty()) {
				spinnerDaysFineDelay.setDisable(true);
			} else {
				spinnerDaysFineDelay.setDisable(false);
			}
			// Calculate percent value
			if(!newValue.isEmpty() && lastValueFineDelayChanged.equals("valueFineDelay")) {
				if(!textParcelValue.getText().isEmpty()) {
					if(textToDouble(textParcelValue.getText()) != 0.0) {
						Double percent = textToDouble(newValue) / textToDouble(textParcelValue.getText())  * 100;
						textPercentValueFineDelay.setText(String.format("%.4f", percent).replace(".", ","));
					} else {
						textPercentValueFineDelay.setText("");
					}
				}
			} else if (lastValueFineDelayChanged.equals("valueFineDelay")) {
				textPercentValueFineDelay.setText("");
			}
			// ======= Values with fine delay =====
			Double valueFineDelay = 0.0;
			// get value of fine delay
			if (!newValue.isEmpty()) {
				valueFineDelay = textToDouble(textValueFineDelay.getText());
			}
			// Update value with fine delay
			if (!textParcelValue.getText().isEmpty()) {
				Double value = textToDouble(textParcelValue.getText());
				value = value + valueFineDelay;
				textParcelValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				textParcelValueWithFineDelay.setText("");
			}
		});
		// ========= FINE DELAY PERCENT =========
		textPercentValueFineDelay.textProperty().addListener((obs, oldValue, newValue) -> {
			if (textPercentValueFineDelay.isFocused()) {
				lastValueFineDelayChanged = "percentValueFineDelay";
			}
			// Calculate value based in percent
			if (!newValue.isEmpty() && lastValueFineDelayChanged.equals("percentValueFineDelay")
					&& !textParcelValue.getText().isEmpty()) {
				Double valueFineDelay = textToDouble(textParcelValue.getText()) * textToDouble(newValue) / 100;
				textValueFineDelay.setText(String.format("%.2f", valueFineDelay).replace(".", ","));
			} else if (lastValueFineDelayChanged.equals("percentValueFineDelay")) {
				textValueFineDelay.setText("");
			}
		});
		// ========= PARCEL VALUE WITH FINE DELAY =========
		textParcelValueWithFineDelay.textProperty().addListener((obs, oldValue, newValue) -> {
			// Calculate total value with fine delay
			// if value of parcel with fine delay is not empty
			String textParcelValueWithFineDelay = newValue;
			if (!textParcelValueWithFineDelay.isEmpty()) {
				// total = parcel value * number of parcels
				Double value = textToDouble(textParcelValueWithFineDelay);
				value = value * ((double) spinnerNumberOfParcels.getValue());
				this.textTotalValueWithFineDelay.setText(doubleToTextField(value));
			} else {
				this.textTotalValueWithFineDelay.setText("");
			}
		});
	}
	
	private void setDefaultValuesToFields() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date today = new Date();
		Calendar c = DateUtil.dateToCalendar(today);
		c.add(Calendar.DAY_OF_MONTH, 1);
		today = DateUtil.calendarToDate(c);
		textFirstParcelDate.setText(sdf.format(today));
	}

	private String doubleToTextField(Double value) {
		return String.format("%.2f", value).replace(".", ",");
	}
	
	private Double textToDouble(String valueText) {
		return Double.valueOf(valueText.replace(",", "."));
	}
	
}