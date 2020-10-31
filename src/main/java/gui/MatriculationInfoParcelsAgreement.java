package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

import gui.util.DateUtil;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.MatriculationStatusEnum;
import gui.util.enums.ParcelStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import model.entites.Agreement;
import model.entites.AgreementParcel;
import model.entites.Parcel;

public class MatriculationInfoParcelsAgreement implements Initializable{
	
	@FXML private Label labelDate;
	@FXML private Label labelAgreementBy;
	@FXML private Label labelNormalParcels;
	@FXML private TableView<AgreementParcel> tableParcels;
	@FXML private TableColumn<AgreementParcel, String> columnParcelStatus;
	@FXML private TableColumn<AgreementParcel, Integer> columnParcelNumber;
	@FXML private TableColumn<AgreementParcel, Double> columnValue;
	@FXML private TableColumn<AgreementParcel, Date> columnDateParcel;
	@FXML private TableColumn<AgreementParcel, String> columnSituation;
	@FXML private TableColumn<AgreementParcel, Date> columnDatePayment;
	@FXML private TableColumn<AgreementParcel, Double> columnValuePaid;
	@FXML private TableColumn<AgreementParcel, String> columnPaidWith;
	@FXML private TableColumn<AgreementParcel, String> columnPaymentReceivedBy;
	@FXML private TableColumn<AgreementParcel, AgreementParcel> columnButton;
	
	private MatriculationInfoController matriculationInfoController;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		initializeTable();
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	public void handleBtnCancelAgreement(ActionEvent event) {
		// ========== TO DO ========
		System.out.println("Clicked to cancel agreement");
	}

	private void initializeTable() {
		// column of status will show a color according ParcelStatusEnum
		columnParcelStatus.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				AgreementParcel auxParcel = cellData.getValue();
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
			return new TableCell<AgreementParcel, String>() {
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
		// number
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
		// situation
		columnSituation.setCellValueFactory(cellData -> {
			try {
				String situation = cellData.getValue().getSituation();
				AgreementParcel auxParcel = cellData.getValue();
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

	public void setAgreement(Agreement agreement) {
		// agreement info
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		labelDate.setText(sdf.format(agreement.getDateAgreement()));
		labelAgreementBy.setText(agreement.getAgreementBy());
		// normal parcels
		String normalParcels = "|";
		for (Parcel p : agreement.getNormalParcels()) {
			normalParcels += " " + p.getParcelNumber() + " |";
		}
		labelNormalParcels.setText(normalParcels);
		// agreement parcels
		ObservableList<AgreementParcel> parcelsObs = FXCollections.observableArrayList(agreement.getParcels());
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
		Callback<TableColumn<AgreementParcel, AgreementParcel>, TableCell<AgreementParcel, AgreementParcel>> cellFactory =
				new Callback<TableColumn<AgreementParcel, AgreementParcel>, TableCell<AgreementParcel, AgreementParcel>>() {
			@Override
			public TableCell<AgreementParcel, AgreementParcel> call(final TableColumn<AgreementParcel, AgreementParcel> param) {
				final TableCell<AgreementParcel, AgreementParcel> cell = new TableCell<AgreementParcel, AgreementParcel>() {
					@Override
					public void updateItem(AgreementParcel parcel, boolean empty) {
						super.updateItem(parcel, empty);
						Button btn = null;
						AgreementParcel currentParcel = this.getTableRow().getItem(); 
						if (currentParcel != null && 
								currentParcel.getAgreement().getMatriculation().getStatus().equalsIgnoreCase(MatriculationStatusEnum.ABERTA.toString())) {
							// Button to open Parcels (ABERTA)
							if (currentParcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.ABERTA.toString())) {
								btn = Utils.createIconButton(Icons.MONEY_SOLID, Icons.SIZE, "greenIcon");
								btn.setTooltip(new Tooltip("Baixar"));
								btn.setOnAction((ActionEvent event) -> {
									Utils.loadView(this, true, FXMLPath.MATRICULATION_PARCEL_PAYMENT,
											Utils.currentStage(event), "Baixar Pagamento", false,
											(MatriculationParcelPaymentController controller) -> {
												// ============ TO DO =======
												//controller.setParcel(currentParcel);
												//controller.setMatriculationInfoParcels(currentMatriculationInfoParcels);
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