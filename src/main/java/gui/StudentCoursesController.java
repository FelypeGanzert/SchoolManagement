package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.entites.Course;
import model.entites.Student;
import sharedData.Globe;

public class StudentCoursesController implements Initializable{

	@FXML private JFXButton btnReturn;
	@FXML private Label labelStudentName;
	@FXML private JFXButton btnAddCourse;
	// Table courses
	@FXML private TableView<Course> tableCourses;
	@FXML private TableColumn<Course, String> columnCourseName;
	@FXML private TableColumn<Course, Date> columnStartDate;
	@FXML private TableColumn<Course, Date> columnEndDate;
	@FXML private TableColumn<Course, String> columnProfessor;
	@FXML private TableColumn<Course, String> columnCourseLoad;
	@FXML private TableColumn<Course, Course> columnEdit;
	@FXML private TableColumn<Course, Course> columnDelete;
	
	private Student student;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeTableCourses();
	}
	
	public void setStudent(Student student) {
		this.student = student;
		ObservableList<Course> courses = FXCollections.observableArrayList(this.student.getCourses());
		tableCourses.setItems(courses);
		labelStudentName.setText(student.getName());
		String firstName;
		if(student.getName().contains(" ")) {
			firstName = student.getName().substring(0, student.getName().indexOf(" "));	
		} else {
			firstName = student.getName();
		}
		btnReturn.setText("Voltar para " + firstName);
	}

	public void handleBtnAddCourse(ActionEvent event) {
		System.out.println("You clicked to add a new course");
	}
	
	public void handleBtnReturn(ActionEvent event) {
		try {
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
				controller.setReturn(FXMLPath.LIST_STUDENTS, "Alunos");
				controller.setCurrentStudent(student);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void initializeTableCourses() {
		Utils.setCellValueFactory(columnCourseName, "name");
		Utils.setCellValueFactory(columnStartDate, "startDate");
		Utils.formatTableColumnDate(columnStartDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnEndDate, "endDate");
		Utils.formatTableColumnDate(columnEndDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnProfessor, "professor");
		// Course Load
		columnCourseLoad.setCellValueFactory(cellData -> {
			int courseLoad = cellData.getValue().getCourseLoad();
			if (courseLoad > 0) {
				return new SimpleStringProperty(courseLoad + " hr");
			} else {
				return new SimpleStringProperty("");
			}
		});
		// Edit course button
		Utils.initButtons(columnEdit, Icons.SIZE, Icons.PEN_SOLID, "grayIcon", (course, event) -> {
			Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Editar curso", false,
					(controller) -> {
						System.out.println("You clicked to edit");
					});
		});
		// Delete course button
		Utils.initButtons(columnDelete, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (course, event) -> {
			Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Excluir curso", false,
					(controller) -> {
						System.out.println("You clicked to edit");
					});
		});
	}

}
