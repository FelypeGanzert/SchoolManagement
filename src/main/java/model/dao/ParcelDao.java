package model.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Parcel;

public class ParcelDao {
	
	EntityManager manager;
	
	public ParcelDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Parcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(parcel);
		manager.getTransaction().commit();
	}
	
	public void update(Parcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		parcel = manager.merge(parcel);
		manager.getTransaction().commit();
	}
	
	public Parcel findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		Parcel p = manager.find(Parcel.class, id);
		if(p != null && p.getExcluded() == null) {
			return p;
		} else {
			return null;
		}
	}
	
	public void delete(Parcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		parcel = manager.find(Parcel.class, parcel.getDocumentNumber());
		parcel.setExcluded("S");
		parcel = manager.merge(parcel);
		manager.getTransaction().commit();
	}
	
	public List<Parcel> getAllOverdue(Date endDate) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		TypedQuery<Parcel> query = manager.createQuery("SELECT p FROM Parcela p " +
				"WHERE p.situation = 'ABERTA' AND " + 
				"p.dateParcel < '" + sdf.format(endDate) +"' "+ 
				"ORDER BY p.matriculation.student.name asc, p.dateParcel ASC",
				Parcel.class);
		return query.getResultList();
	}
	
	public List<Parcel> getAllOverdue(Date startDate, Date endDate) throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		TypedQuery<Parcel> query = manager.createQuery("SELECT p FROM Parcela p " +
				"WHERE p.situation = 'ABERTA' AND " + 
				"p.dateParcel > '" + sdf.format(startDate) +"' AND"+ 
				"p.dateParcel < '" + sdf.format(endDate) +"' "+ 
				"ORDER BY p.matriculation.student.name asc, p.dateParcel ASC",
				Parcel.class);
		return query.getResultList();
	}
	
}
