package gui;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;

public class ListStudentsController implements Initializable {

	// Filter Student and Register
	@FXML JFXTextField textFilter;
	@FXML JFXComboBox comboBoxfiledFilter;
	@FXML ToggleGroup filterType;
	@FXML ToggleGroup StudentStatus;
	@FXML JFXButton btnRegister;
	// Table Students
	@FXML TableView tableStudents;
	@FXML TableColumn columnStudentStatus;
	@FXML TableColumn columnStudentCode;
	@FXML TableColumn columnStudentName;
	@FXML TableColumn columnStudentContact1;
	// Table Matriculations
	@FXML TableView tableMatriculations;
	@FXML TableColumn columnMatriculationCode;
	@FXML TableColumn columnMatriculationDate;
	@FXML TableColumn columnMatriculationStatus;
	@FXML TableColumn columnMatriculationParcels;
	// Table Parcels
	@FXML Label labelSelectedMatriculation;
	@FXML TableView tableParcels;
	@FXML TableColumn columnParcelStatus;
	@FXML TableColumn columnParcelParcel;
	@FXML TableColumn columnParcelDate;
	@FXML TableColumn columnParcelValue;
	// Annotations
	@FXML Button btnAddAnnotation;
	@FXML JFXListView listViewAnnotations;
	@FXML Label labelSelectedAnnotationDate;
	@FXML JFXTextArea textAreaAnnotation;
	@FXML Button btnEditSelectedAnnotation;
	@FXML Button btnDeleteSelectedAnnottion;
	@FXML Label labelSelectedAnnotationCollaborator;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		
	}

}
