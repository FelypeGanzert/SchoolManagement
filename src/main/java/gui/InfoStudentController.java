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
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import gui.util.enums.StudentStatusEnum;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
	@FXML private TableView<Matriculation> tableMatriculations;
	@FXML private TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML private TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML private TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML private TableColumn<Matriculation, String> columnMatriculationParcels;
	@FXML private TableColumn<Matriculation, String> columnMatriculationResponsible;
	@FXML private TableColumn<Matriculation, Matriculation> columnMatriculationInfo;
	@FXML private Button btnAddMatriculation;
	// Table Contacts
	@FXML private TableView<Contact> tableContacts;
	@FXML private TableColumn<Contact, String> columnContactNumber;
	@FXML private TableColumn<Contact, String> columnContactDescription;
	@FXML private TableColumn<Contact, Contact> columnContactEdit;
	@FXML private TableColumn<Contact, Contact> columnContactDelete;
	@FXML private Button btnAddContact;
	// Table Responsibles
	@FXML private TableView<Responsible> tableResponsibles;
	@FXML private TableColumn<Responsible, String> columnReponsibleName;
	@FXML private TableColumn<Responsible, String> columnReponsibleRelationship;
	@FXML private TableColumn<Responsible, Responsible> columnResponsibleEdit;
	@FXML private TableColumn<Responsible, Responsible> columnResponsibleRemove;
	@FXML private Button btnAddResponsible;

	private final Integer ICON_SIZE = 15;
	private Student student;
	private String returnPath;	

	private ObservableList<Matriculation> matriculationsList;
	private ObservableList<Contact> contactsList;
	private ObservableList<Responsible> responsiblesList;
	
	MainViewController mainView;	

	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTableMatriculationsNodes();
		initiliazeTableContactsNodes();
		initiliazeTableResponsiblesNodes();
		this.returnPath = FxmlPaths.LIST_STUDENTS;
	}
	
	public void setCurrentStudent(Student student) {
		this.student = student;
		updateFormData();
		try {
			if (this.student.getMatriculations() != null) {
				matriculationsList = FXCollections.observableArrayList(this.student.getMatriculations());
				tableMatriculations.setItems(matriculationsList);
			}
			if (this.student.getContacts() != null) {
				contactsList = FXCollections.observableArrayList(this.student.getContacts());
				tableContacts.setItems(contactsList);
			}
			if (this.student.getAllResponsibles() != null) {
				responsiblesList = FXCollections.observableArrayList(this.student.getAllResponsibles());
				tableResponsibles.setItems(responsiblesList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Alerts.showAlert("Erro ao carregar as informações do aluno", "DBException", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void updateFormData() {
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
		// Gender and CivilStatus
		textGender.setText(GenderEnum.fromString(student.getGender()).getfullGender());
		textCivilStatus.setText(CivilStatusEnum.fromFullCivilStatus(student.getCivilStatus()).getFullCivilStatus());
		if(student.getGender() != null && student.getGender().equalsIgnoreCase("Feminino")) {
			int civilStatusLength = textCivilStatus.getText().length();
			String feminineCivilStatus = textCivilStatus.getText().substring(0, civilStatusLength-1) + "a";
			textCivilStatus.setText(feminineCivilStatus);
		}
		textNeighborhood.setText(student.getNeighborhood());
		textAdress.setText(student.getAdress());
		textAdressReference.setText(student.getAdressReference());
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
					controller.updateTableView();
					controller.filterStudents(student.getStatus());
					controller.tableStudents.getSelectionModel().select(student);
				});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleBtnEdit(ActionEvent event) {
		createPersonDialogForm(Utils.currentStage(event));
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
	
	private void initiliazeTableContactsNodes() {
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
	
	private void initiliazeTableResponsiblesNodes() {
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
	
	private void createPersonDialogForm(Stage parentStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FxmlPaths.PERSON_FORM));
			Parent parent= loader.load();
			
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Informações da Pessoa");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(parentStage);
			dialogStage.setResizable(false);
			
			PersonFormController controller = loader.getController();
			controller.setPersonEntity(student);
			controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
			controller.setInfoStudentController(this);
			controller.setMainView(this.mainView);
			
			Scene PersonFormScene = new Scene(parent);
			PersonFormScene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
			dialogStage.setScene(PersonFormScene);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void onDataChanged(Student student) {
		this.student = student;
		updateFormData();
	}
	

}
