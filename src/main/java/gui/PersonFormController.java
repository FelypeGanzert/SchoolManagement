package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
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
import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.FXMLPath;
import gui.util.Utils;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import gui.util.enums.StudentStatusEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.dao.StudentDao;
import model.entites.Collaborator;
import model.entites.Person;
import model.entites.Student;
import sharedData.Globe;

public class PersonFormController implements Initializable {
	// Search Bar
	@FXML private JFXTextField textCPF;
	@FXML private JFXTextField textName;
	@FXML private JFXButton btnFindRegistry;
	@FXML private Label labelFindRegistryResponse;
	// Registry Informations
	@FXML private HBox HBoxRegistryInformations;
	@FXML private JFXComboBox<Collaborator> comboBoxRegisteredBy;
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
	// Buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	// This form can be used to add/edit Students and Responsibles
	private Person entity;
	private StudentDao studentDao;
	
	private InfoStudentController infoStudentController;
	
	private final String DEFAULT_CITY = "Lapa";
	private final String DEFAULT_UF = "PR";
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Hidden some fields by default
		textName.setVisible(false);
		btnFindRegistry.setVisible(false);
		HBoxRegistryInformations.setVisible(false);
		comboBoxRegisteredBy.setVisible(false);
		textDateRegistry.setVisible(false);
		labelFindRegistryResponse.setVisible(false);
		HBoxInformations.setVisible(false);
		btnSave.setVisible(false);
		// Define by default disabled the option to allow to send email
		// and let able only when have some email
		checkBoxSendEmail.setDisable(true);
		textEmail.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > 0) {
				checkBoxSendEmail.setDisable(false);
			} else {
				checkBoxSendEmail.setSelected(false);
				checkBoxSendEmail.setDisable(true);
			}
		});
		// Set requiredFields and Constraints
		initializeFields();
		// Set default values
		setDefaultValuesToFields();
	}
	
	// Called from another controllers
	public void setPersonEntity(Person entity) {
		this.entity = entity;
		// Set dao according the instance of entity
		defineEntityDao();
		// put entity data in fields of UI
		updateFormData();
	}
	
	// Called from another controller
	// We will need the infoStudentController to return to that screen if user saves the Person
	public void setInfoStudentController(InfoStudentController infoStudentController) {
		this.infoStudentController = infoStudentController;
	}
	
	// This will verify if we need a student or resposible dao
	private void defineEntityDao() {
		// Try to get dao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		if (entity instanceof Student) {
			studentDao = Globe.getGlobe().getItem(StudentDao.class, "studentDao");
			if (studentDao == null) {
				studentDao = new StudentDao(DBFactory.getConnection());
				Globe.getGlobe().putItem("studentDao", studentDao);
			}
		}
	}
	
	// Set default values to Form
	private void setDefaultValuesToFields() {
		comboBoxStatus.getSelectionModel().selectFirst();
		comboBoxCivilStatus.getSelectionModel().selectFirst();
		comboBoxGender.getSelectionModel().selectFirst();
		textCity.setText(DEFAULT_CITY);
		textUF.setText(DEFAULT_UF);
	}

	// Set requiredFields and Constraints
	private void initializeFields() {
		// Create Required validator
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
		requiredValidator.setMessage("Campo necessário");
		// Name: always in UpperCase and required
		textName.setValidators(requiredValidator);
		// CPF: required
		Constraints.cpfAutoComplete(textCPF);
		RegexValidator cpfValidator = new RegexValidator("Inválido");
		cpfValidator.setRegexPattern("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$");
		textCPF.setValidators(requiredValidator, cpfValidator);
		// RG
		Constraints.rgAutoComplete(textRG);
		RegexValidator rgValidator = new RegexValidator("Inválido");
		rgValidator.setRegexPattern("^\\d{2}\\.\\d{3}\\.\\d{3}\\-\\d{1}$");
		textRG.setValidators(rgValidator);
		// Date Validator: birthDate and dateRegistry
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textBirthDate.setValidators(dateValidator);
		textDateRegistry.setValidators(dateValidator);
		// Email Validator
		RegexValidator emailValidator = new RegexValidator("Email inválido");
		emailValidator.setRegexPattern("^(.+)@(.+)$");
		textEmail.setValidators(emailValidator);
		Constraints.setTextFieldAlwaysLowerCase(textEmail);
		//textDateRegistry.setValidators(requiredValidator, dateValidator); /// ========= I still have to work in dateRegistry
		// Max length for fields
		Constraints.setTextFieldMaxLength(textUF, 2);
		Constraints.setTextFieldAlwaysUpperCase(textUF);
		Constraints.setTextFieldMaxLength(textName, 50);
		Constraints.setTextFieldAlwaysUpperCase(textName);
		Constraints.setTextFieldMaxLength(textEmail, 50);
		Constraints.setTextFieldNoWhiteSpace(textEmail);
		Constraints.setTextFieldMaxLength(textAdress, 50);
		Constraints.setTextFieldMaxLength(textNeighborhood, 50);
		Constraints.setTextFieldMaxLength(textAdressReference, 50);
		Constraints.setTextFieldMaxLength(textCity, 50);
		Constraints.setTextFieldMaxLength(textBirthDate, 10);
		Constraints.setTextFieldMaxLength(textDateRegistry, 10);
		// ComboBox: status, civilStatus, gender
		comboBoxStatus.getItems().addAll(StudentStatusEnum.values());
		comboBoxCivilStatus.getItems().addAll(CivilStatusEnum.values());
		comboBoxGender.getItems().addAll(GenderEnum.values());
	}
		
	// ===============================================
	// == START OF METHODS TO HANDLE BUTTONS ACTION ==
	// ===============================================
	
	// Find Registry
	public void handleBtnFindRegistry(ActionEvent event) {
		//Check if cpf is valide
		if(!textCPF.validate()) {
			return;
		}
		if(entity instanceof Student) {
			findStudentRegistry(event);
		}
		// I still need a method to find a Resposible
	}
	
	// Save entity
	public void handleBtnSave(ActionEvent event) {
		// Verify if the dao of entity is set, if isn't will throw a exception
		if(entity instanceof Student && studentDao == null) {
			throw new IllegalStateException("StudentDao is null");
		}
		// Get data from UI and put in entity
		getFormData();
		try {
			if(entity instanceof Student) {		
				// check if fields is valid, we have theses situations to stop this method:
				// 1- if cpf or name aren't valide; or
				// 2- if date registry isn't null and has something and isn't valide; or
				// 3- if birthDate isn't null and has something and isn't valide; or
				// 4- if email isn't null and has something and isn't valide
				if(!textCPF.validate() || !textName.validate() ||
						(textDateRegistry.getText()  != null && textDateRegistry.getText().length() > 0 && !textDateRegistry.validate()) || 
						(textRG.getText()  != null && textRG.getText().length() > 0 && !textRG.validate()) ||						
						(textBirthDate.getText()  != null && textBirthDate.getText().length() > 0 && !textBirthDate.validate()) || 
						(textEmail.getText()  != null && textEmail.getText().length() > 0 && !textEmail.validate())) {
					return;
				}
				// If doesn't have an Id, so isn't in database
				// Otherwhise already is in database, so we just update
				if (entity.getId() == null) {
					if(entity instanceof Student) {
						studentDao.insert((Student) entity);
					}
				} else {
					// Edit date
					entity.setDateLastRegistryEdit(new Date());
					if(entity instanceof Student) {
						studentDao.update((Student) entity);
					}
				}
				// Redirect to info of student
				if (this.infoStudentController != null) {
					// if already has an infoStudentController we have come from a info Student screen,
					// so we just update that screen
					this.infoStudentController.onDataChanged((Student) entity);
				} else {
					// Get mainViewController from Globe
					MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
					mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
						controller.setMainViewControllerAndReturnName(FXMLPath.LIST_STUDENTS, "Alunos");
						controller.setCurrentStudent((Student) entity);
					});
				}
			}
			// Finally, if everything occurs fine, we close this form
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR);
		}
	}
	
	// Cancel
	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}
	
	// ===============================================
	// === END OF METHODS TO HANDLE BUTTONS ACTION ===
	// ===============================================
		
	// =========== AUXILIAR METHODS ==================
	
	// Method to find a STUDENT registry
	private void findStudentRegistry(ActionEvent event) {
		Student student;
		try {
			// Try to find a Student with same cpf informed
			student = studentDao.findByCPF(Constraints.getOnlyDigitsValue(textCPF));
			if (student != null) {
				this.entity = student;
				// update fields in UI according the student get from database
				updateFormData();
				// Message to inform that data have come from database using the CPF
				labelFindRegistryResponse.setText("Registro encontrado a partir do CPF. Confira as informações e atualize se necessário, em seguida clique em Salvar");
				labelFindRegistryResponse.setVisible(true);
				// Animations to get attention
				new ZoomIn(HBoxInformations).play();
				new Shake(labelFindRegistryResponse).play();
				// Stop the method
				return;
			}
			labelFindRegistryResponse.setText("Nenhum registro com esse CPF. Insira o nome completo e clique em Procurar Registro");
			labelFindRegistryResponse.setVisible(true);
			new Shake(labelFindRegistryResponse).play();
			// Check if textName field is hidden, if is show him and stop this method
			if(!textName.isVisible()) {
				textName.setVisible(true);
				return;
			}
			// If we are here he doesn't find by the CPF, so we will try to find some similar name
			// Check if name is valide
			if(!textName.validate()) {
				return;
			}
			// Try to find students with name like the one informed
			List<Student> studentsNameLike = studentDao.findAllWithNameLike(textName.getText().trim());
			if(studentsNameLike.size() > 0) {
				// if he have find one we  will cast student to super class Person, and show in a new screen
				// to user see if is one of them
				List<Person> peopleList = studentsNameLike.stream().map(s -> (Person) s).collect(Collectors.toList());
				Utils.loadView(this.getClass(), true, FXMLPath.PERSON_FORM_FIND_REGISTRY, Utils.currentStage(event),
						"Registros semelhantes", false, (PersonFormFindRegistryController controller) -> {
							controller.setPeopleList(peopleList);
							controller.setPersonFormController(this);
						});
				// Stop the method
				return;
			}
		} catch (DbException e) {
			// Show a alert message if something went wrong
			Alerts.showAlert("DBException", e.getMessage(), "Houve um problema ao procurar a existência do cadastro", AlertType.ERROR);
			e.printStackTrace();
		}
		// If we get here, doesn't exist a person with the same CPF even a similar name
		// So, the user is creating a new person
		labelFindRegistryResponse.setText("A pessoa ainda não possui registro. Insira as informações para o novo cadastro.");
		labelFindRegistryResponse.setVisible(true);
		new Shake(labelFindRegistryResponse).play();
		addNewRegistry();
	}
	
	// Called in case of doesn't have find any correspondent registry in database
	// Or in case of the user realy want to create a new registry
	public void addNewRegistry() {
		// Disable cpf and name field to user doenst change this informations anymore
		textCPF.setDisable(true);
		textName.setDisable(true);
		btnFindRegistry.setVisible(false);
		// show registeredBy, dateRegistry, hBox Informations and button to save
		comboBoxRegisteredBy.setVisible(true);
		textDateRegistry.setVisible(true);
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		textDateRegistry.setText(sdf.format(new Date()));
		HBoxInformations.setVisible(true);
		// Animation to get attention
		new ZoomIn(HBoxInformations).play();
		btnSave.setVisible(true);
		setDefaultValuesToFields();
	}
	
	// Update form Data according the data in entity
	public void updateFormData() {
		// If entity has an Id, so he already is in databaseso, we show Informations
		// and button to save, and hidden button to find the registry in db
		if(entity.getId() != null) {
			textName.setVisible(true);
			btnFindRegistry.setVisible(false);
			HBoxInformations.setVisible(true);
			textCPF.setDisable(true);
			btnSave.setVisible(true);
		} else {
			// If entity hasn't an id, so he aren't in databse. So, we
			// show button to find the registry and registryInformations box
			btnFindRegistry.setVisible(true);
			HBoxRegistryInformations.setVisible(true);
			// only students haven status
			if(entity instanceof Student) {
				comboBoxStatus.setVisible(true);
			} else {
				comboBoxStatus.setVisible(false);
			}
		}
		//comboBoxRegisteredBy STILL HAVE TO BE IMPLEMENTED
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
		// birthDate and registryDate
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if(entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
		}
		if(entity.getDateRegistry() != null) {
			textDateRegistry.setText(sdf.format(entity.getDateRegistry()));
		}
		//ComboBox's
		if (entity.getGender() != null) {
			comboBoxGender.getSelectionModel().select(GenderEnum.fromString(entity.getGender()));
		}
		if (entity.getCivilStatus() != null) {
			comboBoxCivilStatus.getSelectionModel()
					.select(CivilStatusEnum.fromFullCivilStatus(entity.getCivilStatus()));
		}
		// Only student have status, so we set if the status of student isn't null
		if(entity instanceof Student && ((Student) entity).getStatus() != null) {
			comboBoxStatus.getSelectionModel().select(StudentStatusEnum.fromString(((Student) entity).getStatus()));
		}
	}
	
	// get form Data and put inside entity
	public void getFormData() {
		// comboBoxRegisteredBy STILL HAVE TO BE IMPLEMENTED
		entity.setName(textName.getText().trim());
		entity.setEmail(textEmail.getText());
		entity.setSendEmail(checkBoxSendEmail.isSelected());
		entity.setCpf(Constraints.getOnlyDigitsValue(textCPF));
		entity.setRg(Constraints.getOnlyDigitsValue(textRG));
		entity.setNeighborhood(textNeighborhood.getText());
		entity.setAdress(textAdress.getText());
		entity.setAdressReference(textAdressReference.getText());
		entity.setCity(textCity.getText());
		entity.setUf(textUF.getText());
		entity.setObservation(textAreaObservation.getText());
		// birthDate and registryDate
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			if(textBirthDate.getText().length() > 0) {
				entity.setDateBirth(sdf.parse(textBirthDate.getText()));
			}
			if(textDateRegistry.getText().length() > 0) {
				entity.setDateRegistry(sdf.parse(textDateRegistry.getText()));
			}
		} catch (ParseException e) {
			System.out.println("======== Some error has ocurred while parsing dates from entity to form");
		}
		// Combo Box's
		entity.setGender(comboBoxGender.getSelectionModel().getSelectedItem().getfullGender());
		entity.setCivilStatus(comboBoxCivilStatus.getSelectionModel().getSelectedItem().getFullCivilStatus());
		// Only student have status
		if (entity instanceof Student) {
			((Student) entity).setStatus(comboBoxStatus.getSelectionModel().getSelectedItem().toString());
		}
		
	}
	
}