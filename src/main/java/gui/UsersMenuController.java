package gui;

import java.net.URL;
import java.util.ResourceBundle;

import gui.util.Roots;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;

public class UsersMenuController implements Initializable {
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
	}

	public void handleBtnUsersInfos(ActionEvent event) {
		Roots.users();
	}
	
	public void handleBtnUsersSchedules(ActionEvent event) {
		Roots.usersSchedules((UsersScheduleViewController controller) -> {});
	}
	
}
