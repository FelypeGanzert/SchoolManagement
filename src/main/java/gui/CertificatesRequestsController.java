package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Icons;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.Validators;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dao.CertificateHistoricDao;
import model.dao.CertificateRequestDao;
import model.dao.MatriculationDao;
import model.entites.CertificateHistoric;
import model.entites.CertificateRequest;
import model.entites.Matriculation;
import model.entites.Parcel;

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
	@FXML private Label labelNumberToPrint;
	// certificate infos
	@FXML private JFXTextArea textCourse;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textCourseLoad;
	@FXML private JFXTextField textPrintDate;
	@FXML private JFXTextField textRecordNumber;
	@FXML private JFXTextField textRecordPageNumber;
	
	private CertificateRequestDao requestDao;
	private MatriculationDao matriculationDao;
	private CertificateHistoricDao certificateDao;
	private List<CertificateRequest> requestsList;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize tables
		initializeTableRequests();
		initializeTableMatriculations();
		initiliazeTablePrint();
		addListeners();
		addFieldsConstraints();
		initDaos();
		getRequestsFromDB();
		// default value to print date: today
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		textPrintDate.setText(sdf.format(new Date()));
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
		if(tablePrint.getItems() == null || tablePrint.getItems().size() == 0) {
			Alerts.showAlert("Erro!", "Nada selecionado.", "Não foi selecionado nenhuma solicitação.", AlertType.ERROR,
					Utils.currentStage(event));
			return;
		}
		if (textCourse.validate() && textStartDate.validate() && textEndDate.validate() && textCourseLoad.validate()
				&& textPrintDate.validate()) {
			// get data from UI
			String course = textCourse.getText();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date startDate = null;
			Date endDate = null;
			Date printDate = null;
			try {
				startDate = sdf.parse(textStartDate.getText());
				endDate = sdf.parse(textEndDate.getText());
				printDate = sdf.parse(textPrintDate.getText());
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// Change time of print date
			SimpleDateFormat time = new SimpleDateFormat ("HH:mm:ss");
			Date now = new Date();
			String nowTime = time.format(now);
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			SimpleDateFormat fullDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			try {
				printDate = fullDate.parse(date.format(printDate) + " " + nowTime);
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			Integer courseLoad = Utils.tryParseToInt(textCourseLoad.getText());
			Integer recordNumber = Utils.tryParseToInt(textRecordNumber.getText());
			Integer recordPageNumber = Utils.tryParseToInt(textRecordPageNumber.getText());
			// Save certificatePrinted in DB
			List<CertificateRequest> requestsDone = new ArrayList<>();
			for (CertificateRequest r : tablePrint.getItems()) {
				// Create a new certificate to add to historic
				CertificateHistoric certificate = new CertificateHistoric();
				certificate.setCourse(course);
				certificate.setStudentName(r.getStudentName());
				certificate.setStartDate(startDate);
				certificate.setEndDate(endDate);
				certificate.setPrintDate(printDate);
				certificate.setCourseLoad(courseLoad);
				certificate.setRecordNumber(recordNumber);
				certificate.setRecordPageNumber(recordPageNumber);
				try {
					// add to certificate historic
					certificateDao.insert(certificate);
					requestsDone.add(r);
					// remove from requests
					requestDao.delete(r);
				} catch (DbException e) {
					e.printStackTrace();
					Alerts.showAlert(
							"ERRO!", "DBException", "Não foi possível salvar no banco de dados o certificado do "
									+ r.getStudentName() + "[ERRO: " + e.getMessage() + "]",
							AlertType.ERROR, Utils.currentStage(event));
				}
			}
			// remove successful certificates from table Print
			tablePrint.getItems().removeAll(requestsDone);
			tableRequests.getItems().removeAll(requestsDone);
			updateLabelNumberToPrint();
			Alerts.showAlert("Concluído", "Sucesso!",
					"Os certificados foram emitidos e adicionados ao histórico. Aguarde, logo será exibido o certificado.",
					AlertType.INFORMATION, Utils.currentStage(event));
		}
	}

	// =====================
	// ==== END BUTTONS ====
	// =====================
	
	// ============================
	// ===== AUXILIAR METHODS =====
	// ============================
		
	// get requests from database and put in ui
	private void getRequestsFromDB() {
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

	private void initDaos() {
		this.requestDao = new CertificateRequestDao(DBFactory.getConnection());
		this.matriculationDao = new MatriculationDao(DBFactory.getConnection());
		this.certificateDao = new CertificateHistoricDao(DBFactory.getConnection());
	}
	
	//  table Requests
	private void initializeTableRequests() {
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
		Utils.initButtons(columnAddToPrint, Icons.SIZE, Icons.ARROW_DOWN, "greenIcon", (request, event) -> {
			if(!tablePrint.getItems().contains(request)) {
				// add to table to print
				tablePrint.getItems().add(request);
				updateLabelNumberToPrint();
				// set text fields: course
				textCourse.setText(request.getCourse());
				// start and end date
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				textStartDate.setText(sdf.format(request.getStartDate()));
				textEndDate.setText(sdf.format(request.getEndDate()));
				// course load
				if(request.getCourseLoad() != null) {
					textCourseLoad.setText(Integer.toString(request.getCourseLoad()));
				} else {
					textCourseLoad.setText("");
				}
			} else {
				Alerts.showAlert("Já incluso", "Já foi incluso.",
						"Essa solicitação já foi adicionada na lista para impressão.", AlertType.WARNING, Utils.currentStage(event));
			}
		}, "Incluir");
		columnAddToPrint.setReorderable(false);
		Utils.initButtons(columnRemoveRequest, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (request, event) -> {
			// Confirmation to delete request
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar solicitação");
			alert.setHeaderText("Deletar a solicitação do aluno #" + request.getStudentId() + " ?");
			alert.setContentText("Isso erá excluir a solicitação do curso: " + request.getCourse());
			alert.initOwner(Utils.currentStage(event));
			Optional<ButtonType> result =alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// remove from tables in UI
				tableRequests.getItems().remove(request);
				tablePrint.getItems().remove(request);
				/// remove from database
				try {
					requestDao.delete(request);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		}, "Excluir");
		columnRemoveRequest.setReorderable(false);
	}
	
	// table matriculations
	private void initializeTableMatriculations() {
		// code
		Utils.setCellValueFactory(columnMatriculationCode, "code");
		columnMatriculationCode.setReorderable(false);
		// date matriculation
		Utils.setCellValueFactory(columnMatriculationDate, "dateMatriculation");
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		columnMatriculationDate.setReorderable(false);
		// status
		Utils.setCellValueFactory(columnMatriculationStatus, "status");
		columnMatriculationStatus.setReorderable(false);
		// parcels
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				// Total of paid parcels = with status equals PAGA
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation().equalsIgnoreCase("PAGA"))
						.collect(Collectors.toList()).size();
				// will show in table number of paid parcels from total
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			} catch (IllegalStateException | IndexOutOfBoundsException e) {
				// if the matriculation doesn't have parcels will show just a line
				return new SimpleStringProperty("-");
			}
		});
		columnMatriculationParcels.setReorderable(false);
	}
	
	// table print
	private void initiliazeTablePrint() {
		// student info: id, name
		Utils.setCellValueFactory(columnPrintStudentId, "studentId");
		columnPrintStudentId.setReorderable(false);
		columnPrintStudentName.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnPrintStudentName, "studentName");
		columnPrintStudentName.setReorderable(false);
		// button
		Utils.initButtons(columnPrintRemoveFromPrint, Icons.SIZE, Icons.BAN, "redIcon", (request, event) -> {
			tablePrint.getItems().remove(request);
			updateLabelNumberToPrint();
		}, "Remover");
		columnPrintRemoveFromPrint.setReorderable(false);
	}
	
	private void addListeners() {
		// Listener to selected student of table
		tableRequests.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					tableMatriculations.setItems(null);
					if (newSelection != null) {
						List<Matriculation> matriculations;
						try {
							labelSelectedStudentName.setText(newSelection.getStudentName());
							matriculations = matriculationDao.findAllFromStudent(newSelection.getStudentId());
							tableMatriculations.setItems(FXCollections.observableArrayList(matriculations));
						} catch (DbException e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	private void updateLabelNumberToPrint() {
		if(tablePrint.getItems() == null || tablePrint.getItems().size() == 0) {
			labelNumberToPrint.setText("");
		} else {
			labelNumberToPrint.setText("(" + Integer.toString(tablePrint.getItems().size()) + ")");
		}
	}
	
	private void addFieldsConstraints() {
		// course
		textCourse.setValidators(Validators.getRequiredFieldValidator());
		// Date Validator: start, end and print date (all required)
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// start
		textStartDate.setValidators(Validators.getRequiredFieldValidator());
		textStartDate.setValidators(dateValidator);
		// end
		textEndDate.setValidators(Validators.getRequiredFieldValidator());
		textEndDate.setValidators(dateValidator);
		// print date
		textPrintDate.setValidators(Validators.getRequiredFieldValidator());
		textPrintDate.setValidators(dateValidator);
		Constraints.setTextFieldMaxLength(textStartDate, 10);
		Constraints.setTextFieldMaxLength(textEndDate, 10);
		Constraints.setTextFieldMaxLength(textPrintDate, 10);
		// Constraints: Course Load
		textCourseLoad.setValidators(Validators.getRequiredFieldValidator());
		Constraints.setTextFieldIntegerYear(textCourseLoad);
		Constraints.setTextFieldMaxLength(textCourseLoad, 5);
		// Constraints: record number and page number
		Constraints.setTextFieldInteger(textRecordNumber);
		Constraints.setTextFieldInteger(textRecordPageNumber);
		Constraints.setTextFieldMaxLength(textRecordNumber, 6);
		Constraints.setTextFieldMaxLength(textRecordPageNumber, 6);
	}

	// ==========================
	// ==== END INIT METHODS ====
	// ==========================
}