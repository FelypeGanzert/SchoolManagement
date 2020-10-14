package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;

import db.DBUtil;
import gui.util.FXMLPath;
import gui.util.Utils;
import gui.util.enums.MatriculationStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.entites.Matriculation;
import model.entites.Parcel;

public class MatriculationInfoController implements Initializable{

	@FXML private JFXButton btnReturn;
	@FXML private TextField textStatus;
	@FXML private HBox hBoxStatus;
	@FXML private Button btnEditStatus;
	// Matriculation main infos
	@FXML private TextField txtCode;
	@FXML private TextField txtDateMatriculation;
	@FXML private TextField txtMatriculatedBy;
	@FXML private TextField txtParcelsSituation;
	@FXML private TextField txtReason;
	@FXML private JFXTextArea txtServiceContracted;
	// Tab's
	@FXML private TabPane tabPanePeople;
	@FXML private TabPane tabPaneParcels;
	
	private Matriculation matriculation;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	// === DEPENDENCES ===
	public void setCurrentMatriculation(Matriculation matriculation) {
		this.matriculation = matriculation;
		updateForm();
		// Add a tab to student infos
		DBUtil.refleshData(matriculation.getStudent());
		Utils.addTab(this, FXMLPath.MATRICULATION_INFO_PERSON, "Aluno", tabPanePeople, 
				(MatriculationInfoPerson controller) -> {
			controller.setPerson(matriculation.getStudent());
		});
		// Add a tab to matriculation responsible if exists
		if(matriculation.getResponsible() != null) {
			DBUtil.refleshData(matriculation.getResponsible());
			Utils.addTab(this, FXMLPath.MATRICULATION_INFO_PERSON, "Responsável", tabPanePeople, 
					(MatriculationInfoPerson controller) -> {
				controller.setPerson(matriculation.getResponsible());
			});
		}
		if(matriculation.getParcels() != null) {
			//DBUtil.refleshData(matriculation.getParcels());
			Utils.addTab(this, FXMLPath.MATRICULATION_INFO_PARCELS, "Parcelas", tabPaneParcels, 
					(MatriculationInfoParcels controller) -> {
				controller.setParcels(matriculation.getParcels());
			});
		}
		
	}
	public void setReturn(String returnPath, String returnText) {
		btnReturn.setText("Voltar para " + returnText);
	}
	
	// === BUTTONS ON TOP ===
	public void handleBtnReturn(ActionEvent event) {
		System.out.println("return button clicked");
	}
	
	// Edit Matriculation Status
	public void handleBtnEditStatus(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.MATRICULATION_STATUS_FORM, Utils.currentStage(event), "Editar Status", false,
				(MatriculationStatusFormController controller) -> {
					controller.setMatriculation(matriculation);
					// We need to set this dependence to update here in the future
					controller.setMatriculationInfoController(this);
				});
	}
	
	// Auxiliar methods
	private void updateForm() {
		// Status
		textStatus.setText(matriculation.getStatus());
		hBoxStatus.setStyle("-fx-background-color: " + MatriculationStatusEnum.fromString(matriculation.getStatus()).getHexColor());
		// Code, date matriculation, matriculated by,reason, serviceContracted
		txtCode.setText(Integer.toString(matriculation.getCode()));
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		txtDateMatriculation.setText(sdf.format(matriculation.getDateMatriculation()));
		txtMatriculatedBy.setText(matriculation.getMatriculatedBy());
		txtReason.setText(matriculation.getReason());
		txtServiceContracted.setText(matriculation.getServiceContracted());
		// Parcels situation
		try {
			// Total of parcels ignoring matriculation tax (parcel 0)
			List<Parcel> parcels = matriculation.getParcels().stream()
					.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
			// Total of paid parcels = with status equals PAGA
			int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
					.equalsIgnoreCase("PAGA")).collect(Collectors.toList()).size();
			// will show in table number of paid parcels from total
			txtParcelsSituation.setText(paidParcels + "/" + parcels.size());
		}catch(IllegalStateException | IndexOutOfBoundsException e) {
			// if the matriculation doenst have parcels will show just a line
			txtParcelsSituation.setText("-");
		}
	}
	
	
	// Called from others controllers
	public void onDataChanged() {
		updateForm();
	}

	


}
