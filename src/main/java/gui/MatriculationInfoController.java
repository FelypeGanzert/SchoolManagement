package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class MatriculationInfoController implements Initializable{

	@FXML private JFXButton btnReturn;
	@FXML private TextField textStatus;
	@FXML private Button btnEditStatus;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}
	
	public void handleBtnReturn(ActionEvent event) {
		System.out.println("return button clicked");
	}

}
