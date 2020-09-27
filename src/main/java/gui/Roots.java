package gui;

import db.DBFactory;
import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.dao.StudentDao;
import sharedData.Globe;

public class Roots {
	
	// Called by DBConnectionURLController after connected with database
	// and when we need to change current user
	public static <T> void loginForm(T currentClass) {
		// Remove currentUser from Globe data whe user logout
		Globe.getState("main", "main").removeItem("currentUser");
		Utils.loadView(currentClass, false, FXMLPath.LOGIN, new Stage(), "Login", false, x -> {});
	}
	
	// Called after user logged
	public static <T> void mainView(T currentClass) {
		Utils.loadView(currentClass, false, FXMLPath.MAIN_VIEW, new Stage(), "Gerenciamento Escolar", true, x -> {});
	}

	public static void listStudents() {
		Alert alertProcessing = Alerts.showProcessingScreen();
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.LIST_STUDENTS, (ListStudentsController controller) -> {
			controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
			controller.setMainViewController(mainView);
			controller.updateTableView();
			controller.filterStudents();
		});
		alertProcessing.close();
	}

	public static void listResponsibles() {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.LIST_RESPONSIBLES, (ListStudentsController controller) -> {
			controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
		});
	}

	public static void home() {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.MAIN_MENU, (MainMenuController controller) -> {});
	}

}
