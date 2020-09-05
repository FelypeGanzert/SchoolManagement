package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import application.Main;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import model.entites.Collaborator;

public class MainViewController implements Initializable {

	@FXML private ScrollPane content;
	@FXML private JFXButton btnHome;
	@FXML private JFXButton btnStudents;
	@FXML private Label labelCurrentUser;
	@FXML private JFXButton btnChangeUser;
	private Main main;
	private MainMenuController mainMenuController;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		try {
			// Load the menu screen and show
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));
			ScrollPane newContent = loader.load();
			this.mainMenuController = loader.getController();
			this.content.setContent(newContent.getContent());
			this.content.setStyle(newContent.getStyle());
			this.content.setFitToHeight(true);
			this.content.setFitToWidth(true);
			this.labelCurrentUser.setText(Main.getCurrentUser().getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void setCurrentUser(Collaborator collaborator) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleBtnHome(ActionEvent action) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));
			ScrollPane newContent = loader.load();
			this.content.setContent(newContent.getContent());
			this.content.setStyle(newContent.getStyle());	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleChangeUser(ActionEvent action) {
		main.showLoginForm();
		Utils.currentStage(action).close();
		
	}
	public void handleBtnStudents(ActionEvent action) {
		this.mainMenuController.showListStudents(action);
	}

}
