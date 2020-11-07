package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Roots;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
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
	private ObservableList<CertificateHistoric> allCertificates;
	private ObservableList<CertificateHistoric> filteredCertificates;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize tables
		initializeTableCertificates();
		addFieldsConstraints();
		initDaos();
		getCertificatesFromDB();
	}
	
	// ======= BUTTONS ======
	
	public void handleBtnReturn(ActionEvent event) {
		Roots.certificatesMenu();
	}
	
	public void handleBtnFilter(ActionEvent event) {
		
	}
	
	public void handleBtnClearFilter(ActionEvent event) {
		
	}
	
	public void handleBtnPrintReport(ActionEvent event) {
		
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
		filteredCertificates = allCertificates;
		tableHistoric.setItems(FXCollections.observableArrayList(filteredCertificates));
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