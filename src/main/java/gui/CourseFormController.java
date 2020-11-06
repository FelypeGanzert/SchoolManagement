package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.DateUtil;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.util.StringConverter;
import model.dao.CourseDao;
import model.entites.Course;
import model.entites.Matriculation;
import model.entites.Student;

public class CourseFormController implements Initializable{

	@FXML private JFXTextField textCourseName;
	@FXML private JFXTextField textProfessor;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textDay;
	@FXML private JFXTextField textHour;
	@FXML private JFXTextField textCourseLoad;
	@FXML private JFXComboBox<Matriculation> comboBoxMatriculation;
	
	private Course course;
	private Student student;
	private StudentCoursesController studentCourses;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Constraints: name, professor
		textCourseName.setValidators(Validators.getRequiredFieldValidator());
		Constraints.setTextFieldMaxLength(textCourseName, 50);
		Constraints.setTextFieldMaxLength(textProfessor, 30);
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
		// constraints: day and hour
		Constraints.setTextFieldMaxLength(textDay, 30);
		Constraints.setTextFieldMaxLength(textHour, 30);
	}

	// DEPENDENCES
	public void setDependences(Course course, Student student, StudentCoursesController studentCourses) {
		this.course = course;
		this.student = student;
		this.studentCourses = studentCourses;
		// comboBox: associated matriculation
		comboBoxMatriculation.getItems().add(null);
		comboBoxMatriculation.getItems().addAll(student.getMatriculations());
		// show only the code and date of the matriculation in comboBox
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		comboBoxMatriculation.setConverter(new StringConverter<Matriculation>() {
			@Override
			public String toString(Matriculation object) {
				if(object != null) {
					return object.getCode() + " - " + sdf.format(object.getDateMatriculation());	
				} else {
					return null;
				}
			}
			@Override
			public Matriculation fromString(String string) {
				return null;
			}
		});
		// select matriculation of current course
		Matriculation selectedMatriculation = null;
		if(this.course.getMatriculationCode() != null) {
			for(Matriculation m : student.getMatriculations()) {
				if(m.getCode() == this.course.getMatriculationCode()) {
					selectedMatriculation = m;
					break;
				}
			}
		}
		comboBoxMatriculation.getSelectionModel().select(selectedMatriculation);
		updateForm();
	}
	
	// Update UI with course information
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
		textDay.setText(course.getDay());
		textHour.setText(course.getHour());
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(textCourseName.validate() && textStartDate.validate()) {
			// stop the method if end date is not null and is not valide
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
								"É impossível o aluno ter terminado o curso antes de ter iniciado",
								AlertType.ERROR, Utils.currentStage(event));
						// stop the method
						return;
					}
				}
				// start and end date: must be in the past or one month in future in the max
				Date oneMonthInFuture = new Date();
				Calendar c = DateUtil.dateToCalendar(oneMonthInFuture);
				c.add(Calendar.MONTH, 1);
				oneMonthInFuture = DateUtil.calendarToDate(c);
				if(course.getStartDate() != null) {
					if(course.getStartDate().compareTo(oneMonthInFuture) > 0) {
						Alerts.showAlert("Inválido", "A data de início é 1 mês posterior a data atual.",
								"Você não pode fazer o registro de algo que ainda não aconteceu.",
								AlertType.ERROR, Utils.currentStage(event));
						// stop the method
						return;
					}
				}
				if(course.getEndDate() != null) {
					if(course.getEndDate().compareTo(oneMonthInFuture) > 0) {
						Alerts.showAlert("Inválido", "A data de término é 1 mês posterior a data atual.",
								"Você não pode fazer o registro de algo que ainda não aconteceu.",
								AlertType.ERROR, Utils.currentStage(event));
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
			// day and hour
			course.setDay(textDay.getText());
			course.setHour(textHour.getText());
			// associated matriculation
			if(comboBoxMatriculation.getSelectionModel().getSelectedItem() != null) {
				course.setMatriculationCode(comboBoxMatriculation.getSelectionModel().getSelectedItem().getCode());
			} else {
				course.setMatriculationCode(null);
			}
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
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),
						AlertType.ERROR, Utils.currentStage(event));
				e.printStackTrace();
			} 
		}
	}

}