package gui;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import animatefx.animation.Shake;
import animatefx.animation.ZoomIn;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FxmlPaths;
import gui.util.Icons;
import gui.util.Utils;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import gui.util.enums.StudentStatusEnum;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.dao.StudentDao;
import model.entites.Contact;
import model.entites.Person;
import model.entites.Student;

public class PersonFormController implements Initializable {
	// Search Bar
	@FXML private JFXTextField textCPF;
	@FXML private JFXTextField textName;
	@FXML private JFXButton btnFindRegistry;
	@FXML private Label labelFindRegistryResponse;
	// Registry Informations
	@FXML private HBox HBoxRegistryInformations;
	@FXML private JFXComboBox comboBoxRegisteredBy;
	@FXML private JFXTextField textDateRegistry;
	// Person Informations
	@FXML private HBox HBoxInformations;
	@FXML private JFXTextField textRG;
	@FXML private JFXTextField textEmail;
	@FXML private JFXComboBox<CivilStatusEnum> comboBoxCivilStatus;
	@FXML private JFXCheckBox checkBoxSendEmail;
	@FXML private JFXTextField textBirthDate;
	@FXML private JFXTextField textAdress;
	@FXML private JFXComboBox<GenderEnum> comboBoxGender;
	@FXML private JFXTextField textNeighborhood;
	@FXML private JFXTextField textCity;
	@FXML private JFXTextField textUF;
	@FXML private JFXComboBox<StudentStatusEnum> comboBoxStatus;
	@FXML private JFXTextField textAdressReference;
	@FXML private JFXTextArea textAreaObservation;
	// Table Contacts
	@FXML private TableView<Contact> tableContacts;
	@FXML private TableColumn<Contact, String> columnContactNumber;
	@FXML private TableColumn<Contact, String> columnContactDescription;
	@FXML private TableColumn<Contact, Contact> columnContactEdit;
	@FXML private TableColumn<Contact, Contact> columnContactDelete;
	@FXML private Button btnAddContact;
	// Buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private Person entity;
	private StudentDao studentDao;
	private ObservableList<Contact> contactsList;
	
	private InfoStudentController infoStudentController;
	private MainViewController mainView;
	
	private final Integer ICON_SIZE = 15;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		btnFindRegistry.setVisible(false);
		HBoxRegistryInformations.setVisible(false);
		comboBoxRegisteredBy.setVisible(false);
		textDateRegistry.setVisible(false);
		labelFindRegistryResponse.setVisible(false);
		HBoxInformations.setVisible(false);
		btnSave.setVisible(false);
		initializeFields();
		initiliazeTableContactsNodes();
	}
	
	private void initializeFields() {
		// Required validator
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
		requiredValidator.setMessage("Campo necessário");
		// Name
		textName.setValidators(requiredValidator);
		Constraints.alwaysUpperCase(textName);
		// CPF
		Constraints.cpf(textCPF);
		RegexValidator cpfValidator = new RegexValidator("CPF inválido");
		cpfValidator.setRegexPattern("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$");
		textCPF.setValidators(requiredValidator, cpfValidator);
		// RG
		Constraints.rg(textRG);
		RegexValidator rgValidator = new RegexValidator("RG inválido");
		rgValidator.setRegexPattern("^\\d{2}\\.\\d{3}\\.\\d{3}\\-\\d{1}$");
		textRG.setValidators(rgValidator);
		// Date Validator
		RegexValidator dateValidator = new RegexValidator("Ex: 21/10/1990");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textBirthDate.setValidators(dateValidator);
		textDateRegistry.setValidators(dateValidator);
		// Email Validator
		RegexValidator emailValidator = new RegexValidator("Email inválido");
		emailValidator.setRegexPattern("^(.+)@(.+)$");
		textEmail.setValidators(emailValidator);
		//textDateRegistry.setValidators(requiredValidator, dateValidator);
		// Max length for fields
		Constraints.setTextFieldMaxLength(textUF, 2);
		Constraints.setTextFieldMaxLength(textName, 50);
		Constraints.setTextFieldMaxLength(textEmail, 50);
		Constraints.noWhiteSpace(textEmail);
		Constraints.setTextFieldMaxLength(textAdress, 50);
		Constraints.setTextFieldMaxLength(textNeighborhood, 50);
		Constraints.setTextFieldMaxLength(textCity, 50);
		Constraints.setTextFieldMaxLength(textBirthDate, 10);
		Constraints.setTextFieldMaxLength(textDateRegistry, 10);
		// ComboBox
		comboBoxStatus.getItems().addAll(StudentStatusEnum.values());
		comboBoxStatus.getSelectionModel().selectFirst();
		comboBoxCivilStatus.getItems().addAll(CivilStatusEnum.values());
		comboBoxCivilStatus.getSelectionModel().selectFirst();
		comboBoxGender.getItems().addAll(GenderEnum.values());
		comboBoxGender.getSelectionModel().selectFirst();
	}
	
	public void handleBtnFindRegistry(ActionEvent event) {
		//Check if cpf is valide
		if(!textCPF.validate()) {
			return;
		}
		Student student;
		try {
			// Try to find a Student with same cpf informed
			student = studentDao.findByCPF(Constraints.onlyDigitsValue(textCPF));
			if (student != null) {
				this.entity = student;
				this.updateFormData();
				HBoxInformations.setVisible(true);
				new ZoomIn(HBoxInformations).play();
				labelFindRegistryResponse.setVisible(true);
				labelFindRegistryResponse.setText("Registro encontrado a partir do CPF. Confira as informações e atualize se necessário, em seguida clique em Salvar");
				new Shake(labelFindRegistryResponse).play();
				btnSave.setVisible(true);
				return;
			}
			// Check if Name is valide
			if(!textName.validate()) {
				return;
			}
			// Try to students with name like the one informed
			List<Student> studentsNameLike = studentDao.findAllWithNameLike(textName.getText().trim());
			if(studentsNameLike.size() > 0) {
				List<Person> peopleList = studentsNameLike.stream().map(s -> (Person) s).collect(Collectors.toList());
				// Show Screen to person see students finded
				loadView(FxmlPaths.PERSON_FORM_FIND_REGISTRY, Utils.currentStage(event), "Registros semelhantes", false,
						(PersonFormFindRegistryController controller) -> {
							controller.setPeopleList(peopleList);
							controller.setPersonFormController(this);
						});
				return;
			}
		} catch (DbException e) {
			Alerts.showAlert("DBException", e.getMessage(), "Houve um problema ao procurar a existência do cadastro", AlertType.ERROR);
			e.printStackTrace();
		}
		labelFindRegistryResponse.setText("A pessoa ainda não possui registro.");
		addNewRegistry();
	}
	

	public void addNewRegistry() {
		textCPF.setDisable(true);
		textName.setDisable(true);
		btnFindRegistry.setVisible(false);
		comboBoxRegisteredBy.setVisible(true);
		// Registry Date
		textDateRegistry.setVisible(true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		textDateRegistry.setText(sdf.format(new Date()));
		HBoxInformations.setVisible(true);
		new ZoomIn(HBoxInformations).play();
		labelFindRegistryResponse.setVisible(true);
		new Shake(labelFindRegistryResponse).play();
		btnSave.setVisible(true);		
	}
	
	public void handleBtnSave(ActionEvent event) {
		if(studentDao == null) {
			throw new IllegalStateException("StudentDao is null");
		}
		getFormData();
		try {
			if(entity instanceof Student) {		
				// check if fields is valid, only cpf and name is always required
				if(!textCPF.validate() || !textName.validate() ||
						(textDateRegistry.getText()  != null && textDateRegistry.getText().length() > 0 && !textDateRegistry.validate()) || 
						(textRG.getText()  != null && textRG.getText().length() > 0 && !textRG.validate()) ||						
						(textBirthDate.getText()  != null && textBirthDate.getText().length() > 0 && !textBirthDate.validate()) || 
						(textEmail.getText()  != null && textEmail.getText().length() > 0 && !textEmail.validate())) {
					return;
				}
				if (entity.getId() != null) {
					studentDao.update((Student) entity);
				} else {
					studentDao.insert((Student) entity);
				}
				if (this.infoStudentController != null) {
					this.infoStudentController.onDataChanged((Student) entity);
				} else {
					try {
						mainView.setContent(FxmlPaths.INFO_STUDENT, (InfoStudentController controller) -> {
							controller.setMainViewControllerAndReturnName(mainView, "Alunos");
							controller.setCurrentStudent((Student) entity);
						});
					} catch (IOException e) {
						e.printStackTrace();
						Alerts.showAlert("IOException", "Erro ao exibir tela prncipal para sair", e.getMessage(), AlertType.ERROR);
					}
				}
			}
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR);
		}
	}
	
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void setPersonEntity(Person entity) {
		this.entity = entity;
		updateFormData();
		if(entity.getId() != null) {
			btnFindRegistry.setVisible(false);
			HBoxInformations.setVisible(true);
			btnSave.setVisible(true);
		} else {
			btnFindRegistry.setVisible(true);
			HBoxRegistryInformations.setVisible(true);
		}
	}

	public void setStudentDao(StudentDao studentDao) {
		this.studentDao = studentDao;
	}
	
	public void setMainView(MainViewController mainView){
		this.mainView = mainView;
	}
	
	public void setInfoStudentController(InfoStudentController infoStudentController) {
		this.infoStudentController = infoStudentController;
	}
	
	public void updateFormData() {
		//comboBoxRegisteredBy
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		if(entity.getSendEmail() != null) {
			checkBoxSendEmail.setSelected(entity.getSendEmail());
		}
		textCPF.setText(entity.getCpf());
		textRG.setText(entity.getRg());
		textNeighborhood.setText(entity.getNeighborhood());
		textAdress.setText(entity.getAdress());
		textAdressReference.setText(entity.getAdressReference());
		textCity.setText(entity.getCity());
		textUF.setText(entity.getUf());
		textAreaObservation.setText(entity.getObservation());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
		}
		if(entity.getDateRegistry() != null) {
			textDateRegistry.setText(sdf.format(entity.getDateRegistry()));
		}
		if (this.entity.getContacts() != null) {
			contactsList = FXCollections.observableArrayList(this.entity.getContacts());
			tableContacts.setItems(contactsList);
		}
		// Only student have status
		if(entity instanceof Student && ((Student) entity).getStatus() != null) {
			comboBoxStatus.getSelectionModel().select(StudentStatusEnum.fromString(((Student) entity).getStatus()));
		}
		//ComboBox's
		if(entity.getGender() != null) {
			comboBoxGender.getSelectionModel().select(GenderEnum.fromString(entity.getGender()));
		}
		if(entity.getCivilStatus() != null) {
			comboBoxCivilStatus.getSelectionModel().select(CivilStatusEnum.fromFullCivilStatus(entity.getCivilStatus()));
		}		
		
	}
	
	public void getFormData() {
		// comboBoxRegisteredBy
		entity.setName(textName.getText().trim());
		entity.setEmail(textEmail.getText());
		entity.setSendEmail(checkBoxSendEmail.isSelected());
		entity.setCpf(Constraints.onlyDigitsValue(textCPF));
		entity.setRg(Constraints.onlyDigitsValue(textRG));
		entity.setNeighborhood(textNeighborhood.getText());
		entity.setAdress(textAdress.getText());
		entity.setAdressReference(textAdressReference.getText());
		entity.setCity(textCity.getText());
		entity.setUf(textUF.getText());
		entity.setObservation(textAreaObservation.getText());
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if(textBirthDate.getText().length() > 0) {
				entity.setDateBirth(sdf.parse(textBirthDate.getText()));
			}
			if(textDateRegistry.getText().length() > 0) {
				entity.setDateRegistry(sdf.parse(textDateRegistry.getText()));
			}
		} catch (ParseException e) {
		}
		// Only student have status
		if (entity instanceof Student) {
			((Student) entity).setStatus(comboBoxStatus.getSelectionModel().getSelectedItem().toString());
		}
		// Combo Box's
		entity.setGender(comboBoxGender.getSelectionModel().getSelectedItem().getfullGender());
		entity.setCivilStatus(comboBoxCivilStatus.getSelectionModel().getSelectedItem().getFullCivilStatus());
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
	
	private synchronized <T> void loadView(String FXMLPath, Stage parentStage, String windowTitle,
			boolean resizable, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath));
			Parent parent = loader.load();
			Stage dialogStage = new Stage();
			dialogStage.setTitle(windowTitle);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(parentStage);
			dialogStage.setResizable(resizable);
			
			Scene scene = new Scene(parent);
			scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());			
			dialogStage.setScene(scene);
			
			T controller = loader.getController();
			initializingAction.accept(controller);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR);
		} catch(IllegalStateException e) {
			Alerts.showAlert("IllegalStateException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR);
		}
	}	

}
