package gui;

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import db.DbExceptioneEntityExcluded;
import gui.util.Alerts;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import model.dao.ParcelDao;
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
						if (currentParcel != null && 
								currentParcel.getMatriculation().getStatus().equalsIgnoreCase(MatriculationStatusEnum.ABERTA.toString())) {
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
							// Button to paid Parcels (PAGA)
							if (currentParcel.getSituation().equalsIgnoreCase(ParcelStatusEnum.PAGA.toString())) {
								btn = Utils.createIconButton(Icons.REDO_SOLID, Icons.SIZE, "redIcon");
								btn.setTooltip(new Tooltip("Anular"));
								btn.setOnAction((ActionEvent event) -> {
									// Confirmation to cancel payment
									Alert alert = new Alert(AlertType.CONFIRMATION);
									alert.setTitle("Anular pagamento");
									alert.setHeaderText("Desfazer o pagamento da parcela " + currentParcel.getParcelNumber() + " ? ");
									alert.setContentText("O status da parccela irá voltar para ABERTA e os dados do pagamento serão excluídos");
									alert.initOwner(Utils.currentStage(event));
									Optional<ButtonType> result =alert.showAndWait();
									if (result.isPresent() && result.get() == ButtonType.OK) {
										Alert alertProcessing = Alerts.showProcessingScreen(Utils.currentStage(event));
										try {
											// refresh data
											DBUtil.refreshData(currentParcel);
											// reset payment informations
											currentParcel.setSituation(ParcelStatusEnum.ABERTA.toString());
											currentParcel.setDatePayment(null);
											currentParcel.setValuePaid(null);
											currentParcel.setPaidWith(null);
											currentParcel.setPaymentReceivedBy(null);
											// update parcel in db
											ParcelDao parcelDao = new ParcelDao(DBFactory.getConnection());
											parcelDao.update(currentParcel);
											// Update info parcels screen
											currentMatriculationInfoParcels.onDataChanged();
										} catch (DbException e) {
											Alerts.showAlert("Erro ao deletar cancelar pagamento", "DbException", e.getMessage(),
													AlertType.ERROR, Utils.currentStage(event));
										} catch (DbExceptioneEntityExcluded e) {
											e.printStackTrace();
										}
										alertProcessing.close();
									}
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