package model.dao;

import javax.persistence.EntityManager;

import db.DbException;
import model.entites.AgreementParcel;

public class ParcelAgreementDao {
	
	EntityManager manager;
	
	public ParcelAgreementDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(AgreementParcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(parcel);
		manager.getTransaction().commit();
	}
	
	public void update(AgreementParcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		parcel = manager.merge(parcel);
		manager.getTransaction().commit();
	}
	
	public AgreementParcel findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		AgreementParcel p = manager.find(AgreementParcel.class, id);
		if(p != null && p.getExcluded() == null) {
			return p;
		} else {
			return null;
		}
	}
	
	public void delete(AgreementParcel parcel) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		parcel = manager.find(AgreementParcel.class, parcel.getDocumentNumber());
		parcel.setExcluded("S");
		parcel = manager.merge(parcel);
		manager.getTransaction().commit();
	}
	
}
