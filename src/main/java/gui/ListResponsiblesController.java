
package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import db.DbExceptioneEntityExcluded;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import model.dao.ResponsibleDao;
import model.entites.Responsible;
import model.entites.Student;
import sharedData.Globe;

public class ListResponsiblesController implements Initializable {

	// Filter Student and Register
	@FXML private JFXTextField textFilter;
	@FXML private ToggleGroup filterType;
	@FXML private Label labelTotalResponsiblesSearch;
	@FXML private Label labelTotalResponsibles;
	// Table Responsibles
	@FXML TableView<Responsible> tableResponsibles;
	@FXML private TableColumn<Responsible, String> columnResponsibleName;
	@FXML private TableColumn<Responsible, String> columnResponsibleContact1;
	@FXML private TableColumn<Responsible, Responsible> columnResponsibleInfo;
	// Table Students
	@FXML protected TableView<Student> tableStudents;
	@FXML private TableColumn<Student, Integer> columnStudentCode;
	@FXML private TableColumn<Student, String> columnStudentName;
	@FXML private TableColumn<Student, Student> columnStudentInfo;
	
	private ResponsibleDao responsibleDao;
	// List to store all responsibles from database
	private ObservableList<Responsible> responsiblesList;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// instantiate a new responsible dao
		if(responsibleDao == null) {
			responsibleDao = new ResponsibleDao(DBFactory.getConnection());
		}
		// Initialize tables
		initializeTableResponsiblesNodes();
		initializeTableStudentsNodes();
		// Add listeners to components
		addListeners();
		// Update tableView to show students data
		getResponsiblesFromDB();
		filterResponsibles();
	}
	
	// ===========================
	// === START OF LISTENERS ====
	// ===========================
	
	private void addListeners() {
		addListenersToFilterResponsibles();
		addListenersToTableResponsibles();
	}

	private void addListenersToFilterResponsibles() {
		// Filter responsibles table when user type anything in search bar
		textFilter.textProperty().addListener((observable, oldValue, newValue) -> {
			filterResponsibles();
		});
		// Filter responsibles table when user select another type of search
		filterType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterResponsibles();
		});
	}
	
	private void addListenersToTableResponsibles() {
		// Listener to double click in responsible to show informations
		tableResponsibles.setRowFactory(tv -> {
			TableRow<Responsible> row = new TableRow<>();
			row.setOnMouseClicked(mouseEvent -> {
				if(mouseEvent.getClickCount() == 2 && !row.isEmpty()){
	               showResponsibleInfo(row.getItem());
	            }
			});
			return row;
		});
		// Listener to double click in student to show informations
		tableStudents.setRowFactory(tv -> {
			TableRow<Student> row = new TableRow<>();
			row.setOnMouseClicked(mouseEvent -> {
				if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
					showStudentInfo(row.getItem());
				}
			});
			return row;
		});
		// Listener to selected responsible of table
		tableResponsibles.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					// show students of responsible
					tableStudents.setItems(null);
					if (newSelection != null) {
						Responsible responsible = newSelection;
						// refresh responsible data
						try {
							DBUtil.refreshData(responsible);
						} catch (DbException e) {
							Alerts.showAlert("DBException", "DBException - excessão no banco de dados", e.getMessage(),
									AlertType.ERROR, (Stage) tableStudents.getScene().getWindow());
							e.printStackTrace();
						} catch (DbExceptioneEntityExcluded e) {
							// Show a message that responsible has been deleted
							Alerts.showAlert("DBExceptionEntityExcluded",
									responsible.getId() + " - " + responsible.getName()
											+ " foi deletado do banco de dados por alguém",
									"DBExceptionEntityExcluded: " + e.getMessage(), AlertType.ERROR,
									(Stage) tableStudents.getScene().getWindow());
							// remove responsible deleted from table
							tableResponsibles.getItems().remove(responsible);
							responsible = null;
							e.printStackTrace();
						} finally {
							// refresh table
							tableResponsibles.refresh();
							if (responsible != null && responsible.getAllStudents() != null && responsible.getAllStudents().size() >= 0) {
								// get students from responsible and put in a ObservableList
								ObservableList<Student> students = FXCollections.observableList(responsible.getAllStudents());
								// sort students by name
								students.sort((s1, s2) -> s1.getName().compareTo(s2.getName()));
								// set students to table in UI
								tableStudents.setItems(students);
							}
						}
					}
					// refresh table
					tableStudents.refresh();
				});
	}

	// ===========================
	// === END OF LISTENERS ====
	// ===========================
	
	// ====================================================
	// ======== START OF INITIALIZE METHODS ===============
	// ====================================================

	// TABLE RESPONSIBLES
	private void initializeTableResponsiblesNodes() {
		// column name
		Utils.setCellValueFactory(columnResponsibleName, "name");
		columnResponsibleName.setReorderable(false);
		// we need this verification because can happen of responsibles doesn't have any contact number
		columnResponsibleContact1.setCellValueFactory(cellData -> {
			try {
				if(!(cellData.getValue().getContacts() == null)) {
					String numberFormated = Constraints.formatNumberContact(cellData.getValue().getContacts().get(0).getNumber());
					return new SimpleStringProperty(numberFormated);
				} else {
					return new SimpleStringProperty("-");
				}
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		columnResponsibleContact1.setReorderable(false);
		// Add info button to responsible and when clicked will show informations of that student
		Utils.initButtons(columnResponsibleInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (responsible, event) -> {
			showResponsibleInfo(responsible);
		});
		columnResponsibleInfo.setReorderable(false);
	}
	
	// TABLE STUDENTS
	private void initializeTableStudentsNodes() {
		// columns to id and name
		Utils.setCellValueFactory(columnStudentCode, "id");
		columnStudentCode.setReorderable(false);
		Utils.setCellValueFactory(columnStudentName, "name");
		columnStudentName.setReorderable(false);
		// Add info button to student and when clicked will show informations of that student
		Utils.initButtons(columnStudentInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			showStudentInfo(student);
		});
		columnStudentInfo.setReorderable(false);

	}
	
	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================
	
	// ===========================
	// == SHOW OTHERS SCREENS  ===
	// ===========================
	
	private void showResponsibleInfo(Responsible responsible) {
		try {
			// refresh responsible data
			DBUtil.refreshData(responsible);
			// show screen of responsible informations
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.INFO_RESPONSIBLE, (InfoResponsibleController controller) -> {
				controller.setReturn(FXMLPath.LIST_RESPONSIBLES, "Responsáveis");
				controller.setCurrentResponsible(responsible);
			});
		} catch (DbException e) {
			Alerts.showAlert("DBException", "DBException - excessão no banco de dados", e.getMessage(), AlertType.ERROR,
					(Stage) tableStudents.getScene().getWindow());
			e.printStackTrace();
		} catch (DbExceptioneEntityExcluded e) {
			// Show a message that student has been deleted
			Alerts.showAlert("DBExceptionoEntityExcluded",
					responsible.getId() + " - " + responsible.getName() + " foi deletado do banco de dados por alguém",
					"DBExceptionEntityExcluded: " + e.getMessage(),
					AlertType.ERROR, (Stage) tableStudents.getScene().getWindow());
			// remove student deleted from table
			tableResponsibles.getItems().remove(responsible);
			tableResponsibles.refresh();
			e.printStackTrace();
		}		
	}
	
	private void showStudentInfo(Student student) {
		try {
			// refresh student data
			DBUtil.refreshData(student);
			// show screen of student informations
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
				controller.setReturn(FXMLPath.LIST_RESPONSIBLES, "Responsáveis");
				controller.setCurrentStudent(student);
				controller.setCurrentResponsible(tableResponsibles.getSelectionModel().getSelectedItem());
			});
		} catch (DbException e) {
			Alerts.showAlert("DBException", "DBException - excessão no banco de dados", e.getMessage(), AlertType.ERROR,
					(Stage) tableStudents.getScene().getWindow());
			e.printStackTrace();
		} catch (DbExceptioneEntityExcluded e) {
			// Show a message that student has been deleted
			Alerts.showAlert("DBExceptionEntityExcluded",
					student.getId() + " - " + student.getName() + " foi deletado do banco de dados por alguém",
					"DBExceptionEntityExcluded: " + e.getMessage(),
					AlertType.ERROR, (Stage) tableStudents.getScene().getWindow());
			// remove student deleted from table
			tableStudents.getItems().remove(student);
			tableStudents.refresh();
			e.printStackTrace();
		}		
	}
	
	// ========================
	// === AUXILIAR METHODS ===
	// ========================
	
	private void filterResponsibles() {
		// Auxiliar list to doenst interfery where all responsibles are stored
		List<Responsible> filteredList = new ArrayList<>();
		filteredList = responsiblesList;
		String totalResponsiblesText = "";
		if (filteredList.size() == 1) {
			totalResponsiblesText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " responsável)";
		} else {
			totalResponsiblesText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " responsáveis)";
		}
		// Filter by text in search bar
		String textSearch = textFilter.getText();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if (filterTypeSelected.equalsIgnoreCase("inicia com") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(responsible -> responsible.getName() != null
							&& responsible.getName().toUpperCase().startsWith(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		} else if (filterTypeSelected.equalsIgnoreCase("contém") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(responsible -> responsible.getName() != null
							&& responsible.getName().toUpperCase().contains(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		}
		if (textSearch.length() > 0) {
			labelTotalResponsiblesSearch.setText("Resultados: " + filteredList.size());
		} else {
			labelTotalResponsiblesSearch.setText(null);
		}
		// set total students to label
		labelTotalResponsibles.setText(totalResponsiblesText);
		// convert filteredList to Observable List and set in tableResponsibles
		ObservableList<Responsible> filteredObsList = FXCollections.observableArrayList(filteredList);
		tableResponsibles.setItems(filteredObsList);
		tableResponsibles.refresh();
		tableResponsibles.getSelectionModel().selectFirst();
	}

	// Get responsibles with contacts loaded from Database
	// and put in responsiblesList 
	private void getResponsiblesFromDB() {
		System.out.println("Will try to get informations");
		if(responsibleDao == null) {
			throw new IllegalStateException("ResponsibleDao service not initialized");
		}
		try {
			responsiblesList = FXCollections.observableArrayList(this.responsibleDao.findAllWithContactsLoaded());
			responsiblesList.sort((r1, r2) -> r1.getName().toUpperCase().compareTo(r2.getName().toUpperCase()));
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os responsáveis", "DBException", e.getMessage(), AlertType.ERROR, null);
		}
	}

}