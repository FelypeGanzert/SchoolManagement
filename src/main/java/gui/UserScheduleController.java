package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

public class UserScheduleController implements Initializable{
	

    @FXML private TextArea textSegundaManha;
    @FXML private TextArea textSegundaTarde;
    @FXML private TextArea textSegundaNoite;
    @FXML private TextArea textTercaManha;
    @FXML private TextArea textTercaTarde;
    @FXML private TextArea textTercaNoite;
    @FXML private TextArea textQuartaManha;
    @FXML private TextArea textQuartaTarde;
    @FXML private TextArea textQuartaNoite;
    @FXML private TextArea textQuintaManha;
    @FXML private TextArea textQuintaTarde;
    @FXML private TextArea textQuintaNoite;
    @FXML private TextArea textSextaManha;
    @FXML private TextArea textSextaTarde;
    @FXML private TextArea textSextaNoite;
    @FXML private TextArea textSabadoManha;
    @FXML private TextArea textSabadoTarde;
    @FXML private TextArea textSabadoNoite;
    @FXML private TextArea textDomingoManha;
    @FXML private TextArea textDomingoTarde;
    @FXML private TextArea textDomingoNoite;
    @FXML private GridPane grid;
    
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		// disable all TextArea's
		grid.getChildren().stream()
			.filter(el -> el instanceof TextArea)
			.forEach(el -> ((TextArea) el).setDisable(true));
		
	}
    
}
