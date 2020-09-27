package gui;

import java.util.function.Consumer;

import gui.util.Alerts;
import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import sharedData.Globe;

public class Roots {
	
	// Called by DBConnectionURLController after connected with database
	// and when we need to change current user
	public static <T> void loginForm(T currentClass) {
		Utils.loadView(currentClass, false, FXMLPath.LOGIN, new Stage(), "Login", false, x -> {});
	}
	
	// Called after user logged
	public static <T> void mainView(T currentClass) {
		Utils.loadView(currentClass, false, FXMLPath.MAIN_VIEW, new Stage(), "Gerenciamento Escolar", true, x -> {});
	}

	public static void listStudents() {
		listStudents(x -> {});
	}
	public static <T> void listStudents(Consumer<T> initializingAction) {
		Alert alertProcessing = Alerts.showProcessingScreen();
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.LIST_STUDENTS, initializingAction);
		alertProcessing.close();
	}

	public static void listResponsibles() {
		listResponsibles(x -> {});
	}
	public static <T> void listResponsibles(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.LIST_RESPONSIBLES, initializingAction);
	}

	public static void home() {
		home(x -> {});
	}
	public static <T> void home(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getStateItem(MainViewController.class, "main", "controller", "mainViewController");
		mainView.setContent(FXMLPath.MAIN_MENU, initializingAction);
	}

}
