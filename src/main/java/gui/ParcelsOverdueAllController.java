package gui;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import db.DBFactory;
import db.DbException;
import gui.util.Utils;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dao.ParcelDao;
import model.entites.Parcel;

public class ParcelsOverdueAllController implements Initializable{

    @FXML private Label labelTotalCertificates;
    @FXML private Label labelNumberOfStudents;
    @FXML private Label labelNumberOfParcels;
    @FXML private Label labelSumNormalValue;
    @FXML private Label labelSumValueWithFineDelay;
    @FXML private Label labelResumeParcelsStudents;
    @FXML private TableView<Parcel> tableParcels;
    @FXML private TableColumn<Parcel, Number> columnStudentId;
    @FXML private TableColumn<Parcel, String> columnStudentName;
    @FXML private TableColumn<Parcel, Number> columnMatriculationCode;
    @FXML private TableColumn<Parcel, Integer> columnDocumentNumber;
    @FXML private TableColumn<Parcel, String> columnParcelNumber;
    @FXML private TableColumn<Parcel, Double> columnValue;
    @FXML private TableColumn<Parcel, Date> columnDateParcel;
    @FXML private TableColumn<Parcel, Date> columnDateFineDelay;
    @FXML private TableColumn<Parcel, Double> columnValueWithFineDelay;

    private List<Parcel> parcels;
   	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTable();
		getDataFromDB();
		if(parcels != null) {
			tableParcels.setItems(FXCollections.observableArrayList(parcels));
		}
		calculateNumberInfos();
	}
	
    public void handleBtnReturn(ActionEvent event) {
    	System.out.println("Clicked to return");
    }
 
    private void getDataFromDB() {
    	ParcelDao parcelDao = new ParcelDao(DBFactory.getConnection());
    	try {
			parcels = parcelDao.getAllOverdue(new Date());
		} catch (DbException e) {
			e.printStackTrace();
			e.printStackTrace();
		}
    }
    
    public void handleBtnPrintReport(ActionEvent event) {
    	System.out.println("Clicked to print");
    }
    
    private void calculateNumberInfos() {
    	Integer numberOfParcels = 0;
    	Set<Integer> numberOfStudents = new HashSet<Integer>();
    	// we will put the studentId - numberOfParcels
    	Map<Integer, Integer> numberOfParcelsForStudent = new HashMap<Integer, Integer>();
    	// we will put the numberOfParcels - numberOfStudents
    	Map<Integer, Integer> numberOfStudentsForParcel = new TreeMap<Integer, Integer>();
    	Double sumNormalValue = 0.0;
    	Double sumValueFineDelay = 0.0;
    	Double sumValueWithFineDelay = 0.0;
    	if(parcels != null) {
    		numberOfParcels = parcels.size();
    		
        	for(Parcel p : parcels) {
        		if(p.getMatriculation() != null && p.getMatriculation().getStudent() != null) {
        			Integer studentId = p.getMatriculation().getStudent().getId();
            		// count the number of students
        			numberOfStudents.add(studentId);
            		// numberOfStudentsForParcels
        			if(numberOfParcelsForStudent.get(studentId) == null) {
        				numberOfParcelsForStudent.put(studentId, 1);
        			} else {
        				int amount = numberOfParcelsForStudent.get(studentId);
        				numberOfParcelsForStudent.put(studentId, amount + 1);
        			}
        		}
        		// sum value
        		if(p.getValue() != null) {
        			sumNormalValue += p.getValue();
        		}
        		// sum value with fine delay
        		if(p.getValueFineDelay() != null) {
            		sumValueFineDelay += p.getValueFineDelay();
        		}
        	}
    	}
    	
    	sumValueWithFineDelay = sumNormalValue + sumValueFineDelay;
    	labelNumberOfParcels.setText(Integer.toString(numberOfParcels));
    	labelNumberOfStudents.setText(Integer.toString(numberOfStudents.size()));
    	labelSumNormalValue.setText(Utils.formatCurrentMoney(sumNormalValue, Utils.DINHEIRO_REAL));
    	labelSumValueWithFineDelay.setText(Utils.formatCurrentMoney(sumValueWithFineDelay, Utils.DINHEIRO_REAL));
    	System.out.println(numberOfParcelsForStudent);
    	numberOfParcelsForStudent.forEach((k,v) -> {
    		if(numberOfStudentsForParcel.get(v) == null) {
    			numberOfStudentsForParcel.put(v, 1);
			} else {
				int amount = numberOfStudentsForParcel.get(v);
				numberOfStudentsForParcel.put(v, amount + 1);
			}
    	});
    	String studentsForParcel = "";
    	for (Map.Entry<Integer, Integer> entry : numberOfStudentsForParcel.entrySet()) {
    		studentsForParcel = studentsForParcel + entry.getKey() + ": " + entry.getValue() + " alunos  |  ";
    	}
    	labelResumeParcelsStudents.setText(studentsForParcel);
    }
   
	private void initializeTable() {
		// student id
		columnStudentId.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getMatriculation() != null &&
						cellData.getValue().getMatriculation().getStudent() != null) {
					return new SimpleIntegerProperty(cellData.getValue().getMatriculation().getStudent().getId());
				}
				return new SimpleIntegerProperty();
			} catch (Exception e) {
				return new SimpleIntegerProperty();
			}
		});
		columnStudentId.setReorderable(false);
		// student name
		columnStudentName.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getMatriculation() != null &&
						cellData.getValue().getMatriculation().getStudent() != null) {
					return new SimpleStringProperty(cellData.getValue().getMatriculation().getStudent().getName());
				}
				return new SimpleStringProperty();
			} catch (Exception e) {
				return new SimpleStringProperty();
			}
		});
		columnStudentName.setReorderable(false);
		// matriculation code
		columnMatriculationCode.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getMatriculation() != null) {
					return new SimpleIntegerProperty(cellData.getValue().getMatriculation().getCode());
				}
				return new SimpleIntegerProperty();
			} catch (Exception e) {
				return new SimpleIntegerProperty();
			}
		});
		columnMatriculationCode.setReorderable(false);
		// document number
		Utils.setCellValueFactory(columnDocumentNumber, "documentNumber");
		columnDocumentNumber.setReorderable(false);
		// parcel number
		columnParcelNumber.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getMatriculation() != null) {
					String output = cellData.getValue().getParcelNumber() + "/" +
							cellData.getValue().getMatriculation().getParcels().size();
					return new SimpleStringProperty(output);
				}
				return new SimpleStringProperty("-");
			} catch (Exception e) {
				return new SimpleStringProperty("-");
			}
		});
		columnParcelNumber.setReorderable(false);
		// value
		Utils.setCellValueFactory(columnValue, "value");
		Utils.formatTableColumnDoubleCurrency(columnValue);
		columnValue.setReorderable(false);
		// date parcel
		Utils.setCellValueFactory(columnDateParcel, "dateParcel");
		Utils.formatTableColumnDate(columnDateParcel, "dd/MM/yyyy");
		columnDateParcel.setReorderable(false);
		// date fine delay
		Utils.setCellValueFactory(columnDateFineDelay, "dateFineDelay");
		Utils.formatTableColumnDate(columnDateFineDelay, "dd/MM/yyyy");
		columnDateFineDelay.setReorderable(false);
		// value with fine delay
		Utils.setCellValueFactory(columnValueWithFineDelay, "valueWithFineDelay");
		Utils.formatTableColumnDoubleCurrency(columnValueWithFineDelay);
		columnValueWithFineDelay.setReorderable(false);
	}

}
