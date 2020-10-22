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
		// Remove all annotations from student
		Query query = manager.createNativeQuery("UPDATE anotacoes a SET a.excluido = 'S' WHERE a.aluno_id = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		// Remove all contacts
		query = manager.createNativeQuery("UPDATE contato c SET c.excluido = 'S' WHERE contact_id_student = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		// Remove all courses
		query = manager.createNativeQuery("UPDATE cursos c SET c.excluido = 'S' WHERE aluno_id = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		// Get all code parcels of this student
		query = manager.createNativeQuery(
				"SELECT p.numero_documento FROM parcela p \r\n" + 
				"JOIN matricula m ON p.matricula_codigo = m.code\r\n" + 
				"JOIN aluno a ON m.aluno_id = a.id\r\n" + 
				"WHERE a.id = ?;");
		query.setParameter(1, student.getId());
		List<Integer> parcelsCode = (List<Integer>)query.getResultList();
		// Remove all parcels
		query = manager.createNativeQuery("UPDATE parcela p SET p.excluido = 'S' WHERE p.numero_documento IN (:codes)");
		query.setParameter("codes", parcelsCode);
		query.executeUpdate();
		// Remove all matriculations from student
		query = manager.createNativeQuery("UPDATE matricula m SET m.excluido = 'S' WHERE m.aluno_id = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		// remove all associations in ResponsibleStudent for this student
		ResponsibleDao responsibleDao = new ResponsibleDao(DBFactory.getConnection());
		for(Responsible ra : student.getAllResponsibles()) {
			responsibleDao.deleteFromStudent(ra, student);
		}
		manager.flush();
		// Remove student entity
		student.setExcluded("S");
		student = manager.merge(student);
		manager.getTransaction().commit();
	}
	
	public Student findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		Student s = manager.find(Student.class, id);
		if(s != null && s.getExcluded() == null) {
			return s;
		} else {
			return null;
		}
	}
	
	public List<Student> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s where excluido is null", Student.class);
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
        criteria.select(root).where(builder.isNull(root.get("excluded")));
        // This will simulate a EAGER loading, solving the problem of n+1
        root.fetch("contacts", JoinType.LEFT);
        TypedQuery<Student> query = manager.createQuery(criteria);
        return query.getResultList();
	}
	
	public List<Student> findAllWithNameLike(String name) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s where nome like :nome and excluido is null", Student.class);
		query.setParameter("nome", name + "%");
		return query.getResultList();
	}
	
	public Student findByCPF(String cpf) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}			
		TypedQuery<Student> query = manager.createQuery("SELECT s FROM Aluno s where cpf = :cpf and excluido is null", Student.class);
		query.setParameter("cpf", cpf);
		try {
			return query.getSingleResult();
		} catch(NoResultException e) {
		}
		return null;
	}

}