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
		// TODO Auto-generated method stub
	}
	
	public void hadleBtnShowListStudents(ActionEvent event) {
		Roots.listStudents();
	}

	public void handleBtnShowListResponsibles(ActionEvent event) {
		Roots.listResponsibles();
	}

}
