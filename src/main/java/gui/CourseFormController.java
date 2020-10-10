package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.dao.CourseDao;
import model.entites.Course;
import model.entites.Student;

public class CourseFormController implements Initializable{

	@FXML private JFXTextField textCourseName;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textProfessor;
	@FXML private JFXTextField textCourseLoad;
	
	private Course course;
	private Student student;
	private StudentCoursesController studentCourses;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Constraints: name, professor
		textCourseName.setValidators(Validators.getRequiredFieldValidator());
		Constraints.setTextFieldMaxLength(textCourseName, 50);
		Constraints.setTextFieldMaxLength(textProfessor, 50);
		// Date Validator: start and end
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textStartDate.setValidators(Validators.getRequiredFieldValidator());
		textStartDate.setValidators(dateValidator);
		textEndDate.setValidators(dateValidator);
		Constraints.setTextFieldMaxLength(textStartDate, 10);
		Constraints.setTextFieldMaxLength(textEndDate, 10);
		// Constraints: Course Load
		Constraints.setTextFieldIntegerYear(textCourseLoad);
		Constraints.setTextFieldMaxLength(textCourseLoad, 5);
	}
	
	// DEPENDENCES
	public void setDependences(Course course, Student student, StudentCoursesController studentCourses) {
		this.course = course;
		this.student = student;
		this.studentCourses = studentCourses;
		updateForm();
	}
	
	// Update Ui with course information
	public void updateForm() {
		textCourseName.setText(course.getName());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(course.getStartDate() != null) {
			textStartDate.setText(sdf.format(course.getStartDate()));
		}

		if(course.getEndDate() != null) {
			textEndDate.setText(sdf.format(course.getEndDate()));
		}
		textProfessor.setText(course.getProfessor());
		if(course.getCourseLoad() != null) {
			textCourseLoad.setText(Integer.toString(course.getCourseLoad()));			
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(textCourseName.validate() && textStartDate.validate()) {
			// stop the method if end date is not null and is not valide
			System.out.println("End date = " + textEndDate.getText());
			if(textEndDate.getText() != null && textEndDate.getText().length() > 0 && !textEndDate.validate()) {
				return;
			}
			// Create an instance if course is null
			if(course == null) {
				course = new Course();
			}
			// Get data from UI Form
			// Name
			course.setName(textCourseName.getText());
			// start and end date
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				if(textStartDate.getText() != null && textStartDate.getText().length() > 0) {
					course.setStartDate(sdf.parse(textStartDate.getText()));
				}
				if(textEndDate.getText() != null && textEndDate.getText().length() > 0) {
					course.setEndDate(sdf.parse(textEndDate.getText()));
				}
				// Check if start date is after of the end date, this can't happen...
				if(course.getStartDate() != null && course.getEndDate() != null) {
					if(course.getStartDate().compareTo(course.getEndDate()) > 0) {
						Alerts.showAlert("Inválido", "A data de término é anterior a data de início.",
								"É impossível o aluno ter terminado um curso antes de ter iniciado.", AlertType.ERROR);
						// stop the method
						return;
					}
				}
			} catch (ParseException e) {
				System.out.println("Problema com data...");
				e.printStackTrace();
			}
			// professor and courseLoad
			course.setProfessor(textProfessor.getText());
			course.setCourseLoad(Utils.tryParseToInt(textCourseLoad.getText()));
			// Save in DB
			try {
				CourseDao courseDao = new CourseDao(DBFactory.getConnection());
				// If he doesn't have a id, is a new course,
				// otherwhise the course already is in database
				if (course.getId() == null) {
					course.setStudent(student);
					student.getCourses().add(course);
					courseDao.insert(course);
				} else {
					courseDao.update(course);
				}
				// Update Screen that opens this form
				if(studentCourses != null) {
					studentCourses.onDataChange();
					studentCourses.tableCourses.getSelectionModel().select(course);
					studentCourses.tableCourses.scrollTo(course);
				}
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),	AlertType.ERROR);
				e.printStackTrace();
			} 
		}
	}

}
