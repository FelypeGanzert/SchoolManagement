package gui;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import gui.util.Alerts;
import gui.util.FxmlPath;
import gui.util.Icons;
import gui.util.Utils;
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
	@FXML TextField textYears;
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

	private ObservableList<Matriculation> matriculationsList;
	private ObservableList<Contact> contactsList;
	private ObservableList<Responsible> responsiblesList;
	
	MainViewController mainView;
	
	Map<String, String> valuesOfStudent = new HashMap<>();
	

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableMatriculationsNodes();
		initiliazeTableContactsNodes();
		initiliazeTableResponsibles();
	}
	
	public void setCurrentStudent(Student student) {
		this.student = student;
		parseStudentInformationToMap();
		
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
		// I think i can reduce this on future
		labelStudentName.setText(valuesOfStudent.get("name"));
		textName.setText(valuesOfStudent.get("name"));
		textEmail.setText(valuesOfStudent.get("email"));
		hBoxStaus.setStyle("-fx-background-color: " + valuesOfStudent.get("colorStatus"));
		textStatus.setText(valuesOfStudent.get("status"));
		textID.setText(valuesOfStudent.get("id"));
		textOldRA.setText(valuesOfStudent.get("oldRA"));
		textCPF.setText(valuesOfStudent.get("cpf"));
		textGender.setText(valuesOfStudent.get("gender"));
		textCivilStatus.setText(valuesOfStudent.get("civilStatus"));
		textNeighborhood.setText(valuesOfStudent.get("neighborhood"));
		textAdress.setText(valuesOfStudent.get("adress"));
		textCity.setText(valuesOfStudent.get("city"));
		textUF.setText(valuesOfStudent.get("uf"));
		textRG.setText(valuesOfStudent.get("rg"));
		textYears.setText(valuesOfStudent.get("years"));
		textAreaObservation.setText(valuesOfStudent.get("observation"));
		labelDateCadastryAndModify.setText(valuesOfStudent.get("dateCadastryAndModify"));
		textBirthDate.setText(valuesOfStudent.get("birthDate"));
		checkBoxPromotionsEmail.selectedProperty().setValue(student.isSendEmail());
		
		
		
	}
	
	public void parseStudentInformationToMap() {
		String name, email, colorStatus, status, id, oldRA, cpf, gender, birthDate, years, civilStatus, rg,
			adress, neighborhood, city, uf, dateCadastryAndModify, observation;
		// I think I can reduce this on future...
		name = student.getName() != null ? student.getName() : "-";
		email = student.getEmail() != null ? student.getEmail() : "-";	
		
		colorStatus = student.getStatus() != null ? StudentStatusEnum.fromString(student.getStatus()).getHexColor() : "white";
		status = student.getStatus() != null ? student.getStatus() : "-";
		
		id = student.getId() != null ? Integer.toString(student.getId()) : "-";
		oldRA = student.getOldRA() != null ? student.getOldRA() : "-";
		cpf = student.getCpf() != null ? student.getCpf() : "-";
		gender = student.getGender() != null ? student.getGender() : "-";
		civilStatus = student.getCivilStatus() != null ? student.getCivilStatus() : "-";
		neighborhood = student.getNeighborhood() != null ? student.getNeighborhood() : "-";
		adress = student.getAdress() != null ? student.getAdress() : "-";
		city = student.getCity() != null ? student.getCity() : "-";
		uf = student.getUf() != null ? student.getUf() : "-";
		rg = student.getRg() != null ? student.getRg() : "-";
		observation = student.getObservation();
		years = student.getAge() > 0 ? Integer.toString(student.getAge()) : "-";
		
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		birthDate = student.getDateBirth() != null ? sdf.format(student.getDateBirth()) : "-";
		try {
			dateCadastryAndModify =  "Cadastrado por " + student.getRegisteredBy() + ", em + " + 
					sdf.format(student.getDateRegistry()) + ". Última edição em: " + sdf.format(student.getDateLastRegistryEdit());
		} catch(Exception e) {
			dateCadastryAndModify = "-";
		}
		
		valuesOfStudent.put("name", name);
		valuesOfStudent.put("email", email);
		valuesOfStudent.put("colorStatus", colorStatus);
		valuesOfStudent.put("status", status);
		valuesOfStudent.put("id", id);
		valuesOfStudent.put("oldRA", oldRA);
		valuesOfStudent.put("cpf", cpf);
		valuesOfStudent.put("gender", gender);
		valuesOfStudent.put("civilStatus", civilStatus);
		valuesOfStudent.put("neighborhood", neighborhood);
		valuesOfStudent.put("adress", adress);
		valuesOfStudent.put("city", city);
		valuesOfStudent.put("uf", uf);
		valuesOfStudent.put("rg", rg);
		valuesOfStudent.put("observation", observation);
		valuesOfStudent.put("years", years);
		valuesOfStudent.put("birthDate", birthDate);
		valuesOfStudent.put("dateCadastryAndModify", dateCadastryAndModify);
		
	}
	

	public void handleBtnReturn(ActionEvent event) {
		try {
			mainView.setContent(FxmlPath.LIST_STUDENTS, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
				controller.setMainViewController(mainView);
				controller.tableStudents.getSelectionModel().select(student);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setMainViewController(MainViewController mainView, String returnBtnText) {
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
