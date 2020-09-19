package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import model.dao.AnnotationDao;
import model.entites.Annotation;
import model.entites.Student;

public class AnnotationController implements Initializable{

	@FXML private Label labelStudentName;
	@FXML private Label labelDate;
	@FXML private Label labelResponsibleEmployee;
	@FXML private JFXTextArea textAreaDescription;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private AnnotationDao annotationDao;
	private Annotation annotation;
	private Student student;
	private String responsibleCollaborator;
	

	private ListStudentsController listStudentsController;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		textAreaDescription.setValidators(Validators.getRequiredFieldValidator());
	}
	
	public void setDependences(Annotation annotation, Student student, String responsibleCollaborator, ListStudentsController listStudentsController) {
		this.listStudentsController = listStudentsController;
		this.annotation = annotation;
		this.student = student;
		this.responsibleCollaborator = responsibleCollaborator;
		setValuesToAnnotation();
		this.setLabels();
	}
	
	public void setDependences(Annotation annotation, String responsibleCollaborator, ListStudentsController listStudentsController) {
		this.listStudentsController = listStudentsController;
		this.annotation = annotation;
		this.student = annotation.getStudent();
		this.responsibleCollaborator = responsibleCollaborator;
		setValuesToAnnotation();
		this.setLabels();
	}
	
	public void setAnnotationDao(AnnotationDao annotationDao) {
		this.annotationDao = annotationDao;
	}
	
	public void handleSaveBtn(ActionEvent event) {
		if (annotationDao == null) {
			throw new IllegalStateException("AnnotationDao not instantied");
		}
		if (textAreaDescription.validate()) {
			annotation.setResponsibleCollaborator(responsibleCollaborator);
			annotation.setDescription(textAreaDescription.getText());

			try {
				if (annotation.getId() == null) {
					annotationDao.insert(annotation);
				} else {
					annotationDao.update(annotation);
				}
				listStudentsController.updateAnnotations(annotation);
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),
						AlertType.ERROR);
			}
		}
	}
	
	public void handleCancelBtn(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	private void setValuesToAnnotation() {
		if(annotation == null) {
			annotation = new Annotation();
			annotation.setDate(new Date());
			annotation.setStudent(student);
			annotation.setResponsibleCollaborator(responsibleCollaborator);
			student.getAnnotations().add(annotation);
		}
	}
	
	private void setLabels() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		labelStudentName.setText(student.getName());
		labelResponsibleEmployee.setText(annotation.getResponsibleCollaborator());
		labelDate.setText(sdf.format(annotation.getDate()));
		textAreaDescription.setText(annotation.getDescription());
	}
	
}
