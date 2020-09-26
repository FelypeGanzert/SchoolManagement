package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import com.jfoenix.controls.JFXButton;

import application.Main;
import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

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
			setContent(FXMLPath.MAIN_MENU, (MainMenuController controller) -> {
				controller.setMainViewController(this);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void setMain(Main main) {
		this.main = main;
	}
	
	public void handleBtnHome(ActionEvent event) {
		Roots.home(this);
	}
	
	public void handleChangeUser(ActionEvent event) {
		main.showLoginForm();
		Utils.currentStage(event).close();
	}
	
	public void handleBtnExit(ActionEvent event) {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Sair");
		alert.setHeaderText("Clique em OK para encerrar o sistema");
		Optional<ButtonType> result =alert.showAndWait();
		if (result.isPresent() && result.get() == ButtonType.OK) {
			Utils.currentStage(event).close();			
		}
	}
	
	public void handleBtnStudents(ActionEvent event) {
		Roots.listStudents(this);
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
