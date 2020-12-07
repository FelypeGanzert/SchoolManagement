package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import model.dao.CollaboratorDao;
import model.entites.Collaborator;

public class UserScheduleEditController implements Initializable {

	@FXML private HBox hBoxScheduleContainer;
	@FXML private JFXButton btnSave;
	@FXML private JFXButton btnCancel;
	
	private UserScheduleController userScheduleController;
	private Collaborator collaborator;
	private CollaboratorDao collaboradorDao;
	private UsersScheduleViewController usersScheduleViewController;
	
	@Override
	public void initialize(URL url, ResourceBundle resource) {
		this.collaboradorDao = new CollaboratorDao(DBFactory.getConnection());
	}
	
	public void setCollaborator(Collaborator collaborator) {
		this.collaborator = collaborator;
		showSchedule();
	}
	
	public void setUsersScheduleViewController(UsersScheduleViewController usersScheduleViewController) {
		this.usersScheduleViewController = usersScheduleViewController;
	}
	
	public void showSchedule() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_SCHEDULE));
			VBox newContent = loader.load();
			userScheduleController = loader.getController();
			userScheduleController.setCollaborator(collaborator);
			userScheduleController.abilityTextAreas();
			// clear Responsible Info and set the informations of selected responsible
			if (hBoxScheduleContainer.getChildren() != null) {
				hBoxScheduleContainer.getChildren().clear();
			}
			hBoxScheduleContainer.getChildren().add(newContent);
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Alerts.showAlert("IllegalStateException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR,
					null);
		}
	}
	
    public void handleCancelBtn(ActionEvent event) {
    	Utils.currentStage(event).close();
    }

    public void handleSaveBtn(ActionEvent event) {
    	userScheduleController.dataToCollaborator();
    	try {
			collaboradorDao.update(collaborator);
			Utils.currentStage(event).close();
			if(usersScheduleViewController != null) {
				usersScheduleViewController.showCollaboratorSchedule(collaborator);
			}
		} catch (DbException e) {
			Alerts.showAlert("DbException", "Erro ao atualizar colaborador", e.getMessage(), AlertType.ERROR,
				null);
			e.printStackTrace();
		}
    }

}
