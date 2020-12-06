package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.enums.ScheduleDayOfWeek;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import model.entites.Collaborator;

public class UserScheduleController implements Initializable {

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

	private Collaborator collaborator;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		// disable all TextArea's
		disableTextAreas();
	}

	public void setCollaborator(Collaborator collaborator) {
		this.collaborator = collaborator;
		setDataToForm();
	}

	private void disableTextAreas() {
		grid.getChildren().stream().filter(el -> el instanceof TextArea)
				.forEach(el -> ((TextArea) el).setDisable(true));
	}

	private void setDataToForm() {
		if(collaborator.getSchedule() == null) {
			return;
		}
		collaborator.getSchedule().getDays().forEach(d -> {
			if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.MONDAY)) {
				textSegundaManha.setText(d.getMorning());
				textSegundaTarde.setText(d.getAfternoon());
				textSegundaNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.TUESDAY)) {
				textTercaManha.setText(d.getMorning());
				textTercaTarde.setText(d.getAfternoon());
				textTercaNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.WEDNESDAY)) {
				textQuartaManha.setText(d.getMorning());
				textQuartaTarde.setText(d.getAfternoon());
				textQuartaNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.THURSDAY)) {
				textQuintaManha.setText(d.getMorning());
				textQuintaTarde.setText(d.getAfternoon());
				textQuintaNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.FRIDAY)) {
				textSextaManha.setText(d.getMorning());
				textSextaTarde.setText(d.getAfternoon());
				textSextaNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.SATURDAY)) {
				textSabadoManha.setText(d.getMorning());
				textSabadoTarde.setText(d.getAfternoon());
				textSabadoNoite.setText(d.getNight());
			} else if (d.getScheduleDayOfWeek().equals(ScheduleDayOfWeek.SUNDAY)) {
				textDomingoManha.setText(d.getMorning());
				textDomingoTarde.setText(d.getAfternoon());
				textDomingoNoite.setText(d.getNight());
			}
		});
	}
}
