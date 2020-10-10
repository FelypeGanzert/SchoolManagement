package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import db.DbException;
import model.entites.Course;
import model.entites.Student;

public class CourseDao {
	
	EntityManager manager;
	
	public CourseDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Course course) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(course);
		manager.getTransaction().commit();
	}
	
	public void update(Course course) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		course = manager.merge(course);
		manager.getTransaction().commit();
	}
	
	public Course findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		return manager.find(Course.class, id);
	}
	
	public void delete(Course course) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		course = manager.find(Course.class, course.getId());
		manager.remove(course);
		manager.getTransaction().commit();
	}
	
	public List<Course> findAllFromStudent(Student student) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
	    CriteriaBuilder criteriaBuilder = manager.getCriteriaBuilder();
		CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
		
		Root<Course> root = criteriaQuery.from(Course.class);
		criteriaQuery.select(root);
		
		criteriaQuery.where(criteriaBuilder.equal(root.get("student"), student.getId()));
		TypedQuery<Course> typedQuery = manager.createQuery(criteriaQuery);
		List<Course> courses = typedQuery.getResultList();
		return courses;
	}
}
