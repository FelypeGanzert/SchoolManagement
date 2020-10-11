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
		manager.remove(collaborator);
		manager.getTransaction().commit();
	}
	
	public Collaborator findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		return manager.find(Collaborator.class, id);
	}
	
	public List<Collaborator> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Collaborator> query = manager.createQuery("SELECT c FROM Colaborador c", Collaborator.class);
		return query.getResultList();
	}
	
	public List<String> findAllInitials() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<String> query = manager.createQuery("SELECT initials FROM Colaborador", String.class);
		return query.getResultList();
	}
	
}