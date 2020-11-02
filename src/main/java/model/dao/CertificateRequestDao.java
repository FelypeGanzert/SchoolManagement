package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DBFactory;
import db.DbException;
import model.entites.CertificateRequest;

public class CertificateRequestDao {
	
	EntityManager manager;
	
	public CertificateRequestDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(CertificateRequest certificateRequest) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		manager.persist(certificateRequest);
		manager.getTransaction().commit();
	}
	
	public void update(CertificateRequest certificateRequest) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		certificateRequest = manager.merge(certificateRequest);
		manager.getTransaction().commit();
	}
	
	public CertificateRequest findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		CertificateRequest cr = manager.find(CertificateRequest.class, id);
		if(cr != null && cr.getExcluded() == null) {
			return cr;
		} else {
			return null;
		}
	}
	
	public void delete(CertificateRequest certificateRequest) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		certificateRequest = manager.find(CertificateRequest.class, certificateRequest.getId());
		certificateRequest.setExcluded("S");
		certificateRequest = manager.merge(certificateRequest);
		manager.getTransaction().commit();
	}
	
	public List<CertificateRequest> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<CertificateRequest> query = manager.createQuery("SELECT cr FROM certificao_pedido cr where excluido is null ORDER BY data_pedido DESC", CertificateRequest.class);
		return query.getResultList();
	}
	
	public int getNumberOfOpenRequests() {
		TypedQuery<Long> q = DBFactory.getConnection()
				.createQuery("SELECT COUNT(id) FROM certificado_pedido WHERE excluido IS NULL", Long.class);
		int numberOfOpenRequests = (int) (long) q.getSingleResult();
		return numberOfOpenRequests;
	}
}
