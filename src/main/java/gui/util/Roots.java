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
	
	public static <T> void certificatesHistoric() {
		certificatesHistoric(x -> {});
	}
	public static <T> void certificatesHistoric(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.CERTIFICATES_HISTORIC, initializingAction);
	}
	
	public static <T> void studentsPresenceForm() {
		studentsPresenceForm(x -> {
		});
	}

	public static <T> void studentsPresenceForm(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.STUDENTS_PRESENCE_FORM, initializingAction);
	}

	public static <T> void parcelPaymentByDocumentNumber() {
		parcelPaymentByDocumentNumber(x -> {
		});
	}

	public static <T> void parcelPaymentByDocumentNumber(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.PARCEL_PAYMENT_BY_DOCUMENT_NUMBER, initializingAction);
	}

	// === Users ====
	
	public static <T> void usersMenu() {
		usersMenu(x -> {
		});
	}

	public static <T> void usersMenu(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.USERS_MENU, initializingAction);
	}
	
	public static <T> void users() {
		users(x -> {
		});
	}

	public static <T> void users(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.USERS, initializingAction);
	}
	
	public static <T> void usersSchedules() {
		usersSchedules(x -> {
		});
	}

	public static <T> void usersSchedules(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.USERS_SCHEDULE_VIEW, initializingAction);
	}
	
	// === Persons Without CPF ====
	public static <T> void regularizeCPFMenu() {
		regularizeCPFMenu(x -> {
		});
	}

	public static <T> void regularizeCPFMenu(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.REGULARIZE_CPF_MENU, initializingAction);
	}
	
	public static <T> void regularizeCPFStudents() {
		regularizeCPFStudents(x -> {
		});
	}

	public static <T> void regularizeCPFStudents(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.REGULARIZE_CPF_STUDENTS, initializingAction);
	}
	
	public static <T> void regularizeCPFResponsibles() {
		regularizeCPFResponsibles(x -> {
		});
	}

	public static <T> void regularizeCPFResponsibles(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.REGULARIZE_CPF_RESPONSIBLES, initializingAction);
	}
	
	// === Overdue Parcels
	
	public static <T> void parcelsOverdueMenu() {
		parcelsOverdueMenu(x -> {
		});
	}

	public static <T> void parcelsOverdueMenu(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.PARCELS_OVERDUE_MENU, initializingAction);
	}
	
	public static <T> void parcelsOverdueModel1() {
		parcelsOverdueModel1(x -> {
		});
	}

	public static <T> void parcelsOverdueModel1(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.PARCELS_OVERDUE_MODEL1, initializingAction);
	}
	
	public static <T> void parcelsOverdueModel2() {
		parcelsOverdueModel2(x -> {
		});
	}

	public static <T> void parcelsOverdueModel2(Consumer<T> initializingAction) {
		// Get mainvViewController from Globe to set content in
		MainViewController mainView = Globe.getGlobe().getItem(MainViewController.class, "mainViewController");
		mainView.setContent(FXMLPath.PARCELS_OVERDUE_MODEL2, initializingAction);
	}

	
}