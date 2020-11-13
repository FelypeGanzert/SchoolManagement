package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.Label;
import model.dao.ResponsibleDao;
import model.dao.StudentDao;
import model.entites.Person;
import model.entites.Responsible;
import model.entites.Student;

public class RegularizeCPFPersonFormController implements Initializable {

	@FXML private Label labelStudentName;
	@FXML private JFXTextField textCPF;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;

	private Person entity;
	private StudentDao studentDao;
	private ResponsibleDao responsibleDao;
	
	private RegularizeCPFStudentsController studentsScreen; 
	private RegularizeCPFResponsiblesController responsiblesScreen; 
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		// CPF: required
		Constraints.cpfAutoComplete(textCPF);
		RegexValidator cpfValidator = new RegexValidator("Inválido");
		cpfValidator.setRegexPattern("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$");
		textCPF.setValidators(Validators.getRequiredFieldValidator(), cpfValidator);
		// define entites daos
		defineEntitiesDaos();
	}
	
	public void setPersonEntity(Person entity) {
		this.entity = entity;
		labelStudentName.setText(this.entity.getName());
	}
	
	public void setRegularizeCPFStudentsController(RegularizeCPFStudentsController studentsScreen) {
		this.studentsScreen = studentsScreen;
	}
	
	public void setRegularizeCPFResponsiblesController(RegularizeCPFResponsiblesController responsiblesScreen) {
		this.responsiblesScreen = responsiblesScreen;
	}
	
	private void defineEntitiesDaos() {
		studentDao = new StudentDao(DBFactory.getConnection());
		responsibleDao = new ResponsibleDao(DBFactory.getConnection());
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(!textCPF.validate()) {
			return;
		}
		String cpf = Constraints.getOnlyDigitsValue(textCPF);
		Student s = null;
		Responsible r = null;
		try {
			s = studentDao.findByCPF(cpf);
			r = responsibleDao.findByCPF(cpf);
		} catch (DbException e) {
			e.printStackTrace();
		}
		if(s != null || r != null) {
			Alerts.showAlert("ERRO", "CPF em uso", "Alguém já está usando esse CPF... "
					+ "Você pode conferir se foi digitado corretamente ou procurar a pessoa com tal CPF "
					+ "na lista de alunos ou responsáveis.", AlertType.ERROR, Utils.currentStage(event));
			// stop the method
			return;
		}
		// update in db
		entity.setCpf(cpf);
		if(entity instanceof Student) {
			try {
				studentDao.update((Student) entity);
			} catch (DbException e) {
				Alerts.showAlert("ERRO", "Houve algum erro...", e.getMessage(), AlertType.ERROR, Utils.currentStage(event));
				e.printStackTrace();
			}
		}
		if(entity instanceof Responsible) {
			try {
				responsibleDao.update((Responsible) entity);
			} catch (DbException e) {
				Alerts.showAlert("ERRO", "Houve algum erro...", e.getMessage(), AlertType.ERROR, Utils.currentStage(event));
				e.printStackTrace();
			}
		}
		// update screen that have called this and closed this
		if(studentsScreen != null) {
			studentsScreen.removeStudent((Student) entity);
		}
		if(responsiblesScreen != null) {
			responsiblesScreen.removeResponsible((Responsible) entity);
		}
		Utils.currentStage(event).close();
	}
	
	public void handleCancelBtn(ActionEvent event) {
		Utils.currentStage(event).close();
	}

}
