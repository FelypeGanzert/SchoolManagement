package test;

import java.text.SimpleDateFormat;
import java.util.List;

import db.DBFactory;
import model.dao.AnnotationDao;
import model.entites.Annotation;
import model.entites.Student;

public class AnnotationDaoTest {

	public static void main(String[] args) {
		DBFactory.setUnits("localhost");
		DBFactory.getConnection();
		try {
			AnnotationDao annotationDao = new AnnotationDao(DBFactory.getConnection());
			
			System.out.println("=== Find by Id ===");
			Annotation a1 = annotationDao.findById(1);
			printAnnotation(a1);
			
			System.out.println("=== Delete ===");
			annotationDao.delete(a1);
			
			System.out.println("==== Update ===");
			Annotation a2 = new Annotation();
			a2.setId(2);
			a2.setDescription("Segunda anotação alterada");
			annotationDao.update(a2);
			
			System.out.println("=== Find all by Student ====");
			Student s = new Student();
			s.setId(2);
			List<Annotation> annotations = annotationDao.findAllFromStudent(s);
			annotations.forEach(a -> printAnnotation(a));
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void printAnnotation(Annotation a) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		System.out.println(a.getId() + ", " + sdf.format(a.getDateAnnotation()) + ", " + a.getDescription());
	}

}
