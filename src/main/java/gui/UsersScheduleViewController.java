package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;

import db.DBFactory;
import db.DbException;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Roots;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import model.dao.CollaboratorDao;
import model.entites.Collaborator;

public class UsersScheduleViewController implements Initializable {

	@FXML
	private JFXComboBox<Collaborator> comboBoxCollaborator;
	@FXML
	private HBox hBoxScheduleContainer;

	private CollaboratorDao collaboradorDao;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		this.collaboradorDao = new CollaboratorDao(DBFactory.getConnection());
		List<Collaborator> collaborators = new ArrayList<>();
		try {
			collaborators = collaboradorDao.findAll();
		} catch (DbException e) {
			e.printStackTrace();
		}
		// initialize comboBox
		comboBoxCollaborator.setItems(FXCollections.observableArrayList(collaborators));
		comboBoxCollaborator.setConverter(new StringConverter<Collaborator>() {
			@Override
			public String toString(Collaborator object) {
				if(object != null) {
					return object.getName();	
				} else {
					return null;
				}
			}

			@Override
			public Collaborator fromString(String arg0) {
				return null;
			}
		});
		// listener
		comboBoxCollaborator.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				showCollaboratorSchedule(newValue);
			};
		});
		// select the first collaborator
		comboBoxCollaborator.getSelectionModel().selectFirst();
	}

	public void handleBtnReturn(ActionEvent event) {
		Roots.usersMenu();
	}
	
	private void showCollaboratorSchedule(Collaborator collaborator) {
		// Show Collaborator Schedule
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath.USER_SCHEDULE));
			VBox newContent = loader.load();
			UserScheduleController controller = loader.getController();
			controller.setCollaborator(collaborator);
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

}
