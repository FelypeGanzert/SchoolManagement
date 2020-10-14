package model.dao;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import db.DbException;
import model.entites.Matriculation;

public class MatriculationDao {

	EntityManager manager;

	public MatriculationDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}

	public void insert(Matriculation matriculation) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		;
		manager.persist(matriculation);
		manager.getTransaction().commit();
	}

	public void update(Matriculation matriculation) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		;
		matriculation = manager.merge(matriculation);
		manager.getTransaction().commit();
	}

	public Matriculation findById(Integer id) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		return manager.find(Matriculation.class, id);
	}

	public void delete(Matriculation matriculation) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		System.out.println("============================================================ Started here delelte matriculation");
		manager.getTransaction().begin();
		matriculation = manager.find(Matriculation.class, matriculation.getCode());
		// Remove all parcels
//		Query query = manager.createNativeQuery("DELETE FROM parcela p WHERE p.matricula_codigo = ?");
//		query.setParameter(1, matriculation.getCode());
//		query.executeUpdate();
		// Remove matriculation
		manager.remove(matriculation);
		manager.getTransaction().commit();
		System.out.println("============================================================ Ended here delelte matriculation");
	}

}
