package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DbException;
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
import model.entites.Responsible;
import model.entites.Student;
import model.entites.util.PersonUtils;

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
		tableColumnCPF.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(Utils.formatCPF(cellData.getValue().getCpf()));
			} catch (Exception e) {
			}
			return null;
		});
		tableColumnCPF.setReorderable(false);
		Utils.setCellValueFactory(tableColumnName, "name");
		tableColumnName.setReorderable(false);
		Utils.setCellValueFactory(tableColumnBirthDate, "dateBirth");
		Utils.formatTableColumnDate(tableColumnBirthDate, "dd/MM/yyyy");
		tableColumnBirthDate.setReorderable(false);
		tableColumnAge.setCellValueFactory(cellData -> {
			try {
				return new SimpleStringProperty(Integer.toString(cellData.getValue().getAge()));
			} catch (Exception e) {
			}
			return null;
		});
		tableColumnAge.setReorderable(false);
		tableViewPersons.getSelectionModel().selectedItemProperty().addListener(
				(observable, oldSelection, newSelection) -> {
					if (newSelection != null) {
						btnUpdate.setDisable(false);
						btnUpdate.setText("Atualizar [" + newSelection.getName() + "]");
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
		// get the person selected
		Person personSelected = tableViewPersons.getSelectionModel().getSelectedItem();
		// Check the instance of entity in personForm
		Person entity = this.personFormController.getEntity();
		Person searchByCPF = null;
		Person personUpdate = null;
		if(entity instanceof Student) {
			personUpdate = new Student();
			try {
				searchByCPF = personFormController.getStudentDao().findByCPF(personSelected.getCpf());
			} catch (DbException e) {
				e.printStackTrace();
			}
		} else if(entity instanceof Responsible) {
			personUpdate = new Responsible();
			try {
				searchByCPF = personFormController.getResponsibleDao().findByCPF(personSelected.getCpf());
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(searchByCPF != null) {
			personSelected = searchByCPF;
		}
		if(entity instanceof Student && personSelected instanceof Responsible) {
			PersonUtils.parseDataFromResponsibleToStudent((Responsible) personSelected, (Student) personUpdate);
			personFormController.setisEntityFromAnotherTable(true);
		} else if(entity instanceof Responsible && personSelected instanceof Student) {
			PersonUtils.parseDataFromStudentToResponsible((Student) personSelected, (Responsible) personUpdate);
			personFormController.setisEntityFromAnotherTable(true);
		} else {
			personUpdate = personSelected;
		}
		personFormController.setPersonEntity(personUpdate);
		Utils.currentStage(event).close();
	}
	
	public void handleBtnNew(ActionEvent event) {
		personFormController.addNewRegistry();
		Utils.currentStage(event).close();
	}
		
}