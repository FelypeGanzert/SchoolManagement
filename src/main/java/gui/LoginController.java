package gui;

import java.net.URL;
import java.util.ResourceBundle;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import animatefx.animation.Shake;
import animatefx.animation.Tada;
import db.DBFactory;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.entites.Collaborator;
import sharedData.Globe;

public class LoginController implements Initializable {

	@FXML private JFXTextField txtUser;
	@FXML private JFXPasswordField txtPassword;
	@FXML private JFXButton btnLogin;
	@FXML private Label labelError;
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		// Remove currentUser from Globe data when user logout
		Globe.getGlobe().removeItem("currentUser");
		txtUser.getValidators().add(Validators.getRequiredFieldValidator());
		txtPassword.getValidators().add(Validators.getRequiredFieldValidator());
		addListeners();
	}
	
	public void addListeners() {
		// Listeners to hidden error message
		txtUser.textProperty().addListener((observable, oldValue, newValue) -> {
			labelError.setVisible(false);
		});
		txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
			labelError.setVisible(false);
		});
	}
	
	public void handleBtnLogin(ActionEvent event) {
		labelError.setVisible(false);
		if (!txtUser.validate() || !txtPassword.validate()) {
			// return of fields aren't valid
			return;
		}
		// Try to find a Collaborator with the same user and password in database
		try {
			Collaborator collaborator = getCollaboratorFromDB();
			// set currentCollaborator (User) to Globe
			Globe.getGlobe().putItem("currentUser", collaborator);
			// Close Login dialogStage if find a collaborator
			Utils.currentStage(event).close();
			// show MainView
			Roots.mainView(this);
		} catch (NoResultException e) {
			// Show login error message in case we don't find any
			// collaborator with same name and password in DB
			labelError.setVisible(true);
			labelError.setText("Usuário ou senha incorretos");
			// Animations to call atention to error
			new Tada(labelError).play();
			new Shake(txtUser).play();
			new Shake(txtPassword).play();
		}
	}
	
	private Collaborator getCollaboratorFromDB()  throws NoResultException{
		String user = txtUser.getText();
		String password = txtPassword.getText();
		EntityManager entityManager = DBFactory.getConnection();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Collaborator> criteriaQuery = criteriaBuilder.createQuery(Collaborator.class);
        Root<Collaborator> root = criteriaQuery.from(Collaborator.class);
        criteriaQuery.select(root);
        Predicate userPredicate = criteriaBuilder.equal(root.get("userLogin"), user);
        Predicate passwordPredicate = criteriaBuilder.equal(root.get("passwordLogin"), password);
        criteriaQuery.where(criteriaBuilder.and(userPredicate, passwordPredicate));
        // Try to find a correspondent result
		TypedQuery<Collaborator> typedQuery = entityManager.createQuery(criteriaQuery);
		Collaborator collaborator = typedQuery.getSingleResult();
		return collaborator;
	}
	
}