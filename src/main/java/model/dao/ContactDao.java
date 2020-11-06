package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Contact;

public class ContactDao {
	
	EntityManager manager;
	
	public ContactDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Contact contact) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(contact);
		manager.getTransaction().commit();
	}
	
	public void update(Contact contact) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		contact = manager.merge(contact);
		manager.getTransaction().commit();
	}
	
	public Contact findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		Contact c = manager.find(Contact.class, id);
		if(c != null && c.getExcluded() == null) {
			return c;
		} else {
			return null;
		}
	}
	
	public void delete(Contact contact) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		contact = manager.find(Contact.class, contact.getId());
		contact.setExcluded("S");
		contact = manager.merge(contact);
		manager.getTransaction().commit();
	}
	
	public List<Contact> findAllFromStudent(int studentId) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Contact> query = manager.createQuery(
				"SELECT c FROM Contato c where excluido is null and contact_id_student = :studentId", Contact.class);
		query.setParameter("studentId", studentId);
		return query.getResultList();
	}
}
