package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import db.DBUtil;
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
import model.dao.CollaboratorDao;
import model.dao.ResponsibleDao;
import model.dao.ResponsibleStudentDao;
import model.dao.StudentDao;
import model.entites.Collaborator;
import model.entites.Person;
import model.entites.Responsible;
import model.entites.ResponsibleStudent;
import model.entites.Student;
import model.entites.util.PersonUtils;
import sharedData.Globe;

public class PersonFormController implements Initializable {
	// Search Bar
	@FXML private JFXTextField textCPF;
	@FXML private JFXTextField textName;
	@FXML private JFXButton btnFindRegistry;
	@FXML private Label labelFindRegistryResponse;
	// Registry Informations
	@FXML private HBox HBoxRegistryInformations;
	@FXML private JFXComboBox<String> comboBoxRegisteredBy;
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
	@FXML private JFXTextField textRelationship;
	// Buttons
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	@FXML private JFXButton btnWithoutCPF;

	// This form can be used to add/edit Students and Responsibles
	private Person entity;
	private Student studentOfResponsible;
	private StudentDao studentDao;
	private ResponsibleDao responsibleDao;
	// flag to know if we have get person data from a different table of the instance of entity
	private boolean isEntityFromAnotherTable;

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
		HBoxInformations.setVisible(false);
		btnSave.setVisible(false);
		textRelationship.setVisible(false);
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
		// Define entities daos
		defineEntitiesDaos();
	}
	
	public Person getEntity() {
		return this.entity;
	}
	
	public StudentDao getStudentDao() {
		return this.studentDao;
	}
	public ResponsibleDao getResponsibleDao() {
		return this.responsibleDao;
	}

	// Called from another controllers
	public void setPersonEntity(Person entity) {
		this.entity = entity;
		// put entity data in fields of UI
		updateFormData();
		// Hidden new registry message about CPF if the person already is in db
		if (entity.getId() != null) {
			btnWithoutCPF.setVisible(false);
			labelFindRegistryResponse.setVisible(false);
		}
	}
	
	public void setisEntityFromAnotherTable(boolean isEntityFromAnotherTable) {
		this.isEntityFromAnotherTable = isEntityFromAnotherTable;
	}

	public void setStudentOfResponsible(Student studentOfResponsible) {
		// hidden option to change student status
		comboBoxStatus.setVisible(false);
		this.studentOfResponsible = studentOfResponsible;
		// Make required the relationship beetwen them
		RequiredFieldValidator requiredValidator = new RequiredFieldValidator();
		requiredValidator.setMessage("Campo necessário");
		textRelationship.setValidators(requiredValidator);
		textRelationship.setVisible(true);
		// Set the relationship to field
		if (entity != null) {
			Responsible responsible = (Responsible) entity;
			String relationship = responsible.getRelationship(studentOfResponsible);
			textRelationship.setText(relationship);
		}
	}

	// Called from another controller
	// We will need the infoStudentController to return to that screen if user saves
	// the Person
	public void setInfoStudentController(InfoStudentController infoStudentController) {
		this.infoStudentController = infoStudentController;
	}

	// This will verify if we need a student or resposible dao
	private void defineEntitiesDaos() {
		// Try to get dao from Globe, if he doens't find then
		// instantiate a new and add to Globe
		if (studentDao == null) {
			studentDao = Globe.getGlobe().getItem(StudentDao.class, "studentDao");
			if (studentDao == null) {
				studentDao = new StudentDao(DBFactory.getConnection());
				Globe.getGlobe().putItem("studentDao", studentDao);
			}
		}
		if (responsibleDao == null) {
			responsibleDao = Globe.getGlobe().getItem(ResponsibleDao.class, "responsibleDao");
			if (responsibleDao == null) {
				responsibleDao = new ResponsibleDao(DBFactory.getConnection());
				Globe.getGlobe().putItem("responsibleDao", responsibleDao);
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
		// Date Validator: birthDate and dateRegistry (required)
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textBirthDate.setValidators(dateValidator);
		textDateRegistry.setValidators(dateValidator);
		// Email Validator
		RegexValidator emailValidator = new RegexValidator("Email inválido");
		emailValidator.setRegexPattern("^(.+)@(.+)$");
		textEmail.setValidators(emailValidator);
		Constraints.setTextFieldAlwaysLowerCase(textEmail);
		// textDateRegistry.setValidators(requiredValidator, dateValidator); ///
		// ========= I still have to work in dateRegistry
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
		// Responsible relationship
		Constraints.setTextFieldMaxLength(textRelationship, 30);
		Constraints.setTextFieldAlwaysUpperCase(textRelationship);
		// ComboBox: status, civilStatus, gender, registered by
		comboBoxStatus.getItems().addAll(StudentStatusEnum.values());
		comboBoxCivilStatus.getItems().addAll(CivilStatusEnum.values());
		comboBoxGender.getItems().addAll(GenderEnum.values());
		try {
			// Get all initials from the collaborators in db
			List<String> collaboratorsInitials = new CollaboratorDao(DBFactory.getConnection()).findAllInitials();
			comboBoxRegisteredBy.getItems().addAll(collaboratorsInitials);
			// Select the current user logged
			String currentUser = Globe.getGlobe().getItem(Collaborator.class, "currentUser").getInitials();
			comboBoxRegisteredBy.getSelectionModel().select(currentUser);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	// ===============================================
	// == START OF METHODS TO HANDLE BUTTONS ACTION ==
	// ===============================================

	// Pre-registry without CPF
	public void handleBtnPreRegistry(ActionEvent event) {
		// Hidden button to pre-registry without CPF
		btnWithoutCPF.setVisible(false);
		// Show message to user remember to update the CPF in future
		labelFindRegistryResponse.setText("Será feito um pré-registro sem o CPF. NÃO SE ESQUEÇA de atualizar o CPF dessa pessoa no futuro.");
		new Shake(labelFindRegistryResponse).play();
		//  Remove all Validators from cpf, clear and disable field
		RegexValidator cpfValidator = new RegexValidator("Inválido");
		cpfValidator.setRegexPattern("^\\d{3}\\.\\d{3}\\.\\d{3}\\-\\d{2}$");
		textCPF.getValidators().clear();
		textCPF.setDisable(true);
		// Show name field
		textName.setVisible(true);
	}
	
	// Find Registry
	public void handleBtnFindRegistry(ActionEvent event) {
		// Check if cpf is valide
		if (!textCPF.validate()) {
			return;
		}
		findPersonRegistry(event);
		// I still need a method to find a Resposible
	}

	// Save entity
	public void handleBtnSave(ActionEvent event) {
		// Verify if the dao of entity is set, if isn't will throw a exception
		if (studentDao == null || responsibleDao == null) {
			throw new IllegalStateException("Entity dao is null");
		}
		try {
			// check if fields is valid, we have theses situations to stop this method:
			// 1- if cpf or name aren't valide; or
			// 2- if date registry isn't null and has something and isn't valide; or
			// 3- if birthDate isn't null and has something and isn't valide; or
			// 4- if email isn't null and has something and isn't valide
			if (!textCPF.validate() || !textName.validate() || !textDateRegistry.validate()
					|| (textRG.getText() != null && textRG.getText().length() > 0 && !textRG.validate())
					|| (textBirthDate.getText() != null && textBirthDate.getText().length() > 0 && !textBirthDate.validate())
					|| (textEmail.getText() != null && textEmail.getText().length() > 0 && !textEmail.validate())) {
				return;
			}
			// Check if registry date is after today
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				Date today = new Date();
				if (sdf.parse(textDateRegistry.getText()).compareTo(today) > 0) {
					Alerts.showAlert("Inválido", "A data de cadastro é posterior a data atual do computador.",
							"É impossível fazer um cadastro no futuro.", AlertType.ERROR);
					// stop the method
					return;
				}
				if (textBirthDate.getText() != null && textBirthDate.getText().length() > 0) {
					if (sdf.parse(textBirthDate.getText()).compareTo(today) > 0) {
						Alerts.showAlert("Inválido",
								"A data de nascimento do aluno é posterior a data atual do computador.",
								"É impossível fazer um cadastro de um aluno que ainda nem nasceu.", AlertType.ERROR);
						// stop the method
						return;
					}
				}
			} catch (ParseException e) {
				System.out.println("Erro durante conversão para verificar datas...");
				e.printStackTrace();
			}
			// If we have a student to make a relationship with a responsible, we check if
			// the textFieldRelationship field is valid
			if (studentOfResponsible != null && !textRelationship.validate()) {
				return;
			}
			// Get data from UI and put in entity
			getFormData();
			saveDataInDB();
			saveRelationship();
			// Redirect to info of student
			if (this.infoStudentController != null) {
				// if already has an infoStudentController we have come from a info Student
				// screen,
				// so we just update that screen
				this.infoStudentController.onDataChanged();
			} else {
				// Get mainViewController from Globe
				MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
				if (entity instanceof Student) {
					mainView.setContent(FXMLPath.INFO_STUDENT, (InfoStudentController controller) -> {
						controller.setReturn(FXMLPath.LIST_STUDENTS, "Alunos");
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

	// Auxiliar method to save person entity
	private void saveDataInDB() throws DbException {
		// Person with same CPF with same cpf to also update
		Person personOtherTable;
		// Save data in db
		if (entity instanceof Student) {
			// If doesn't have an Id, so isn't in database
			// Otherwhise already is in database, so we just update
			if (entity.getId() == null) {
				studentDao.insert((Student) entity);
			} else {
				// Edit date
				entity.setDateLastRegistryEdit(new Date());
				studentDao.update((Student) entity);
			}
			// We try to find in the other table a registry with the same cpf
			personOtherTable = responsibleDao.findByCPF(entity.getCpf());
			// Update date
			if(personOtherTable != null) {
				PersonUtils.parseDataFromStudentToResponsible((Student) entity, (Responsible) personOtherTable);
				personOtherTable.setDateLastRegistryEdit(new Date());
				responsibleDao.update((Responsible) personOtherTable);
			}
		}
		if (entity instanceof Responsible) {
			// If doesn't have an Id, so isn't in database
			// Otherwhise already is in database, so we just update
			if (entity.getId() == null) {
				responsibleDao.insert((Responsible) entity);
			} else {
				// Edit date
				entity.setDateLastRegistryEdit(new Date());
				responsibleDao.update((Responsible) entity);
			}
			// We try to find in the other table a registry with the same cpf
			personOtherTable = studentDao.findByCPF(entity.getCpf());
			// Update date
			if (personOtherTable != null) {
				PersonUtils.parseDataFromResponsibleToStudent((Responsible) entity, (Student) personOtherTable);
				personOtherTable.setDateLastRegistryEdit(new Date());
				studentDao.update((Student) personOtherTable);
			}
		}
	}

	// Auxiliar method
	private void saveRelationship() throws DbException {
		if (studentOfResponsible != null) {
			// DownCast to Responsible
			Responsible responsible = (Responsible) entity;
			// Verify if already exists a relationship beetwen the responsible and the
			// student
			List<ResponsibleStudent> listSearch = null;
			listSearch = responsible.getStudents().stream()
					.filter(rs -> rs.getStudent().getId() == studentOfResponsible.getId()).collect(Collectors.toList());
			// Save/Update date
			ResponsibleStudentDao rsDao = new ResponsibleStudentDao(DBFactory.getConnection());
			ResponsibleStudent rs = null;
			// If have any object in list Search, so already exist a relation created
			String relationship = textRelationship.getText();
			if (listSearch.size() > 0) {
				rs = listSearch.get(0);
				rs.setRelationship(relationship);
				rsDao.update(rs);
			} else {
				rs = new ResponsibleStudent();
				rs.setStudent(studentOfResponsible);
				rs.setResponsible(responsible);
				rs.setRelationship(relationship);
				rsDao.insert(rs);
			}
			// Reflesh data of persons to get the relationship
			DBUtil.refleshData(studentOfResponsible);
			DBUtil.refleshData(responsible);
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

	// Method to find a existinG Person registry
	private void findPersonRegistry(ActionEvent event) {
		// Hidden button to pre-registry without CPF and the label with some informations
		btnWithoutCPF.setVisible(false);
		labelFindRegistryResponse.setVisible(false);
		Person person = null;
		try {
			// Try to find a Person with same cpf informed
			if (textCPF.getText() != null && textCPF.getText().length() > 0) {
				if (entity instanceof Student) {
					person = studentDao.findByCPF(Constraints.getOnlyDigitsValue(textCPF));
				}
				if (entity instanceof Responsible) {
					person = responsibleDao.findByCPF(Constraints.getOnlyDigitsValue(textCPF));
				}
				if (person != null) {
					entity = person;
					// update fields in UI according the student get from database
					updateFormData();
					// Message to inform that data have come from database using the CPF
					labelFindRegistryResponse.setText(
							"Registro encontrado a partir do CPF. Confira as informações e atualize se necessário, em seguida clique em Salvar");
					labelFindRegistryResponse.setVisible(true);
					// Animations to get attention
					new ZoomIn(HBoxInformations).play();
					new Shake(labelFindRegistryResponse).play();
					// Stop the method because we find a person
					return;
				} else {
					// We try to find in the other table, for example, if the entity is a student and we don't
					// find a registry with the cpf, so we will try to find a resposible with the cpf
					if (entity instanceof Student) {
						person = responsibleDao.findByCPF(Constraints.getOnlyDigitsValue(textCPF));
					}
					if (entity instanceof Responsible) {
						person = studentDao.findByCPF(Constraints.getOnlyDigitsValue(textCPF));
					}
					// if he have find a registry in the other table we have to copy the data to the entity
					if(person != null) {
						isEntityFromAnotherTable = true;
						String dataComeFrom = "";
						if(person instanceof Student && entity instanceof Responsible) {
							PersonUtils.parseDataFromStudentToResponsible((Student) person, (Responsible) entity);
							dataComeFrom = "alunos";
						}
						if(person instanceof Responsible && entity instanceof Student) {
							PersonUtils.parseDataFromResponsibleToStudent((Responsible) person, (Student) entity);
							dataComeFrom = "responsáveis";
						}
						// update fields in UI according the student get from database
						updateFormData();
						// Message to inform that data have come from other database using the CPF
						labelFindRegistryResponse.setText(
								"Registro encontrado nos " + dataComeFrom + " a partir do CPF. Confira as informações e atualize se necessário, em seguida clique em Salvar");
						labelFindRegistryResponse.setVisible(true);
						// Animations to get attention
						new ZoomIn(HBoxInformations).play();
						new Shake(labelFindRegistryResponse).play();
						// Stop the method because we find a person
						return;
					}
				}
				labelFindRegistryResponse
						.setText("Nenhum registro com esse CPF. Insira o nome completo e clique em Procurar Registro");
				labelFindRegistryResponse.setVisible(true);
				new Shake(labelFindRegistryResponse).play();
			}
			// Check if textName field is hidden, if is show him and stop this method
			// because we find the person
			if (!textName.isVisible()) {
				textName.setVisible(true);
				textName.requestFocus();
				return;
			}
			// If we are here he doesn't find by the CPF, so we will try to find some similar name
			// Check if name is valide
			if (!textName.validate()) {
				return;
			}
			// Try to find students/responsibles with name like the one informed
			List<Person> studentsNameLike = null;
			List<Person> responsiblesNameLike = null;
				// we will cast student to super class Person
			studentsNameLike = studentDao.findAllWithNameLike(textName.getText().trim()).stream()
						.map(s -> (Person) s).collect(Collectors.toList());
				// we will cast responsibles to super class Person
			responsiblesNameLike = responsibleDao.findAllWithNameLike(textName.getText().trim()).stream()
						.map(s -> (Person) s).collect(Collectors.toList());
			// unites students and responsibles
			List<Person> personsNameLike = new ArrayList<>();
			personsNameLike.addAll(studentsNameLike);
			personsNameLike.addAll(responsiblesNameLike);
			if (personsNameLike.size() > 0) {
				// Sort by name the results of the list
				personsNameLike.sort((p1, p2) -> p1.getName().toUpperCase().compareTo(p2.getName().toUpperCase()));
				// Filter only distinct peopless
				personsNameLike = personsNameLike.stream()
						.filter(PersonUtils.distinctByKey(Person::getName))
						.collect(Collectors.toList());
				// hidden text informations
				labelFindRegistryResponse.setText("");
				// if he have find one we will cast student to super class Person, and show in a
				// new screen to user see if is one of them
				List<Person> peopleList = personsNameLike;
				Utils.loadView(this, true, FXMLPath.PERSON_FORM_FIND_REGISTRY, Utils.currentStage(event),
						"Registros semelhantes", false, (PersonFormFindRegistryController controller) -> {
							controller.setPeopleList(peopleList);
							controller.setPersonFormController(this);
						});
				// Stop the method
				return;
			}
		} catch (DbException e) {
			// Show a alert message if something went wrong
			Alerts.showAlert("DBException", e.getMessage(), "Houve um problema ao procurar a existência do cadastro",
					AlertType.ERROR);
			e.printStackTrace();
		}
		// If we get here, doesn't exist a person with the same CPF even a similar name
		// So, the user is creating a new person
		labelFindRegistryResponse
				.setText("A pessoa ainda não possui registro. Insira as informações para o novo cadastro.");
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
		// If the user is adding a new Responsible, so We copy some informations from
		// student to the form
		if (studentOfResponsible != null) {
			textAdress.setText(studentOfResponsible.getAdress());
			textNeighborhood.setText(studentOfResponsible.getNeighborhood());
			textCity.setText(studentOfResponsible.getCity());
			textUF.setText(studentOfResponsible.getUf());
			textAdressReference.setText(studentOfResponsible.getAdressReference());
		}
	}

	// Update form Data according the data in entity
	public void updateFormData() {
		// If entity has an Id, so he already is in databaseso, we show Informations
		// and button to save, and hidden button to find the registry in db
		if (entity.getId() != null || isEntityFromAnotherTable) {
			textName.setVisible(true);
			btnFindRegistry.setVisible(false);
			HBoxInformations.setVisible(true);
			textCPF.setDisable(true);
			btnSave.setVisible(true);
			// show registeredBy, dateRegistry if the person has come from another table
			if(isEntityFromAnotherTable) {
				comboBoxRegisteredBy.setVisible(true);
				textDateRegistry.setVisible(true);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				textDateRegistry.setText(sdf.format(new Date()));
			}
			// check if the person has a registry date and the person who makes the register
			if(entity.getRegisteredBy() == null) {
				comboBoxRegisteredBy.setVisible(true);
			}
			if(entity.getDateRegistry() == null) {
				textDateRegistry.setVisible(true);
				SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
				textDateRegistry.setText(sdf.format(new Date()));
			}
		} else {
			// If entity hasn't an id, so he aren't in databse. So, we
			// show button to find the registry and registryInformations box
			btnFindRegistry.setVisible(true);
			HBoxRegistryInformations.setVisible(true);
			// only students haven status
			if (entity instanceof Student) {
				comboBoxStatus.setVisible(true);
			} else {
				comboBoxStatus.setVisible(false);
			}
		}
		textName.setText(entity.getName());
		textEmail.setText(entity.getEmail());
		if (entity.getSendEmail() != null) {
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
		if (entity.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(entity.getDateBirth()));
		}
		if (entity.getDateRegistry() != null) {
			textDateRegistry.setText(sdf.format(entity.getDateRegistry()));
		}
		// ComboBox's
		if (entity.getGender() != null) {
			comboBoxGender.getSelectionModel().select(GenderEnum.fromString(entity.getGender()));
		}
		if (entity.getCivilStatus() != null) {
			comboBoxCivilStatus.getSelectionModel()
					.select(CivilStatusEnum.fromFullCivilStatus(entity.getCivilStatus()));
		}
		if(entity.getRegisteredBy() != null) {
			// Check if the name of the person who register this entity is already in the comboBox,
			// If the name isn't in, them we put inside and select that
			if(comboBoxRegisteredBy.getItems().contains(entity.getRegisteredBy())) {
				comboBoxRegisteredBy.getSelectionModel().select(entity.getRegisteredBy());
			} else {
				comboBoxRegisteredBy.getItems().add(entity.getRegisteredBy());;
				comboBoxRegisteredBy.getSelectionModel().select(entity.getRegisteredBy());
			}
		}
		// Only student have status, so we set if the status of student isn't null
		if (entity instanceof Student && ((Student) entity).getStatus() != null) {
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			if (textBirthDate.getText().length() > 0) {
				entity.setDateBirth(sdf.parse(textBirthDate.getText() + " 00:00"));
			}
			if (textDateRegistry.getText().length() > 0) {
				SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
				Date now = new Date();
				entity.setDateRegistry(sdf.parse(textDateRegistry.getText() + " " + sdf2.format(now)));
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
		// Colaborrator who makes the registry
		entity.setRegisteredBy(comboBoxRegisteredBy.getSelectionModel().getSelectedItem());
	}

}