package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import gui.util.DateUtil;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
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
	
	private MatriculationInfoController matriculationInfoController;
	private MatriculationInfoParcels currentMatriculationInfoParcels;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		currentMatriculationInfoParcels = this;
		initializeTable();
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}

	private void initializeTable() {
		// column of status will show a color according ParcelStatusEnum
		columnParcelStatus.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				Parcel auxParcel = cellData.getValue();
				// check if date parcel is before today
				if (auxParcel.getDateParcel() != null && auxParcel.getSituation().equalsIgnoreCase("ABERTA")) {
					if(DateUtil.compareTwoDates(auxParcel.getDateParcel(), new Date()) < 0) {
						situation = "ATRASADA";
					}
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
				// check if date parcel is before today
				if (auxParcel.getDateParcel() != null && auxParcel.getSituation().equalsIgnoreCase("ABERTA")) {
					if(DateUtil.compareTwoDates(auxParcel.getDateParcel(), new Date()) < 0) {
						situation = "ATRASADA";
					}
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
		initButtons();
	}

	public void setParcels(List<Parcel> parcels) {
		ObservableList<Parcel> parcelsObs = FXCollections.observableArrayList(parcels);
		tableParcels.setItems(parcelsObs);
		tableParcels.refresh();
	}
	
	public void onDataChanged() {
		if(matriculationInfoController != null) {
			matriculationInfoController.onDataChanged();
		} else {
			tableParcels.refresh();
		}
	}
	
	private void initButtons() {
		final int COLUMN_ICON_SPACE = 20;
		columnButton.setMinWidth(Icons.SIZE + COLUMN_ICON_SPACE);
		Callback<TableColumn<Parcel, Parcel>, TableCell<Parcel, Parcel>> cellFactory = new Callback<TableColumn<Parcel, Parcel>, TableCell<Parcel, Parcel>>() {
			@Override
			public TableCell<Parcel, Parcel> call(final TableColumn<Parcel, Parcel> param) {
				final TableCell<Parcel, Parcel> cell = new TableCell<Parcel, Parcel>() {
					@Override
					public void updateItem(Parcel parcel, boolean empty) {
						super.updateItem(parcel, empty);
						Button btn = null;
						Parcel currentParcel = this.getTableRow().getItem(); 
						if (currentParcel != null) {
							// Button to open Parcels (ABERTA)
							if (currentParcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.ABERTA.toString())) {
								btn = Utils.createIconButton(Icons.MONEY_SOLID, Icons.SIZE, "greenIcon");
								btn.setTooltip(new Tooltip("Baixar"));
								btn.setOnAction((ActionEvent event) -> {
									Utils.loadView(this, true, FXMLPath.MATRICULATION_PARCEL_PAYMENT,
											Utils.currentStage(event), "Baixar Pagamento", false,
											(MatriculationParcelPaymentController controller) -> {
												controller.setParcel(currentParcel);
												controller.setMatriculationInfoParcels(currentMatriculationInfoParcels);
											});
								});
							}
						}
						if (empty || btn == null) {
							setGraphic(null);
						} else {
							setGraphic(btn);
						}
					}
				};
				return cell;
			}
		};
		columnButton.setCellFactory(cellFactory);
	}
}