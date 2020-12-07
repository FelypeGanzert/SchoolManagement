package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.enums.ScheduleDayOfWeek;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import model.entites.Collaborator;
import model.entites.CollaboratorSchedule;
import model.entites.ScheduleDay;

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

	public void disableTextAreas() {
		grid.getChildren().stream().filter(el -> el instanceof TextArea)
				.forEach(el -> ((TextArea) el).setDisable(true));
	}
	
	public void abilityTextAreas() {
		grid.getChildren().stream().filter(el -> el instanceof TextArea)
				.forEach(el -> ((TextArea) el).setDisable(false));
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

	public void dataToCollaborator() {
		CollaboratorSchedule schedule = new CollaboratorSchedule();
		// MONDAY
		ScheduleDay day0 = new ScheduleDay();
		day0.setScheduleDayOfWeek(ScheduleDayOfWeek.MONDAY);
		day0.setMorning(textSegundaManha.getText());
		day0.setAfternoon(textSegundaTarde.getText());
		day0.setNight(textSegundaNoite.getText());
		schedule.getDays().add(day0);
		// TUESDAY
		ScheduleDay day1 = new ScheduleDay();
		day1.setScheduleDayOfWeek(ScheduleDayOfWeek.TUESDAY);
		day1.setMorning(textTercaManha.getText());
		day1.setAfternoon(textTercaTarde.getText());
		day1.setNight(textTercaNoite.getText());
		schedule.getDays().add(day1);
		// WEDNESDAY
		ScheduleDay day2 = new ScheduleDay();
		day2.setScheduleDayOfWeek(ScheduleDayOfWeek.WEDNESDAY);
		day2.setMorning(textQuartaManha.getText());
		day2.setAfternoon(textQuartaTarde.getText());
		day2.setNight(textQuartaNoite.getText());
		schedule.getDays().add(day2);
		// THURSDAY
		ScheduleDay day3 = new ScheduleDay();
		day3.setScheduleDayOfWeek(ScheduleDayOfWeek.THURSDAY);
		day3.setMorning(textQuintaManha.getText());
		day3.setAfternoon(textQuintaTarde.getText());
		day3.setNight(textQuintaNoite.getText());
		schedule.getDays().add(day3);
		// FRIDAY
		ScheduleDay day4 = new ScheduleDay();
		day4.setScheduleDayOfWeek(ScheduleDayOfWeek.FRIDAY);
		day4.setMorning(textSextaManha.getText());
		day4.setAfternoon(textSextaTarde.getText());
		day4.setNight(textSextaNoite.getText());
		schedule.getDays().add(day4);
		// SATURDAY
		ScheduleDay day5 = new ScheduleDay();
		day5.setScheduleDayOfWeek(ScheduleDayOfWeek.SATURDAY);
		day5.setMorning(textSabadoManha.getText());
		day5.setAfternoon(textSabadoTarde.getText());
		day5.setNight(textSabadoNoite.getText());
		schedule.getDays().add(day5);
		// SUNDAY
		ScheduleDay day6 = new ScheduleDay();
		day6.setScheduleDayOfWeek(ScheduleDayOfWeek.SUNDAY);
		day6.setMorning(textDomingoManha.getText());
		day6.setAfternoon(textDomingoTarde.getText());
		day6.setNight(textDomingoNoite.getText());
		schedule.getDays().add(day6);
		
		collaborator.setSchedule(schedule);
	}
}
