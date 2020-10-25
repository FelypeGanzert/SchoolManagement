package db;

import model.dao.MatriculationDao;
import model.dao.ParcelDao;
import model.dao.ResponsibleDao;
import model.dao.ResponsibleStudentDao;
import model.dao.StudentDao;
import model.entites.Matriculation;
import model.entites.Parcel;
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
			if (entity.getExcluded() != null) {
				throw new DbExceptioneEntityExcluded("Entity has been deleted");
			}
		}
	}

	public static <T> void refreshData(Responsible entity) throws DbException, DbExceptioneEntityExcluded {
		entity = new ResponsibleDao(DBFactory.getConnection()).findById(entity.getId());
		if (entity != null) {
			DBFactory.getConnection().refresh(entity);
			if (entity.getExcluded() != null) {
				throw new DbExceptioneEntityExcluded("Entity has been deleted");
			}
		}
	}

	public static <T> void refreshData(ResponsibleStudent responsibleStudent) throws DbException, DbExceptioneEntityExcluded {
		responsibleStudent = new ResponsibleStudentDao(DBFactory.getConnection())
				.findById(responsibleStudent.getId());
		if (responsibleStudent != null) {
			DBFactory.getConnection().refresh(responsibleStudent);
			if (responsibleStudent.getExcluded() != null) {
				throw new DbExceptioneEntityExcluded("Entity has been deleted");
			}
		}
	}

	public static <T> void refreshData(Matriculation matriculation) throws DbException, DbExceptioneEntityExcluded {
		matriculation = new MatriculationDao(DBFactory.getConnection())
				.findById(matriculation.getCode());
		if (matriculation != null) {
			DBFactory.getConnection().refresh(matriculation);
			if (matriculation.getExcluded() != null) {
				throw new DbExceptioneEntityExcluded("Entity has been deleted");
			}
		}
	}

	public static <T> void refreshData(Parcel parcel) throws DbException, DbExceptioneEntityExcluded {
		parcel = new ParcelDao(DBFactory.getConnection())
				.findById(parcel.getDocumentNumber());
		if (parcel != null) {
			DBFactory.getConnection().refresh(parcel);
			if (parcel.getExcluded() != null) {
				throw new DbExceptioneEntityExcluded("Entity has been deleted");
			}
		}
	}
	
}
