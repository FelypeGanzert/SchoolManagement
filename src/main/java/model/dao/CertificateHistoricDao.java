package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.CertificateHistoric;

public class CertificateHistoricDao {
	
	EntityManager manager;
	
	public CertificateHistoricDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(CertificateHistoric certificateHistoric) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		manager.persist(certificateHistoric);
		manager.getTransaction().commit();
	}
	
	public void update(CertificateHistoric certificateHistoric) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		certificateHistoric = manager.merge(certificateHistoric);
		manager.getTransaction().commit();
	}
	
	public CertificateHistoric findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		CertificateHistoric cr = manager.find(CertificateHistoric.class, id);
		if(cr != null && cr.getExcluded() == null) {
			return cr;
		} else {
			return null;
		}
	}
	
	public void delete(CertificateHistoric certificateHistoric) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		certificateHistoric = manager.find(CertificateHistoric.class, certificateHistoric.getId());
		certificateHistoric.setExcluded("S");
		certificateHistoric = manager.merge(certificateHistoric);
		manager.getTransaction().commit();
	}
	
	public List<CertificateHistoric> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<CertificateHistoric> query = manager.createQuery("SELECT cr FROM certificado_historico cr where excluido is null ORDER BY aluno_nome ASC", CertificateHistoric.class);
		return query.getResultList();
	}
}
