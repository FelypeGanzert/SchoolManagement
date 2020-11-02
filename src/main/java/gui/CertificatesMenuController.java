package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DBFactory;
import gui.util.Roots;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import model.dao.CertificateRequestDao;

public class CertificatesMenuController implements Initializable {

	@FXML private Label labelOpenRequests;
	
	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		CertificateRequestDao cr = new CertificateRequestDao(DBFactory.getConnection());
		labelOpenRequests.setText("(" + Integer.toString(cr.getNumberOfOpenRequests()) + ")");
	}

	public void handleBtnRequests(ActionEvent event) {
		Roots.certificatesRequests();
	}
	
	public void handleBtnAllStudents(ActionEvent event) {
		Roots.certificatesAllStudents();
	}
	
	public void handleBtnHistoric(ActionEvent event) {
		Roots.certificatesHistoric();
	}
	
}
