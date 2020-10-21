package db;

import model.dao.MatriculationDao;
import model.dao.ResponsibleDao;
import model.dao.ResponsibleStudentDao;
import model.dao.StudentDao;
import model.entites.Matriculation;
import model.entites.Person;
import model.entites.Responsible;
import model.entites.ResponsibleStudent;
import model.entites.Student;

public class DBUtil {

	public static <T> void refreshData(Person entity) throws DbException, DbExceptioneEntityExcluded {
		if (entity instanceof Student) {
			refreshData((Student) entity);
		}
		if (entity instanceof Responsible) {
			refreshData((Responsible) entity);
		}
	}

	public static <T> void refreshData(Student entity) throws DbException, DbExceptioneEntityExcluded {
		entity = new StudentDao(DBFactory.getConnection()).findById(entity.getId());
		if (entity != null) {
			DBFactory.getConnection().refresh(entity);
		}
		if (entity.getExcluded() != null) {
			throw new DbExceptioneEntityExcluded("Entity has been deleted");
		}
	}

	public static <T> void refreshData(Responsible entity) {
		try {
			entity = new ResponsibleDao(DBFactory.getConnection()).findById(entity.getId());
			if (entity != null) {
				DBFactory.getConnection().refresh(entity);
			}
		} catch (Exception e) {
			entity = null;
			e.printStackTrace();
		}
	}

	public static <T> void refreshData(ResponsibleStudent responsibleStudent) {
		try {
			responsibleStudent = new ResponsibleStudentDao(DBFactory.getConnection())
					.findById(responsibleStudent.getId());
			if (responsibleStudent != null) {
				DBFactory.getConnection().refresh(responsibleStudent);
			}
		} catch (Exception e) {
			responsibleStudent = null;
			e.printStackTrace();
		}
	}

	public static <T> void refreshData(Matriculation matriculation) {
		try {
			int code = matriculation.getCode();
			DBFactory.getConnection().detach(matriculation);
			matriculation = new MatriculationDao(DBFactory.getConnection()).findById(code);
			if (matriculation != null) {
				DBFactory.getConnection().refresh(matriculation);
			}
		} catch (Exception e) {
			matriculation = null;
			e.printStackTrace();
		}
	}

}
