package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

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
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		
	}

	
}
