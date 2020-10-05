
package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import gui.util.enums.StudentStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import model.dao.AnnotationDao;
import model.dao.StudentDao;
import model.entites.Annotation;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Student;
import sharedData.Globe;

public class ListStudentsController implements Initializable {

	// Filter Student and Register
	@FXML private JFXTextField textFilter;
	@FXML private JFXComboBox<String> comboBoxFieldFilter;
	@FXML private ToggleGroup filterType;
	@FXML private ToggleGroup filterStudentStatus;
	@FXML private JFXRadioButton statusTODOS;
	@FXML private JFXRadioButton statusATIVOS;
	@FXML private JFXRadioButton statusAGUARDANDO;
	@FXML private JFXRadioButton statusINATIVOS;
	@FXML private Label labelTotalStudents;
	@FXML private JFXButton btnRegister;
	// Table Students
	@FXML TableView<Student> tableStudents;
	@FXML private TableColumn<Student, String> columnStudentStatus;
	@FXML private TableColumn<Student, Integer> columnStudentCode;
	@FXML private TableColumn<Student, String> columnStudentName;
	@FXML private TableColumn<Student, String> columnStudentContact1;
	@FXML private TableColumn<Student, Student> columnStudentInfo;
	// Table Matriculations
	@FXML private TableView<Matriculation> tableMatriculations;
	@FXML private TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML private TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML private TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML private TableColumn<Matriculation, String> columnMatriculationParcels;
	// Table Parcels
	@FXML private Label labelSelectedMatriculation;
	@FXML private TableView<Parcel> tableParcels;
	@FXML private TableColumn<Parcel, String> columnParcelStatus;
	@FXML private TableColumn<Parcel, Integer> columnParcelParcel;
	@FXML private TableColumn<Parcel, Date> columnParcelDate;
	@FXML private TableColumn<Parcel, Double> columnParcelValue;
	// Annotations
	@FXML private Button btnAddAnnotation;
	@FXML private JFXListView<Annotation> listViewAnnotation;
	@FXML private Label labelSelectedAnnotationDate;
	@FXML private JFXTextArea textAreaAnnotation;
	@FXML private Button btnEditSelectedAnnotation;
	@FXML private Button btnDeleteSelectedAnnotation;
	@FXML private Label labelSelectedAnnotationCollaborator;
	
	private StudentDao studentDao;
	// List to store all students from db
	private ObservableList<Student> studentsList;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Try to get studentDao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		studentDao =  Globe.getGlobe().getItem(StudentDao.class, "studentDao");
		if(studentDao == null) {
			studentDao = new StudentDao(DBFactory.getConnection());
			Globe.getGlobe().putItem("studentDao", studentDao);
		}
		// Initialize tables
		initializeTableStudentsNodes();
		initializeTableMatriculationsNodes();
		initiliazeTableParcelsNodes();
		initiliazeListViewAnnotations();
		// Add listeners to components
		addListeners();
		// Update tableView to show students data
		getStudentsFromDB();
		filterStudents();
	}
	
	private void addListeners() {
		// Filter students table when user type anything in search bar
		textFilter.textProperty().addListener((observable, oldValue, newValue) -> {
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
		// Hidden edit and remove button of annotation if nothing is selected
		labelSelectedAnnotationDate.textProperty().addListener((observable, oldValue, newValue) -> {
			if(newValue != null && newValue.length() > 0) {
				btnEditSelectedAnnotation.setVisible(true);
				btnDeleteSelectedAnnotation.setVisible(true);
			} else {
				btnEditSelectedAnnotation.setVisible(false);
				btnDeleteSelectedAnnotation.setVisible(false);
			}
		});
	}
	
	// Get students with contacts loaded from Database
	// and put in studentsList 
	public void getStudentsFromDB() {
		if(studentDao == null) {
			throw new IllegalStateException("StudentDao service not initialized");
		}
		try {
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithContactsLoaded());
			studentsList.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os alunos", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void filterStudents() {
		// Auxiliar list to doenst interfery where all students are stored
		List<Student> filteredList = new ArrayList<>();
		filteredList = studentsList;
		String totalStudentsText = "";
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
			totalStudentsText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " alunos " + statusSelected + ")";
		} else {
			totalStudentsText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " alunos)";
		}
		// Filter by text in search bar
		String value = textFilter.getText();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if(filterTypeSelected.equalsIgnoreCase("inicia com")) {
			filteredList = filteredList.stream()
					.filter(student -> student.getName().toUpperCase().startsWith(value.toUpperCase()))
					.collect(Collectors.toList());
		} else if(filterTypeSelected.equalsIgnoreCase("contém")) {
			filteredList = filteredList.stream()
					.filter(student -> student.getName().toUpperCase().contains(value.toUpperCase()))
					.collect(Collectors.toList());
		}
		// set total students to label
		labelTotalStudents.setText(totalStudentsText);
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
	private void initializeTableStudentsNodes() {
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
		// columns to id and name
		Utils.setCellValueFactory(columnStudentCode, "id");
		Utils.setCellValueFactory(columnStudentName, "name");
		// we need this verification because can happen of students doenst have any contact number
		columnStudentContact1.setCellValueFactory(cellData -> {
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
		// Add info button to student and when clicked will show informations of that student
		Utils.initButtons(columnStudentInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			showStudentInfo(student);
		});
		// Listener to selected student, 
		tableStudents.getSelectionModel().selectedItemProperty().addListener(
	            (observable, oldSelection, newSelection) -> {
					if (newSelection != null && newSelection != oldSelection) {
						// Reflesh student data if was update in database
						refreshStudentFromDB(newSelection);
		            	updateAnnotations(newSelection);
						updateMatriculations(newSelection);
					}
				}
	    );
	}
	
	// TABLE MATRICULATIONS
	private void initializeTableMatriculationsNodes() {
		Utils.setCellValueFactory(columnMatriculationCode, "code");
		Utils.setCellValueFactory(columnMatriculationDate, "dateMatriculation");
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnMatriculationStatus, "status");
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				// Total of paid parcels = with status equals PAGA
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGA")).collect(Collectors.toList()).size();
				// will show in table number of paid parcels from total
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				// if the matriculation doenst have parcels will show just a line
				return new SimpleStringProperty("-");
			}
		});
		// Listener to selected Matriculation
		tableMatriculations.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {	
					labelSelectedMatriculation.setText("");
					if (newSelection != null && newSelection.getParcels().size() > 0) {
						updateParcels(newSelection);
						String matriculationCode = Integer.toString(newSelection.getCode());
						setCurrentMatriculationId(matriculationCode);
					}
				});
	}
	
	// TABLE PARCELS
	private void initiliazeTableParcelsNodes() {
		// verify if a parcel open (ABERTA) has the date before TODAY, if this happen
		// the payment situation is late (ATRASADO), otherwise the situation is equal
		// the parcel situation
		columnParcelStatus.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				Parcel auxParcel = cellData.getValue();
				if (auxParcel.getDateParcel().before(new Date()) && auxParcel.getSituation().equalsIgnoreCase("ABERTA")) {
					situation = "ATRASADA";
				}
				return new SimpleStringProperty(situation);
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("");
			}
		});
		// column of status will show a color according ParcelStatusEnum
		columnParcelStatus.setCellFactory(column -> {
			return new TableCell<Parcel, String>() {
				@Override
				protected void updateItem(String situation, boolean empty) {
					super.updateItem(situation, empty);
					setText("");
					setGraphic(null);
					if (!isEmpty()) {
						this.setStyle("-fx-background-color:" + ParcelStatusEnum.fromString(getItem()).getHexColor());
					}
				}
			};
	    });
		Utils.setCellValueFactory(columnParcelParcel, "parcelNumber");
		Utils.setCellValueFactory(columnParcelDate, "dateParcel");
		Utils.formatTableColumnDate(columnParcelDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnParcelValue, "value");
		Utils.formatTableColumnDoubleCurrency(columnParcelValue);
	}
	
	// LIST VIEW ANNOTATIONS
	private void initiliazeListViewAnnotations() {
		// Will show the item inside the list in a hbox centered
		listViewAnnotation.setCellFactory(param -> new ListCell<Annotation>() {
		    @Override
		    protected void updateItem(Annotation item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					// Create the HBox to become possible center the date
					HBox hBox = new HBox();
					hBox.setAlignment(Pos.CENTER);
					// Create centered Label
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Label label = new Label(sdf.format(item.getDate()));
		            label.setAlignment(Pos.CENTER);
		            hBox.getChildren().add(label);
					setGraphic(hBox);
				}
			}
		});
		// Listener to selected Annotation
		listViewAnnotation.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					try {
						labelSelectedAnnotationDate.setText("");
						textAreaAnnotation.setText("");
						labelSelectedAnnotationCollaborator.setText("");
						if (newSelection != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							labelSelectedAnnotationDate.setText(sdf.format(newSelection.getDate()));
							textAreaAnnotation.setText(newSelection.getDescription());
							labelSelectedAnnotationCollaborator.setText(newSelection.getResponsibleCollaborator());
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
	}
	
	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================
	
	
	// ====================================================
	// ========= HANDLE BUTTONS ACTIONS ====================
	// ====================================================
	
	public void handleBtnAddNewStudent(ActionEvent event) {
		// load view Person Form and create a new student
		Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Novo cadatro", false,
				(PersonFormController controller) -> {
					Student student = new Student();
					controller.setPersonEntity(student);
				});
	}
	
	public void handleBtnAddAnnotation(ActionEvent event) {
		// load view to add a new annotation to selected student
		Student studentSelected = tableStudents.getSelectionModel().getSelectedItem();
		if (studentSelected != null) {
			Utils.loadView(this, true, FXMLPath.ANNOTATION, Utils.currentStage(event), "Adicionar Anotação", false, (AnnotationController controller) -> {
				controller.setDependences(studentSelected, this);
			});
		}
	}
	
	public void handleBtnEditAnnotation(ActionEvent event){
		// load view to edit annotation selected
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			Utils.loadView(this, true, FXMLPath.ANNOTATION, Utils.currentStage(event), "Editar Anotação", false, (AnnotationController controller) -> {
				controller.setDependences(itemSelected, this);
				
			});
		}
	}
	
	public void handleBtnDeleteAnnotation(ActionEvent event){
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			Student studentOwner = itemSelected.getStudent();
			// Confirmation to delete annotation
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar anotação");
			alert.setHeaderText("Deletar a anotação do dia " + sdf.format(itemSelected.getDate()) + " ?");
			Optional<ButtonType> result =alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Show a screen of deleting annotation process
				Alert alertProcessing = Alerts.showProcessingScreen();
				try {
					// AnnotationDao to delete from db
					AnnotationDao annotationDao = new AnnotationDao(DBFactory.getConnection());
					annotationDao.delete(itemSelected);
					// remove annotation from student in memory and update Annotations list
					studentOwner.getAnnotations().remove(itemSelected);
					updateAnnotations(studentOwner);
				} catch (DbException e) {
					Alerts.showAlert("Erro ao deletar anotação", "DbException", e.getMessage(), AlertType.ERROR);
				}
				alertProcessing.close();
			}
		}
	}
	
	// called when user click in info button in student table
	private void showStudentInfo(Student student) {
		try {
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
				controller.setMainViewControllerAndReturnName(FXMLPath.LIST_STUDENTS, "Alunos");
				controller.setCurrentStudent(student);
				// Reflesh student data
				refreshStudentFromDB(student);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// called when user select a matriculation
	private void setCurrentMatriculationId(String matriculationCode) {
		labelSelectedMatriculation.setText("Matrícula: " + matriculationCode);
	}

	// Refresh student data
	public void refreshStudentFromDB(Student student) {
		// he will try to find the student in db and then will refresh the data
		DBUtil.refleshData(student);
		// refresh tables and lists
		tableStudents.refresh();
		tableMatriculations.refresh();
		tableParcels.refresh();
		listViewAnnotation.refresh();		
	}
	
	// This method can be called from others controller to select a status
	public void selectStatusToFilter(String status) {
		if (status != null) {
			if (status.equalsIgnoreCase("ATIVO")) {
				statusATIVOS.setSelected(true);
			} else if (status.equalsIgnoreCase("AGUARDANDO")) {
				statusAGUARDANDO.setSelected(true);
			} else if (status.equalsIgnoreCase("INATIVO")) {
				statusINATIVOS.setSelected(true);
			} else {
				statusTODOS.setSelected(true);
			}
		}
	}
	
	// ====================================================
	// === START OF METHODS TO UPDATE TABLES AND LISTS ====
	// ====================================================
	
	public void updateMatriculations(Student student) {
		tableMatriculations.setItems(null);
		// return if doesnt have any matriculation to show
		if (student == null || student.getMatriculations() == null || student.getMatriculations().size() <= 0) {
			return;
		}
		// get matriculations from student and put in a ObservableList
		ObservableList<Matriculation> matriculations = FXCollections.observableList(student.getMatriculations());
		// sort matriculations by date
		matriculations.sort((m1, m2) -> m2.getDateMatriculation().compareTo(m1.getDateMatriculation()));
		// set matriculations to table in UI and select the first one
		tableMatriculations.setItems(matriculations);
		listViewAnnotation.refresh();
		listViewAnnotation.getSelectionModel().selectFirst();
	}
	
	public void updateParcels(Matriculation matriculation) {
		tableParcels.setItems(null);
		// return if doesnt have any parcel to show
		if (matriculation == null || matriculation.getParcels() == null || matriculation.getParcels().size() <= 0) {
			return;
		}
		// get parcels from matriculation and put in a ObservableList
		ObservableList<Parcel> parcels = FXCollections.observableList(matriculation.getParcels());
		// sort parcels by date
		parcels.sort((p1, p2) -> p2.getDateParcel().compareTo(p1.getDateParcel()));
		// set parcels to table in UI and select the first one
		tableParcels.setItems(parcels);
		tableParcels.refresh();
		tableParcels.getSelectionModel().selectFirst();
	}
	
	public void updateAnnotations(Student student) {
		listViewAnnotation.setItems(null);
		// return if doenst have any annotation to show
		if (student == null || student.getAnnotations() == null || student.getAnnotations().size() <= 0) {
			return;
		}
		// get annotations from student and put in a ObservableList
		ObservableList<Annotation> annotations = FXCollections.observableList(student.getAnnotations());
		// sort annotations by date
		annotations.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
		// set annotations to listView in UI and select the first one
		listViewAnnotation.setItems(annotations);
		listViewAnnotation.refresh();
		listViewAnnotation.getSelectionModel().selectFirst();
	}
	
	// ====================================================
	// ===== END OF METHODS TO UPDATE TABLES AND LISTS ====
	// ====================================================
	
}