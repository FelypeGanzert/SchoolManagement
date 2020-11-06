package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Roots;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dao.CertificateRequestDao;
import model.entites.CertificateRequest;
import model.entites.Matriculation;

public class CertificatesRequestsController implements Initializable{

	// table certificate requests
	@FXML private TableView<CertificateRequest> tableRequests;
	@FXML private TableColumn<CertificateRequest, Integer> columnStudentId;
	@FXML private TableColumn<CertificateRequest, String> columnStudentName;
	@FXML private TableColumn<CertificateRequest, String> columnCourse;
	@FXML private TableColumn<CertificateRequest, Date> columnStartDate;
	@FXML private TableColumn<CertificateRequest, Date> columnEndDate;
	@FXML private TableColumn<CertificateRequest, Integer> columnCourseLoad;
	@FXML private TableColumn<CertificateRequest, Date> columnRequestDate;
	@FXML private TableColumn<CertificateRequest, CertificateRequest> columnAddToPrint;
	@FXML private TableColumn<CertificateRequest, CertificateRequest> columnRemoveRequest;
	// selected request: student matriculation
	@FXML private Label labelSelectedStudentName;
	@FXML private TableView<Matriculation> tableMatriculations;
	@FXML private TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML private TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML private TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML private TableColumn<Matriculation, String> columnMatriculationParcels;
	// certificates to be printed
	@FXML private TableView<CertificateRequest> tablePrint;
	@FXML private TableColumn<CertificateRequest, Integer> columnPrintStudentId;
	@FXML private TableColumn<CertificateRequest, String> columnPrintStudentName;
	@FXML private TableColumn<CertificateRequest, CertificateRequest> columnPrintRemoveFromPrint;
	// certificate infos
	@FXML private JFXTextArea textCourse;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textCourseLoad;
	@FXML private JFXTextField textPrintDate;
	@FXML private JFXTextField textRecordNumber;
	@FXML private JFXTextField textRecordPageNumber;
	
	private CertificateRequestDao requestDao;
	private List<CertificateRequest> requestsList;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize tables
		initializeTableRequests();
		//initializeTableMatriculations();
		//initiliazeTablePrint();
		initRequestDao();
		getRequestsFromDB();
	}
	
	// =====================
	// === START BUTTONS ===
	// =====================
	
	// Return button
	public void handleBtnReturn(ActionEvent event) {
		Roots.certificatesMenu();
	}
	
	// Print Certificates
	public void handleBtnPrint(ActionEvent event) {
		System.out.println("Clicked to print");
	}

	// =====================
	// ==== END BUTTONS ====
	// =====================
	
	// ============================
	// ===== AUXILIAR METHODS =====
	// ============================
	
	public void initRequestDao() {
		this.requestDao = new CertificateRequestDao(DBFactory.getConnection());
	}
	
	// get requests from database and put in ui
	public void getRequestsFromDB() {
		if(requestDao == null) {
			throw new IllegalStateException("RequestDao service not initialized");
		}
		try {
			requestsList = FXCollections.observableArrayList(this.requestDao.findAll());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os pedidos", "DBException", e.getMessage(), AlertType.ERROR, null);
		}
		tableRequests.setItems(FXCollections.observableArrayList(requestsList));
	}
	
	// ==========================
	// === START INIT METHODS ===
	// ==========================

	// Requests
	public void initializeTableRequests() {
		// student info: id, name
		Utils.setCellValueFactory(columnStudentId, "studentId");
		columnStudentId.setReorderable(false);
		columnStudentName.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnStudentName, "studentName");
		columnStudentName.setReorderable(false);
		// course
		columnCourse.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnCourse, "course");
		columnCourse.setReorderable(false);
		// dates: start, end and request
		Utils.setCellValueFactory(columnStartDate, "startDate");
		Utils.formatTableColumnDate(columnStartDate, "dd/MM/yyyy");
		columnStartDate.setReorderable(false);
		Utils.setCellValueFactory(columnEndDate, "endDate");
		Utils.formatTableColumnDate(columnEndDate, "dd/MM/yyyy");
		columnEndDate.setReorderable(false);
		Utils.setCellValueFactory(columnRequestDate, "requestDate");
		Utils.formatTableColumnDate(columnRequestDate, "dd/MM/yyyy");
		columnRequestDate.setReorderable(false);
		// course load
		Utils.setCellValueFactory(columnCourseLoad, "courseLoad");
		columnCourseLoad.setReorderable(false);
		// buttons
		// ========== TODO
		// columnAddToPrint
		// columnRemoveRequest
	}

	// ==========================
	// ==== END INIT METHODS ====
	// ==========================
}