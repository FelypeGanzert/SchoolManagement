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
import model.dao.ContactDao;
import model.entites.Contact;
import model.entites.Person;

public class ContactFormController implements Initializable {

	@FXML private JFXTextField textNumber;
	@FXML private JFXTextField textDescription;
	// Buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private final String DEFAULT_NUMBER = "9";
	
	private Contact contact;
	private Person person;
	// Screen that can call this form
	private InfoStudentController infoStudent;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Constraints
		Constraints.setTextFieldInteger(textNumber);
		Constraints.setTextFieldMaxLength(textNumber, 15);
		Constraints.setTextFieldMaxLength(textDescription, 30);
		// Validators
		textNumber.setValidators(Validators.getRequiredFieldValidator());
		RegexValidator lengthValidator = new RegexValidator("Mínimo de 8 dígitos");
		lengthValidator.setRegexPattern("^\\d{8,}$");
		textNumber.setValidators(lengthValidator);
		// Set default text to number
		textNumber.setText(DEFAULT_NUMBER);
	}
	
	// DEPENDENCES
	public void setDependences(Contact contact, Person person, InfoStudentController infoStudent) {
		this.contact = contact;
		this.person = person;
		this.infoStudent = infoStudent;
		if(contact.getId() != null) {
			this.updateForm();
		}
	}
	
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();;
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(textNumber.validate()) {
			// Create an instance if contact is null
			if(contact == null) {
				contact = new Contact();
			}
			// Get data from UI Form
			contact.setNumber(textNumber.getText());
			contact.setDescription(textDescription.getText());
			// Save in DB
			try {
				ContactDao contactDao = new ContactDao(DBFactory.getConnection());
				// If he doesn't have a id, is a new contact,
				// otherwhise the contact already is in database
				if (contact.getId() == null) {
					person.getContacts().add(contact);
					contactDao.insert(contact);
				} else {
					contactDao.update(contact);
				}
				// Update Screen that opens this form
				if(infoStudent != null) {
					infoStudent.onDataChanged();
					infoStudent.tableContacts.getSelectionModel().select(contact);
					infoStudent.tableContacts.scrollTo(contact);
				}
				Utils.currentStage(event).close();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(),	AlertType.ERROR);
				e.printStackTrace();
			} 
		}
	}
	
	// Update UI with contact infomartions
	public void updateForm() {
		textNumber.setText(contact.getNumber());
		textDescription.setText(contact.getDescription());
	}

}
