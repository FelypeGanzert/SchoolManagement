package gui;

import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dao.CourseDao;
import model.entites.Course;
import model.entites.Student;
import sharedData.Globe;

public class StudentCoursesController implements Initializable{

	@FXML private JFXButton btnReturn;
	@FXML private Label labelStudentName;
	@FXML private JFXButton btnAddCourse;
	// Table courses
	@FXML protected TableView<Course> tableCourses;
	@FXML private TableColumn<Course, String> columnCourseName;
	@FXML private TableColumn<Course, Date> columnStartDate;
	@FXML private TableColumn<Course, Date> columnEndDate;
	@FXML private TableColumn<Course, String> columnProfessor;
	@FXML private TableColumn<Course, String> columnCourseLoad;
	@FXML private TableColumn<Course, Course> columnEdit;
	@FXML private TableColumn<Course, Course> columnDelete;
	
	private Student student;
	private ObservableList<Course> courses;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeTableCourses();
	}
	
	public void setStudent(Student student) {
		this.student = student;
		// Refresh student data
		DBUtil.refleshData(this.student);
		updateForm();
	}

	public void handleBtnAddCourse(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.COURSE_FORM, Utils.currentStage(event), "Adicionar curso", false,
				(CourseFormController controller) -> {
					controller.setDependences(new Course(), student, this);
				});
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
			if(cellData.getValue().getCourseLoad() != null) {
				int courseLoad = cellData.getValue().getCourseLoad();
				if (courseLoad > 0) {
					return new SimpleStringProperty(courseLoad + " hr");
				} else {
					return new SimpleStringProperty("");
				}
			} else {
				return new SimpleStringProperty("");
			}			
		});
		// Edit course button
		Utils.initButtons(columnEdit, Icons.SIZE, Icons.PEN_SOLID, "grayIcon", (course, event) -> {
			Utils.loadView(this, true, FXMLPath.COURSE_FORM, Utils.currentStage(event), "Adicionar curso", false,
					(CourseFormController controller) -> {
						controller.setDependences(course, student, this);
					});
		});
		// Delete course button
		Utils.initButtons(columnDelete, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (course, event) -> {
			// Confirmation to delete contact
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar curso");
			alert.setHeaderText("Deletar o curso: " + course.getName() + " ?");
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Show a screen of deleting course process
				try {
					Alert alertProcessing = Alerts.showProcessingScreen();
					// CourseDao to delete from db
					CourseDao courseDao = new CourseDao(DBFactory.getConnection());
					courseDao.delete(course);
					// remove course from student in memory and refresh UI
					student.getCourses().remove(course);
					updateForm();
					alertProcessing.close();
				} catch (DbException e) {
					Alerts.showAlert("Erro ao deletar curso", "DbException", e.getMessage(), AlertType.ERROR);
				}
			}
		});
	}
	
	public void updateForm() {
		// Set name and return button
		labelStudentName.setText(student.getName());
		String firstName;
		if (student.getName().contains(" ")) {
			firstName = student.getName().substring(0, student.getName().indexOf(" "));
		} else {
			firstName = student.getName();
		}
		btnReturn.setText("Voltar para " + firstName);
		// Course
		if (student.getCourses() != null) {
			courses = FXCollections.observableArrayList(student.getCourses());
			tableCourses.setItems(courses);
			tableCourses.refresh();
		}
	}
	
	public void onDataChange() {
		updateForm();
	}

}
