package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DBUtil;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import gui.util.enums.StudentStatusEnum;
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
	@FXML private JFXButton btnReturn;
	@FXML private Label labelStudentName;
	@FXML private HBox hBoxStaus;
	@FXML private TextField textStatus;
	@FXML private Button btnCourses;
	@FXML private Button btnEditStatus;
	@FXML private Button btnEditStudent;
	@FXML private Button btnDeleteStudent;
	// Student infos
	@FXML private TextField textID;
	@FXML private TextField textName;
	@FXML private TextField textOldRA;
	@FXML private TextField textCPF;
	@FXML private TextField textGender;
	@FXML private TextField textBirthDate;
	@FXML private TextField textAge;
	@FXML private TextField textCivilStatus;
	@FXML private TextField textRG;
	@FXML private TextField textEmail;
	@FXML private CheckBox checkBoxPromotionsEmail;
	@FXML private TextField textAdress;
	@FXML private TextField textNeighborhood;
	@FXML private TextField textAdressReference;
	@FXML private TextField textCity;
	@FXML private TextField textUF;
	@FXML private Label labelDateCadastryAndModify;
	@FXML private TextArea textAreaObservation;
	// Table Matriculations
	@FXML public TableView<Matriculation> tableMatriculations;
	@FXML private TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML private TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML private TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML private TableColumn<Matriculation, String> columnMatriculationParcels;
	@FXML private TableColumn<Matriculation, String> columnMatriculationResponsible;
	@FXML private TableColumn<Matriculation, Matriculation> columnMatriculationInfo;
	@FXML private Button btnAddMatriculation;
	// Table Contacts
	@FXML public TableView<Contact> tableContacts;
	@FXML private TableColumn<Contact, String> columnContactNumber;
	@FXML private TableColumn<Contact, String> columnContactDescription;
	@FXML private TableColumn<Contact, Contact> columnContactEdit;
	@FXML private TableColumn<Contact, Contact> columnContactDelete;
	@FXML private Button btnAddContact;
	// Table Responsibles
	@FXML public TableView<Responsible> tableResponsibles;
	@FXML private TableColumn<Responsible, String> columnReponsibleName;
	@FXML private TableColumn<Responsible, String> columnReponsibleRelationship;
	@FXML private TableColumn<Responsible, Responsible> columnResponsibleEdit;
	@FXML private TableColumn<Responsible, Responsible> columnResponsibleRemove;
	@FXML private Button btnAddResponsible;
	
	private Student student;
	private String returnPath;	

	private ObservableList<Matriculation> matriculationsList;
	private ObservableList<Contact> contactsList;
	private ObservableList<Responsible> responsiblesList;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableMatriculationsNodes();
		initiliazeTableContactsNodes();
		initiliazeTableResponsiblesNodes();
		// Default return path
		this.returnPath = FXMLPath.LIST_STUDENTS;
	}
	
	// DEPENDENCES	
	public void setCurrentStudent(Student student) {
		this.student = student;
		// Refresh student data
		DBUtil.refleshData(this.student);
		// Update UI with student informations
		updateFormData();
		updateTablesData();
	}
	
	public void setMainViewControllerAndReturnName(String returnPath, String returnText) {
		btnReturn.setText("Voltar para " + returnText);
	}
	
	// ====================================================
	// ===== START OF METHODS TO UPDATE DATA IN UI ========
	// ====================================================
	
	private void updateFormData() {
		// Menu: Name
		labelStudentName.setText(student.getName());
		// Status
		textStatus.setText(student.getStatus());
		hBoxStaus.setStyle("-fx-background-color: " + StudentStatusEnum.fromString(student.getStatus()).getHexColor());
		// Menu: ID, OldRA, Name, Email, PromotionEmail, CPF, RG,  
		textID.setText(Integer.toString(student.getId()));
		textOldRA.setText(student.getOldRA());
		textName.setText(student.getName());
		textEmail.setText(student.getEmail());
		checkBoxPromotionsEmail.selectedProperty().setValue(student.getSendEmail());
		textCPF.setText(Utils.formatCPF(student.getCpf()));
		textRG.setText(Utils.formatRG(student.getRg()));
		// Gender and CivilStatus
		textGender.setText(GenderEnum.fromString(student.getGender()).getfullGender());
		textCivilStatus.setText(CivilStatusEnum.fromFullCivilStatus(student.getCivilStatus()).getFullCivilStatus());
		// If the person is a woman, so we change civilStatus to end with A
		if(student.getGender() != null && student.getGender().equalsIgnoreCase("Feminino")) {
			int civilStatusLength = textCivilStatus.getText().length();
			String feminineCivilStatus = textCivilStatus.getText().substring(0, civilStatusLength-1) + "a";
			textCivilStatus.setText(feminineCivilStatus);
		}
		// Adress: Neighborhood, adress, adressReferemce, city, uf
		textNeighborhood.setText(student.getNeighborhood());
		textAdress.setText(student.getAdress());
		textAdressReference.setText(student.getAdressReference());
		textCity.setText(student.getCity());
		textUF.setText(student.getUf());
		// Observation
		textAreaObservation.setText(student.getObservation());
		// DATES: birthdate, age
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(student.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(student.getDateBirth()));
		}
		textAge.setText(Integer.toString(student.getAge()) + " anos");
		// REGISTRY INFORMATIONS: registeredBy, dateRegistry, DateLastEdit
		String dateCadastryAndModify = "";
		if (student.getRegisteredBy() != null && student.getDateRegistry() != null) {
			dateCadastryAndModify = "Cadastrado por " + student.getRegisteredBy();
			dateCadastryAndModify += ", em " + sdf.format(student.getDateRegistry()) + ".";
			// If the student have been changed we show the date of the last edit
			if (student.getDateLastRegistryEdit() != null) {
				dateCadastryAndModify += " Última edição em: " + sdf.format(student.getDateLastRegistryEdit());
			}
		}
		labelDateCadastryAndModify.setText(dateCadastryAndModify);
	}
	
	private void updateTablesData() {
		try {
			// Matriculations
			if (student.getMatriculations() != null && student.getMatriculations().size() > 0) {
				matriculationsList = FXCollections.observableArrayList(this.student.getMatriculations());
				tableMatriculations.setItems(matriculationsList);
			}
			// Contacts
			if (student.getContacts() != null && student.getContacts().size() > 0) {
				contactsList = FXCollections.observableArrayList(this.student.getContacts());
				tableContacts.setItems(contactsList);
			}
			// Responsibles
			if (student.getAllResponsibles() != null && student.getAllResponsibles().size() > 0) {
				responsiblesList = FXCollections.observableArrayList(this.student.getAllResponsibles());
				tableResponsibles.setItems(responsiblesList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao carregar as informações do aluno em alguma das tabelas", "Erro", e.getMessage(), AlertType.ERROR);
		}
	}
	
	// ====================================================
	// ======= END OF METHODS TO UPDATE DATA IN UI ========
	// ====================================================
	
	// ====================================================
	// ======== START OF BUTTONS ACTION ON MENU ===========
	// ====================================================

	// Return button
	public void handleBtnReturn(ActionEvent event) {
		try {
			if(returnPath == FXMLPath.LIST_STUDENTS) {
				Roots.listStudents((ListStudentsController controller) -> {
					// Select this student
					controller.selectStatusToFilter(student.getStatus());
					controller.tableStudents.getSelectionModel().select(student);
					controller.tableStudents.scrollTo(student);
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao retornar", "Erro", e.getMessage(), AlertType.ERROR);
		}
	}
	
	// Edit student
	public void handleBtnEdit(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Informações pessoais", false,
				(PersonFormController controller) -> {
					controller.setPersonEntity(student);
					// We need to set this dependence to return to here in the future
					controller.setInfoStudentController(this);
				});
	}
	
	// Remove student 
	public void handleBtnRemove(ActionEvent event) {
		// Confirmation Alert to delete
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Deletar Estudante?");
		alert.setHeaderText("Tem certeza que deseja deletar as informações de " + student.getName() + "?\n" +
				"Todos os seus dados serão excluídos e não será possível reverter tal ação.");
		Optional<ButtonType> result = alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			// Call studentDao to delete
			try {
				Alert alertProcessing = Alerts.showProcessingScreen();
				StudentDao studentDao = new StudentDao(DBFactory.getConnection());
				studentDao.deleteById(student.getId());
				alertProcessing.close();
				// Return to List of students
				Roots.listStudents();
			} catch (DbException e) {
				e.printStackTrace();
				Alerts.showAlert("Erro ao deletar o estudante...", "DbException", e.getMessage(), AlertType.ERROR);
			}
		}
	}
	
	// ====================================================
	// ========== END OF BUTTONS ACTION ON MENU ===========
	// ====================================================
	
	// ====================================================
	// ========== START OF BUTTONS ON TABLES===============
	// ====================================================
	
	public void handleBtnAddContact(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.CONTACT_FORM, Utils.currentStage(event), "Novo contato", false,
				(ContactFormController controller) -> {
					controller.setDependences(new Contact(), student, this);
				});
	}

	// ====================================================
	// ============ END OF BUTTONS ON TABLES===============
	// ====================================================

	// ====================================================
	// ======== START OF INITIALIZE METHODS ===============
	// ====================================================

	// MATRICULATION
	private void initializeTableMatriculationsNodes() {
		// code, dateMatriculation, status, 
		Utils.setCellValueFactory(columnMatriculationCode, "code");
		Utils.setCellValueFactory(columnMatriculationDate, "dateMatriculation");
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		Utils.setCellValueFactory(columnMatriculationStatus, "status");
		// Parcels
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				// Total of paid parcels = with status equals PAGA
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGO")).collect(Collectors.toList()).size();
				// will show in table number of paid parcels from total
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				// if the matriculation doenst have parcels will show just a line
				return new SimpleStringProperty("-");
			}
		});
		// Matriculation Responsible name
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
		Utils.initButtons(columnMatriculationInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (item, event) -> {
			System.out.println("info matriculation"); //IN PROGRESS
		});
	}
	
	// CONTACTS
	private void initiliazeTableContactsNodes() {
		// number, description
		Utils.setCellValueFactory(columnContactNumber, "number");
		Utils.setCellValueFactory(columnContactDescription, "description");
		// Edit button
		Utils.initButtons(columnContactEdit, Icons.SIZE, Icons.PEN_SOLID, "grayIcon", (contact, event) -> {
			Utils.loadView(this, true, FXMLPath.CONTACT_FORM, Utils.currentStage(event), "Editar contato", false,
					(ContactFormController controller) -> {
						// IN PROGRESS
						System.out.println("You clicked to edit " + contact.getNumber() + " - " + contact.getDescription());
					});
		});
		// Remove button
		Utils.initButtons(columnContactDelete, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (contact, event) -> {
			// IN PROGRESS
			System.out.println("You clicked to remove " + contact.getNumber() + " - " + contact.getDescription());
		});
	}
	
	// RESPONSIBLES
	private void initiliazeTableResponsiblesNodes() {
		// name
		Utils.setCellValueFactory(columnReponsibleName, "name");
		// relationship
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
		Utils.initButtons(columnResponsibleEdit, Icons.SIZE, Icons.PEN_SOLID, "grayIcon", (item, event) -> {
			System.out.println("edit responsible"); // IN PROGRESS
		});
		// Remove button
		Utils.initButtons(columnResponsibleRemove, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (item, event) -> {
			System.out.println("remove responsible"); // IN PROGRESS
		});
	}
	
	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================
	
	// Called from another controller
	public void onDataChanged(Student student) {
		this.student = student;
		updateFormData();
		updateTablesData();
	}

}