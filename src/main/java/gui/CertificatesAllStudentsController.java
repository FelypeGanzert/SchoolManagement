package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
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
import gui.util.enums.StudentStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import model.dao.StudentDao;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Student;
import sharedData.Globe;

public class CertificatesAllStudentsController implements Initializable{
	
	// Filter Student and Register
	@FXML private JFXTextField textFilter;
	@FXML private JFXComboBox<String> comboBoxFieldFilter;
	@FXML private ToggleGroup filterType;
	@FXML private Label labelTotalStudentsSearch;
	@FXML private ToggleGroup filterStudentStatus;
	@FXML private JFXRadioButton statusTODOS;
	@FXML private JFXRadioButton statusATIVOS;
	@FXML private JFXRadioButton statusAGUARDANDO;
	@FXML private JFXRadioButton statusINATIVOS;
	// Table Students
	@FXML TableView<Student> tableStudents;
	@FXML private TableColumn<Student, String> columnStudentStatus;
	@FXML private TableColumn<Student, Integer> columnStudentCode;
	@FXML private TableColumn<Student, String> columnStudentName;
	@FXML private TableColumn<Student, Student> columnAddToPrint;
	// selected student: matriculation
	@FXML private Label labelSelectedStudentName;
	@FXML private TableView<Matriculation> tableMatriculations;
	@FXML private TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML private TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML private TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML private TableColumn<Matriculation, String> columnMatriculationParcels;
	// students to be printed
	@FXML private TableView<Student> tablePrint;
	@FXML private TableColumn<Student, Integer> columnPrintStudentId;
	@FXML private TableColumn<Student, String> columnPrintStudentName;
	@FXML private TableColumn<Student, Student> columnPrintRemoveFromPrint;
	@FXML private Label labelNumberToPrint;
	// certificate infos
	@FXML private JFXTextArea textCourse;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textCourseLoad;
	@FXML private JFXTextField textPrintDate;
	@FXML private JFXTextField textRecordNumber;
	@FXML private JFXTextField textRecordPageNumber;
	
	private StudentDao studentDao;
	// List to store all students from database
	private ObservableList<Student> studentsList;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Try to get studentDao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		studentDao = Globe.getGlobe().getItem(StudentDao.class, "studentDao");
		if (studentDao == null) {
			studentDao = new StudentDao(DBFactory.getConnection());
			Globe.getGlobe().putItem("studentDao", studentDao);
		}
		// Initialize tables
		initializeTableStudents();
		initializeTableMatriculations();
		initiliazeTablePrint();
		// Initialize Fields to allow the search
		String[] fieldsToFilter = { "Nome", "CPF", "RG", "ID" };
		comboBoxFieldFilter.setItems(FXCollections.observableArrayList(fieldsToFilter));
		comboBoxFieldFilter.getSelectionModel().selectFirst();
		// Add listeners to components
		addListeners();
		// Update tableView to show students data
		getStudentsFromDB();
		filterStudents();
		addFieldsConstraints();
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
		if (textCourse.validate() && textStartDate.validate() && textEndDate.validate() && textCourseLoad.validate()
				&& textPrintDate.validate()) {
			System.out.println("Clicked to print and every field is valid");
		}
	}

	// =====================
	// ==== END BUTTONS ====
	// =====================
	
	// ===== AUXILIAR METHODS
	
	private void getStudentsFromDB() {
		if(studentDao == null) {
			throw new IllegalStateException("StudentDao service not initialized");
		}
		try {
			studentsList = FXCollections.observableArrayList(this.studentDao.findAll());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os alunos", "DBException", e.getMessage(), AlertType.ERROR, null);
		}
	}

	private void filterStudents() {
		// Auxiliar list to doenst interfery where all students are stored
		List<Student> filteredList = new ArrayList<>();
		filteredList = studentsList;
		// Filter by status selected in UI
		String statusSelected = ((RadioButton) filterStudentStatus.getSelectedToggle()).getText();
		Map<String, String> statusMap = new HashMap<>();
		statusMap.put("TODOS", null);
		statusMap.put("ATIVOS", "ATIVO");
		statusMap.put("AGUARDANDO", "AGUARDANDO");
		statusMap.put("INATIVOS", "INATIVO");
		// I use this to correspond with status stored in database
		String statusToFilter = statusMap.get(statusSelected.toUpperCase());
		if (statusToFilter != null) {
			filteredList = filteredList.stream().filter(student -> student.getStatus().equalsIgnoreCase(statusToFilter))
					.collect(Collectors.toList());
			if (filteredList.size() == 1) {
				if (statusSelected.charAt(statusSelected.length() - 1) == 's') {
					statusSelected = statusSelected.substring(0, statusSelected.length() - 1);
				}
			}
		} else {
			if (filteredList.size() == 1) {
				if (statusSelected.charAt(statusSelected.length() - 1) == 's') {
					statusSelected = statusSelected.substring(0, statusSelected.length() - 1);
				}
			}
		}
		// Filter by text in search bar
		String textSearch = textFilter.getText();
		String fieldFilter = comboBoxFieldFilter.getSelectionModel().getSelectedItem();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if (filterTypeSelected.equalsIgnoreCase("inicia com") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(student -> student.getValue(fieldFilter) != null
							&& student.getValue(fieldFilter).toUpperCase().startsWith(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		} else if (filterTypeSelected.equalsIgnoreCase("contém") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(student -> student.getValue(fieldFilter) != null
							&& student.getValue(fieldFilter).toUpperCase().contains(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		}
		if (textSearch.length() > 0) {
			labelTotalStudentsSearch.setText("Resultados: " + filteredList.size());
		} else {
			labelTotalStudentsSearch.setText(null);
		}
		// convert filteredList to Observable List and set in tableStudents
		ObservableList<Student> filteredObsList = FXCollections.observableArrayList(filteredList);
		tableStudents.setItems(filteredObsList);
		tableStudents.refresh();
		tableStudents.getSelectionModel().selectFirst();
	}
	
	// ====================================================
	// ======== START OF INITIALIZE METHODS ===============
	// ====================================================

	// TABLE STUDENTS
	private void initializeTableStudents() {
		// column of status will show a color according StudentStatusEnum
		Utils.setCellValueFactory(columnStudentStatus, "status");
		columnStudentStatus.setCellFactory(column -> {
			return new TableCell<Student, String>() {
				@Override
				protected void updateItem(String status, boolean empty) {
					super.updateItem(status, empty);
					setText("");
					setGraphic(null);
					if (!isEmpty() && status != null) {
						this.setStyle("-fx-background-color:" + StudentStatusEnum.fromString(getItem()).getHexColor());
					}
				}
			};
		});
		columnStudentStatus.setReorderable(false);
		// columns to id and name
		Utils.setCellValueFactory(columnStudentCode, "id");
		columnStudentCode.setReorderable(false);
		Utils.setCellValueFactory(columnStudentName, "name");
		columnStudentName.setReorderable(false);
		// button
		Utils.initButtons(columnAddToPrint, Icons.SIZE, Icons.ARROW_RIGHT, "greenIcon", (student, event) -> {
			if(!tablePrint.getItems().contains(student)) {
				// add to table to print
				tablePrint.getItems().add(student);
				updateLabelNumberToPrint();
			} else {
				Alerts.showAlert("Já incluso", "Já foi incluso.",
						"Esse aluno já foi adicionado na lista para impressão.", AlertType.WARNING, Utils.currentStage(event));
			}
		}, "Incluir");
		columnAddToPrint.setReorderable(false);
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
	public void initiliazeTablePrint() {
		// student info: id, name
		Utils.setCellValueFactory(columnPrintStudentId, "id");
		columnPrintStudentId.setReorderable(false);
		columnPrintStudentName.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnPrintStudentName, "name");
		columnStudentName.setReorderable(false);
		// button
		Utils.initButtons(columnPrintRemoveFromPrint, Icons.SIZE, Icons.BAN, "redIcon", (request, event) -> {
			tablePrint.getItems().remove(request);
			updateLabelNumberToPrint();
		}, "Remover");
	}
	
	private void updateLabelNumberToPrint() {
		if(tablePrint.getItems() == null || tablePrint.getItems().size() == 0) {
			labelNumberToPrint.setText("");
		} else {
			labelNumberToPrint.setText("(" + Integer.toString(tablePrint.getItems().size()) + ")");
		}
	}

	private void addListeners() {
		addListenersToFilterStudents();
		addListenersToTableStudents();
	}
	
	private void addListenersToFilterStudents() {
		// Filter students table when user type anything in search bar
		textFilter.textProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
		// Filter students table when user select another field to make the search
		comboBoxFieldFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
		// Filter students table when user select another type of search
		filterType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
		// Filter students table when user select another status
		filterStudentStatus.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
	}

	private void addListenersToTableStudents() {
		// Listener to selected student of table
		tableStudents.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					tableMatriculations.setItems(null);
					if (newSelection != null) {
						List<Matriculation> matriculations;
						labelSelectedStudentName.setText(newSelection.getName());
						matriculations = newSelection.getMatriculations();
						tableMatriculations.setItems(FXCollections.observableArrayList(matriculations));
					}
				});
	}
	
	private void addFieldsConstraints() {
		// course
		textCourse.setValidators(Validators.getRequiredFieldValidator());
		// Date Validator: start, end and print date (all required)
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// tstart
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

	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================


}