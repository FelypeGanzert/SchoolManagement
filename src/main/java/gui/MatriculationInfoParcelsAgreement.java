package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.util.Callback;
import model.dao.MatriculationDao;
import model.entites.Agreement;
import model.entites.AgreementParcel;
import model.entites.Parcel;

public class MatriculationInfoParcelsAgreement implements Initializable{
	
	@FXML private JFXButton btnCancelAgreement;
	@FXML private JFXButton btnPrint;
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
	
	private Agreement agreement;
	private MatriculationInfoController matriculationInfoController;
	private MatriculationInfoParcelsAgreement currentMatriculationInfoParcelsAgreement;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		currentMatriculationInfoParcelsAgreement = this;
		initializeTable();
	}
	
	public void setMatriculationInfoController(MatriculationInfoController matriculationInfoController) {
		this.matriculationInfoController = matriculationInfoController;
	}
	
	public void handleBtnCancelAgreement(ActionEvent event) {
		// Confirmation to cancel agreement
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Anular acordo");
		alert.setHeaderText("Tem certeza que deseja anular o acordo #" + agreement.getCode() + " ? ");
		alert.setContentText("Todas as parcelas desse acordo que não estiverem com o status igual a " +
				"PAGA serão desvinculadas desse acordo.");
		alert.initOwner(Utils.currentStage(event));
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			agreement.setCanceled(true);
			// unlinks agreement from normal parel that are not paid
			for(Parcel p : agreement.getNormalParcels()){
				if(!p.getSituation().equalsIgnoreCase(ParcelStatusEnum.PAGA.toString())) {
					p.setSituation(ParcelStatusEnum.ABERTA.toString());
					p.setAgreement(null);
				}
			}
			// set situation to all open parcels (from agreement) to canceled
			for(AgreementParcel p : agreement.getParcels()) {
				if(p.getSituation().equals(ParcelStatusEnum.ABERTA.toString())) {
					p.setSituation(ParcelStatusEnum.CANCELADA.toString());					
				}
			}
			// Updated matriculation in DB
			try {
				MatriculationDao matriculationDao = new MatriculationDao(DBFactory.getConnection());
				// Update matriculation with the agreement created
				if (agreement.getMatriculation().getCode() != null) {
					matriculationDao.update(agreement.getMatriculation());
				}
				this.onDataChanged();
			} catch (DbException e) {
				Alerts.showAlert("Erro de conexão com o banco de dados", "DBException", e.getMessage(), AlertType.ERROR,
						Utils.currentStage(event));
				e.printStackTrace();
			}
		}
	}
	
	public void handleBtnPrint(ActionEvent event) {
		// ========== TO DO ========
		System.out.println("Clicked to print agreement contract");
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
		this.agreement = agreement;
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
		// disable buttons if agreement is canceled
		if(agreement.isCanceled()) {
			btnCancelAgreement.setDisable(true);
			btnPrint.setDisable(true);
		} else  {
			btnCancelAgreement.setDisable(false);
			btnPrint.setDisable(false);
		}
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
									Utils.loadView(this, true, FXMLPath.MATRICULATION_AGREEMENT_PARCEL_PAYMENT,
											Utils.currentStage(event), "Baixar Pagamento", false,
											(MatriculationAgreementParcelPaymentController controller) -> {
												controller.setParcel(currentParcel);
												controller.setMatriculationInfoParcelsAgreement(currentMatriculationInfoParcelsAgreement);
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