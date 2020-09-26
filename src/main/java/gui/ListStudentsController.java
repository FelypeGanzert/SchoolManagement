
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

import application.Main;
import db.DBFactory;
import db.DbException;
import db.DbUtil;
import gui.util.Alerts;
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
	@FXML private Button btnDeleteSelectedAnnottion;
	@FXML private Label labelSelectedAnnotationCollaborator;
	
	private StudentDao studentDao;
	private ObservableList<Student> studentsList;
	private MainViewController mainView;	
	private final Integer ICON_SIZE = 15;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableStudentsNodes();
		initializeTableMatriculationsNodes();
		initiliazeTableParcelsNodes();
		initiliazeListViewAnnotations();
		addListeners();
	}
	
	public void addListeners() {
		textFilter.textProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
		filterType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
		filterStudentStatus.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterStudents();
		});
	}
	
	public void setMainViewController(MainViewController mainView) {
		this.mainView = mainView;
	}
	
	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
	}
	
	public void filterStudents(String status) {
		List<Student> filteredList = new ArrayList<>();
		String totalStudentsText = "";
		// Filter by status
		String statusSelected = ((RadioButton) filterStudentStatus.getSelectedToggle()).getText();
		Map<String, String> statusMap = new HashMap<>();
		statusMap.put("TODOS", null);
		statusMap.put("ATIVOS", "ATIVO");
		statusMap.put("AGUARDANDO", "AGUARDANDO");
		statusMap.put("INATIVOS", "INATIVO");
		String statusToFilter;
		if (status != null) {
			statusToFilter = status;
			if (statusToFilter.equalsIgnoreCase("ATIVO")) {
				statusATIVOS.setSelected(true);
			}
			if (statusToFilter.equalsIgnoreCase("AGUARDANDO")) {
				statusAGUARDANDO.setSelected(true);
			}
			if (statusToFilter.equalsIgnoreCase("INATIVO")) {
				statusINATIVOS.setSelected(true);
			}
		} else {
			statusToFilter = statusMap.get(statusSelected.toUpperCase());
		}
		if (statusToFilter != null) {
			final String statusSearchFinal = statusToFilter;
			filteredList = studentsList.stream().filter(student -> student.getStatus().equalsIgnoreCase(statusSearchFinal))
					.collect(Collectors.toList());
			totalStudentsText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " alunos " + statusSelected + ")";
		} else {
			filteredList = studentsList;
			totalStudentsText = "(Total de: " + Utils.pointSeparator(filteredList.size()) + " alunos)";
		}
		// Filter to search bar
		String value = textFilter.getText();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if(filterTypeSelected.equalsIgnoreCase("inicia com")) {
			filteredList = filteredList.stream()
					.filter(student -> student.getName().toUpperCase().startsWith(value.toUpperCase()))
					.collect(Collectors.toList());
		} else {
			filteredList = filteredList.stream()
					.filter(student -> student.getName().toUpperCase().contains(value.toUpperCase()))
					.collect(Collectors.toList());
		}
		
		labelTotalStudents.setText(totalStudentsText);
		ObservableList<Student> filteredObsList = FXCollections.observableArrayList(filteredList);
		tableStudents.setItems(filteredObsList);
		tableStudents.refresh();
		tableStudents.getSelectionModel().selectFirst();
	}
	
	public void filterStudents() {
		filterStudents(null);
	}
	
	public void updateTableView() {
		if(studentDao == null) {
			throw new IllegalStateException("StudentDao service not initialized");
		}
		try {
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithContactsLoaded());
			studentsList.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));
			filterStudents();
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os alunos", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}
	
	private void initializeTableStudentsNodes() {
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
		Utils.setCellValueFactory(columnStudentCode, "id");
		Utils.setCellValueFactory(columnStudentName, "name");
		columnStudentContact1.setCellValueFactory(cellData -> {
			try {
				if(!(cellData.getValue().getContacts() == null)) {
					return new SimpleStringProperty(cellData.getValue().getContacts().get(0).getNumber());
				} else {
					return new SimpleStringProperty("-");
				}
				
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		// Info button
		Utils.initButtons(columnStudentInfo, ICON_SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			showStudentInfo(student, FXMLPath.INFO_STUDENT);
		});
		// Listener to selected student
		tableStudents.getSelectionModel().selectedItemProperty().addListener(
	            (observable, oldSelection, newSelection) -> {
					tableMatriculations.setItems(null);
					if (newSelection != null) {
						// Reflesh student data
						DBFactory.getConnection().getTransaction().begin();
						DbUtil.refleshData(newSelection);
						DBFactory.getConnection().getTransaction().commit();
		            	tableStudents.refresh();
						updateAnnotations(null);
						if (newSelection.getMatriculations() != null && newSelection.getMatriculations().size() > 0) {
							try {
								tableMatriculations
										.setItems(FXCollections.observableList(newSelection.getMatriculations()));
								tableMatriculations.getSelectionModel().selectFirst();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
	    );
	}
	
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
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGA")).collect(Collectors.toList()).size();
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		// Listener to selected Matriculation
		tableMatriculations.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {					
					tableParcels.setItems(null);
					tableParcels.refresh();
					labelSelectedMatriculation.setText("");
					if (newSelection != null && newSelection.getParcels().size() > 0) {
						try {
							tableParcels.setItems(FXCollections.observableList(newSelection.getParcels()));
							String matriculationCode = Integer.toString(newSelection.getCode());
							setCurrentMatriculationId(matriculationCode);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
	}
	
	private void initiliazeTableParcelsNodes() {		
		// Status Color
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
	
	private void initiliazeListViewAnnotations() {
		listViewAnnotation.setCellFactory(param -> new ListCell<Annotation>() {
		    @Override
		    protected void updateItem(Annotation item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null) {
					setGraphic(null);
				} else {
					// Create the HBox
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
						tableParcels.refresh();
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
	
	public void handleBtnAddNewStudent(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Novo cadatro", false,
				(PersonFormController controller) -> {
					Student student = new Student();
					controller.setPersonEntity(student);
					controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
					controller.setInfoStudentController(null);
					controller.setMainView(this.mainView);
				});
	}
	
	public void handleBtnAddAnnotation(ActionEvent event) {
		Student studentSelected = tableStudents.getSelectionModel().getSelectedItem();
		if (studentSelected != null) {
			Utils.loadView(this, true, FXMLPath.ANNOTATION, Utils.currentStage(event), "Adicionar Anotação", false, (controller) -> {
				AnnotationController annotationController = (AnnotationController) controller;
				annotationController.setAnnotationDao(new AnnotationDao(DBFactory.getConnection()));
				annotationController.setDependences(null, studentSelected, Main.getCurrentUser().getName(), this);
			});
		}
	}
	
	public void handleBtnEditAnnotation(ActionEvent event){
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			Utils.loadView(this, true, FXMLPath.ANNOTATION, Utils.currentStage(event), "Editar Anotação", false, (controller) -> {
				AnnotationController annotationController = (AnnotationController) controller;
				annotationController.setAnnotationDao(new AnnotationDao(DBFactory.getConnection()));
				annotationController.setDependences(itemSelected, Main.getCurrentUser().getName(), this);
				
			});
		}
	}
	
	public void handleBtnDeleteAnnotation(ActionEvent event){
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar anotação");
			alert.setHeaderText("Deletar a anotação do dia " + sdf.format(itemSelected.getDate()) + " ?");
			Optional<ButtonType> result =alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				AnnotationDao annotationDao = new AnnotationDao(DBFactory.getConnection());
				try {
					Alert alertProcessing = Alerts.showProcessingScreen();
					annotationDao.delete(itemSelected);
					itemSelected.getStudent().getAnnotations().remove(itemSelected);
					updateAnnotations(null);
					alertProcessing.close();
				} catch (DbException e) {
					Alerts.showAlert("Erro ao deletar anotação", "DbException", e.getMessage(), AlertType.ERROR);
				}
			}
		}
	}
	
	private void showStudentInfo(Student student, String FxmlPath) {
		try {
			mainView.setContent(FxmlPath, (InfoStudentController controller) -> {
				controller.setMainViewControllerAndReturnName(mainView, "Alunos");
				// Reflesh student data
				DbUtil.refleshData(student);
				tableStudents.refresh();
				controller.setCurrentStudent(student);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setCurrentMatriculationId(String matriculationCode) {
		labelSelectedMatriculation.setText("Matrícula: " + matriculationCode);
	}
	
	public void updateAnnotations(Annotation annotation) {
		listViewAnnotation.setItems(null);
		Student studentSelected = tableStudents.getSelectionModel().getSelectedItem();
		try {
			if(studentSelected.getAnnotations() == null) {
				return;
			}
			ObservableList<Annotation> annotations = FXCollections.observableList(studentSelected.getAnnotations());
			annotations.sort((a1, a2) -> a2.getDate().compareTo(a1.getDate()));
			listViewAnnotation.setItems(annotations);
			listViewAnnotation.refresh();
			if (annotation != null) {
				listViewAnnotation.getSelectionModel().select(annotation);
			} else {
				listViewAnnotation.getSelectionModel().selectFirst();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
