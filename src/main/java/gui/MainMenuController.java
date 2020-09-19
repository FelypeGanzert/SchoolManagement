package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MainMenuController implements Initializable {

	@FXML private JFXButton btnListStudents;
	@FXML private JFXButton btnListResponsibles;
	
	private MainViewController mainView;	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	public void setMainViewController(MainViewController mainView) {
		this.mainView = mainView;
	}
	
	public void showListStudents(ActionEvent event) {
		Roots.listStudents(mainView);
	}

	public void showListResponsibles(ActionEvent event) {
		Roots.listResponsibles(mainView);
	}

}
