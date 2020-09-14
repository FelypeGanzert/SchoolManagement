package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import db.DBFactory;
import gui.util.FxmlPaths;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import model.dao.StudentDao;

public class MainMenuController implements Initializable {

	@FXML private JFXButton btnListStudents;
	@FXML private JFXButton btnListResponsibles;
	
	private MainViewController mainView;	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	public void setMainViewController(MainViewController mainView) {
		this.mainView = mainView;
	}
	
	public void showListStudents(ActionEvent action) {
		try {
			mainView.setContent(FxmlPaths.LIST_STUDENTS, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
				controller.setMainViewController(mainView);
				controller.updateTableView();
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showListResponsibles(ActionEvent action) {
		try {
			mainView.setContent(FxmlPaths.LIST_RESPONSIBLES, (ListStudentsController controller) -> {
				controller.setStudentDao(new StudentDao(DBFactory.getConnection()));
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
