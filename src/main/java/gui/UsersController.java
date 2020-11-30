package gui;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.enums.CivilStatusEnum;
import gui.util.enums.GenderEnum;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import model.dao.CollaboratorDao;
import model.entites.Collaborator;
import sharedData.Globe;

public class UsersController implements Initializable {


	@FXML private Label labelTotalCertificates;
	// table
	@FXML private TableView<Collaborator> tableCollaborators;
	@FXML private TableColumn<Collaborator, String> columnName;
	// collaborator (user) infos
	@FXML private TextField textName;
	@FXML private TextField textInitials;
	@FXML private TextField textCPF;
	@FXML private TextField textGender;
	@FXML private TextField textBirthDate;
	@FXML private TextField textAge;
	@FXML private TextField textCivilStatus;
	@FXML private TextField textRG;
	@FXML private TextField textEmail;
	@FXML private TextField textAdress;
	@FXML private TextField textNeighborhood;
	@FXML private TextField textAdressReference;
	@FXML private TextField textCity;
	@FXML private TextField textUF;
	@FXML private TextField textPost;
	@FXML private TextField textContact;
	@FXML private TextField textLogin;
	@FXML private TextField textPassword;
	@FXML private JFXButton btnChangeAutorizations;
	@FXML private JFXButton btnEdit;
	@FXML private JFXButton btnDelete;

	private CollaboratorDao collaboratorDao;
	private List<Collaborator> collaborators;
	private Collaborator currentUser;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initializeTable();
		addListeners();
		collaborators = new ArrayList<>();
		// get all collaborators from db
		collaboratorDao = new CollaboratorDao(DBFactory.getConnection());
		currentUser = Globe.getGlobe().getItem(Collaborator.class, "currentUser");
		getDataFromDB();
	}

	public void handleBtnAdd(ActionEvent event) {
		// load view Users New Collaborator Form and create a new student
		Utils.loadView(this, true, FXMLPath.USERS_NEW, Utils.currentStage(event), "Novo colaborador", false,
				(UsersNewController controller) -> {
					Collaborator collaborator = new Collaborator();
					controller.setCollaborator(collaborator);
					controller.setUsersController(this);
				});
	}

	public void handleBtnChangeAutorizations(ActionEvent event) {
		System.out.println("Clicked to change Autorizations");
	}

	public void handleBtnDelete(ActionEvent event) {
		Collaborator selectedCollaborator = tableCollaborators.getSelectionModel().getSelectedItem();
		if (selectedCollaborator != null) {
			// Confirmation to delete annotation;
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Deletar colaborador");
			alert.setHeaderText("Deletar " + selectedCollaborator.getName() + " ?");
			alert.initOwner(Utils.currentStage(event));
			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				try {
					collaboratorDao.deleteById(selectedCollaborator.getId());
					// remove annotation from student in memory and update Annotations list
					tableCollaborators.getItems().remove(selectedCollaborator);
					tableCollaborators.refresh();
				} catch (DbException e) {
					Alerts.showAlert("Erro ao deletar colaborador", "DbException", e.getMessage(), AlertType.ERROR,
							Utils.currentStage(event));
				}
			}
		}
	}

	public void handleBtnEdit(ActionEvent event) {
		// load view Users New Collaborator Form and create a new student
		Utils.loadView(this, true, FXMLPath.USERS_NEW, Utils.currentStage(event), "Editar colaborador", false,
				(UsersNewController controller) -> {
					controller.setCollaborator(tableCollaborators.getSelectionModel().getSelectedItem());
					controller.setUsersController(this);
				});
	}

	private void initializeTable() {
		Utils.setCellValueFactory(columnName, "name");
	}
	
	private void addListeners() {
		tableCollaborators.getSelectionModel().selectedItemProperty()
		.addListener((observable, oldSelection, newSelection) -> {
			if (newSelection != null) {
				updateUI(newSelection);
				btnChangeAutorizations.setVisible(true);
				btnEdit.setVisible(true);
				if(newSelection != currentUser) {
					btnDelete.setVisible(true);
				} else {
					btnDelete.setVisible(false);
				}
			} else {
				btnChangeAutorizations.setVisible(false);
				btnEdit.setVisible(false);
				btnDelete.setVisible(false);
			}
		});
	}
	
	private void updateUI(Collaborator c) {
		textName.setText(c.getName());
		textInitials.setText(c.getInitials());
		textCPF.setText(c.getCpf());
		// Gender
		if(c.getGender() != null) {
			textGender.setText(GenderEnum.fromString(c.getGender()).getfullGender());
		} else {
			textGender.setText("");
		}
		// CivilStatus
		if(c.getCivilStatus() != null) {
			textCivilStatus.setText(CivilStatusEnum.fromFullCivilStatus(c.getCivilStatus()).getFullCivilStatus());
			// If the person is a woman, so we change civilStatus to end with A
			if(c.getGender() != null && c.getGender().equalsIgnoreCase("Feminino")) {
				int civilStatusLength = textCivilStatus.getText().length();
				String feminineCivilStatus = textCivilStatus.getText().substring(0, civilStatusLength-1) + "a";
				textCivilStatus.setText(feminineCivilStatus);
			}
		} else {
			textCivilStatus.setText("");
		}
		// DATES: birthdate, age
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		if (c.getDateBirth() != null) {
			textBirthDate.setText(sdf.format(c.getDateBirth()));
		} else {
			textBirthDate.setText("");
		}
		textAge.setText(Integer.toString(c.getAge()) + " anos");
		textRG.setText(c.getRg());
		textEmail.setText(c.getEmail());
		textAdress.setText(c.getAdress());
		textNeighborhood.setText(c.getNeighborhood());
		textAdressReference.setText(c.getAdressReference());
		textCity.setText(c.getCity());
		textUF.setText(c.getUf());
		textPost.setText(c.getPost());
		textContact.setText(c.getContactNumber());
		if(c.equals(currentUser)) {
			textLogin.setText(c.getUserLogin());
			textPassword.setText(c.getPasswordLogin());
		} else {
			textLogin.setText("************");
			textPassword.setText("************");
		}
	}
	
	private void getDataFromDB() {
		try {
			collaborators = collaboratorDao.findAll();
		} catch (DbException e) {
			Alerts.showAlert("ERRO", "Erro ao acessar banco de dados",
					"DBException: " + e.getMessage(), AlertType.ERROR, null);
			e.printStackTrace();
		}
		tableCollaborators.setItems(FXCollections.observableArrayList(collaborators));
		tableCollaborators.refresh();
	}
	
	public void onDataChanged() {
		getDataFromDB();
	}
	

	public void handleBtnReturn(ActionEvent event) {
		Roots.usersMenu();
	}
	

}
