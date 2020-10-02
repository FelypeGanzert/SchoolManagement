package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import gui.util.Constraints;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	// Screens that can call this form
	private PersonFormController personForm;
	private InfoStudentController infoStudent;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// Constraints and validators
		Constraints.setTextFieldInteger(textNumber);
		Constraints.setTextFieldMaxLength(textNumber, 15);
		Constraints.setTextFieldMaxLength(textDescription, 30);
		textNumber.setValidators(Validators.getRequiredFieldValidator());
		// Set default text to number
		textNumber.setText(DEFAULT_NUMBER);
		Constraints.positionCaret(textNumber);
	}
	
	// DEPENDENCES
	public void setDependences(Contact contact, Person person, PersonFormController personForm, InfoStudentController infoStudent) {
		this.contact = contact;
		this.person = person;
		this.personForm = personForm;
		this.infoStudent = infoStudent;
	}
	
	// Called when user will add a new contact to a person in form
	public void setDependences(Person person, PersonFormController personForm) {
		setDependences(null, person, personForm, null);
	}

	// Called when user will add a new contact to a person in info screen
	public void setDependences(Person person, PersonFormController personForm) {
		setDependences(null, person, personForm, null);
	}
	
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event);
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(!textNumber.validate()) {
			return;
		}
		System.out.println("Saved contact " + textNumber.getText() + " - " + textDescription.getText());
	}

}
