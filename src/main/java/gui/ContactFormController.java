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

public class ContactFormController implements Initializable {

	@FXML private JFXTextField textNumber;
	@FXML private JFXTextField textDescription;
	// Buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		Constraints.setTextFieldInteger(textNumber);
		Constraints.setTextFieldMaxLength(textNumber, 11);
		Constraints.setTextFieldMaxLength(textDescription, 30);
		textNumber.setValidators(Validators.getRequiredFieldValidator());		
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
