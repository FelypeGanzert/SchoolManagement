package test;

import java.text.SimpleDateFormat;

import db.DBFactory;
import db.DbException;
import model.dao.MatriculationDao;
import model.entites.Annotation;
import model.entites.Matriculation;

public class Tests {

	public static void main(String[] args) {
		

		//Alert alertProcessing = Alerts.showProcessingScreen(null);
		
		DBFactory.setUnits("localhost");
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
		
		MatriculationDao mDao = new MatriculationDao(DBFactory.getConnection());
		try {
			for(Matriculation m : mDao.findAllFromStudent(151)) {
				System.out.println("Matricula #" + m.getCode() + ", with " + m.getParcels().size() + " parcels.");
			}
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void printAnnotation(Annotation a) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		System.out.println(a.getId() + ", " + sdf.format(a.getDate()) + ", " + a.getDescription());
	}

}
