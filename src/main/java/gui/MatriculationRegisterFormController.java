package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import gui.util.Constraints;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import model.entites.Responsible;

public class MatriculationRegisterFormController implements Initializable{

	// Simple Informations
	@FXML private JFXTextField textDate;
	@FXML private JFXComboBox<String> comboBoxMatriculatedBy;
	@FXML private JFXTextField textReason;
	@FXML private JFXTextArea textAreaServiceContracted;
	@FXML private JFXComboBox<Responsible> comboBoxResponsible;
	@FXML private JFXButton btnSelectPaymentPlan;
	// Normal Value
	@FXML private TextField textMatriculationTax;
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
	@FXML private JFXTextField textMatriculationTaxDate;
	@FXML private JFXTextField textFirstParcelDate;
	@FXML private Spinner<Integer> spinnerParcelsDueDate;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private String lastValueChanged = "parcelValue";
	private String lastValueFineDelayChanged = "valueFineDelay";
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Set requiredFields and Constraints
		initializeFields();
		// Set fields listeners
		setListeners();
		// Set default values
		setDefaultValuesToFields();
		// Define entities daos
		defineEntitiesDaos();
	}

	// ==========================
	// ==== START BUTTONS =======
	// ==========================
	
	public void handleBtnSave(ActionEvent event) {
		System.out.println("Save button clicked");
	}
	
	public void handleBtnCancel(ActionEvent event) {
		System.out.println("Cancel button clicked");
	}

	// ==========================
	// ===== END BUTTONS ========
	// ==========================
	
	// ==========================
	// === INITIALIZE METHODS  ==
	// ==========================
	
	private void initializeFields() {
		// Disable some fieds
		textPercentValueFineDelay.setDisable(true);
		spinnerDaysFineDelay.setDisable(true);
		textMatriculationTaxDate.setDisable(true);
		textFirstParcelDate.setDisable(true);
		spinnerParcelsDueDate.setDisable(true);
		// === Constraints and validators
		// Create Required and Date validator
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// Matriculation date: required and need to be a valid date
		textDate.setValidators(requiredValidator);
		textDate.setValidators(dateValidator);
		// Others dates
		textMatriculationTaxDate.setValidators(dateValidator);
		textFirstParcelDate.setValidators(dateValidator);
		// ServiceContracted: required
		textAreaServiceContracted.setValidators(requiredValidator);
		// MatriculationTax, ParcelValue, TotalValue, ValueFineDelay, TotalValueWithFineDelay  : only number with ,
		Constraints.setTextFieldDoubleMoney(textMatriculationTax);
		Constraints.setTextFieldDoubleMoney(textParcelValue);
		Constraints.setTextFieldDoubleMoney(textTotalValue);
		Constraints.setTextFieldDoubleMoney(textValueFineDelay);
		Constraints.setTextFieldDoubleMoney(textParcelValueWithFineDelay);
		Constraints.setTextFieldDoubleMoney(textTotalValueWithFineDelay);
		// Dates
		Constraints.setTextFieldMaxLength(textDate, 10);
		Constraints.setTextFieldMaxLength(textMatriculationTaxDate, 10);
		Constraints.setTextFieldMaxLength(textFirstParcelDate, 10);
		// ======= Spinners ============
		// IntegerSpinnerValueFactory(int min, int max, int initialValue, int amountToStepBy)
		spinnerNumberOfParcels.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,40, 0, 1));
		spinnerDaysFineDelay.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,15, 0, 1));
		spinnerParcelsDueDate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,25, 0, 1));		
	}

	private void setListeners() {
		// ====== MATRICULATION TAX ========
		textMatriculationTax.textProperty().addListener((obs, oldValue, newValue) -> {
			// if is null: disable matriculation tax date
			if(newValue.isEmpty()) {
				textMatriculationTaxDate.setText("");
				textMatriculationTaxDate.setDisable(true);
			} else {
				textMatriculationTaxDate.setDisable(false);
			}
		});
		// ============ NUMBER OF PARCELS ========
		spinnerNumberOfParcels.valueProperty().addListener((obs, oldValue, newValue) -> {
			// disable first parcel date, total and % of fine delay
			// if number of parcels = 0
			if (newValue == 0) {
				textFirstParcelDate.setText("");
				textFirstParcelDate.setDisable(true);
				textTotalValue.setText("");
				textTotalValue.setDisable(true);
			} else {
				textFirstParcelDate.setDisable(false);
				textTotalValue.setDisable(false);
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
		// ====== FIRST PARCAL DATE ========
		textFirstParcelDate.textProperty().addListener((obs, oldValue, newValue) -> {
			// if is null: disable parcels due date
			if (newValue.isEmpty()) {
				spinnerParcelsDueDate.setDisable(true);
			} else {
				spinnerParcelsDueDate.setDisable(false);
			}
		});
	}

	private String doubleToTextField(Double value) {
		return String.format("%.2f", value).replace(".", ",");
	}
	
	private Double textToDouble(String valueText) {
		return Double.valueOf(valueText.replace(",", "."));
	}
	

	private void setDefaultValuesToFields() {
		
	}
	
	private void defineEntitiesDaos() {
		
	}

}