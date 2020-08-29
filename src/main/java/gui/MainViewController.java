package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;

public class MainViewController implements Initializable {

	@FXML private ScrollPane content;
	@FXML private JFXButton btnHome;
	@FXML private JFXButton btnStudents;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		try {
			// Load the screen and show
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));
			ScrollPane newContent = loader.load();
			this.content.setContent(newContent.getContent());
			this.content.setStyle(newContent.getStyle());
			this.content.setFitToHeight(true);
			this.content.setFitToWidth(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public void handleBtnStudents(ActionEvent action) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ListStudents.fxml"));
			ScrollPane newContent = loader.load();
			this.content.setContent(newContent.getContent());
			this.content.setStyle(newContent.getStyle());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
