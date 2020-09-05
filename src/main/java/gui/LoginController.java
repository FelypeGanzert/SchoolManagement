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
import application.Main;
import db.DBFactory;
import gui.util.Utils;
import gui.util.Validators;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.entites.Collaborator;

public class LoginController implements Initializable {

	@FXML private JFXTextField txtUser;
	@FXML private JFXPasswordField txtPassword;
	@FXML private JFXButton btnLogin;
	@FXML private Label labelError;
	private Main main; // To call Main Screen
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		txtUser.getValidators().add(Validators.getRequiredFieldValidator());
		txtPassword.getValidators().add(Validators.getRequiredFieldValidator());
		// Listeners to hidden error message
		txtUser.textProperty().addListener((observable, oldValue, newValue) -> {
			labelError.setVisible(false);
		});
		txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
			labelError.setVisible(false);
		});
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void handleBtnLogin(ActionEvent event) {
		labelError.setVisible(false);
		if(txtUser.validate() && txtPassword.validate()) {
			Collaborator collaborator = login();
			if(collaborator != null) {
				Utils.currentStage(event).close();
				main.showMainView(collaborator);
			}
		}
	}
	
	private Collaborator login() {
		String user = txtUser.getText();
		String password = txtPassword.getText();
		// Settings to find a Collaborator with the same user and password in database
		EntityManager entityManager = DBFactory.getFactory().createEntityManager();
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Collaborator> criteriaQuery = criteriaBuilder.createQuery(Collaborator.class);
        Root<Collaborator> root = criteriaQuery.from(Collaborator.class);
        criteriaQuery.select(root);
        Predicate userPredicate = criteriaBuilder.equal(root.get("userLogin"), user);
        Predicate passwordPredicate = criteriaBuilder.equal(root.get("passwordLogin"), password);
        criteriaQuery.where(criteriaBuilder.and(userPredicate, passwordPredicate));
        // Try to find a correspondent result
        try {
        	TypedQuery<Collaborator> typedQuery = entityManager.createQuery(criteriaQuery);
        	Collaborator collaborator = typedQuery.getSingleResult();
        	return collaborator;
        } catch(NoResultException e) {
        	labelError.setVisible(true);
        	labelError.setText("Usuário ou senha incorretos");
        	// Animations to call atention to error
        	new Tada(labelError).play();
        	new Shake(txtUser).play();
        	new Shake(txtPassword).play();
        }
        return null; // in case of doens't find one correspondence
	}
	
}
