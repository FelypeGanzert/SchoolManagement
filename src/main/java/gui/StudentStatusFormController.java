package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.enums.StudentStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import model.dao.StudentDao;
import model.entites.Student;
import sharedData.Globe;

public class StudentStatusFormController implements Initializable {

	private Student student;
	private InfoStudentController infoStudentController;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public void setInfoStudentController(InfoStudentController infoStudentController) {
		this.infoStudentController = infoStudentController;		
	}

	public void handleBtnClick(ActionEvent event) {
		if (student == null) {
			throw new IllegalStateException("Student is null");
		}
		// Try to get dao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		StudentDao studentDao = Globe.getGlobe().getItem(StudentDao.class, "studentDao");
		if (studentDao == null) {
			studentDao = new StudentDao(DBFactory.getConnection());
			Globe.getGlobe().putItem("studentDao", studentDao);
		}
		// Try to update student status
		try {
			// Get the status clicked and set in student
			String btnText = ((JFXButton) event.getSource()).getText();
			student.setStatus(StudentStatusEnum.fromString(btnText).toString());
			// Update student in db
			studentDao.update(student);
			// Finally, if everything occurs fine, we update InfoStudent and close this form
			this.infoStudentController.onDataChanged();
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(),
					AlertType.ERROR, Utils.currentStage(event));
		}
	}

}
