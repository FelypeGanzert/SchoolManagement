package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.enums.StudentStatusEnum;
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import model.dao.StudentDao;
import model.entites.Collaborator;
import model.entites.Person;
import model.entites.Student;
import sharedData.Globe;

public class RegularizeCPFStudentsController implements Initializable{
	
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
	// person info
	@FXML private VBox VBoxInfos;
	@FXML private TextField textName;
	@FXML private TextField textCPF;
	@FXML private TextField textRG;
	@FXML private TextField textGender;
	@FXML private TextField textBirthDate;
	@FXML private TextField textAge;
	@FXML private TextField textAdress;
	@FXML private TextField textNeighborhood;
	@FXML private TextField textCity;
	@FXML private TextField textUF;
	@FXML private JFXButton btnEdit;
	
	private StudentDao studentDao;
	// List to store all students from database
	private ObservableList<Student> studentsList;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		VBoxInfos.setVisible(false);
		// Try to get studentDao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		studentDao = Globe.getGlobe().getItem(StudentDao.class, "studentDao");
		if (studentDao == null) {
			studentDao = new StudentDao(DBFactory.getConnection());
			Globe.getGlobe().putItem("studentDao", studentDao);
		}
		// Initialize tables
		initializeTableStudents();
		// Initialize Fields to allow the search
		String[] fieldsToFilter = { "Nome", "CPF", "RG", "ID" };
		comboBoxFieldFilter.setItems(FXCollections.observableArrayList(fieldsToFilter));
		comboBoxFieldFilter.getSelectionModel().selectFirst();
		// Add listeners to components
		addListeners();
		// Update tableView to show students data
		getStudentsFromDB();
		filterStudents();
	}

	// =====================
	// === START BUTTONS ===
	// =====================

	// Return button
	public void handleBtnReturn(ActionEvent event) {
		Roots.regularizeCPFMenu();
	}
	
	public void handleBtnEdit(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.REGULARIZE_CPF_PERSON_FORM, Utils.currentStage(event), "Regularizar CPF", false,
				(RegularizeCPFPersonFormController controller) -> {
					controller.setPersonEntity(tableStudents.getSelectionModel().getSelectedItem());
					controller.setRegularizeCPFStudentsController(this);
				});
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
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithoutCPF());
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
					if (newSelection != null) {
						if(!VBoxInfos.isVisible()) {
							VBoxInfos.setVisible(true);
						}
						setDataToUI(newSelection);
					} else {
						if(VBoxInfos.isVisible()) {
							VBoxInfos.setVisible(false);
						}
					}
				});
	}
	
	private void setDataToUI(Person entity) {
		textName.setText(entity.getName());
		textCPF.setText(entity.getCpf());
		textRG.setText(entity.getRg());
		textGender.setText(entity.getGender());
		// DATES: birthdate, age
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
		} else {
			textBirthDate.setText("");
		}
		textAge.setText(Integer.toString(entity.getAge()) + " anos");
		textAdress.setText(entity.getAdress());
		textNeighborhood.setText(entity.getNeighborhood());
		textCity.setText(entity.getCity());
		textUF.setText(entity.getUf());
	}

	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================

	public void removeStudent(Student student) {
		tableStudents.getItems().remove(student);
		tableStudents.refresh();
	}
	

}