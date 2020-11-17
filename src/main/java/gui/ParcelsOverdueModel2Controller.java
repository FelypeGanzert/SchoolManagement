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
import java.util.stream.Collectors;

import db.DBFactory;
import db.DbException;
import gui.util.DateUtil;
import gui.util.FXMLPath;
import gui.util.Roots;
import gui.util.Utils;
import gui.util.enums.ParcelStatusEnum;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import model.dao.ParcelDao;
import model.entites.Matriculation;
import model.entites.Parcel;
import model.entites.Student;

public class ParcelsOverdueModel2Controller implements Initializable{

    @FXML private Label labelTotalCertificates;
    @FXML private Label labelNumberOfStudents;
    @FXML private Label labelNumberOfParcels;
    @FXML private Label labelSumNormalValue;
    @FXML private Label labelSumValueWithFineDelay;
    @FXML private Label labelResumeParcelsStudents;
    @FXML private TableView<Student> tableStudents;
    @FXML private TableColumn<Student, Number> columnStudentId;
    @FXML private TableColumn<Student, String> columnStudentName;
    @FXML private TableColumn<Student, Number> columnNumberOfLateParcels;
    @FXML private TableColumn<Student, Number> columnTotalValueWithFineDelay;
	@FXML private TabPane tabPaneParcels;

    private List<Parcel> parcels;
    private Set<Student> students = new HashSet<>();
   	
	@Override
	public void initialize(URL url, ResourceBundle resources) {
		initializeTable();
		getDataFromDB();
		if(parcels != null) {
			parcels.forEach(p -> students.add(p.getMatriculation().getStudent()));
			tableStudents.setItems(FXCollections.observableArrayList(students));
		}
		calculateNumberInfos();
	}
	
    public void handleBtnReturn(ActionEvent event) {
    	Roots.parcelsOverdueMenu();
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
    		studentsForParcel = studentsForParcel + entry.getKey() + ": " + entry.getValue() + " | ";
    	}
    	labelResumeParcelsStudents.setText(studentsForParcel);
    }
   
	private void initializeTable() {
		// student id
		Utils.setCellValueFactory(columnStudentId, "id");
		columnStudentId.setReorderable(false);
		// student name
		Utils.setCellValueFactory(columnStudentName, "name");
		columnStudentName.setReorderable(false);
		// number of late parcels
		columnNumberOfLateParcels.setCellValueFactory(cellData -> {
			try {
				Date now = new Date();
				Integer count = 0;
				if (cellData.getValue().getMatriculations() != null) {
					for (Matriculation m : cellData.getValue().getMatriculations()) {
						List<Parcel> overdueParcels = m.getParcels().stream().filter(p -> p.getSituation().equalsIgnoreCase(ParcelStatusEnum.ABERTA.toString()) &&
								DateUtil.compareTwoDates(p.getDateParcel(), now) < 0).collect(Collectors.toList());
						count += overdueParcels.size();
					}
					return new SimpleIntegerProperty(count);
				}
				return new SimpleIntegerProperty();
			} catch (Exception e) {
				return new SimpleIntegerProperty();
			}
		});
		columnNumberOfLateParcels.setReorderable(false);
		// Total Value With FineDelay
		columnTotalValueWithFineDelay.setCellValueFactory(cellData -> {
			try {
				if (cellData.getValue().getMatriculations() != null) {
					Date now = new Date();
					Double total = 0.0;
					for (Matriculation m : cellData.getValue().getMatriculations()) {
						List<Parcel> overdueParcels = m.getParcels().stream().filter(p -> p.getSituation().equalsIgnoreCase(ParcelStatusEnum.ABERTA.toString()) &&
								DateUtil.compareTwoDates(p.getDateParcel(), now) < 0).collect(Collectors.toList());
						for(Parcel p : overdueParcels) {
							if(p.getValueWithFineDelay() != null) {
								total += p.getValueWithFineDelay();
							} else if(p.getValue() != null) {
								total += p.getValue();
							}
						}
					}
					return new SimpleDoubleProperty(total);
				}
				return new SimpleDoubleProperty();
			} catch (Exception e) {
				return new SimpleDoubleProperty();
			}
		});
		Utils.formatTableColumnNumberCurrency(columnTotalValueWithFineDelay);
		columnTotalValueWithFineDelay.setReorderable(false);
		// listener
		// Listener to selected student of table
		tableStudents.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldSelection, newSelection) -> {
					if(newSelection != null) {
						tabPaneParcels.getTabs().clear();
						for(Matriculation m : newSelection.getMatriculations()) {
							Utils.addTab(this, FXMLPath.PARCELS_OVERDUE_INFO_PARCELS, "#" + m.getCode(), tabPaneParcels,
									(ParcelsOverdueInfoParcels controller) -> {
										controller.setParcels(m.getParcels());
									});
						}
					}
				});
	}
	
}
