package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

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
import javafx.scene.control.Label;
import model.dao.CertificateRequestDao;
import model.entites.CertificateRequest;
import model.entites.Student;

public class CertificateRequestFormController implements Initializable{

	@FXML private Label labelStudentName;
	@FXML private JFXTextArea textCourse;
	@FXML private JFXTextField textStartDate;
	@FXML private JFXTextField textEndDate;
	@FXML private JFXTextField textCourseLoad;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Student student;
	private CertificateRequest certificateRequest;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// === Constraints and validators
		// Create Required and Date validator
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		// Course : required
		textCourse.setValidators(requiredValidator);
		// start and end date: required and have to be a valid date
		textStartDate.setValidators(requiredValidator);
		textStartDate.setValidators(dateValidator);
		textEndDate.setValidators(requiredValidator);
		textEndDate.setValidators(dateValidator);
		// course load: required and accept only numbers
		textCourseLoad.setValidators(requiredValidator);
		Constraints.setTextFieldInteger(textCourseLoad);
		// constraints to text length
		Constraints.setTextFieldMaxLength(textStartDate, 10);
		Constraints.setTextFieldMaxLength(textEndDate, 10);
		Constraints.setTextFieldMaxLength(textCourseLoad, 5);
	}
	
	public void setDependences(Student student) {
		this.student = student;
		// Set values from student to UI
		this.updateForm();
	}
	
	public void handleSaveBtn(ActionEvent event) {
		if (textCourse.validate() && textStartDate.validate() && textEndDate.validate() && textCourseLoad.validate()) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			Date startDate = null;
			Date endDate = null;
			try {
				startDate = sdf.parse(textStartDate.getText());
				endDate = sdf.parse(textEndDate.getText());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			// Check if start date is after of the end date, this can't happen...
			if (startDate != null && endDate != null) {
				if (startDate.compareTo(endDate) > 0) {
					Alerts.showAlert("Inválido", "A data de término é anterior a data de início.",
							"É impossível o aluno ter terminado um curso antes de ter iniciado.", AlertType.ERROR,
							Utils.currentStage(event));
					// stop the method
					return;
				}
			}
			// start: must be in the past
			// end: must be in the past or one month in future in the max
			Date oneMonthInFuture = new Date();
			Calendar c = DateUtil.dateToCalendar(oneMonthInFuture);
			c.add(Calendar.MONTH, 1);
			oneMonthInFuture = DateUtil.calendarToDate(c);
			if (startDate != null) {
				if (startDate.compareTo(new Date()) > 0) {
					Alerts.showAlert("Inválido", "A data de início é 1 posterior a data atual.",
							"Você não pode fazer a solicitação de algo que ainda não aconteceu.", AlertType.ERROR,
							Utils.currentStage(event));
					// stop the method
					return;
				}
			}
			if(endDate != null) {
				if(endDate.compareTo(oneMonthInFuture) > 0) {
					Alerts.showAlert("Inválido", "A data de término é 1 mês posterior a data atual.",
							"Você não pode fazer a solicitação de algo que ainda não aconteceu.",
							AlertType.ERROR, Utils.currentStage(event));
					// stop the method
					return;
				}
			}
			// Instantiate a certificate request if it is null
			if (certificateRequest == null) {
				certificateRequest = new CertificateRequest();
			}
			// student id and name
			certificateRequest.setStudentId(student.getId());
			certificateRequest.setStudentName(student.getName());
			// course
			certificateRequest.setCourse(textCourse.getText());
			// start and end date
			certificateRequest.setStartDate(startDate);
			certificateRequest.setEndDate(endDate);
			// course load
			certificateRequest.setCourseLoad(Utils.tryParseToInt(textCourseLoad.getText()));
			// request date
			certificateRequest.setRequestDate(new Date());
			// save in db
			try {
				CertificateRequestDao certificateRequestDao = new CertificateRequestDao(DBFactory.getConnection());
				// If he doesn't have a id, is a new request
				// otherwhise the request already is in database
				if (certificateRequest.getId() == null) {
					certificateRequestDao.insert(certificateRequest);
				} else {
					certificateRequestDao.update(certificateRequest);
				}
				// Show a message that certificate was requisited and close this form
				Alerts.showAlert("Solicitado", "Certificado Solicitado", "Tudo pronto!", AlertType.INFORMATION,
						Utils.currentStage(event));
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(), AlertType.ERROR,
						Utils.currentStage(event));
				e.printStackTrace();
			}
		}
	}

	public void handleCancelBtn(ActionEvent event) {
		Utils.currentStage(event).close();
	}
		
	private void updateForm() {
		labelStudentName.setText(student.getName());
	}
	
}