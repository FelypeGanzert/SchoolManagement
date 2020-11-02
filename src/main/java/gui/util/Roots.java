package gui.util;

import java.util.function.Consumer;

import gui.MainViewController;
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

	// List Student
	public static void listStudents() {
		listStudents(x -> {});
	}
	public static <T> void listStudents(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.LIST_STUDENTS, initializingAction);
	}

	// List Responsibles
	public static void listResponsibles() {
		listResponsibles(x -> {});
	}
	public static <T> void listResponsibles(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.LIST_RESPONSIBLES, initializingAction);
	}

	// Home
	public static void home() {
		home(x -> {});
	}
	public static <T> void home(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.MAIN_MENU, initializingAction);
	}

	// Birthdays
	public static <T> void birthdays() {
		birthdays(x -> {});
	}
	public static <T> void birthdays(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.BIRTHDAYS, initializingAction);
	}

	// === Certificates ====
	public static <T> void certificatesMenu() {
		certificatesMenu(x -> {});
	}
	public static <T> void certificatesMenu(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.CERTIFICATES_MENU, initializingAction);
	}
	
	public static <T> void certificatesRequests() {
		certificatesRequests(x -> {});
	}
	public static <T> void certificatesRequests(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.CERTIFICATES_REQUESTS, initializingAction);
	}
	
	public static <T> void certificatesAllStudents() {
		certificatesAllStudents(x -> {});
	}
	public static <T> void certificatesAllStudents(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.CERTIFICATES_ALL_STUDENTS, initializingAction);
	}

}