package test;

import java.text.SimpleDateFormat;

import db.DBFactory;
import gui.util.Alerts;
import javafx.scene.control.Alert;
import model.dao.AnnotationDao;
import model.entites.Annotation;

public class Tests {

	public static void main(String[] args) {
		

		Alert alertProcessing = Alerts.showProcessingScreen();
		
//		DBFactory.setUnits("localhost");
//		DBFactory.getConnection();
//		try {
//			AnnotationDao annotationDao = new AnnotationDao(DBFactory.getConnection());
//			
//			System.out.println("=== Find by Id ===");
//			Annotation a1 = annotationDao.findById(2);
//			printAnnotation(a1);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		String value = "8419592498";
//		value = value.replaceAll("[^0-9]", "");
//		value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
//		value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
//		value = value.replaceFirst("(\\d{3})(\\d)", "$1-$2");
//		System.out.println(value);
		
	}
	
	public static void printAnnotation(Annotation a) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		System.out.println(a.getId() + ", " + sdf.format(a.getDate()) + ", " + a.getDescription());
	}

}
