package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import db.DbException;
import model.entites.Contact;
import model.entites.Student;

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
		return manager.find(Contact.class, id);
	}
	
	public void delete(Contact contact) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		contact = manager.find(Contact.class, contact.getId());
		manager.remove(contact);
		manager.getTransaction().commit();
	}
	
	public List<Contact> findAllFromStudent(Student student) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
	    CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Contact> criteriaQuery = criteriaBuilder.createQuery(Contact.class);
		
		Root<Contact> root = criteriaQuery.from(Contact.class);
		criteriaQuery.select(root);
		
		criteriaQuery.where(criteriaBuilder.equal(root.get("student"), student.getId()));
		TypedQuery<Contact> typedQuery = manager.createQuery(criteriaQuery);
		List<Contact> contacts = typedQuery.getResultList();
		return contacts;
	}
}
