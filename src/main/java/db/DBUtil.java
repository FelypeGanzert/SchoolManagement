package db;

import model.dao.StudentDao;
import model.entites.Student;

public class DBUtil {

	public static <T> void refleshData(Student entity) {
		try {
			entity = new StudentDao(DBFactory.getConnection()).findById(entity.getId());
			DBFactory.getConnection().refresh(entity);
		} catch (DbException e) {
			entity = null;
			e.printStackTrace();
		}
	}

}