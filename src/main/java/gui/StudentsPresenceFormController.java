package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Icons;
import gui.util.Utils;
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
import model.entites.Student;
import sharedData.Globe;

public class StudentsPresenceFormController implements Initializable{
	
	// Filter Students
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
	// students to be printed
	@FXML private TableView<Student> tablePrint;
	@FXML private TableColumn<Student, Integer> columnPrintStudentId;
	@FXML private TableColumn<Student, String> columnPrintStudentName;
	@FXML private TableColumn<Student, String> columnPrintStudentContact;
	@FXML private TableColumn<Student, Student> columnPrintRemoveFromPrint;
	@FXML private Label labelNumberToPrint;
	@FXML private Label labelNumberToPrintMax;
	// presence infos
	@FXML private JFXTextArea textCourse;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textStartTime;
	@FXML private JFXTextField textEndTime;
	@FXML private JFXComboBox<String> comboBoxDayOfWeek;
	@FXML private JFXTextField textClassroom;
	
	private StudentDao studentDao;
	// List to store all students from database
	private ObservableList<Student> studentsList;
	private int maxNumberToPrint;
	
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
	}

	// =====================
	// === START BUTTONS ===
	// =====================

	// Print Presence
	public void handleBtnPrint(ActionEvent event) {
		if(tablePrint.getItems() == null || tablePrint.getItems().size() == 0) {
			Alerts.showAlert("Erro!", "Nada selecionado.", "Não foi selecionado nenhum aluno.", AlertType.ERROR,
					Utils.currentStage(event));
			return;
		}
		// get data from UI
		String course = textCourse.getText();
		String startDate = textStartDate.getText();
		String startTime = textStartTime.getText();
		String endTime = textEndTime.getText();
		String dayOfWeek = comboBoxDayOfWeek.getSelectionModel().getSelectedItem();
		String classroom = textClassroom.getText();
		// Save certificatePrinted in DB
		// ========
		// TODO: show window allowing user select a docx model to print the presence
		// ========
		Alerts.showAlert("Concluído", "Sucesso!",
				"A chamada foi gerada. Aguarde, logo ela será exibida.",
				AlertType.INFORMATION, Utils.currentStage(event));
	}
	
	public void handleBtnClearPrintTable(ActionEvent event) {
		tablePrint.getItems().clear();
		updateLabelNumberToPrint();
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
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithContactsLoaded());
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
			if (tablePrint.getItems().contains(student)) {
				Alerts.showAlert(
						"Já incluso", "Já foi incluso.",
						"Esse aluno já foi adicionado na lista para impressão.", AlertType.WARNING,
						Utils.currentStage(event));
				return;
			}
			if (tablePrint.getItems().size() >= maxNumberToPrint) {
				Alerts.showAlert("Limite", "Não é possível adicionar mais.",
						"O limite estabelecido de " + maxNumberToPrint + " alunos para inserir na chamada já foi alcançado.", AlertType.WARNING,
						Utils.currentStage(event));
				return;
			}
			// add to table to print
			tablePrint.getItems().add(student);
			updateLabelNumberToPrint();

		}, "Incluir");
		columnAddToPrint.setReorderable(false);
	}

	// table print
	private void initiliazeTablePrint() {
		// student info: id, name
		Utils.setCellValueFactory(columnPrintStudentId, "id");
		columnPrintStudentId.setReorderable(false);
		columnPrintStudentName.setCellFactory(Utils.getWrappingCellFactory());
		Utils.setCellValueFactory(columnPrintStudentName, "name");
		columnPrintStudentName.setReorderable(false);
		// column contact 1
		// we need this verification because can happen of students doesn't have any
		// contact number
		columnPrintStudentContact.setCellValueFactory(cellData -> {
			try {
				if (!(cellData.getValue().getContacts() == null)) {
					String numberFormated = Constraints
							.formatNumberContact(cellData.getValue().getContacts().get(0).getNumber());
					return new SimpleStringProperty(numberFormated);
				} else {
					return new SimpleStringProperty("-");
				}
			} catch (IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		columnPrintStudentContact.setReorderable(false);
		// button
		Utils.initButtons(columnPrintRemoveFromPrint, Icons.SIZE, Icons.BAN, "redIcon", (request, event) -> {
			tablePrint.getItems().remove(request);
			updateLabelNumberToPrint();
		}, "Remover");
		columnPrintRemoveFromPrint.setReorderable(false);
	}
	
	private void updateLabelNumberToPrint() {
		if(tablePrint.getItems() == null || tablePrint.getItems().size() == 0) {
			labelNumberToPrint.setText("0");
		} else {
			labelNumberToPrint.setText(Integer.toString(tablePrint.getItems().size()));
		}
	}

	private void addListeners() {
		addListenersToFilterStudents();
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
	
	private void addFieldsConstraints() {
		Constraints.setTextFieldMaxLength(textStartDate, 10);
		Constraints.setTextFieldMaxLength(textStartTime, 10);
		Constraints.setTextFieldMaxLength(textEndTime, 10);
		Constraints.setTextFieldMaxLength(textClassroom, 5);
		maxNumberToPrint = 30;
		labelNumberToPrintMax.setText(Integer.toString(maxNumberToPrint));
	}

	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================


}