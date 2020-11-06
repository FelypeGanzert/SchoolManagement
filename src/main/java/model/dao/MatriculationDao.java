package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

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
		Matriculation m =  manager.find(Matriculation.class, id);
		if(m != null && m.getExcluded() == null) {
			return m;
		} else {
			return null;
		}
	}

	public void delete(Matriculation matriculation) throws DbException {
		if (manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		matriculation = manager.find(Matriculation.class, matriculation.getCode());
		// Remove all parcels
		Query query = manager.createNativeQuery("UPDATE parcela p SET excluido = 'S' WHERE p.matricula_codigo = ?");
		query.setParameter(1, matriculation.getCode());
		query.executeUpdate();
		// Remove all agreement parcels
		query = manager.createNativeQuery("UPDATE parcela_acordo p SET excluido = 'S' WHERE p.matricula_codigo = ?");
		query.setParameter(1, matriculation.getCode());
		query.executeUpdate();
		// Remove all agreement
		query = manager.createNativeQuery("UPDATE acordo a SET excluido = 'S' WHERE a.matricula_codigo = ?");
		query.setParameter(1, matriculation.getCode());
		query.executeUpdate();
		// Remove matriculation
		matriculation.setExcluded("S");
		matriculation = manager.merge(matriculation);
		manager.getTransaction().commit();
	}
	
	public List<Matriculation> findAllFromStudent(int studentId) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Matriculation> query = manager.createQuery(
				"SELECT m FROM Matricula m where excluido is null and aluno_id = :studentId", Matriculation.class);
		query.setParameter("studentId", studentId);
		return query.getResultList();
	}

}