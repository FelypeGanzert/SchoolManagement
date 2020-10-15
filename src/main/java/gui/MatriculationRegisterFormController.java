package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class MatriculationRegisterFormController implements Initializable{

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
	}
	
	public void handleBtnSave(ActionEvent event) {
		System.out.println("Save button clicked");
	}
	
	public void handleBtnCancel(ActionEvent event) {
		System.out.println("Cancel button clicked");
	}

}
