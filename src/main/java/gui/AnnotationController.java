package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import db.DBFactory;
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
import model.entites.Collaborator;
import model.entites.Student;
import sharedData.Globe;

public class AnnotationController implements Initializable{

	@FXML private Label labelStudentName;
	@FXML private Label labelDate;
	@FXML private Label labelResponsibleCollaborator;
	@FXML private JFXTextArea textAreaDescription;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Annotation annotation;
	private Student student;
	private ListStudentsController listStudentsController;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		textAreaDescription.setValidators(Validators.getRequiredFieldValidator());
	}
	
	// All dependences
	public void setDependences(Annotation annotation, Student student, ListStudentsController listStudentsController) {
		this.annotation = annotation;
		this.student = student;
		this.listStudentsController = listStudentsController;
		// if annotation is null, user is adding a new annotation.
		// So, are setted some defaults values
		if(annotation == null) {
			this.annotation = new Annotation();
			this.annotation.setDate(new Date());
			Collaborator currentUser = Globe.getStateItem(Collaborator.class, "main", "main", "currentUser");
			this.annotation.setResponsibleCollaborator(currentUser.getInitials());
		}
		// Set values from annotation variable to UI
		this.updateForm();
	}
	
	// Called when user will add a new annotation to a student
	public void setDependences(Student student, ListStudentsController listStudentsController) {
		setDependences(null, student, listStudentsController);
	}
	
	// Called when user will edit a annotation
	public void setDependences(Annotation annotation, ListStudentsController listStudentsController) {
		setDependences(annotation, annotation.getStudent(), listStudentsController);
	}
	
	public void handleSaveBtn(ActionEvent event) {
		if (textAreaDescription.validate()) {
			Collaborator currentUser = Globe.getStateItem(Collaborator.class, "main", "main", "currentUser");
			annotation.setResponsibleCollaborator(currentUser.getInitials());
			annotation.setDescription(textAreaDescription.getText());
			// Add annotation to student in memory if is a new Annotation
			if(annotation.getStudent() == null) {
				this.annotation.setStudent(this.student);
				this.student.getAnnotations().add(this.annotation);
				
			}
			try {
				AnnotationDao annotationDao = new AnnotationDao(DBFactory.getConnection());
				// If he doesn't have a id, is a new annotation,
				// otherwhise the annotations already is in database
				if (annotation.getId() == null) {
					annotationDao.insert(annotation);
				} else {
					annotationDao.update(annotation);
				}
				// Update annotations from student in listStudent and then close this form
				listStudentsController.updateAnnotations(annotation.getStudent());
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),	AlertType.ERROR);
				e.printStackTrace();
			} 
		}
	}
	
	public void handleCancelBtn(ActionEvent event) {
		Utils.currentStage(event).close();
	}
		
	private void updateForm() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
		labelStudentName.setText(student.getName());
		labelResponsibleCollaborator.setText(annotation.getResponsibleCollaborator());
		labelDate.setText(sdf.format(annotation.getDate()));
		textAreaDescription.setText(annotation.getDescription());
	}
	
}