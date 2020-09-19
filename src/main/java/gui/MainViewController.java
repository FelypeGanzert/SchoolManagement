package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import application.Main;
import db.DBFactory;
import gui.util.Alerts;
import gui.util.FxmlPaths;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import model.dao.StudentDao;

public class MainViewController implements Initializable {

	@FXML private ScrollPane content;
	@FXML private JFXButton btnHome;
	@FXML private JFXButton btnStudents;
	@FXML private Label labelCurrentUser;
	@FXML private JFXButton btnChangeUser;
	
	private Main main;

	@Override
	public void initialize(URL url, ResourceBundle resource) {
		this.content.setFitToHeight(true);
		this.content.setFitToWidth(true);
		this.labelCurrentUser.setText(Main.getCurrentUser().getName());
		try {
			setContent(FxmlPaths.MAIN_MENU, (MainMenuController controller) -> {
				controller.setMainViewController(this);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void handleBtnHome(ActionEvent action) {
		try {
			setContent(FxmlPaths.MAIN_MENU, (MainMenuController controller) -> {
				controller.setMainViewController(this);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void handleChangeUser(ActionEvent action) {
		main.showLoginForm();
		Utils.currentStage(action).close();
	}
	
	public void handleBtnStudents(ActionEvent action) {
		try {
			Alert alertProcessing = Alerts.showProcessingScreen();
			setContent(FxmlPaths.LIST_STUDENTS, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
				controller.setMainViewController(this);
				controller.updateTableView();
				controller.filterStudents();
			alertProcessing.close();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public <T> void setContent(String path, Consumer<T> initializingAction) throws IOException {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
			ScrollPane newContent = loader.load();
			this.content.setContent(newContent.getContent());
			this.content.setStyle(newContent.getStyle());
			
			T controller = loader.getController();
			initializingAction.accept(controller);
	}
	
	public ScrollPane getMainContent() {
		return this.content;
	}

}
