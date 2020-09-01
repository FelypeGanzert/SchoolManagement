package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import application.Main;
import db.DBFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import model.dao.StudentDao;

public class MainMenuController implements Initializable {

	@FXML JFXButton btnListStudents;
	@FXML JFXButton btnListResponsibles;
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	public void showListStudents(ActionEvent action) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListStudents.fxml"));
			ScrollPane newContent = loader.load();
			ListStudentsController controller = loader.getController();
			controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
			
			ScrollPane mainContent = (ScrollPane) Main.getMainScene().lookup("#content");
			mainContent.setContent(newContent.getContent());
			mainContent.setStyle(newContent.getStyle());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showListResponsibles(ActionEvent action) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListResponsibles.fxml"));
			ScrollPane newContent = loader.load();
			ScrollPane mainContent = (ScrollPane) Main.getMainScene().lookup("#content");
			mainContent.setContent(newContent.getContent());
			mainContent.setStyle(newContent.getStyle());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
