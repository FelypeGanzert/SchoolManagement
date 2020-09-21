package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import gui.util.Utils;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.entites.Person;

public class PersonFormFindRegistryController implements Initializable {

	@FXML private TableView<Person> tableViewPersons;
	@FXML private TableColumn<Person, String> tableColumnCPF;
	@FXML private TableColumn<Person, String> tableColumnName;
	@FXML private TableColumn<Person, Date> tableColumnBirthDate;
	@FXML private TableColumn<Person, String> tableColumnAge;
	@FXML private JFXButton btnUpdate;
	@FXML private JFXButton btnNewRegistry;
	
	private ObservableList<Person> peopleList;
	private PersonFormController personFormController;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeTableStudentsNodes();
	
	}
	
	private void initializeTableStudentsNodes() {
		btnUpdate.setDisable(true);
		Utils.setCellValueFactory(tableColumnCPF, "cpf");
		Utils.setCellValueFactory(tableColumnName, "name");
		Utils.setCellValueFactory(tableColumnBirthDate, "dateBirth");
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnAge.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(Integer.toString(cellData.getValue().getAge()));
			} catch (Exception e) {
			}
			return null;
		});
		tableViewPersons.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldSelection, newSelection) -> {
					if (newSelection != null) {
						btnUpdate.setDisable(false);
					} else {
						btnUpdate.setDisable(true);
					}
				});
	}
	
	public void setPeopleList(List<Person> peopleList) {
		this.peopleList = FXCollections.observableArrayList(peopleList);
		this.tableViewPersons.setItems(this.peopleList);
	}
	
	public void setPersonFormController(PersonFormController personFormController) {
		this.personFormController = personFormController;
	}
	
	public void handleBtnUpdate(ActionEvent event) {
		if(personFormController == null) {
			throw new IllegalStateException("PersonFormController is null");
		}
		Person personSelected = tableViewPersons.getSelectionModel().getSelectedItem();
		personFormController.setPersonEntity(personSelected);
		Utils.currentStage(event).close();
	}
	
	public void handleBtnNew(ActionEvent event) {
		personFormController.addNewRegistry();
		Utils.currentStage(event).close();
	}
	
	
}
