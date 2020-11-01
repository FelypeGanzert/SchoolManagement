package gui;

import java.net.URL;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.persistence.TypedQuery;

import com.jfoenix.controls.JFXComboBox;

import db.DBFactory;
import gui.util.DateUtil;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import model.entites.dto.StudentBirthday;

public class BirthdaysController implements Initializable {

	@FXML private JFXComboBox<String> comboBoxMonth;
    @FXML private ToggleGroup studentStatus;
    @FXML private ToggleGroup studentGender;
    @FXML private Label labelTotalResults;
    @FXML private TableView<StudentBirthday> tableBirthdays;
    @FXML private TableColumn<StudentBirthday, Date> columnDay;
    @FXML private TableColumn<StudentBirthday, String> columnGender;
    @FXML private TableColumn<StudentBirthday, String> columnName;
	
	private final Map<String, Integer> monthsArray = new LinkedHashMap<>();
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeComboBoxMonths();
		initializeTableBirthdays();
	}
	
	public void initializeComboBoxMonths() {
		monthsArray.put("Janeiro", 1);
		monthsArray.put("Fevereiro", 2);
		monthsArray.put("Março", 3);
		monthsArray.put("Abril", 4);
		monthsArray.put("Maio", 5);
		monthsArray.put("Junho", 6);
		monthsArray.put("Julho", 7);
		monthsArray.put("Agosto", 8);
		monthsArray.put("Setembro", 9);
		monthsArray.put("Outubro", 10);
		monthsArray.put("Novembro", 11);
		monthsArray.put("Dezembro", 12);
		comboBoxMonth.getItems().addAll(monthsArray.keySet());
		comboBoxMonth.getSelectionModel().selectFirst();
	}
	
	public void initializeTableBirthdays() {
		// day of birth
		Utils.setCellValueFactory(columnDay, "dateBirth");
		columnDay.setCellFactory(column -> {
			TableCell<StudentBirthday, Date> cell = new TableCell<StudentBirthday, Date>() {
				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
					} else {
						Calendar c = DateUtil.dateToCalendar(item);
						setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
					}
				}
			};
			return cell;
		});
		columnDay.setReorderable(false);
		// gender
		Utils.setCellValueFactory(columnGender, "gender");
		columnGender.setReorderable(false);
		// name
		Utils.setCellValueFactory(columnName, "name");
		columnName.setReorderable(false);
	}
	
	public void handleBtnFind(ActionEvent event) {
		String monthSelected = comboBoxMonth.getSelectionModel().getSelectedItem();
		int monthSelectedIndex = monthsArray.get(monthSelected);
		// get status selected
		String statusSelected = ((RadioButton) studentStatus.getSelectedToggle()).getText();
		Map<String, String> statusMap = new HashMap<>();
		statusMap.put("TODOS", null);
		statusMap.put("ATIVOS", "ATIVO");
		statusMap.put("AGUARDANDO", "AGUARDANDO");
		statusMap.put("INATIVOS", "INATIVO");
		// I use this to correspond with status stored in database
		statusSelected = statusMap.get(statusSelected.toUpperCase());
		// get gender selected
		String genderSelected = ((RadioButton) studentGender.getSelectedToggle()).getText();
		if(genderSelected.equalsIgnoreCase("TODOS")) {
			genderSelected = null;
		}
		// get birthdays from database
		String filterText = "excluido is null AND MONTH(data_nascimento) = " + monthSelectedIndex;
		if(statusSelected != null) {
			filterText += " AND situacao = '" + statusSelected + "'";
		}
		if(genderSelected != null) {
			filterText += " AND sexo = '" + genderSelected + "'";
		}
		TypedQuery<StudentBirthday> q = DBFactory.getConnection()
				.createQuery("SELECT new model.entites.dto.StudentBirthday(a.id, a.name, a.dateBirth, a.gender) FROM Aluno a "
						+ "WHERE " + filterText,
						StudentBirthday.class);
		List<StudentBirthday> students = q.getResultList();
		// sort students by: day, gender and finally by name
		Collections.sort(students);
		tableBirthdays.setItems(FXCollections.observableArrayList(students));
		// Show number of results:
		labelTotalResults.setText("Aniversariantes de " + monthSelected + ": " + students.size());
	}
	
	public void handleBtnPrint(ActionEvent event) {
		System.out.println("Clicked to print");
	}

}