package gui;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.validation.RegexValidator;
import com.jfoenix.validation.RequiredFieldValidator;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import gui.util.Validators;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import model.dao.CollaboratorDao;
import model.dao.PostsDao;
import model.entites.Collaborator;
import sharedData.Globe;

public class UsersNewController implements Initializable {
	
	@FXML private JFXTextField textName;
	@FXML private JFXTextField textInitials;
	@FXML private JFXTextField textCPF;
	@FXML private JFXComboBox<GenderEnum> comboBoxGender;
	@FXML private JFXTextField textBirthDate;
	@FXML private JFXComboBox<CivilStatusEnum> comboBoxCivilStatus;
	@FXML private JFXTextField textRG;
	@FXML private JFXTextField textEmail;
	@FXML private JFXTextField textAdress;
	@FXML private JFXTextField textNeighborhood;
	@FXML private JFXTextField textCity;
	@FXML private JFXTextField textUF;
	@FXML private JFXTextField textAdressReference;
	@FXML private JFXComboBox<String> comboBoxPost;
	@FXML private JFXTextField textContactNumber;
	@FXML private HBox HBoxLoginInfo;
	@FXML private JFXTextField textUserLogin;
	@FXML private JFXTextField textUserPassword;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;

	private Collaborator collaborator;
	private CollaboratorDao collaboratorDao;
	private UsersController usersController;
	private Collaborator currentUser;
	private boolean isAbleToChangeLoginInfo;

	private final String DEFAULT_CITY = "Lapa";
	private final String DEFAULT_UF = "PR";

	public void initialize(URL url, ResourceBundle resource) {
		// Set requiredFields and Constraints
		initializeFields();
		// Set default values
		setDefaultValuesToFields();
		// Define entities daos
		defineEntitiesDaos();
		currentUser = Globe.getGlobe().getItem(Collaborator.class, "currentUser");
	}
	
	// Called from another controller
	public void setCollaborator(Collaborator collaborator) {
		this.collaborator = collaborator;
		// Show Login infos if collaborator is the current user or if doesn't is in db
		if (collaborator == currentUser || collaborator.getId() == null) {
			isAbleToChangeLoginInfo = true;
			HBoxLoginInfo.setVisible(true);
		} else {
			HBoxLoginInfo.setVisible(false);
		}
		if(collaborator.getId() != null) {
			// put collaborator data in fields of UI
			updateFormData();
		}
	}
	
	public void setUsersController(UsersController usersController) {
		this.usersController = usersController;
	}

	public void handleBtnCancel(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	public void handleBtnSave(ActionEvent event) {
		// Verify if the dao of collaborator is set, if isn't will throw a exception
		if (collaboratorDao == null || collaboratorDao == null) {
			throw new IllegalStateException("CollaboratorDao dao is null");
		}
		try {
			// check if fields is valid, we have theses situations to stop this method:
			// 1- if cpf, name, initials, user login or user password aren't valide; or
			// 3- if birthDate isn't null and has something and isn't valide; or
			// 4- if email isn't null and has something and isn't valide
			if (!textName.validate() || !textInitials.validate() || !textCPF.validate() || !textUserLogin.validate() || !textUserPassword.validate()
					|| (textRG.getText() != null && textRG.getText().length() > 0 && !textRG.validate())
					|| (textBirthDate.getText() != null && textBirthDate.getText().length() > 0
							&& !textBirthDate.validate())
					|| (textEmail.getText() != null && textEmail.getText().length() > 0 && !textEmail.validate())) {
				return;
			}
			if(isAbleToChangeLoginInfo) {
				// check if doens't exist a user already using the user login name
				String userLogin = textUserLogin.getText();
				try {
				Query query = DBFactory.getConnection().createQuery("select 1 from Colaborador c where c.userLogin = :user AND c.userLogin != :currentUser");
				query.setParameter("user", userLogin);
				query.setParameter("currentUser", collaborator.getUserLogin());
				boolean alreadyExists = (query.getSingleResult() != null);
				if(alreadyExists) {
					Alerts.showAlert("ERRO", "Login já em uso", "Alguém já está usando esse login", AlertType.ERROR,
							Utils.currentStage(event));
					return;
				}
				} catch(NoResultException e) {
					System.out.println("Ok! Doenst exists any user with this login");
				}  catch(Exception e) {
					System.out.println("Some error have occured");
					e.printStackTrace();
				}
			}
			// Get data from UI and put in collaborator
			getFormData();
			// save Data
			if(collaborator.getId() != null) {
				collaboratorDao.update(collaborator);
			} else {
				collaboratorDao.insert(collaborator);
			}
			// update users screen
			if (this.usersController != null) {
				this.usersController.onDataChanged();
			}
			// Finally, if everything occurs fine, we close this form
			Utils.currentStage(event).close();
		} catch (DbException e) {
			e.printStackTrace();
			Alerts.showAlert("DbException", "Erro ao salvar as informações", e.getMessage(), AlertType.ERROR,
					Utils.currentStage(event));
		}
	}
	
	private void initializeFields() {
		RequiredFieldValidator requiredValidator = Validators.getRequiredFieldValidator();
		// user login and password: required
		textUserLogin.setValidators(requiredValidator);
		textUserPassword.setValidators(requiredValidator);
		// Name: always in UpperCase and required
		textName.setValidators(requiredValidator);
		// initials: required
		textInitials.setValidators(requiredValidator);
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
		// Date Validator: birthDate
		RegexValidator dateValidator = new RegexValidator("Inválido");
		dateValidator.setRegexPattern("^\\d{1,2}\\/\\d{1,2}\\/\\d{4}$");
		textBirthDate.setValidators(dateValidator);
		// Email Validator
		RegexValidator emailValidator = new RegexValidator("Email inválido");
		emailValidator.setRegexPattern("^(.+)@(.+)$");
		textEmail.setValidators(emailValidator);
		Constraints.setTextFieldAlwaysLowerCase(textEmail);
		// Max length for fields
		Constraints.setTextFieldMaxLength(textUF, 2);
		Constraints.setTextFieldAlwaysUpperCase(textUF);
		Constraints.setTextFieldMaxLength(textName, 50);
		Constraints.setTextFieldAlwaysUpperCase(textName);
		Constraints.setTextFieldMaxLength(textInitials, 30);
		Constraints.setTextFieldMaxLength(textEmail, 50);
		Constraints.setTextFieldNoWhiteSpace(textEmail);
		Constraints.setTextFieldMaxLength(textAdress, 50);
		Constraints.setTextFieldMaxLength(textNeighborhood, 50);
		Constraints.setTextFieldMaxLength(textAdressReference, 50);
		Constraints.setTextFieldMaxLength(textContactNumber, 50);
		Constraints.setTextFieldMaxLength(textCity, 50);
		Constraints.setTextFieldMaxLength(textBirthDate, 10);
		Constraints.setTextFieldMaxLength(textUserLogin, 30);
		Constraints.setTextFieldMaxLength(textUserPassword, 30);
		// ComboBox: civilStatus, gender, post
		comboBoxCivilStatus.getItems().addAll(CivilStatusEnum.values());
		comboBoxGender.getItems().addAll(GenderEnum.values());
		try {
			// Get all initials from the collaborators in db
			List<String> posts = new PostsDao(DBFactory.getConnection()).findAllPosts();
			comboBoxPost.getItems().addAll(posts);
		} catch (DbException e) {
			e.printStackTrace();
		}
	}
	
	private void setDefaultValuesToFields(){
		comboBoxPost.getSelectionModel().selectFirst();
		comboBoxCivilStatus.getSelectionModel().selectFirst();
		comboBoxGender.getSelectionModel().selectFirst();
		textCity.setText(DEFAULT_CITY);
		textUF.setText(DEFAULT_UF);
	}
	
	private void defineEntitiesDaos() {
		collaboratorDao = new CollaboratorDao(DBFactory.getConnection());
	}
	
	public void updateFormData() {
		textName.setText(collaborator.getName());
		textInitials.setText(collaborator.getInitials());
		textEmail.setText(collaborator.getEmail());
		textCPF.setText(collaborator.getCpf());
		textRG.setText(collaborator.getRg());
		textNeighborhood.setText(collaborator.getNeighborhood());
		textAdress.setText(collaborator.getAdress());
		textAdressReference.setText(collaborator.getAdressReference());
		textCity.setText(collaborator.getCity());
		textUF.setText(collaborator.getUf());
		textContactNumber.setText(collaborator.getContactNumber());
		// birthDate
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (collaborator.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(collaborator.getDateBirth()));
		}
		// ComboBox's
		if (collaborator.getGender() != null) {
			comboBoxGender.getSelectionModel().select(GenderEnum.fromString(collaborator.getGender()));
		}
		if (collaborator.getCivilStatus() != null) {
			comboBoxCivilStatus.getSelectionModel()
					.select(CivilStatusEnum.fromFullCivilStatus(collaborator.getCivilStatus()));
		}
		if(collaborator.getPost() != null) {
			// Check if the post of the collaborator is already in the comboBox,
			// If the post isn't in, them we put inside and select that
			if(comboBoxPost.getItems().contains(collaborator.getPost())) {
				comboBoxPost.getSelectionModel().select(collaborator.getPost());
			} else {
				comboBoxPost.getItems().add(collaborator.getPost());;
				comboBoxPost.getSelectionModel().select(collaborator.getPost());
			}
		}
		textUserLogin.setText(collaborator.getUserLogin());
		textUserPassword.setText(collaborator.getPasswordLogin());
	}
	
	public void getFormData() {
		collaborator.setName(textName.getText().trim());
		collaborator.setInitials(textInitials.getText().trim());
		collaborator.setEmail(textEmail.getText());
		collaborator.setCpf(Constraints.getOnlyDigitsValue(textCPF));
		collaborator.setRg(Constraints.getOnlyDigitsValue(textRG));
		collaborator.setNeighborhood(textNeighborhood.getText());
		collaborator.setAdress(textAdress.getText());
		collaborator.setAdressReference(textAdressReference.getText());
		collaborator.setCity(textCity.getText());
		collaborator.setUf(textUF.getText());
		collaborator.setContactNumber(textContactNumber.getText());
		// birthDate
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		try {
			if (textBirthDate.getText().length() > 0) {
				collaborator.setDateBirth(sdf.parse(textBirthDate.getText() + " 00:00"));
			}
		} catch (ParseException e) {
			System.out.println("======== Some error has ocurred while parsing dates from collaborator to form");
		}
		// Combo Box's
		collaborator.setGender(comboBoxGender.getSelectionModel().getSelectedItem().getfullGender());
		collaborator.setCivilStatus(comboBoxCivilStatus.getSelectionModel().getSelectedItem().getFullCivilStatus());
		collaborator.setPost(comboBoxPost.getSelectionModel().getSelectedItem());
		// Login infos
		collaborator.setUserLogin(textUserLogin.getText());
		collaborator.setPasswordLogin(textUserPassword.getText());
	}
	
}
