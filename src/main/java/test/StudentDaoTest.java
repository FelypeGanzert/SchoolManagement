package test;

import java.util.List;

import db.DBFactory;
import model.dao.StudentDao;
import model.entites.Student;

public class StudentDaoTest {

	public static void main(String[] args) {
		DBFactory.setUnits("localhost");
		DBFactory.getConnection();
		try {
			StudentDao studentDao = new StudentDao(DBFactory.getConnection());
			/*
			System.out.println("=== Find by Id ===");
			Student s1 = studentDao.findById(1);
			System.out.println(s1.getId() + ", " + s1.getName() + ", " + s1.getSituacao());
			
			System.out.println("=== Updated ===");
			s1.setSituacao("Aguardando");
			studentDao.update(s1);
			System.out.println(s1.getId() + ", " + s1.getName() + ", " + s1.getSituacao());
			
			System.out.println("=== Remove ===");
			Student s2 = new Student();
			s2.setId(1);
			studentDao.remove(s2);
			 */
			
			System.out.println("=== Find all ===");
			List<Student> students = studentDao.findAll();
			students.forEach(s -> System.out.println(s.getId() + ", " + s.getName() + ", " + s.getStatus()));
			System.out.println(students.get(0).getMatriculations().size());
			System.out.println(students.get(0).getMatriculations().size());
			System.out.println(students.get(2).getMatriculations().size());
			System.out.println(students.get(2).getMatriculations().size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
