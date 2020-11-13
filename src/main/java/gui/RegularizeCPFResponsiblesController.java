package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Roots;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import model.dao.ResponsibleDao;
import model.entites.Person;
import model.entites.Responsible;
import sharedData.Globe;

public class RegularizeCPFResponsiblesController implements Initializable{
	
	// Filter Students
	@FXML private JFXTextField textFilter;
	@FXML private ToggleGroup filterType;
	@FXML private Label labelTotalResponsiblesSearch;
	// Table Responsibles
	@FXML TableView<Responsible> tableResponsibles;
	@FXML private TableColumn<Responsible, String> columnResponsibleName;
	// person info
	@FXML private VBox VBoxInfos;
	@FXML private TextField textName;
	@FXML private TextField textCPF;
	@FXML private TextField textRG;
	@FXML private TextField textGender;
	@FXML private TextField textBirthDate;
	@FXML private TextField textAge;
	@FXML private TextField textAdress;
	@FXML private TextField textNeighborhood;
	@FXML private TextField textCity;
	@FXML private TextField textUF;
	@FXML private JFXButton btnEdit;
	
	private ResponsibleDao responsibleDao;
	// List to store all students from database
	private ObservableList<Responsible> responsiblesList;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		VBoxInfos.setVisible(false);
		// Try to get studentDao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		responsibleDao = Globe.getGlobe().getItem(ResponsibleDao.class, "responsibleDao");
		if (responsibleDao == null) {
			responsibleDao = new ResponsibleDao(DBFactory.getConnection());
			Globe.getGlobe().putItem("responsibleDao", responsibleDao);
		}
		// Initialize tables
		initializeTableResponsibles();
		// Add listeners to components
		addListeners();
		// Update tableView to show responsibles data
		getResponsiblesFromDB();
		filterResponsibles();
	}

	// =====================
	// === START BUTTONS ===
	// =====================

	// Return button
	public void handleBtnReturn(ActionEvent event) {
		Roots.regularizeCPFMenu();
	}
	
	public void handleBtnEdit(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.REGULARIZE_CPF_PERSON_FORM, Utils.currentStage(event), "Regularizar CPF", false,
				(RegularizeCPFPersonFormController controller) -> {
					controller.setPersonEntity(tableResponsibles.getSelectionModel().getSelectedItem());
					controller.setRegularizeCPFResponsiblesController(this);
				});
	}

	// =====================
	// ==== END BUTTONS ====
	// =====================
	
	// ===== AUXILIAR METHODS
	
	private void getResponsiblesFromDB() {
		if(responsibleDao == null) {
			throw new IllegalStateException("ResponsibleDao service not initialized");
		}
		try {
			responsiblesList = FXCollections.observableArrayList(responsibleDao.findAllWithoutCPF());
		} catch (DbException e) {
			Alerts.showAlert("Erro ao carregar os responsáveis", "DBException", e.getMessage(), AlertType.ERROR, null);
		}
	}

	private void filterResponsibles() {
		// Auxiliar list to doenst interfery where all responsibles are stored
		List<Responsible> filteredList = new ArrayList<>();
		filteredList = responsiblesList;
		// Filter by text in search bar
		String textSearch = textFilter.getText();
		String filterTypeSelected = ((RadioButton) filterType.getSelectedToggle()).getText();
		if (filterTypeSelected.equalsIgnoreCase("inicia com") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(responsible -> responsible.getName() != null
							&& responsible.getName().toUpperCase().startsWith(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		} else if (filterTypeSelected.equalsIgnoreCase("contém") && textSearch.length() > 0) {
			filteredList = filteredList.stream()
					.filter(responsible -> responsible.getName() != null
							&& responsible.getName().toUpperCase().contains(textSearch.toUpperCase()))
					.collect(Collectors.toList());
		}
		if (textSearch.length() > 0) {
			labelTotalResponsiblesSearch.setText("Resultados: " + filteredList.size());
		} else {
			labelTotalResponsiblesSearch.setText(null);
		}
		// convert filteredList to Observable List and set in tableResponsibles
		ObservableList<Responsible> filteredObsList = FXCollections.observableArrayList(filteredList);
		tableResponsibles.setItems(filteredObsList);
		tableResponsibles.refresh();
		tableResponsibles.getSelectionModel().selectFirst();
	}
	
	// ====================================================
	// ======== START OF INITIALIZE METHODS ===============
	// ====================================================
	// TABLE RESPONSIBLES
	private void initializeTableResponsibles() {
		// column of status will show a color according StudentStatusEnum
		Utils.setCellValueFactory(columnResponsibleName, "name");
		columnResponsibleName.setReorderable(false);
	}

	private void addListeners() {
		addListenersToFilterResponsibles();
		addListenersToTableResponsibles();
	}
	
	private void addListenersToFilterResponsibles() {
		// Filter responsibles table when user type anything in search bar
		textFilter.textProperty().addListener((observable, oldValue, newValue) -> {
			filterResponsibles();
		});
		// Filter responsibles table when user select another type of search
		filterType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			filterResponsibles();
		});
	}

	private void addListenersToTableResponsibles() {
		// Listener to selected student of table
		tableResponsibles.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					if (newSelection != null) {
						if(!VBoxInfos.isVisible()) {
							VBoxInfos.setVisible(true);
						}
						setDataToUI(newSelection);
					} else {
						if(VBoxInfos.isVisible()) {
							VBoxInfos.setVisible(false);
						}
					}
				});
	}
	
	private void setDataToUI(Person entity) {
		textName.setText(entity.getName());
		textCPF.setText(entity.getCpf());
		textRG.setText(entity.getRg());
		textGender.setText(entity.getGender());
		// DATES: birthdate, age
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
		} else {
			textBirthDate.setText("");
		}
		textAge.setText(Integer.toString(entity.getAge()) + " anos");
		textAdress.setText(entity.getAdress());
		textNeighborhood.setText(entity.getNeighborhood());
		textCity.setText(entity.getCity());
		textUF.setText(entity.getUf());
	}

	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================

	public void removeResponsible(Responsible responsible) {
		tableResponsibles.getItems().remove(responsible);
		tableResponsibles.refresh();
	}
	

}