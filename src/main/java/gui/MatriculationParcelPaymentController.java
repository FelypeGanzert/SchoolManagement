package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import db.DbExceptioneEntityExcluded;
import gui.util.Alerts;
import gui.util.DateUtil;
import gui.util.Utils;
import gui.util.Validators;
import gui.util.enums.MatriculationStatusEnum;
import gui.util.enums.ParcelStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.dao.CollaboratorDao;
import model.dao.ParcelDao;
import model.entites.Collaborator;
import model.entites.Parcel;
import sharedData.Globe;

public class MatriculationParcelPaymentController implements Initializable{

	@FXML private Label labelMatriculationCode;
	@FXML private Label labelParcelDocumentNumber;
	@FXML private Label labelParcelNumber;
	@FXML private Label labelStudentId;
	@FXML private Label labelStudentName;
	@FXML private ToggleGroup value;
	@FXML private RadioButton radioValueNormal; 
	@FXML private Label labelValueNormal;
	@FXML private RadioButton radioValueWithFineDelay; 
	@FXML private Label labelValueWithFineDelay;
	@FXML private RadioButton radioValueOther;
	@FXML private TextField textValueOther;
	@FXML private JFXComboBox<String> comboPaidWith;
	@FXML private JFXTextField textDatePayment;
	@FXML private JFXComboBox<String> comboPaymentReceivedBy;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Parcel parcel;
	private String[] paidWithMethods = {"DINHEIRO", "DÉBITO", "CRÉDITO", "BANCO", "CHEQUE", "OUTRO"};
	
	private MatriculationInfoParcels matriculationInfoParcelsController;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeFields();
		setListeners();
	}

	// =========================
	// ====== DEPENDENCES ======
	// =========================
	
	public void setParcel(Parcel parcel) {
		this.parcel = parcel;
		// refresh data
		try {
			DBUtil.refreshData(this.parcel);
		} catch (DbException | DbExceptioneEntityExcluded e) {
			e.printStackTrace();
		}
		setParcelDataToForm();
	}
	
	public void setMatriculationInfoParcels(MatriculationInfoParcels matriculationInfoParcelsController) {
		this.matriculationInfoParcelsController = matriculationInfoParcelsController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================

	public void handleBtnSave(ActionEvent event) {
		// check if the user has entry a valid date
		if (!textDatePayment.validate()) {
			return;
		}
		// Check if date of first parcel is after today, this can't happen
		try {
			if (!textDatePayment.getText().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (DateUtil.compareTwoDates(sdf.parse(textDatePayment.getText()), today) > 0) {
					Alerts.showAlert("Inválido", "Inválido: Data de pagamento.",
							"Não é possível fazer o pagamento em uma data do futuro", AlertType.ERROR,
							Utils.currentStage(event));
					// stop the method
					return;
				}
			}
		} catch (ParseException e) {
			System.out.println("Erro durante conversão para verificar datas...");
			e.printStackTrace();
		}
		try {
			// Get form data and parse payment to parcel
			getFormData();
			parcel.setSituation(ParcelStatusEnum.PAGA.toString());
		} catch(IllegalStateException e) {
			Alerts.showAlert("Erro", "Algo não está certo.",
					e.getMessage(),
					AlertType.ERROR, Utils.currentStage(event));
			return;
		}		
		// Check if all parcels are paid, if this happen we will change matriculation status to completed
		List<Parcel> parcelsNotPaid = new ArrayList<>();
		parcelsNotPaid = parcel.getMatriculation().getParcels().stream()
		.filter(p -> !p.getSituation().equalsIgnoreCase(ParcelStatusEnum.PAGA.toString()))
		.collect(Collectors.toList());
		if(parcelsNotPaid.size() == 0) {
			Alerts.showAlert("Matrícula concluída", "Matrícula CONCLUÍDA",
					"Todas as parcelas da matrícula foram pagas, portanto o status da matrícula foi alterado para CONCLUIDA.",
					AlertType.INFORMATION,
					Utils.currentStage(event));
			parcel.getMatriculation().setStatus(MatriculationStatusEnum.CONCLUIDA.toString());
		}
		// Save in DB
		try {
			ParcelDao parcelDao = new ParcelDao(DBFactory.getConnection());
			// Update matriculation with the updated parcels
			if (parcel.getDocumentNumber() != null) {
				parcelDao.update(parcel);
			}
			// Update screen of matriculation info and close this form
			if (matriculationInfoParcelsController != null) {
				matriculationInfoParcelsController.onDataChanged();
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
		// paid with
		comboPaidWith.getItems().addAll(paidWithMethods);
		comboPaidWith.getSelectionModel().selectFirst();
		// Create Required and Date validator
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// date payment: required, have to be a valid date
		textDatePayment.setValidators(requiredValidator);
		textDatePayment.setValidators(dateValidator);
		// set current date to date payment
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		textDatePayment.setText(sdf.format(new Date()));
		// payment received by
		try {
			// Get all initials from the collaborators in db
			List<String> collaboratorsInitials = new CollaboratorDao(DBFactory.getConnection()).findAllInitials();
			comboPaymentReceivedBy.getItems().addAll(collaboratorsInitials);
			// Select the current user logged
			String currentUser = Globe.getGlobe().getItem(Collaborator.class, "currentUser").getInitials();
			comboPaymentReceivedBy.getSelectionModel().select(currentUser);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	private void setListeners() {
		// other value
		textValueOther.textProperty().addListener((obs, oldValue, newValue) -> {
			// if user has typed anything, select the radio button parent
			if (!newValue.isEmpty()) {
				radioValueOther.setSelected(true);
			}
		});
	}
	
	// === AUXILIAR METHODS

	private String doubleToTextField(Double value) {
		return String.format("%.2f", value).replace(".", ",");
	}
	
	private Double textToDouble(String valueText) {
		return Double.valueOf(valueText.replace(",", "."));
	}
	
	private void setParcelDataToForm() {
		// === VALUE PAID ===
		// value paid: normal
		labelValueNormal.setText(doubleToTextField(parcel.getValue()));
		if(parcel.getValueWithFineDelay() != null) {
			labelValueWithFineDelay.setText(doubleToTextField(parcel.getValueWithFineDelay()));
		} else {
			radioValueWithFineDelay.setDisable(true);
		}
		labelMatriculationCode.setText(Integer.toString(parcel.getMatriculation().getCode()));
		labelParcelDocumentNumber.setText(Integer.toString(parcel.getDocumentNumber()));
		labelParcelNumber.setText(Integer.toString(parcel.getParcelNumber()));
		labelStudentId.setText(Integer.toString(parcel.getMatriculation().getStudent().getId()));
		labelStudentName.setText(parcel.getMatriculation().getStudent().getName());
	}

	private void getFormData() {
		// ========= VALUE PAID ============
		// value paid: normal
		if (radioValueNormal.isSelected()) {
			parcel.setValuePaid(parcel.getValue());
		}
		// value paid: value with fine delay
		if (radioValueWithFineDelay.isSelected()) {
			parcel.setValuePaid(parcel.getValueWithFineDelay());
		}
		// value paid: other
		if (value.getSelectedToggle().equals(radioValueOther)) {
			if(textValueOther.getText().isEmpty()) {
				throw new IllegalStateException("Foi selecionado que foi pago outro valor, porém não foi inserido nenhum valor.");
			}
			Double valueOther = textToDouble(textValueOther.getText());
			parcel.setValuePaid(valueOther);
		}
		// ===== PAID WITH =======
		parcel.setPaidWith(comboPaidWith.getSelectionModel().getSelectedItem());
		// ===== DATE PAYMENT =====
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			parcel.setDatePayment(sdf.parse(textDatePayment.getText()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// === PAYMENT RECEIVED BY ====
		parcel.setPaymentReceivedBy(comboPaymentReceivedBy.getSelectionModel().getSelectedItem());
	}

}