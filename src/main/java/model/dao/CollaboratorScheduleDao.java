package model.dao;

import javax.persistence.EntityManager;

import db.DbException;
import model.entites.CollaboratorSchedule;

public class CollaboratorScheduleDao {
	
	EntityManager manager;
	
	public CollaboratorScheduleDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(CollaboratorSchedule schedule) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		manager.persist(schedule);
		manager.getTransaction().commit();
	}
	
	public void update(CollaboratorSchedule schedule) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		schedule = manager.merge(schedule);
		manager.getTransaction().commit();
	}
	
}
