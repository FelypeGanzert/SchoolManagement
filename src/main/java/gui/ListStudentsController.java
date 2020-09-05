
package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import db.DbException;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import gui.util.enums.StudentStatusEnum;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.dao.StudentDao;
import model.entites.Annotation;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Student;

public class ListStudentsController implements Initializable {

	// Filter Student and Register
	@FXML JFXTextField textFilter;
	@FXML JFXComboBox<String> comboBoxfiledFilter;
	@FXML ToggleGroup filterType;
	@FXML ToggleGroup StudentStatus;
	@FXML JFXButton btnRegister;
	// Table Students
	@FXML TableView<Student> tableStudents;
	@FXML TableColumn<Student, String> columnStudentStatus;
	@FXML TableColumn<Student, Integer> columnStudentCode;
	@FXML TableColumn<Student, String> columnStudentName;
	@FXML TableColumn<Student, String> columnStudentContact1;
	@FXML TableColumn<Student, Student> columnStudentInfo;
	// Table Matriculations
	@FXML TableView<Matriculation> tableMatriculations;
	@FXML TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML TableColumn<Matriculation, String> columnMatriculationParcels;
	// Table Parcels
	@FXML Label labelSelectedMatriculation;
	@FXML TableView<Parcel> tableParcels;
	@FXML TableColumn<Parcel, String> columnParcelStatus;
	@FXML TableColumn<Parcel, Integer> columnParcelParcel;
	@FXML TableColumn<Parcel, Date> columnParcelDate;
	@FXML TableColumn<Parcel, Double> columnParcelValue;
	// Annotations
	@FXML Button btnAddAnnotation;
	@FXML JFXListView<Annotation> listViewAnnotation;
	@FXML Label labelSelectedAnnotationDate;
	@FXML JFXTextArea textAreaAnnotation;
	@FXML Button btnEditSelectedAnnotation;
	@FXML Button btnDeleteSelectedAnnottion;
	@FXML Label labelSelectedAnnotationCollaborator;
	
	private StudentDao studentDao;
	private ObservableList<Student> studentsList;
	
	
	private final Integer ICON_SIZE = 15;
	private final Integer COLUMN_ICON_SPACE = 20;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableStudentsNodes();
		initializeTableMatriculationsNodes();
		initiliazeTableParcelsNodes();
		initiliazeListViewAnnotations();
	}
	
	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
		try {
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithContactsLoaded());
		} catch (DbException e) {
			e.printStackTrace();
		}
		tableStudents.setItems(studentsList);
		tableStudents.getSelectionModel().selectFirst();
		tableMatriculations.getSelectionModel().selectFirst();
	}
	
	private void initializeTableStudentsNodes() {
		columnStudentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		columnStudentStatus.setCellFactory(column -> {
			return new TableCell<Student, String>() {
				@Override
				protected void updateItem(String status, boolean empty) {
					super.updateItem(status, empty);
					setText("");
					setGraphic(null);
					if (!isEmpty()) {
						this.setStyle("-fx-background-color:" + StudentStatusEnum.fromString(getItem()).getHexColor());
					}
				}
			};
	    });
		columnStudentCode.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
		columnStudentContact1.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(cellData.getValue().getContacts().get(0).getNumber());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		// Info button
		initButtons(columnStudentInfo, ICON_SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			showStudentInfo(student, "/gui/SomeFile.fxml", Utils.currentStage(event));
		});
		// Listener to selected student
		tableStudents.getSelectionModel().selectedItemProperty().addListener(
	            (observable, oldSelection, newSelection) -> {
					tableMatriculations.setItems(null);
					listViewAnnotation.setItems(null);
					if (newSelection != null && newSelection.getMatriculations().size() > 0) {
						try {
							tableMatriculations.setItems(FXCollections.observableList(newSelection.getMatriculations()));
							tableMatriculations.getSelectionModel().selectFirst();
							listViewAnnotation.setItems(FXCollections.observableList(newSelection.getAnnotations()));
							listViewAnnotation.getSelectionModel().selectFirst();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
	    );
	}
	
	private void initializeTableMatriculationsNodes() {
		columnMatriculationCode.setCellValueFactory(new PropertyValueFactory<>("code"));
		columnMatriculationDate.setCellValueFactory(new PropertyValueFactory<>("dateMatriculation"));
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		columnMatriculationStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGO")).collect(Collectors.toList()).size();
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
							labelSelectedMatriculation.setText(Integer.toString(newSelection.getCode()));
							tableParcels.setItems(FXCollections.observableList(newSelection.getParcels()));
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

		columnParcelParcel.setCellValueFactory(new PropertyValueFactory<>("parcelNumber"));
		columnParcelDate.setCellValueFactory(new PropertyValueFactory<>("dateParcel"));
		Utils.formatTableColumnDate(columnParcelDate, "dd/MM/yyyy");
		columnParcelValue.setCellValueFactory(new PropertyValueFactory<>("value"));
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
					Label label = new Label(sdf.format(item.getDateAnnotation()));
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
					labelSelectedMatriculation.setText("");
					if (newSelection != null) {
							SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
							labelSelectedAnnotationDate.setText(sdf.format(newSelection.getDateAnnotation()));
							textAreaAnnotation.setText(newSelection.getDescription());
							labelSelectedAnnotationCollaborator.setText(newSelection.getResponsibleEmployee());
						
					}
					
					
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
	}
	
	public void handleBtnAddAnnotation(ActionEvent event) {
		System.out.println("You will add a new annotation");
	}
	
	public void handleBtnEditAnnotation(ActionEvent event){
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			System.out.println("You will edit annotation from day " + itemSelected.getDateAnnotation());
		}
	}
	
	public void handleBtnDeleteAnnotation(ActionEvent event){
		Annotation itemSelected = listViewAnnotation.getSelectionModel().getSelectedItem();
		if(itemSelected != null) {
			System.out.println("You will delete annotation from day " + itemSelected.getDateAnnotation());
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar anotação");
			alert.setHeaderText("Deletear a anotação do dia " + itemSelected.getDateAnnotation() + " ?");
			
			Optional<ButtonType> result =alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
			    System.out.println("You confirmed te deletation...");
			}
		}
	}
	
	
	private <T, T2> void initButtons(TableColumn<Student, Student> tableColumn,
			int size, String svgIcon, String className, BiConsumer<Student, ActionEvent> buttonAction) {
		tableColumn.setMinWidth(size+COLUMN_ICON_SPACE);
		tableColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
		tableColumn.setCellFactory(param -> new TableCell<Student, Student>() {
			private final Button button = Utils.createIconButton(svgIcon, size, className);
			@Override
			protected void updateItem(Student student, boolean empty) {
				super.updateItem(student, empty);
				if (student == null) {
					setGraphic(null);
					return;
				}
				setGraphic(button);
				button.setOnAction(event -> {
					buttonAction.accept(student, event);
				});
			}
		});
	}
	
	private void showStudentInfo(Student student, String string, Stage stage) {
		System.out.println("Will show info of: " + student.getName());
	}

}
