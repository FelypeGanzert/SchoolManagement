package gui;

import java.io.IOException;

import db.DBFactory;
import gui.util.Alerts;
import gui.util.FXMLPath;
import javafx.scene.control.Alert;
import model.dao.StudentDao;

public class Roots {
	
	public static void listStudents(MainViewController mainView) {
		try {
			Alert alertProcessing = Alerts.showProcessingScreen();
			mainView.setContent(FXMLPath.LIST_STUDENTS, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
				controller.setMainViewController(mainView);
				controller.updateTableView();
				controller.filterStudents();
			});
			alertProcessing.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void listResponsibles(MainViewController mainView) {
		try {
			mainView.setContent(FXMLPath.LIST_RESPONSIBLES, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void home(MainViewController mainView) {
		try {
			mainView.setContent(FXMLPath.MAIN_MENU, (MainMenuController controller) -> {
				controller.setMainViewController(mainView);
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
