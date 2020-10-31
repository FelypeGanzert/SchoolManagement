package gui;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.util.Callback;
import model.dao.MatriculationDao;
import model.entites.Agreement;
import model.entites.AgreementParcel;
import model.entites.Collaborator;
import model.entites.Matriculation;
import model.entites.Parcel;
import sharedData.Globe;

public class MatriculationParcelsAgreementController implements Initializable{

	@FXML private TableView<Parcel> tableParcels;
	@FXML private TableColumn<Parcel, Boolean> columnSelected;
	@FXML private TableColumn<Parcel, Integer> columnDocumentNumber;
	@FXML private TableColumn<Parcel, Integer> columnParcelNumber;
	@FXML private TableColumn<Parcel, Double> columnValue;
	@FXML private TableColumn<Parcel, Double> columnValueWithFineDelay;
	@FXML private TableColumn<Parcel, Date> columnDateParcel;
	@FXML private JFXComboBox<String> comboBoxValueToConsider;
	@FXML private TextField textEntryValue;
	@FXML private TextField textAgreementValue;
	@FXML private Spinner<Integer> spinnerNumberOfParcels;
	@FXML private TextField textParcelsAgreementValue;
	@FXML private JFXTextField textFirstParcelDate;
	@FXML private Spinner<Integer> spinnerParcelsDueDate;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController; 
	private ObservableList<Parcel> lateParcels;
	
	private Double totalValue;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeFields();
		initializeTable();
		setListeners();
		// To do in listeners:
		// update total value if value to consider change
		// show total value
		// update total value according to the entry value
		// show value of each parcel when number of parcels changed
	}
	
	private void initializeFields() {
		// entry value
		Constraints.setTextFieldDoubleMoney(textEntryValue);
		// first parcel date: required and have to be a valid date
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textFirstParcelDate.setValidators(requiredValidator);
		textFirstParcelDate.setValidators(dateValidator);
		Constraints.setTextFieldMaxLength(textFirstParcelDate, 10);
		// values to consider
		String[] valuesToConsider = {"Valor com multa", "Valor normal"};
		comboBoxValueToConsider.getItems().addAll(valuesToConsider);
		comboBoxValueToConsider.getSelectionModel().selectFirst();
		// === SPINNER ===
		// IntegerSpinnerValueFactory(int min, int max, int initialValue, int amountToStepBy)
		spinnerNumberOfParcels.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 1, 1));
		spinnerParcelsDueDate.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 25, 1, 1));
	}
	
	private void initializeTable() {
		columnSelected.setCellValueFactory(cellData -> cellData.getValue().getSelected());
		columnSelected.setCellFactory(CheckBoxTableCell.forTableColumn(columnSelected));
		columnSelected.setCellFactory(CheckBoxTableCell.forTableColumn(new Callback<Integer, ObservableValue<Boolean>>() {

		    @Override
		    public ObservableValue<Boolean> call(Integer param) {
		        //System.out.println("Parcel "+lateParcels.get(param).getParcelNumber()+" changed value to " +lateParcels.get(param).isSelected());
		    	updateTotalValue();
		        return lateParcels.get(param).getSelected();
		    }
		}));
		columnSelected.setReorderable(false);
		// document number
		Utils.setCellValueFactory(columnDocumentNumber, "documentNumber");
		columnDocumentNumber.setReorderable(false);
		// parcel number
		Utils.setCellValueFactory(columnParcelNumber, "parcelNumber");
		columnParcelNumber.setReorderable(false);
		// value
		Utils.setCellValueFactory(columnValue, "value");
		Utils.formatTableColumnDoubleCurrency(columnValue);
		columnValue.setReorderable(false);
		// value with fine delay
		Utils.setCellValueFactory(columnValueWithFineDelay, "valueWithFineDelay");
		Utils.formatTableColumnDoubleCurrency(columnValueWithFineDelay);
		columnValueWithFineDelay.setReorderable(false);
		// date parcel
		Utils.setCellValueFactory(columnDateParcel, "dateParcel");
		Utils.formatTableColumnDate(columnDateParcel, "dd/MM/yyyy");
		columnDateParcel.setReorderable(false);
	}
	
	// =========================
	// ====== DEPENDENCES ======
	// =========================
	
	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
		// get all parcels from matriculation
		List<Parcel> parcels = matriculation.getParcels();
		// filter to get parcels with situation = ATRASADA
		Date now = new Date();
		parcels = parcels.stream().filter(p -> {
			return p.getSituation().equalsIgnoreCase("ABERTA") &&
					p.getDateParcel() != null &&
					DateUtil.compareTwoDates(p.getDateParcel(), now) < 0;
		}).collect(Collectors.toList());
		// select all parcels filtered
		parcels.forEach(p -> p.setSelected(true));
		// put in the table
		this.lateParcels = FXCollections.observableArrayList(parcels);
		tableParcels.setItems(this.lateParcels);
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================

	public void handleBtnSave(ActionEvent event) {
		// check if date of first parcel is valid
		if(!textFirstParcelDate.validate()) {
			return;
		}
		// Check if date of first parcel is before today, this can't happen
		try {
			if (!textFirstParcelDate.getText().isEmpty()) {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (DateUtil.compareTwoDates(sdf.parse(textFirstParcelDate.getText()), today) < 0) {
					Alerts.showAlert("Inválido", "Inválido: Data de vencimento da 1ª Parcela.",
							"Não é possível definir mensalidades com data de vencimento no passado", AlertType.ERROR,
							Utils.currentStage(event));
					// stop the method
					return;
				}
			}
		} catch (ParseException e) {
			System.out.println("Erro durante conversão para verificar datas...");
			e.printStackTrace();
		}
		// check if there 0 selected parcel, this cant happen
		List<Parcel> selectedParcels = lateParcels.stream().filter(p -> p.isSelected()).collect(Collectors.toList());
		if(selectedParcels.size() <= 0) {
			Alerts.showAlert("Nada para acordar", "Nada selecionado.",
					"Não foi selecionado nenhuma parcela para fazer o acordo.", AlertType.ERROR, Utils.currentStage(event));
			// stop the method
			return;
		}
		// =========================================
		// ====== LOGIC TO CREATE A AGREEMENT ======
		// =========================================
		// create a new agreement
		Agreement agreement = new Agreement();
		matriculation.getAgreements().add(agreement);
		agreement.setMatriculation(matriculation);
		// change situation of selected parcels to ACORDO (agreement)
		// and  vinculate parcels of matriculation with the agreement
		selectedParcels.forEach(p -> {
			p.setSituation(ParcelStatusEnum.ACORDO.toString());
			p.setAgreement(agreement);
			agreement.getNormalParcels().add(p);
		});
		// agreement infos
		agreement.setDateAgreement(new Date());
		agreement.setAgreementBy(Globe.getGlobe().getItem(Collaborator.class, "currentUser").getInitials());
		// === parcel of entry value
		boolean hasEntryValue = false;
		if(!textEntryValue.getText().isEmpty()) {
			Double entryValue = textToDouble(textEntryValue.getText());
			if(entryValue > 0) {
				hasEntryValue = true;
				AgreementParcel entryParcel = new AgreementParcel();
				entryParcel.setAgreement(agreement);
				agreement.getParcels().add(entryParcel);
				entryParcel.setDateParcel(new Date());
				entryParcel.setSituation(ParcelStatusEnum.ABERTA.toString());
				entryParcel.setValue(entryValue);
				entryParcel.setParcelNumber(1);
			}
		}
		// === other parcels
		Double parcelValue = textToDouble(textParcelsAgreementValue.getText());
		if (parcelValue > 0.0) {
			// parcels due date
			Integer parcelsDueDate = spinnerParcelsDueDate.getValue();
			// number of parcels
			Integer numberOfParcels = spinnerNumberOfParcels.getValue();
			// first parcel date
			Date firstParcelDate = null;
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			try {
				firstParcelDate = sdf.parse(textFirstParcelDate.getText());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// create parcels and add to matriculation
			int parcelNumber = 1;
			if (hasEntryValue) {
				parcelNumber = 2;
			}
			for (int i = parcelNumber; i < (numberOfParcels + parcelNumber); i++) {
				AgreementParcel parcel = new AgreementParcel();
				parcel.setAgreement(agreement);
				agreement.getParcels().add(parcel);
				// number
				parcel.setParcelNumber(i);
				// date
				parcel.setDateParcel(firstParcelDate);
				// add a month to first date parcel, and change the day to
				Calendar c = DateUtil.dateToCalendar(firstParcelDate);
				c.add(Calendar.MONTH, 1);
				c.set(Calendar.DAY_OF_MONTH, parcelsDueDate);
				firstParcelDate = DateUtil.calendarToDate(c);
				// value
				parcel.setValue(parcelValue);
				parcel.setSituation(ParcelStatusEnum.ABERTA.toString());
			}
		}
		// Updated matriculation in DB
		try {
			MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
			// Update matriculation with the agreement created
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
	
	public void handleBtnSelectAll(ActionEvent event) {
		lateParcels.forEach(p -> p.setSelected(true));
	}
	
	public void handleBtnUnselectAll(ActionEvent event) {
		lateParcels.forEach(p -> p.setSelected(false));
	}

	// ==========================
	// ===== END BUTTONS ========
	// ==========================

	// ==========================
	// === INITIALIZE METHODS  ==
	// ==========================
	
	private void setListeners() {
		// Listener to click on a row to change the selection of that parcel
		tableParcels.setRowFactory( tv -> {
		    TableRow<Parcel> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (! row.isEmpty()) {
		        	// get that data in the row and change selection
		        	Parcel rowData = row.getItem();
		            rowData.setSelected(!rowData.isSelected());
		        }
		    });
		    return row ;
		});
		// Listener to value to consider comboBox
		comboBoxValueToConsider.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateTotalValue();
			}
		});
		// Listener to entry value
		textEntryValue.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateTotalValue();
			}
		});
		// Listener to number of parcels
		spinnerNumberOfParcels.valueProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				updateParcelsValue();
			}
		});
	}
	
	// ============================
	// ==== AUXILIAR METHODS ======
	// ============================
	
	public void updateTotalValue() {
		String valueToConsider = comboBoxValueToConsider.getSelectionModel().getSelectedItem();
		Double entryValue = 0.0;
		if(!textEntryValue.getText().isEmpty()) {
			entryValue = textToDouble(textEntryValue.getText());
		}
		totalValue = 0.0;
		if (valueToConsider.equalsIgnoreCase("Valor normal")) {
			for(Parcel p : lateParcels) {
				if(p.isSelected()) {
					totalValue += p.getValue();
				}
			}
		} else if (valueToConsider.equalsIgnoreCase("Valor com multa")) {
			for(Parcel p : lateParcels) {
				if(p.isSelected()) {
					totalValue += p.getValueWithFineDelay();
				}
			}
		}
		totalValue -= entryValue;
		textAgreementValue.setText(doubleToTextField(totalValue));
		updateParcelsValue();
	}
	
	public void updateParcelsValue() {
		Integer numberOfParcels = spinnerNumberOfParcels.getValue();
		Double parcelsValue = totalValue / numberOfParcels;
		System.out.println(parcelsValue);
		textParcelsAgreementValue.setText(doubleToTextField(roundValue(parcelsValue)));
	}
	
	private String doubleToTextField(Double value) {
		return String.format("%.2f", value).replace(".", ",");
	}
	
	private Double textToDouble(String valueText) {
		return Double.valueOf(valueText.replace(",", "."));
	}
	
	private Double roundValue(double value) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
		DecimalFormat df = new DecimalFormat("0.0", symbols);
		df.setRoundingMode(RoundingMode.HALF_UP);
		return Double.valueOf(df.format(value));
	}
		
}