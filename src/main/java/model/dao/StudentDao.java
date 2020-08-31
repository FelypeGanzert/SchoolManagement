package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Student;

public class StudentDao {
	
	EntityManager manager;
	
	public StudentDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Student student) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(student);
		manager.getTransaction().commit();
	}
	
	public void update(Student student) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		student = manager.merge(student);
		manager.getTransaction().commit();
		
	}
	
	public void remove(Student student) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		student = manager.find(Student.class, student.getId());
		manager.remove(student);
		manager.getTransaction().commit();
	}
	
	public Student findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		return manager.find(Student.class, id);
	}
	
	public List<Student> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s", Student.class);
		return query.getResultList();
	}
	

}
