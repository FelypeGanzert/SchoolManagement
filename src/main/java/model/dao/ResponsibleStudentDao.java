package model.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import db.DBUtil;
import db.DbException;
import model.entites.ResponsibleStudent;

public class ResponsibleStudentDao {

	EntityManager manager;

	public ResponsibleStudentDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}

	public void insert(ResponsibleStudent responsibleStudent) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		// Insert into db
		Query query = manager.createNativeQuery("INSERT INTO responsavel_aluno (parentesco, responsavel_id, aluno_id) VALUES (?, ?, ?)");
		query.setParameter(1, responsibleStudent.getRelationship());
		query.setParameter(2, responsibleStudent.getResponsible().getId());
		query.setParameter(3, responsibleStudent.getStudent().getId());
		query.executeUpdate();
		// Get the id of the relantionship inserted and reflesh data
		query = manager.createNativeQuery("SELECT ra.id FROM responsavel_aluno ra WHERE ra.responsavel_id = ? AND ra.aluno_id = ?");
		Integer id = query.getFirstResult();
		responsibleStudent.setId(id);
		DBUtil.refreshData(responsibleStudent);
		manager.getTransaction().commit();
	}

	public void update(ResponsibleStudent responsibleStudent) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		responsibleStudent = manager.merge(responsibleStudent);
		manager.getTransaction().commit();
	}
	
	public ResponsibleStudent findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		ResponsibleStudent rs = manager.find(ResponsibleStudent.class, id);
		if(rs != null && rs.getExcluded() == null) {
			return rs;
		} else {
			return null;
		}
	}

	public void deleteById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		ResponsibleStudent responsibleStudent = manager.find(ResponsibleStudent.class, id);
		manager.refresh(responsibleStudent);
		responsibleStudent.setExcluded("S");
		responsibleStudent = manager.merge(responsibleStudent);
		manager.getTransaction().commit();
	}

}
