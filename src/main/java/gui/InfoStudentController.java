package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import gui.util.Alerts;
import gui.util.FxmlPaths;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.GenderEnum;
import gui.util.enums.StudentStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.dao.StudentDao;
import model.entites.Contact;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Responsible;
import model.entites.Student;

public class InfoStudentController implements Initializable {
	// Menu
	@FXML JFXButton btnReturn;
	@FXML Label labelStudentName;
	@FXML HBox hBoxStaus;
	@FXML TextField textStatus;
	@FXML Button btnEditStatus;
	@FXML Button btnEditStudent;
	@FXML Button btnDeleteStudent;
	// Student infos
	@FXML TextField textID;
	@FXML TextField textName;
	@FXML TextField textOldRA;
	@FXML TextField textCPF;
	@FXML TextField textGender;
	@FXML TextField textBirthDate;
	@FXML TextField textAge;
	@FXML TextField textCivilStatus;
	@FXML TextField textRG;
	@FXML TextField textEmail;
	@FXML CheckBox checkBoxPromotionsEmail;
	@FXML TextField textAdress;
	@FXML TextField textNeighborhood;
	@FXML TextField textAdressReference;
	@FXML TextField textCity;
	@FXML TextField textUF;
	@FXML Label labelDateCadastryAndModify;
	@FXML TextArea textAreaObservation;
	// Table Matriculations
	@FXML TableView<Matriculation> tableMatriculations;
	@FXML TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML TableColumn<Matriculation, String> columnMatriculationParcels;
	@FXML TableColumn<Matriculation, String> columnMatriculationResponsible;
	@FXML TableColumn<Matriculation, Matriculation> columnMatriculationInfo;
	@FXML Button btnAddMatriculation;
	// Table Contacts
	@FXML TableView<Contact> tableContacts;
	@FXML TableColumn<Contact, String> columnContactNumber;
	@FXML TableColumn<Contact, String> columnContactDescription;
	@FXML TableColumn<Contact, Contact> columnContactEdit;
	@FXML TableColumn<Contact, Contact> columnContactDelete;
	@FXML Button btnAddContact;
	// Table Responsibles
	@FXML TableView<Responsible> tableResponsibles;
	@FXML TableColumn<Responsible, String> columnReponsibleName;
	@FXML TableColumn<Responsible, String> columnReponsibleRelationship;
	@FXML TableColumn<Responsible, Responsible> columnResponsibleEdit;
	@FXML TableColumn<Responsible, Responsible> columnResponsibleRemove;
	@FXML Button btnAddResponsible;

	private final Integer ICON_SIZE = 15;
	private Student student;
	private String returnPath;
	private Responsible responsibleReturn;
	private Matriculation matriculationReturn;
	

	private ObservableList<Matriculation> matriculationsList;
	private ObservableList<Contact> contactsList;
	private ObservableList<Responsible> responsiblesList;
	
	MainViewController mainView;	

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableMatriculationsNodes();
		initiliazeTableContactsNodes();
		initiliazeTableResponsibles();
	}
	
	public void setCurrentStudent(Student student, String returnPath, Responsible responsibleReturn, Matriculation matriculationReturn) {
		this.student = student;
		this.returnPath = returnPath;
		this.responsibleReturn = responsibleReturn;
		this.matriculationReturn = matriculationReturn;
		setStudentInformationsToUI();
		;
		try {
			matriculationsList = FXCollections.observableArrayList(this.student.getMatriculations());
			contactsList = FXCollections.observableArrayList(this.student.getContacts());
			responsiblesList = FXCollections.observableArrayList(this.student.getAllResponsibles());
			tableMatriculations.setItems(matriculationsList);
			tableContacts.setItems(contactsList);
			tableResponsibles.setItems(responsiblesList);
		} catch (Exception e) {
			Alerts.showAlert("Erro ao carregar as informações do aluno", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void setCurrentStudent(Student student, String returnPath) {
		setCurrentStudent(student, returnPath, null, null);
	}
	
	public void setStudentInformationsToUI() {
		labelStudentName.setText(student.getName());
		textStatus.setText(student.getStatus());
		hBoxStaus.setStyle("-fx-background-color: " + StudentStatusEnum.fromString(student.getStatus()).getHexColor());

		textID.setText(Integer.toString(student.getId()));
		textOldRA.setText(student.getOldRA());
		textName.setText(student.getName());
		textEmail.setText(student.getEmail());
		checkBoxPromotionsEmail.selectedProperty().setValue(student.getSendEmail());
		textCPF.setText(student.getCpf());
		textRG.setText(student.getRg());
		textGender.setText(GenderEnum.fromString(student.getGender()).getfullGender());
		textCivilStatus.setText(student.getCivilStatus());
		textNeighborhood.setText(student.getNeighborhood());
		textAdress.setText(student.getAdress());
		textCity.setText(student.getCity());
		textUF.setText(student.getUf());
		textAreaObservation.setText(student.getObservation());

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(student.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(student.getDateBirth()));
		}
		textAge.setText(Integer.toString(student.getAge()) + " anos");
		String dateCadastryAndModify = "";
		if (student.getRegisteredBy() != null && student.getDateRegistry() != null) {
			dateCadastryAndModify = "Cadastrado por " + student.getRegisteredBy();
			dateCadastryAndModify += ", em " + sdf.format(student.getDateRegistry()) + ".";
			if (student.getDateLastRegistryEdit() != null) {
				dateCadastryAndModify += " Última edição em: " + sdf.format(student.getDateLastRegistryEdit());
			}
		}
		labelDateCadastryAndModify.setText(dateCadastryAndModify);
	}
	

	public void handleBtnReturn(ActionEvent event) {
		try {
			if(returnPath == FxmlPaths.LIST_STUDENTS) {
				mainView.setContent(returnPath, (ListStudentsController controller) -> {
					controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
					controller.setMainViewController(mainView);
					controller.tableStudents.getSelectionModel().select(student);
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMainViewControllerAndReturnName(MainViewController mainView, String returnBtnText) {
		this.mainView = mainView;
		btnReturn.setText("Voltar para " + returnBtnText);
		
	}

	private void initializeTableMatriculationsNodes() {
		Utils.setCellValueFactory(columnMatriculationCode, "code");
		Utils.setCellValueFactory(columnMatriculationDate, "dateMatriculation");
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnMatriculationStatus, "status");
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGO")).collect(Collectors.toList()).size();
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				return new SimpleStringProperty("-");
			}
		});
		columnMatriculationResponsible.setCellValueFactory(cellData -> {
			try {
				if(cellData.getValue().getResponsible() != null) {
					return new SimpleStringProperty(cellData.getValue().getResponsible().getName());
				}
				return new SimpleStringProperty();
			} catch(Exception e) {	
				return new SimpleStringProperty("-");
			}
		});
		// Info button
		Utils.initButtons(columnMatriculationInfo, ICON_SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (item, event) -> {
			System.out.println("info matriculation");
		});
	}
	
	private void initiliazeTableResponsibles() {
		Utils.setCellValueFactory(columnContactNumber, "number");
		Utils.setCellValueFactory(columnContactDescription, "description");
		// Edit button
		Utils.initButtons(columnContactEdit, ICON_SIZE, Icons.PEN_SOLID, "grayIcon", (item, event) -> {
			System.out.println("edit contact");
		});
		// Remove button
		Utils.initButtons(columnContactDelete, ICON_SIZE, Icons.TRASH_SOLID, "redIcon", (item, event) -> {
			System.out.println("remove contact");
		});
	}
	
	private void initiliazeTableContactsNodes() {
		Utils.setCellValueFactory(columnReponsibleName, "name");
		columnReponsibleRelationship.setCellValueFactory(cellData -> {
			try {
				if(cellData.getValue().getName() != null) {
					return new SimpleStringProperty(cellData.getValue().getRelationship(student));
				}
				return new SimpleStringProperty();
			} catch(Exception e) {	
				return new SimpleStringProperty("-");
			}
		});
		// Edit button
		Utils.initButtons(columnResponsibleEdit, ICON_SIZE, Icons.PEN_SOLID, "grayIcon", (item, event) -> {
			System.out.println("edit responsible");
		});
		// Remove button
		Utils.initButtons(columnResponsibleRemove, ICON_SIZE, Icons.TRASH_SOLID, "redIcon", (item, event) -> {
			System.out.println("remove responsible");
		});
	}
	

}
