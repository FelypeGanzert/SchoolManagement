package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Collaborator;

public class CollaboratorDao {
	
	EntityManager manager;
	
	public CollaboratorDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Collaborator collaborator) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(collaborator);
		manager.getTransaction().commit();
	}
	
	public void update(Collaborator collaborator) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		collaborator = manager.merge(collaborator);
		manager.getTransaction().commit();
	}
	
	public void deleteById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		Collaborator collaborator = manager.find(Collaborator.class, id);
		manager.refresh(collaborator);
		collaborator.setExcluded("S");
		collaborator = manager.merge(collaborator);
		manager.getTransaction().commit();
	}
	
	public Collaborator findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		Collaborator c = manager.find(Collaborator.class, id);
		if(c != null && c.getExcluded() == null) {
			return c;
		} else {
			return null;
		}
	}
	
	public List<Collaborator> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Collaborator> query = manager.createQuery("SELECT c FROM Colaborador c where excluido is null", Collaborator.class);
		return query.getResultList();
	}
	
	public List<String> findAllInitials() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<String> query = manager.createQuery("SELECT initials FROM Colaborador c where excluido is null", String.class);
		return query.getResultList();
	}
	
}