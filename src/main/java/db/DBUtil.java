package db;

import model.dao.ResponsibleDao;
import model.dao.ResponsibleStudentDao;
import model.dao.StudentDao;
import model.entites.Responsible;
import model.entites.ResponsibleStudent;
import model.entites.Student;

public class DBUtil {

	public static <T> void refleshData(Student entity) {
		try {
			entity = new StudentDao(DBFactory.getConnection()).findById(entity.getId());
			if(entity != null) {
				DBFactory.getConnection().refresh(entity);
			}
		} catch (DbException e) {
			entity = null;
			e.printStackTrace();
		}
	}
	
	public static <T> void refleshData(Responsible entity) {
		try {
			entity = new ResponsibleDao(DBFactory.getConnection()).findById(entity.getId());
			if(entity != null) {
				DBFactory.getConnection().refresh(entity);
			}
		} catch (DbException e) {
			entity = null;
			e.printStackTrace();
		}
	}
	
	public static <T> void refleshData(ResponsibleStudent responsibleStudent) {
		try {
			responsibleStudent = new ResponsibleStudentDao(DBFactory.getConnection()).findById(responsibleStudent.getId());
			if(responsibleStudent != null) {
				DBFactory.getConnection().refresh(responsibleStudent);
			}
		} catch (DbException e) {
			responsibleStudent = null;
			e.printStackTrace();
		}
	}

}
