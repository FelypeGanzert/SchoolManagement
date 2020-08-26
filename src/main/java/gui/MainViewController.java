package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;

public class MainViewController implements Initializable {

	@FXML
	private ScrollPane content;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainMenu.fxml"));
			ScrollPane content = loader.load();
			ScrollPane contentMainScene = this.content; //...lookup("#content");
			contentMainScene.setContent(content.getContent());
			contentMainScene.setStyle(content.getStyle());
			
			contentMainScene.setFitToHeight(true);
			contentMainScene.setFitToWidth(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
