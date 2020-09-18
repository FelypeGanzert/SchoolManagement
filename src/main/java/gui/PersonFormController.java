package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import animatefx.animation.ZoomIn;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FxmlPaths;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import model.dao.StudentDao;
import model.entites.Contact;
import model.entites.Person;
import model.entites.Student;

public class PersonFormController implements Initializable {
	// Search Bar
	@FXML JFXTextField textCPF;
	@FXML JFXTextField textName;
	@FXML JFXButton btnFindRegistry;
	@FXML Label labelFindRegistryResponse;
	// Registry Informations
	@FXML HBox HBoxRegistryInformations;
	@FXML JFXComboBox comboBoxRegisteredBy;
	@FXML JFXTextField textDateRegistry;
	// Person Informations
	@FXML HBox HBoxInformations;
	@FXML JFXTextField textRG;
	@FXML JFXTextField textEmail;
	@FXML JFXComboBox comboBoxCivilStatus;
	@FXML JFXCheckBox checkBoxSendEmail;
	@FXML JFXTextField textBirthDate;
	@FXML JFXTextField textAdress;
	@FXML JFXComboBox comboBoxGender;
	@FXML JFXTextField textNeighborhood;
	@FXML JFXTextField textCity;
	@FXML JFXTextField textUF;
	@FXML JFXComboBox comboBoxStatus;
	@FXML JFXTextField textAdressReference;
	@FXML JFXTextArea textAreaObservation;
	// Table Contacts
	@FXML TableView<Contact> tableContacts;
	@FXML TableColumn<Contact, String> columnContactNumber;
	@FXML TableColumn<Contact, String> columnContactDescription;
	@FXML TableColumn<Contact, Contact> columnContactEdit;
	@FXML TableColumn<Contact, Contact> columnContactDelete;
	@FXML Button btnAddContact;
	// Buttons
	@FXML JFXButton btnSave;
	@FXML JFXButton btnCancel;
	
	private Person entity;
	private StudentDao studentDao;
	
	private InfoStudentController infoStudentController;
	private MainViewController mainView;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		HBoxInformations.setVisible(false);
		HBoxRegistryInformations.setVisible(true);
		setMasksAndValidators();
	}
	
	private void setMasksAndValidators() {
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
		requiredValidator.setMessage("Campo necessário");
		
		textName.setValidators(requiredValidator);
		
		Constraints.cpf(textCPF);
		RegexValidator cpfValidator = new RegexValidator("Insira um CPF válido");
		cpfValidator.setRegexPattern("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$");
		textCPF.setValidators(requiredValidator, cpfValidator);
		
		Constraints.rg(textRG);
		RegexValidator rgValidator = new RegexValidator("Insira um RG válido");
		rgValidator.setRegexPattern("^\\d{2}\\.\\d{3}\\.\\d{3}\\-\\d{1}$");
		textRG.setValidators(rgValidator);
	}
	
	public void handleBtnFindRegistry(ActionEvent event) {
		// Just testing...
		if(HBoxInformations.isVisible()) {
			HBoxInformations.setVisible(false);
		} else {
			new ZoomIn(HBoxInformations).play();
			HBoxInformations.setVisible(true);
		}
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(studentDao == null) {
			throw new IllegalStateException("StudentDao is null");
		}
		getFormData();
		try {
			if(entity instanceof Student) {
				if(!textCPF.validate() || !textName.validate() || (textRG.getText().length() > 0 && !textRG.validate())) {
					return;
				}
				if (entity.getId() != null) {
					studentDao.update((Student) entity);
				} else {
					((Student) entity).setStatus("ATIVO");
					((Student) entity).setGender("M");
					studentDao.insert((Student) entity);
				}
				
				if (this.infoStudentController != null) {
					this.infoStudentController.onDataChanged((Student) entity);
				} else {
					try {
						mainView.setContent(FxmlPaths.INFO_STUDENT, (InfoStudentController controller) -> {
							controller.setMainViewControllerAndReturnName(mainView, "Alunos");
							controller.setCurrentStudent((Student) entity);
						});
					} catch (IOException e) {
						e.printStackTrace();
						Alerts.showAlert("IOException", "Erro ao exibir tela prncipal para sair", e.getMessage(), AlertType.ERROR);
					}
				}
			}
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		//Utils.currentStage(event).close();
		textCPF.validate();
		textRG.validate();
	}

	public void setPersonEntity(Person entity) {
		this.entity = entity;
		updateFormData();
		if(entity.getId() != null) {
			btnFindRegistry.setVisible(false);
			HBoxRegistryInformations.setVisible(false);
			labelFindRegistryResponse.setVisible(false);
			HBoxInformations.setVisible(true);
		}
	}

	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
	}
	
	public void setMainView(MainViewController mainView){
		this.mainView = mainView;
	}
	
	public void setInfoStudentController(InfoStudentController infoStudentController) {
		this.infoStudentController = infoStudentController;
	}
	
	public void updateFormData() {
		//comboBoxRegisteredBy
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		if(entity.getSendEmail() != null) {
			checkBoxSendEmail.setSelected(entity.getSendEmail());
		}
		textCPF.setText(entity.getCpf());
		textRG.setText(entity.getRg());
		//comboBoxGender
		//comboBoxCivilStatus
		textNeighborhood.setText(entity.getNeighborhood());
		textAdress.setText(entity.getAdress());
		textAdressReference.setText(entity.getAdressReference());
		textCity.setText(entity.getCity());
		textUF.setText(entity.getUf());
		textAreaObservation.setText(entity.getObservation());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
			textDateRegistry.setText(sdf.format(entity.getDateRegistry()));
		}
	}
	
	public void getFormData() {
		// comboBoxRegisteredBy
		entity.setName(textName.getText());
		entity.setEmail(textEmail.getText());
		entity.setSendEmail(checkBoxSendEmail.isSelected());
		entity.setCpf(textCPF.getText());
		entity.setRg(textRG.getText());
		// comboBoxGender
		// comboBoxCivilStatus
		entity.setNeighborhood(textNeighborhood.getText());
		entity.setAdress(textAdress.getText());
		entity.setAdressReference(textAdressReference.getText());
		entity.setCity(textCity.getText());
		entity.setUf(textUF.getText());
		entity.setObservation(textAreaObservation.getText());
//		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//		try {
//			entity.setDateBirth(sdf.parse(textBirthDate.getText()));
//			entity.setDateRegistry(sdf.parse(textDateRegistry.getText()));
//		} catch (ParseException e) {
//			e.printStackTrace();
//		}

	}

}
