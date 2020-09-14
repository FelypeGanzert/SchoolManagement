package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import animatefx.animation.ZoomIn;
import db.DbException;
import gui.util.Alerts;
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
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		HBoxInformations.setVisible(false);
		HBoxRegistryInformations.setVisible(true);
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
		getFormData();
		try {
			if (entity.getId() != 0) {
				if (entity instanceof Student) {
					studentDao.update((Student) entity);
				}
			} else {
				if (entity instanceof Student) {
					studentDao.insert((Student) entity);
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR);
		}
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
		System.out.println(" ================== Person name: " + entity.getName());
	}

	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
	}
	
	public void updateFormData() {
		//comboBoxRegisteredBy
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		checkBoxSendEmail.setSelected(entity.getSendEmail());
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
