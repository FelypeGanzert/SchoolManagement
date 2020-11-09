package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import gui.util.Roots;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainMenuController implements Initializable {

	@FXML private JFXButton btnListStudents;
	@FXML private JFXButton btnListResponsibles;	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
	}
	
	public void hadleBtnShowListStudents(ActionEvent event) {
		Roots.listStudents();
	}

	public void handleBtnShowListResponsibles(ActionEvent event) {
		Roots.listResponsibles();
	}
	
	public void handleBtnBirthdays(ActionEvent event){
		Roots.birthdays();
	}
	
	public void handleBtnCertificates(ActionEvent event) {
		Roots.certificatesMenu();
	}
	
	public void handleBtnStudentsPresence(ActionEvent event) {
		Roots.studentsPresenceForm();
	}
	
	public void handleBtnParcelPayment(ActionEvent event) {
		Roots.parcelPaymentByDocumentNumber();
	}


}
