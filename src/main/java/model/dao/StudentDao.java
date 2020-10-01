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
		Query query;
		// get all responsibles that respond only for this student
		query = manager.createNativeQuery(
				"SELECT ra.responsavel_id FROM responsavel_aluno ra \r\n" + 
				"JOIN responsavel r ON ra.responsavel_id = r.id\r\n" + 
				"JOIN responsavel_aluno ra2 ON r.id = ra2.responsavel_id\r\n" + 
				"WHERE ra2.aluno_id = ? GROUP BY ra.responsavel_id HAVING count(ra.aluno_id) = 1");
		query.setParameter(1, student.getId());
		List<Integer> responsiblesId = (List<Integer>)query.getResultList();
		// remove all associations in ResponsibleStudent for this student
		for(Responsible ra : student.getAllResponsibles()) {
			ra.removeStudent(student);
		}
		query = manager.createNativeQuery("DELETE FROM responsavel_aluno ra WHERE ra.aluno_id = ?");
		query.setParameter(1, student.getId());
		query.executeUpdate();
		// Remove student entity
		manager.remove(student);
		manager.flush();
		// remove all responsibles contacts that respond only for this student
		query = manager.createNativeQuery("DELETE FROM contato c WHERE c.contact_id_responsible IN (:ids)");
		query.setParameter("ids", responsiblesId);
		query.executeUpdate();
		// remove all responsibles entitys that respond only for this student
		query = manager.createNativeQuery("DELETE FROM responsavel r WHERE r.id IN (:ids)");
		query.setParameter("ids", responsiblesId);
		query.executeUpdate();
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