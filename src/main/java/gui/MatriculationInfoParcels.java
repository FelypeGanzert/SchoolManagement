package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.entites.Parcel;

public class MatriculationInfoParcels implements Initializable{
	
	@FXML private TableView<Parcel> tableParcels;
	@FXML private TableColumn<Parcel, String> columnParcelStatus;
	@FXML private TableColumn<Parcel, Integer> columnDocumentNumber;
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
	@FXML private TableColumn<Parcel, Parcel> columnButton;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeTable();
	}

	private void initializeTable() {
		// column of status will show a color according ParcelStatusEnum
		columnParcelStatus.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				Parcel auxParcel = cellData.getValue();
				if (auxParcel.getDateParcel() != null && auxParcel.getDateParcel().before(new Date())
						&& auxParcel.getSituation().equalsIgnoreCase("ABERTA")) {
					situation = "ATRASADA";
				}
				return new SimpleStringProperty(situation);
			} catch (IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("");
			}
		});
		columnParcelStatus.setCellFactory(column -> {
			return new TableCell<Parcel, String>() {
				@Override
				protected void updateItem(String situation, boolean empty) {
					super.updateItem(situation, empty);
					setText("");
					setGraphic(null);
					if (!isEmpty()) {
						this.setStyle("-fx-background-color:" + ParcelStatusEnum.fromString(getItem()).getHexColor());
					}
				}
			};
		});
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
		// date fine delay
		Utils.setCellValueFactory(columnDateFineDelay, "dateFineDelay");
		Utils.formatTableColumnDate(columnDateFineDelay, "dd/MM/yyyy");
		columnDateFineDelay.setReorderable(false);
		// value with fine delay
		Utils.setCellValueFactory(columnValueWithFineDelay, "valueWithFineDelay");
		Utils.formatTableColumnDoubleCurrency(columnValueWithFineDelay);
		columnValueWithFineDelay.setReorderable(false);
		// situation
		columnSituation.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				Parcel auxParcel = cellData.getValue();
				if (auxParcel.getDateParcel() != null && auxParcel.getDateParcel().before(new Date()) && auxParcel.getSituation().equalsIgnoreCase("ABERTA")) {
					situation = "ATRASADA";
				}
				return new SimpleStringProperty(situation);
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("");
			}
		});
		columnSituation.setReorderable(false);
		// date payment
		Utils.setCellValueFactory(columnDatePayment, "datePayment");
		Utils.formatTableColumnDate(columnDatePayment, "dd/MM/yyyy");
		columnDatePayment.setReorderable(false);
		// value paid
		Utils.setCellValueFactory(columnValuePaid, "valuePaid");
		Utils.formatTableColumnDoubleCurrency(columnValuePaid);
		columnValuePaid.setReorderable(false);
		// value paid with
		Utils.setCellValueFactory(columnPaidWith, "paidWith");
		columnPaidWith.setReorderable(false);
		// payment received by
		Utils.setCellValueFactory(columnPaymentReceivedBy, "paymentReceivedBy");
		columnPaymentReceivedBy.setReorderable(false);
		// button
		columnButton.setReorderable(false);
	}

	public void setParcels(List<Parcel> parcels) {
		ObservableList<Parcel> parcelsObs = FXCollections.observableArrayList(parcels);
		tableParcels.setItems(parcelsObs);
		tableParcels.refresh();
	}
}