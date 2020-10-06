package model.dao;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Responsible;
import model.entites.Student;

public class ResponsibleDao {
	
	EntityManager manager;
	
	public ResponsibleDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Responsible responsible) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		manager.persist(responsible);
		manager.getTransaction().commit();
	}
	
	public void update(Responsible responsible) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		responsible = manager.merge(responsible);
		manager.getTransaction().commit();
	}
	
	public void deleteFromStudent(Responsible responsible, Student student) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		boolean openedTransaction = false;
		if(!manager.getTransaction().isActive()) {
			openedTransaction = true;
			manager.getTransaction().begin();
		}
		// Refresh data (because cache...)
		manager.refresh(responsible);
		manager.refresh(student);
		// Check how many students this responsible respond for (not including this student)
		List<Student> otherStudentsResponding = responsible.getAllStudents().stream().filter(s -> s.getId() != student.getId()).collect(Collectors.toList());
		// Remove association between them
		responsible.removeStudent(student);
		manager.flush();
		// Delete responsible if he doesnt have any other student
		if(otherStudentsResponding.size() <= 0) {
			manager.remove(responsible);
		}
		if(openedTransaction) {
			manager.getTransaction().commit();
		}
	}
	
	public Responsible findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		return manager.find(Responsible.class, id);
	}
	
	public List<Responsible> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Responsible> query = manager.createQuery("SELECT r FROM Responsavel r", Responsible.class);
		return query.getResultList();
	}
	
	public List<Responsible> findAllWithNameLike(String name) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Responsible> query = manager.createQuery("SELECT s FROM Responsavel s where nome like :nome", Responsible.class);
		query.setParameter("nome", name + "%");
		return query.getResultList();
	}
	
	public Responsible findByCPF(String cpf) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Responsible> query = manager.createQuery("SELECT s FROM Responsavel s where cpf = :cpf", Responsible.class);
		query.setParameter("cpf", cpf);
		try {
			return query.getSingleResult();
		} catch(NoResultException e) {
		}
		return null;
	}

}