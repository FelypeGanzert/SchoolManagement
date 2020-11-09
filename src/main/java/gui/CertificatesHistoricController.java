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
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.DateUtil;
import gui.util.Roots;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import model.dao.CertificateHistoricDao;
import model.entites.CertificateHistoric;

public class CertificatesHistoricController implements Initializable{

	@FXML private Label labelTotalCertificates;
	// Filter Historic
	@FXML private JFXTextField textFilter;
	@FXML private ToggleGroup filterType;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private Label labelFilterResults;
	@FXML private JFXButton btnClearFilter;
	// table historic
	@FXML private TableView<CertificateHistoric> tableHistoric;
	@FXML private TableColumn<CertificateHistoric, String> columnStudentName;
	@FXML private TableColumn<CertificateHistoric, String> columnCourse;
	@FXML private TableColumn<CertificateHistoric, Date> columnStartDate;
	@FXML private TableColumn<CertificateHistoric, Date> columnEndDate;
	@FXML private TableColumn<CertificateHistoric, Integer> columnCourseLoad;
	@FXML private TableColumn<CertificateHistoric, Date> columnPrintDate;
	@FXML private TableColumn<CertificateHistoric, String> columnFullRecordPath;
	
	private CertificateHistoricDao certificateHistoricDao;
	private List<CertificateHistoric> allCertificates;	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize tables
		initializeTableCertificates();
		addFieldsConstraints();
		initDaos();
		getCertificatesFromDB();
		// dates text fields validators
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textStartDate.setValidators(dateValidator);
		textEndDate.setValidators(dateValidator);
		Constraints.setTextFieldMaxLength(textStartDate, 10);
		Constraints.setTextFieldMaxLength(textEndDate, 10);
	}
	
	// ======= BUTTONS ======
	
	public void handleBtnReturn(ActionEvent event) {
		Roots.certificatesMenu();
	}

	public void handleBtnFilter(ActionEvent event) {
		// check if user has entry a valid date
		if(!textStartDate.getText().isEmpty() && !textStartDate.validate()) {
			return;
		}
		if(!textEndDate.getText().isEmpty() && !textEndDate.validate()) {
			return;
		}
		// Auxiliar list to doenst interfery where all certificates are stored
		List<CertificateHistoric> filteredCertificates = new ArrayList<>();
		filteredCertificates = allCertificates;
		// Filter by text in search bar
		String textSearch = textFilter.getText();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if (filterTypeSelected.equalsIgnoreCase("inicia com") && textSearch.length() > 0) {
			filteredCertificates = filteredCertificates.stream()
					.filter(certificate -> certificate.getStudentName() != null
							&& certificate.getStudentName().toUpperCase().startsWith(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		} else if (filterTypeSelected.equalsIgnoreCase("contém") && textSearch.length() > 0) {
			filteredCertificates = filteredCertificates.stream()
					.filter(certificate -> certificate.getStudentName() != null
							&& certificate.getStudentName().toUpperCase().contains(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		}
		// filter by dates inserted
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date startPrintDate = null;
		Date endPrintDate = null;
		try {
			if(!textStartDate.getText().isEmpty()) {
				startPrintDate = sdf.parse(textStartDate.getText());
			}
			if(!textEndDate.getText().isEmpty()) {
				endPrintDate = sdf.parse(textEndDate.getText());
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// filter by start and end date
		if(startPrintDate != null && endPrintDate != null) {
			System.out.println("Will filter only by both date");
			final Date finalStartDate = startPrintDate;
			final Date finalEndDate = endPrintDate;
			filteredCertificates = filteredCertificates.stream()
					.filter(certificate -> certificate.getPrintDate() != null &&
					DateUtil.compareTwoDates(certificate.getPrintDate(), finalStartDate) >= 0 &&
					DateUtil.compareTwoDates(certificate.getPrintDate(), finalEndDate) <= 0)
					.collect(Collectors.toList());
		}
		// filter only by start date
		if(startPrintDate != null && endPrintDate == null) {
			System.out.println("Will filter only by start date");
			final Date finalStartDate = startPrintDate;
			filteredCertificates = filteredCertificates.stream()
					.filter(certificate -> certificate.getPrintDate() != null &&
					DateUtil.compareTwoDates(certificate.getPrintDate(), finalStartDate) >= 0)
					.collect(Collectors.toList());
		}
		// filter only by end date
		if(startPrintDate == null && endPrintDate != null) {
			final Date finalEndDate = endPrintDate;
			filteredCertificates = filteredCertificates.stream()
					.filter(certificate -> certificate.getPrintDate() != null &&
					DateUtil.compareTwoDates(certificate.getPrintDate(), finalEndDate) <= 0)
					.collect(Collectors.toList());
		}
		// convert auxiliarList to Observable List and set in tableCertificates
		ObservableList<CertificateHistoric> filteredObsList = FXCollections.observableArrayList(filteredCertificates);
		tableHistoric.setItems(filteredObsList);
		tableHistoric.refresh();
		if (filteredCertificates.size() > 0) {
			labelFilterResults.setText("Resultados: " + filteredCertificates.size());
		} else {
			labelFilterResults.setText("Nenhum resultado encontrado");
		}
		// show button to clear filters
		btnClearFilter.setVisible(true);
	}

	public void handleBtnClearFilter(ActionEvent event) {
		tableHistoric.setItems(FXCollections.observableArrayList(allCertificates));
		labelFilterResults.setText("");
		textFilter.clear();
		textStartDate.clear();
		textEndDate.clear();
		// hidden button to clear filters
		btnClearFilter.setVisible(false);
	}

	public void handleBtnPrintReport(ActionEvent event) {
		System.out.println("Clicked to print a report");
	}

	// ============================
	// ===== AUXILIAR METHODS =====
	// ============================

	// get requests from database and put in ui
	private void getCertificatesFromDB() {
		if (certificateHistoricDao == null) {
			throw new IllegalStateException("CertificateHistoricDao service not initialized");
		}
		try {
			allCertificates = FXCollections.observableArrayList(certificateHistoricDao.findAll());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar dados", "DBException", e.getMessage(), AlertType.ERROR, null);
		}
		tableHistoric.setItems(FXCollections.observableArrayList(allCertificates));
		if(allCertificates != null && allCertificates.size() != 0) {
			labelTotalCertificates.setText("( " + allCertificates.size() + " certificados )");
		}
	}

	// ==========================
	// ====== INIT METHODS ======
	// ==========================

	private void initDaos() {
		this.certificateHistoricDao = new CertificateHistoricDao(DBFactory.getConnection());
	}

	// table Certificates
	private void initializeTableCertificates() {
		// student name
		columnStudentName.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnStudentName, "studentName");
		columnStudentName.setReorderable(false);
		// course
		columnCourse.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnCourse, "course");
		columnCourse.setReorderable(false);
		// dates: start, end and print
		Utils.setCellValueFactory(columnStartDate, "startDate");
		Utils.formatTableColumnDate(columnStartDate, "dd/MM/yyyy");
		columnStartDate.setReorderable(false);
		Utils.setCellValueFactory(columnEndDate, "endDate");
		Utils.formatTableColumnDate(columnEndDate, "dd/MM/yyyy");
		columnEndDate.setReorderable(false);
		Utils.setCellValueFactory(columnPrintDate, "printDate");
		Utils.formatTableColumnDate(columnPrintDate, "dd/MM/yyyy");
		columnPrintDate.setReorderable(false);
		// course load
		Utils.setCellValueFactory(columnCourseLoad, "courseLoad");
		columnCourseLoad.setReorderable(false);
		// full record path
		Utils.setCellValueFactory(columnFullRecordPath, "fullRecordPath");
		columnFullRecordPath.setReorderable(false);
	}
	
	private void addFieldsConstraints() {
		
	}

}