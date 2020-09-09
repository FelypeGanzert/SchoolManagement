package gui;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import model.entites.Contact;
import model.entites.Matriculation;
import model.entites.Responsible;

public class InfoStudentController implements Initializable {
	// Menu
	@FXML JFXButton btnReturn;
	@FXML Label labelStudentName;
	@FXML HBox hBoxStaus;
	@FXML TextField textStatus;
	@FXML Button btnEditStatus;
	@FXML Button btnEditStudent;
	@FXML Button btnDeleteStudent;
	// Student infos
	@FXML TextField textID;
	@FXML TextField textName;
	@FXML TextField textOldRA;
	@FXML TextField textCPF;
	@FXML TextField textGender;
	@FXML TextField textBirthDate;
	@FXML TextField textYears;
	@FXML TextField textCivilStatus;
	@FXML TextField textRG;
	@FXML TextField textEmail;
	@FXML CheckBox checkBoxPromotionsEmail;
	@FXML TextField textAdress;
	@FXML TextField textNeighborhood;
	@FXML TextField textAdressReference;
	@FXML TextField textCity;
	@FXML TextField textUF;
	@FXML Label labelDateCadastryAndModify;
	@FXML TextArea textAreaObservation;
	// Table Matriculations
	@FXML TableView<Matriculation> tableMatriculations;
	@FXML TableColumn<Matriculation, Integer> columnMatriculationCode;
	@FXML TableColumn<Matriculation, Date> columnMatriculationDate;
	@FXML TableColumn<Matriculation, String> columnMatriculationStatus;
	@FXML TableColumn<Matriculation, String> columnMatriculationParcels;
	@FXML TableColumn<Matriculation, String> columnMatriculationResponsible;
	@FXML TableColumn<Matriculation, Matriculation> columnMatriculationInfo;
	@FXML Button btnAddMatriculation;
	// Table Contacts
	@FXML TableView<Contact> tableContacts;
	@FXML TableColumn<Contact, String> columnContactNumber;
	@FXML TableColumn<Contact, String> columnContactDescription;
	@FXML TableColumn<Contact, Contact> columnContactEdit;
	@FXML TableColumn<Contact, Contact> columnContactDelete;
	@FXML Button btnAddContact;
	// Table Responsibles
	@FXML TableView<Responsible> tableResponsibles;
	@FXML TableColumn<Responsible, String> columnReponsibleName;
	@FXML TableColumn<Responsible, String> columnReponsibleRelationship;
	@FXML TableColumn<Responsible, Contact> columnResponsibleEdit;
	@FXML TableColumn<Responsible, Contact> columnResponsibleRemove;
	@FXML Button btnAddResponsible;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
	}
	
	

}
