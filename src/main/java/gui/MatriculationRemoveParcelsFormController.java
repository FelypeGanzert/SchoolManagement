package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import model.dao.MatriculationDao;
import model.entites.Matriculation;
import model.entites.Parcel;

public class MatriculationRemoveParcelsFormController implements Initializable{

	@FXML private TableView<Parcel> tableParcels;
	@FXML private TableColumn<Parcel, Boolean> columnSelected;
	@FXML private TableColumn<Parcel, Integer> columnDocumentNumber;
	@FXML private TableColumn<Parcel, Integer> columnParcelNumber;
	@FXML private TableColumn<Parcel, Double> columnValue;
	@FXML private TableColumn<Parcel, Date> columnDateParcel;
	// Bottom buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Matriculation matriculation;
	private MatriculationInfoController matriculationInfoController; 
	private ObservableList<Parcel> openParcels;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Initialize table
		initializeTable();
		// Set listeners
		setListeners();
	}
	
	private void initializeTable() {
		columnSelected.setCellValueFactory(cellData -> cellData.getValue().getSelected());
		columnSelected.setCellFactory(CheckBoxTableCell.forTableColumn(columnSelected));
		columnSelected.setReorderable(false);
		// document number
		Utils.setCellValueFactory(columnDocumentNumber, "documentNumber");
		columnDocumentNumber.setReorderable(false);
		// parcel number
		Utils.setCellValueFactory(columnParcelNumber, "parcelNumber");
		columnParcelNumber.setReorderable(false);
		// value
		Utils.setCellValueFactory(columnValue, "value");
		Utils.formatTableColumnDoubleCurrency(columnValue);
		columnValue.setReorderable(false);
		// date parcel
		Utils.setCellValueFactory(columnDateParcel, "dateParcel");
		Utils.formatTableColumnDate(columnDateParcel, "dd/MM/yyyy");
		columnDateParcel.setReorderable(false);
	}
	
	// =========================
	// ====== DEPENDENCES ======
	// =========================
	
	public void setMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
		// get all parcels from matriculation
		List<Parcel> parcels = matriculation.getParcels();
		// filter to get parcels with situation = ABERTA
		parcels = parcels.stream().filter(p -> p.getSituation().equals(ParcelStatusEnum.ABERTA.toString()))
				.collect(Collectors.toList());
		// select all parcels filtered
		parcels.forEach(p -> p.setSelected(true));
		// put in the table
		this.openParcels = FXCollections.observableArrayList(parcels);
		tableParcels.setItems(this.openParcels);
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	// ==========================
	// ==== START BUTTONS =======
	// ==========================

	public void handleBtnSave(ActionEvent event) {
		// check if there is any selected parcel, this cant happen
		List<Parcel> selectedParcels = openParcels.stream().filter(p -> p.isSelected()).collect(Collectors.toList());
		if(selectedParcels.size() <= 0) {
			Alerts.showAlert("Nada para remover", "Nada selecionado.",
					"Não foi selecionado nenhuma parcela para remover.", AlertType.ERROR, Utils.currentStage(event));
			// stop the method
			return;
		}
		// remove selected parcels from matriculation
		// .... here we have a particular situation, imagine this:
		// The user has the possibility to delete parcels with the numbers: 5, 6, 7, 8
		// if he choose to delete 5 and 6, we will remain with parcels with this numbers:
		// 1, 2, 3, 4, 7, 8 - and this sequence is very bad.
		// To solve this, first we will get the number of the first parcel to delete and then
		// set into the next parcel number, them we remove the parcel and repeat this until the end.
		// With this, thinking in the above situation the remaining parcels if user delete
		// parcels with number 5 and 6 will be: 1, 2, 3, 4, 5, 6, where:
		// 7 become 5 and 8 become 6
		// == remove selected parcels from matriculation
		int firstParcelRemovedIndexInMatriculation = matriculation.getParcels().indexOf(selectedParcels.get(0));
		for(int i = 0; i < openParcels.size(); i++) {
			if(openParcels.get(i).isSelected()) {
				matriculation.getParcels().remove(openParcels.get(i));
			}
		}
		// update parcel numbers using the first parcel selected has parameter
		Integer firstParcelRemovedNumber = selectedParcels.get(0).getParcelNumber();
		// check if the number is 0, if is zero we will desconsider
		if(firstParcelRemovedNumber == 0) {
			// check if there other removed parcel
			if(selectedParcels.size() > 1) {
				firstParcelRemovedNumber = selectedParcels.get(1).getParcelNumber();
			} else {
				firstParcelRemovedNumber = null;
			}
		}
		for(int i = firstParcelRemovedIndexInMatriculation; i < matriculation.getParcels().size(); i++) {
			if(firstParcelRemovedNumber != null) {
				 matriculation.getParcels().get(i).setParcelNumber(firstParcelRemovedNumber);
				 firstParcelRemovedNumber++;
			}
		}
		// Updated matriculation in DB
		try {
			MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
			// Update matriculation with the removed parcels
			if (matriculation.getCode() != null) {
				matriculationDao.update(matriculation);
			}
			// Update screen of matriculation info and close this form
			if (matriculationInfoController != null) {
				matriculationInfoController.onDataChanged();
			}
			Utils.currentStage(event).close();
		} catch (DbException e) {
			Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(), AlertType.ERROR,
					Utils.currentStage(event));
			e.printStackTrace();
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	public void handleBtnSelectAll(ActionEvent event) {
		openParcels.forEach(p -> p.setSelected(true));
	}
	
	public void handleBtnUnselectAll(ActionEvent event) {
		openParcels.forEach(p -> p.setSelected(false));
	}

	// ==========================
	// ===== END BUTTONS ========
	// ==========================

	// ==========================
	// === INITIALIZE METHODS  ==
	// ==========================
	
	private void setListeners() {
		// Listener to click on a row to change the selection of that parcel
		tableParcels.setRowFactory( tv -> {
		    TableRow<Parcel> row = new TableRow<>();
		    row.setOnMouseClicked(event -> {
		        if (! row.isEmpty()) {
		        	// get tha data in the row and change selection
		        	Parcel rowData = row.getItem();
		            rowData.setSelected(!rowData.isSelected());
		        }
		    });
		    return row ;
		});
	}
		
}