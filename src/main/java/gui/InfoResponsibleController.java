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
import db.DbExceptioneEntityExcluded;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FXMLPath;
import gui.util.Icons;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
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
import model.dao.ContactDao;
import model.entites.Contact;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Responsible;
import model.entites.Student;
import sharedData.Globe;

public class InfoResponsibleController implements Initializable {
	// Menu
	@FXML private JFXButton btnReturn;
	@FXML private Label labelResponsibleName;
	@FXML private Button btnEditResponsible;
	// Student infos
	@FXML private TextField textID;
	@FXML private TextField textName;
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
	@FXML private TableColumn<Matriculation, String> columnMatriculationStudent;
	@FXML private TableColumn<Matriculation, Matriculation> columnMatriculationInfo;
	@FXML private Button btnAddMatriculation;
	// Table Contacts
	@FXML public TableView<Contact> tableContacts;
	@FXML private TableColumn<Contact, String> columnContactNumber;
	@FXML private TableColumn<Contact, String> columnContactDescription;
	@FXML private TableColumn<Contact, Contact> columnContactEdit;
	@FXML private TableColumn<Contact, Contact> columnContactDelete;
	@FXML private Button btnAddContact;
	// Table Students
	@FXML public TableView<Student> tableStudents;
	@FXML private TableColumn<Student, String> columnStudentName;
	@FXML private TableColumn<Student, String> columnStudentRelationship;
	@FXML private TableColumn<Student, Student> columnStudentInfo;
	
	private Responsible responsible;
	private Student studentToReturn;
	private String returnPath;	

	private ObservableList<Matriculation> matriculationsList;
	private ObservableList<Contact> contactsList;
	private ObservableList<Student> studentsList;

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableMatriculationsNodes();
		initiliazeTableContactsNodes();
		initiliazeTableStudentsNodes();
		// Default return path
		this.returnPath = FXMLPath.LIST_RESPONSIBLES;
	}
	
	// ===== DEPENDENCES =====
	
	public void setCurrentResponsible(Responsible responsible) {
		this.responsible = responsible;
		// Update UI with student informations
		updateFormData();
		updateAllTablesData();
	}
	
	public void setCurrentStudent(Student studentToReturn) {
		this.studentToReturn = studentToReturn;
	}
	
	public void setReturn(String returnPath, String returnText) {
		this.returnPath  = returnPath;
		btnReturn.setText("Voltar para " + returnText);
	}
	
	// ====================================================
	// ===== START OF METHODS TO UPDATE DATA IN UI ========
	// ====================================================
	
	private void updateFormData() {
		// Menu: Name
		labelResponsibleName.setText(responsible.getName());
		// Menu: ID, OldRA, Name, Email, PromotionEmail, CPF, RG,  
		textID.setText(Integer.toString(responsible.getId()));
		textName.setText(responsible.getName());
		textEmail.setText(responsible.getEmail());
		checkBoxPromotionsEmail.selectedProperty().setValue(responsible.getSendEmail());
		textCPF.setText(Utils.formatCPF(responsible.getCpf()));
		textRG.setText(Utils.formatRG(responsible.getRg()));
		// Gender and CivilStatus
		textGender.setText(GenderEnum.fromString(responsible.getGender()).getfullGender());
		textCivilStatus.setText(CivilStatusEnum.fromFullCivilStatus(responsible.getCivilStatus()).getFullCivilStatus());
		// If the person is a woman, so we change civilStatus to end with A
		if(responsible.getGender() != null && responsible.getGender().equalsIgnoreCase("Feminino")) {
			int civilStatusLength = textCivilStatus.getText().length();
			String feminineCivilStatus = textCivilStatus.getText().substring(0, civilStatusLength-1) + "a";
			textCivilStatus.setText(feminineCivilStatus);
		}
		// Adress: Neighborhood, adress, adressReferemce, city, uf
		textNeighborhood.setText(responsible.getNeighborhood());
		textAdress.setText(responsible.getAdress());
		textAdressReference.setText(responsible.getAdressReference());
		textCity.setText(responsible.getCity());
		textUF.setText(responsible.getUf());
		// Observation
		textAreaObservation.setText(responsible.getObservation());
		// DATES: birthdate, age
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(responsible.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(responsible.getDateBirth()));
		}
		textAge.setText(Integer.toString(responsible.getAge()) + " anos");
		// REGISTRY INFORMATIONS: registeredBy, dateRegistry, DateLastEdit
		String dateCadastryAndModify = "";
		if (responsible.getRegisteredBy() != null && responsible.getDateRegistry() != null) {
			dateCadastryAndModify = "Registrado por " + responsible.getRegisteredBy();
			dateCadastryAndModify += ", em " + sdf.format(responsible.getDateRegistry()) + ".";
			// If the student have been changed we show the date of the last edit
			if (responsible.getDateLastRegistryEdit() != null) {
				dateCadastryAndModify += " Última edição em: " + sdf.format(responsible.getDateLastRegistryEdit());
			}
		}
		labelDateCadastryAndModify.setText(dateCadastryAndModify);
	}
	
	private void updateAllTablesData() {
		updateTableMatriculation();
		updateTableContacts();
		updateTableStudents();
	}
	
	private void updateTableMatriculation() {
		if (responsible.getMatriculationsThatIsResponsible() != null) {
			matriculationsList = FXCollections.observableArrayList(this.responsible.getMatriculationsThatIsResponsible());
			matriculationsList.sort((m1, m2) -> m2.getDateMatriculation().compareTo(m1.getDateMatriculation()));
			tableMatriculations.setItems(matriculationsList);
			tableMatriculations.refresh();
		}
	}
	
	private void updateTableContacts() {
		if (responsible.getContacts() != null) {
			contactsList = FXCollections.observableArrayList(this.responsible.getContacts());
			contactsList.sort((c1, c2) -> c2.getId().compareTo(c1.getId()));
			tableContacts.setItems(contactsList);
			tableContacts.refresh();
		}
	}

	private void updateTableStudents() {
		if (responsible.getAllStudents() != null) {
			studentsList = FXCollections.observableArrayList(this.responsible.getAllStudents());
			studentsList.sort((r1, r2) -> r2.getId().compareTo(r1.getId()));
			tableStudents.setItems(studentsList);
			tableStudents.refresh();
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
		if (returnPath == FXMLPath.LIST_RESPONSIBLES) {
			Roots.listResponsibles((ListResponsiblesController controller) -> {
				// Select this responsible
				controller.tableResponsibles.getSelectionModel().select(responsible);
				controller.tableResponsibles.scrollTo(responsible);
			});
		}
		if (returnPath == FXMLPath.INFO_STUDENT) {
			try {
				// refresh student data
				DBUtil.refreshData(studentToReturn);
				// show screen of student informations
				MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
				mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
					controller.setReturn(FXMLPath.LIST_STUDENTS, "Alunos");
					controller.setCurrentStudent(studentToReturn);
				});
			} catch (DbException e) {
				e.printStackTrace();
				Alerts.showAlert("DBException", "DBException - excessão no banco de dados", e.getMessage(),
						AlertType.ERROR, Utils.currentStage(event));
			} catch (DbExceptioneEntityExcluded e) {
				e.printStackTrace();
				// Show a message that student has been deleted
				Alerts.showAlert("DBExceptionEntityExcluded",
						studentToReturn.getId() + " - " + studentToReturn.getName() + " foi deletado do banco de dados por alguém",
						"DBExceptionEntityExcluded: " + e.getMessage(), AlertType.ERROR,
						Utils.currentStage(event));
				// remove student deleted from table
				tableStudents.getItems().remove(studentToReturn);
				tableStudents.refresh();
			}
		}
	}

	// Edit responsible
	public void handleBtnEdit(ActionEvent event) {
		Utils.loadView(this, true, FXMLPath.PERSON_FORM, Utils.currentStage(event), "Informações pessoais", false,
				(PersonFormController controller) -> {
					controller.setPersonEntity(responsible);
					// We need to set this dependence to return to here in the future
					controller.setInfoResponsibleController(this);
				});
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
					controller.setDependences(new Contact(), responsible, this);
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
		columnMatriculationCode.setReorderable(false);
		Utils.setCellValueFactory(columnMatriculationDate, "dateMatriculation");
		Utils.formatTableColumnDate(columnMatriculationDate, "dd/MM/yyyy");
		columnMatriculationDate.setReorderable(false);
		Utils.setCellValueFactory(columnMatriculationStatus, "status");
		columnMatriculationStatus.setReorderable(false);
		// Parcels
		columnMatriculationParcels.setCellValueFactory(cellData -> {
			try {
				// Total of parcels ignoring matriculation tax (parcel 0)
				List<Parcel> parcels = cellData.getValue().getParcels().stream()
						.filter(parcel -> parcel.getParcelNumber() != 0).collect(Collectors.toList());
				// Total of paid parcels = with status equals PAGA
				int paidParcels = parcels.stream().filter(parcel -> parcel.getSituation()
						.equalsIgnoreCase("PAGA")).collect(Collectors.toList()).size();
				// will show in table number of paid parcels from total
				return new SimpleStringProperty(paidParcels + "/" + parcels.size());
			}catch(IllegalStateException | IndexOutOfBoundsException e) {
				// if the matriculation doenst have parcels will show just a line
				return new SimpleStringProperty("-");
			}
		});
		columnMatriculationParcels.setReorderable(false);
		// Matriculation Responsible name
		columnMatriculationStudent.setCellValueFactory(cellData -> {
			try {
				if(cellData.getValue().getStudent() != null) {
					return new SimpleStringProperty(cellData.getValue().getStudent().getName());
				}
				return new SimpleStringProperty();
			} catch(Exception e) {	
				return new SimpleStringProperty("-");
			}
		});
		columnMatriculationStudent.setReorderable(false);
		// Info button
		Utils.initButtons(columnMatriculationInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (matriculation, event) -> {
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.MATRICULATION_INFO, (MatriculationInfoController controller) -> {
				controller.setCurrentMatriculation(matriculation, FXMLPath.INFO_RESPONSIBLE);
			});
		});
		columnMatriculationInfo.setReorderable(false);
	}
	
	// Contacts
	private void initiliazeTableContactsNodes() {
		// number
		columnContactNumber.setCellValueFactory(cellData -> {
			return new SimpleStringProperty(Constraints.formatNumberContact(cellData.getValue().getNumber()));
		});
		columnContactNumber.setReorderable(false);
		// description
		Utils.setCellValueFactory(columnContactDescription, "description");
		columnContactDescription.setReorderable(false);
		// Edit button
		Utils.initButtons(columnContactEdit, Icons.SIZE, Icons.PEN_SOLID, "grayIcon", (contact, event) -> {
			Utils.loadView(this, true, FXMLPath.CONTACT_FORM, Utils.currentStage(event), "Editar contato", false,
					(ContactFormController controller) -> {
						controller.setDependences(contact, responsible, this);
					});
		});
		columnContactEdit.setReorderable(false);
		// Delete button
		Utils.initButtons(columnContactDelete, Icons.SIZE, Icons.TRASH_SOLID, "redIcon", (contact, event) -> {
			// Confirmation to delete contact
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar contato");
			alert.setHeaderText("Deletar o contato " +  contact.getNumber()  + " - " + contact.getDescription() + " ?");
			alert.initOwner(Utils.currentStage(event));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				// Show a screen of deleting contact process
				try {
					Alert alertProcessing = Alerts.showProcessingScreen(Utils.currentStage(event));
					// ContactDao to delete from db
					ContactDao contactDao = new ContactDao(DBFactory.getConnection());
					contactDao.delete(contact);
					// remove contact from student in memory and refresh tables in UI
					responsible.getContacts().remove(contact);
					updateTableContacts();
					alertProcessing.close();
				} catch (DbException e) {
					Alerts.showAlert("Erro ao deletar contato", "DbException", e.getMessage(),
							AlertType.ERROR, Utils.currentStage(event));
				}
			}
		});
		columnContactDelete.setReorderable(false);
	}
	
	// STUDENTS
	private void initiliazeTableStudentsNodes() {
		// name
		Utils.setCellValueFactory(columnStudentName, "name");
		columnStudentName.setReorderable(false);
		// relationship
		columnStudentRelationship.setCellValueFactory(cellData -> {
			try {
				if(cellData.getValue().getName() != null) {
					return new SimpleStringProperty(cellData.getValue().getRelationship(responsible));
				}
				return new SimpleStringProperty();
			} catch(Exception e) {	
				return new SimpleStringProperty("-");
			}
		});
		columnStudentRelationship.setReorderable(false);
		// Info button
		Utils.initButtons(columnStudentInfo, Icons.SIZE, Icons.INFO_CIRCLE_SOLID, "grayIcon", (student, event) -> {
			MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
			mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
				controller.setCurrentStudent(student);
				String responsibleFirstName;
				if (responsible.getName().contains(" ")) {
					responsibleFirstName = responsible.getName().substring(0, responsible.getName().indexOf(" "));
				} else {
					responsibleFirstName = responsible.getName();
				}
				controller.setReturn(FXMLPath.INFO_RESPONSIBLE, responsibleFirstName);
				controller.setCurrentResponsible(responsible);
			});
		});
		columnStudentInfo.setReorderable(false);
	}
	
	// ====================================================
	// ========== END OF INITIALIZE METHODS ===============
	// ====================================================
	
	// Called from others controllers
	public void onDataChanged() {
		updateFormData();
		updateAllTablesData();
	}

}