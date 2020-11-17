package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Roots;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class ParcelsOverdueMenuController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	public void handleBtnModel1(ActionEvent event) {
		Roots.parcelsOverdueModel1();
	}
	
	public void handleBtnModel2(ActionEvent event) {
		Roots.parcelsOverdueModel2();
	}

}
