package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.entites.Parcel;

public class MatriculationInfoParcels implements Initializable{
	
	@FXML private TableView<Parcel> tableParcels;
	@FXML private TableColumn<Parcel, Integer> columnCode;
	@FXML private TableColumn<Parcel, Integer> columnParcelNumber;
	@FXML private TableColumn<Parcel, Double> columnValue;
	@FXML private TableColumn<Parcel, Date> columnDateParcel;
	@FXML private TableColumn<Parcel, Date> columnDateFineDelay;
	@FXML private TableColumn<Parcel, Double> columnValueWithFineDelay;
	@FXML private TableColumn<Parcel, String> columnSituation;
	@FXML private TableColumn<Parcel, Date> columnDatePayment;
	@FXML private TableColumn<Parcel, Double> columnValuePaid;
	@FXML private TableColumn<Parcel, String> columnPaidWith;
	@FXML private TableColumn<Parcel, String> columnPaymentReceivedBy;
	@FXML private TableColumn<Parcel, Parcel> columnButtons;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeTable();
	}

	private void initializeTable() {
		// code
		Utils.setCellValueFactory(columnCode, "documentNumber");
		// parcel number
		Utils.setCellValueFactory(columnParcelNumber, "parcelNumber");
		// value
		Utils.setCellValueFactory(columnValue, "value");
		Utils.formatTableColumnDoubleCurrency(columnValue);
		// date parcel
		Utils.setCellValueFactory(columnDateParcel, "dateParcel");
		Utils.formatTableColumnDate(columnDateParcel, "dd/MM/yyyy");
		// date fine delay
		Utils.setCellValueFactory(columnDateFineDelay, "dateFineDelay");
		Utils.formatTableColumnDate(columnDateFineDelay, "dd/MM/yyyy");
		// value with fine delay
		Utils.setCellValueFactory(columnValueWithFineDelay, "valueWithFineDelay");
		Utils.formatTableColumnDoubleCurrency(columnValueWithFineDelay);
		// situation
		Utils.setCellValueFactory(columnSituation, "situation");
		// date payment
		Utils.setCellValueFactory(columnDatePayment, "datePayment");
		Utils.formatTableColumnDate(columnDatePayment, "dd/MM/yyyy");
		// value paid
		Utils.setCellValueFactory(columnValuePaid, "valuePaid");
		Utils.formatTableColumnDoubleCurrency(columnValuePaid);
		// value paid with
		Utils.setCellValueFactory(columnPaidWith, "paidWith");
		// payment received by
		Utils.setCellValueFactory(columnPaymentReceivedBy, "paymentReceivedBy");
	}

	public void setParcels(List<Parcel> parcels) {
		ObservableList<Parcel> parcelsObs = FXCollections.observableArrayList(parcels);
		tableParcels.setItems(parcelsObs);
		
	}
}