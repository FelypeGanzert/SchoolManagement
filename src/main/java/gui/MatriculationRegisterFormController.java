package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import animatefx.animation.FadeInLeft;
import animatefx.animation.FadeOutRight;
import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.DateUtil;
import gui.util.FXMLPath;
import gui.util.Utils;
import gui.util.Validators;
import gui.util.enums.MatriculationStatusEnum;
import gui.util.enums.ParcelStatusEnum;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.dao.CollaboratorDao;
import model.dao.MatriculationDao;
import model.entites.Collaborator;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Responsible;
import model.entites.Student;
import sharedData.Globe;

public class MatriculationRegisterFormController implements Initializable{

	// Simple Informations
	@FXML private JFXTextField textDate;
	@FXML private JFXComboBox<String> comboBoxMatriculatedBy;
	@FXML private JFXTextField textReason;
	@FXML private JFXTextArea textAreaServiceContracted;
	@FXML private JFXButton btnSelectServiceContracted;
	@FXML private JFXComboBox<Responsible> comboBoxResponsible;
	@FXML private Pane paneResponsibleInfo;
	@FXML private Button btnRemoveResponsible;
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
	
	
	private boolean isBtnRemoveResponsibleVisible;
	private boolean isUnderage;
	
	private String lastValueChanged = "parcelValue";
	private String lastValueFineDelayChanged = "valueFineDelay";
	
	private Matriculation matriculation;
	private Student student;
	
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
	public void setStudent(Student student) {
		this.student = student;
		// comboBox responsible
		List<Responsible> list = new ArrayList<>();
		list.addAll(this.student.getAllResponsibles());
		comboBoxResponsible.setItems(FXCollections.observableArrayList(list));
		// show only the name of the responsible in comboBox
		comboBoxResponsible.setConverter(new StringConverter<Responsible>() {
			@Override
			public String toString(Responsible object) {
				return object.getName();
			}
			@Override
			public Responsible fromString(String string) {
				return null;
			}
		});
		// select the last responsible if the age of the student is less than 18
		// and change value to doens't allow to remove the responsible 
		if(student.getAge() < 18) {
			isUnderage = true;
			comboBoxResponsible.getSelectionModel().selectLast();
		}
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================
	
	public void handleBtnSave(ActionEvent event) {
		try {
			// check if fields is valid, we have theses situations to stop this method:
			// 1- textDate is empty or not a valid date
			// 2 - textAreaService is empty
			// 3 - If matriculationTaxDate is not empty, we check if is a valid date
			// 4 - If firstParcelDate is not empty, we check if is a valid date
			if (!textDate.validate() || !textAreaServiceContracted.validate()
					|| (textMatriculationTaxDate.getText() != null && textMatriculationTaxDate.getText().length() > 0
							&& !textMatriculationTaxDate.validate())
					|| (textFirstParcelDate.getText() != null && textFirstParcelDate.getText().length() > 0
							&& !textFirstParcelDate.validate())) {
				return;
			}
			// Check if date of matriculation is after today, this can't happen
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (DateUtil.compareTwoDates(sdf.parse(textDate.getText()), today) > 0) {
					Alerts.showAlert("Inválido", "A data de cadastro é posterior a data atual do computador.",
							"É impossível fazer uma matrícula no futuro.", AlertType.ERROR, Utils.currentStage(event));
					// stop the method
					return;
				}
			} catch (ParseException e) {
				System.out.println("Erro durante conversão para verificar datas...");
				e.printStackTrace();
			}
			// Check if date of tax is before today, this can't happen
			try {
				if(!textMatriculationTaxDate.getText().isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date today = new Date();
					if (DateUtil.compareTwoDates(sdf.parse(textMatriculationTaxDate.getText()), today) < 0) {
						Alerts.showAlert("Inválido", "Inválido: Data de vencimento da Taxa de Matrícula.",
								"Não é possível realizar uma nova matrícula com data de vencimento no passado", AlertType.ERROR, Utils.currentStage(event));
						// stop the method
						return;
					}	
				}
			} catch (ParseException e) {
				System.out.println("Erro durante conversão para verificar datas...");
				e.printStackTrace();
			}		
			// Check if date of first parcel is before today, this can't happen
			try {
				if(!textFirstParcelDate.getText().isEmpty()) {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date today = new Date();
					if (DateUtil.compareTwoDates(sdf.parse(textFirstParcelDate.getText()), today) < 0) {
						Alerts.showAlert("Inválido", "Inválido: Data de vencimento da 1ª Parcela.",
								"Não é possível realizar uma nova matrícula com data de vencimento no passado", AlertType.ERROR, Utils.currentStage(event));
						// stop the method
						return;
					}	
				}
			} catch (ParseException e) {
				System.out.println("Erro durante conversão para verificar datas...");
				e.printStackTrace();
			}
			// Get data in form and generate a matriculation
			getFormData();
			// Save in DB
			try {
				MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
				// If he doesn't have a id, is a new matriculation,
				// otherwhise the course already is in database
				// ... Even I know that this screen will only be used to add
				// a new matriculation, its is important make this verification
				if (matriculation.getCode() == null) {
					// set current student to matriculation
					matriculation.setStudent(student);
					student.getMatriculations().add(matriculation);
					matriculationDao.insert(matriculation);
				} else {
					matriculationDao.update(matriculation);
				}
				// Go to screen of matriculation info and close this form
				MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
				mainView.setContent(FXMLPath.MATRICULATION_INFO, (MatriculationInfoController controller) -> {
					controller.setCurrentMatriculation(matriculation, FXMLPath.INFO_STUDENT);
				});
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
	
	public void handleBtnRemoveResponsible(ActionEvent event) {
		// clear responsible selection
		comboBoxResponsible.getSelectionModel().clearSelection();
		// clear Responsible Info
		paneResponsibleInfo.getChildren().clear();
	}

	// ==========================
	// ===== END BUTTONS ========
	// ==========================
	
	
	// ===== AUXILIAR METHODS =====
	
	// set all informations in the form to matriculation
	private void getFormData() {
		// initialize Matriculation if is null
		if(matriculation == null) {
			matriculation = new Matriculation();
		}
		//
		matriculation.setStatus(MatriculationStatusEnum.ABERTA.toString());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		// matriculation date
		try {
			matriculation.setDateMatriculation(sdf.parse(textDate.getText()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// matriculatedBy
		matriculation.setMatriculatedBy(comboBoxMatriculatedBy.getSelectionModel().getSelectedItem());
		// reason
		matriculation.setReason(textReason.getText().trim());
		// service contracted
		matriculation.setServiceContracted(textAreaServiceContracted.getText().trim());
		// responsible for the matriculation
		if(comboBoxResponsible.getSelectionModel().getSelectedItem() != null) {
			matriculation.setResponsible(comboBoxResponsible.getSelectionModel().getSelectedItem());
		}
		// ========== PARCELS ==============
		// Matriculation tax
		if(!textMatriculationTax.getText().isEmpty()) {
			// create parcel to tax
			Parcel taxParcel = new Parcel();
			taxParcel.setMatriculation(matriculation);
			matriculation.getParcels().add(taxParcel);
			// parcel number
			taxParcel.setParcelNumber(0);
			// tax value
			Double taxValue = textToDouble(textMatriculationTax.getText());
			taxParcel.setValue(taxValue);
			// tax date
			if (!textMatriculationTaxDate.getText().isEmpty()) {
				try {
					Date taxDate = sdf.parse(textMatriculationTaxDate.getText());
					taxParcel.setDateParcel(taxDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			// tax situation
			taxParcel.setSituation(ParcelStatusEnum.ABERTA.toString());
		}
		// other parcels
		if(spinnerNumberOfParcels.getValue() > 0 && !textParcelValue.getText().isEmpty()) {
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
			// create parcels and add to matriculation
			for(int i = 1; i <= numberOfParcels; i++) {
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
					if(i == 1) {
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
		spinnerParcelsDueDate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,25, 1, 1));
		// comboBox: matriculated by
		try {
			// Get all initials from the collaborators in db
			List<String> collaboratorsInitials = new CollaboratorDao(DBFactory.getConnection()).findAllInitials();
			comboBoxMatriculatedBy.getItems().addAll(collaboratorsInitials);
			// Select the current user logged
			String currentUser = Globe.getGlobe().getItem(Collaborator.class, "currentUser").getInitials();
			comboBoxMatriculatedBy.getSelectionModel().select(currentUser);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void setListeners() {
		// ======= COMBO BOX: RESPONSIBLE =========
		comboBoxResponsible.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// animation to show the button to remove the responsible if he not visible
				if(!isBtnRemoveResponsibleVisible && !isUnderage) {
					new FadeInLeft(btnRemoveResponsible).play();
					btnRemoveResponsible.setVisible(true);
					isBtnRemoveResponsibleVisible = true;
				}
				// Show Responsible Infos
				try {
					FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.MATRICULATION_INFO_PERSON));
					VBox newContent = loader.load();
					MatriculationInfoPerson controller = loader.getController();
					// clear Responsible Info and set the informations of selected responsible
					if (paneResponsibleInfo.getChildren() != null) {
						paneResponsibleInfo.getChildren().clear();
					}
					paneResponsibleInfo.getChildren().add(newContent);
					controller.setPerson(newValue);
				} catch (IOException e) {
					e.printStackTrace();
					Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR, null);
				} catch (IllegalStateException e) {
					e.printStackTrace();
					Alerts.showAlert("IllegalStateException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR,
							null);
				}
			} else {
				// hidden the button to remove the responsible
				new FadeOutRight(btnRemoveResponsible).play();
				isBtnRemoveResponsibleVisible = false;
				
			}
		});

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
	
	private void setDefaultValuesToFields() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		textDate.setText(sdf.format(new Date()));
	}

	private String doubleToTextField(Double value) {
		return String.format("%.2f", value).replace(".", ",");
	}
	
	private Double textToDouble(String valueText) {
		return Double.valueOf(valueText.replace(",", "."));
	}
	
}