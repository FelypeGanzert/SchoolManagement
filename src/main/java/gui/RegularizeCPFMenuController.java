package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class RegularizeCPFMenuController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	public void handleBtnStudents(ActionEvent event) {
		System.out.println("=== students");
	}
	
	public void handleBtnResponsibles(ActionEvent event) {
		System.out.println("===== responsibles");
	}
	
}
