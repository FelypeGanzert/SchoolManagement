package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import db.DBFactory;
import db.DbException;
import model.entites.Responsible;
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
	
	public void deleteById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		Student student = manager.find(Student.class, id);
		manager.refresh(student);
		// Get all code parcels of this student
		Query query = manager.createNativeQuery(
				"SELECT p.numero_documento FROM parcela p \r\n" + 
				"JOIN matricula m ON p.matricula_codigo = m.code\r\n" + 
				"JOIN aluno a ON m.aluno_id = a.id\r\n" + 
				"WHERE a.id = ?;");
		query.setParameter(1, student.getId());
		List<Integer> parcelsCode = (List<Integer>)query.getResultList();
		// Remove all parcels
		query = manager.createNativeQuery("DELETE FROM parcela p WHERE p.numero_documento IN (:codes)");
		query.setParameter("codes", parcelsCode);
		query.executeUpdate();
		// Remove all matriculations from student
		query = manager.createNativeQuery("DELETE FROM matricula m WHERE m.aluno_id = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		//manager.refresh(student);
		// remove all associations in ResponsibleStudent for this student
		ResponsibleDao responsibleDao = new ResponsibleDao(DBFactory.getConnection());
		for(Responsible ra : student.getAllResponsibles()) {
			responsibleDao.deleteFromStudent(ra, student);
		}
		manager.flush();
		// Remove student entity
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
	
	public List<Student> findAllWithNameLike(String name) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s where nome like :nome", Student.class);
		query.setParameter("nome", name + "%");
		return query.getResultList();
	}
	
	public Student findByCPF(String cpf) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s where cpf = :cpf", Student.class);
		query.setParameter("cpf", cpf);
		try {
			return query.getSingleResult();
		} catch(NoResultException e) {
		}
		return null;
	}

}