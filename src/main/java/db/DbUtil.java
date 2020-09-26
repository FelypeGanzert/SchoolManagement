package db;

import model.dao.StudentDao;
import model.entites.Student;

public class DbUtil {
	
	public static <T> void refleshData(Student entity) {
		try {
			entity = new StudentDao(DBFactory.getConnection()).findById(entity.getId());
		} catch (DbException e) {
			entity = null;
			e.printStackTrace();
		}
		DBFactory.getConnection().refresh(entity);
	}

}
