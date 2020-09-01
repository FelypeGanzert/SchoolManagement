
package gui;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import db.DbException;
import gui.util.Icons;
import gui.util.Utils;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.dao.StudentDao;
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
	@FXML JFXListView<Date> listViewAnnotations;
	@FXML Label labelSelectedAnnotationDate;
	@FXML JFXTextArea textAreaAnnotation;
	@FXML Button btnEditSelectedAnnotation;
	@FXML Button btnDeleteSelectedAnnottion;
	@FXML Label labelSelectedAnnotationCollaborator;
	
	private StudentDao studentDao;
	private ObservableList<Student> studentsList;
	
	
	private final Integer ICON_SIZE = 15;
	private final Integer COLUMN_ICON_SPACE = 20;
	private Map<String, String> statusColors = new HashMap<>();
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initiliazeTableStudentsNodes();
		statusColors.put("ATIVO", "#a2d6f9");
		statusColors.put("AGUARDANDO", "#b7e4c7");
		statusColors.put("INATIVO", "#BFBFBF");
	}
	
	public void setStudentDao(StudentDao studentDao) {
		System.out.println("==================== setStudentDao method ========");
		this.studentDao = studentDao;
		try {
			studentsList = FXCollections.observableArrayList(this.studentDao.findAllWithContactsLoaded());
		} catch (DbException e) {
			e.printStackTrace();
		}
		tableStudents.setItems(studentsList);
	}
	
	private void initiliazeTableStudentsNodes() {
		columnStudentStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
		columnStudentStatus.setCellFactory(column -> {
			return new TableCell<Student, String>() {
				@Override
				protected void updateItem(String status, boolean empty) {
					super.updateItem(status, empty);
					setText("");
					setGraphic(null);
					if (!isEmpty()) {
						this.setStyle("-fx-background-color:" + statusColors.get(getItem().toUpperCase()));
					}
				}
			};
	    });
		
		columnStudentCode.setCellValueFactory(new PropertyValueFactory<>("id"));
		columnStudentName.setCellValueFactory(new PropertyValueFactory<>("name"));
		
		columnStudentContact1.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(cellData.getValue().getContacts().get(1).getNumber());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		
		// Info button
		initButtons(columnStudentInfo, ICON_SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			showStudentInfo(student, "/gui/SomeFile.fxml", Utils.currentStage(event));
		});
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
