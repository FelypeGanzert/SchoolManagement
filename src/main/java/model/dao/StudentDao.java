package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

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
	
	public List<Student> findAllWithContactsLoaded() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		
		CriteriaBuilder builder = manager.getCriteriaBuilder();
        CriteriaQuery<Student> criteria = builder.createQuery(Student.class);
        criteria.distinct(true);
        Root<Student> root = criteria.from(Student.class);
        // This will simulate a EAGER loading, solving the problem of n+1
        root.fetch("contacts", JoinType.LEFT);
        TypedQuery<Student> query = manager.createQuery(criteria);
        return query.getResultList();
	}
	

}
