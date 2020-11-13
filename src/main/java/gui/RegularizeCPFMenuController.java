package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Roots;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class RegularizeCPFMenuController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	public void handleBtnStudents(ActionEvent event) {
		Roots.regularizeCPFStudents();
	}
	
	public void handleBtnResponsibles(ActionEvent event) {
		Roots.regularizeCPFResponsibles();
	}
	
}
