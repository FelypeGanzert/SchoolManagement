package model.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import db.DbException;
import model.entites.Posts;

public class PostsDao {
	
	EntityManager manager;
	
	public PostsDao(EntityManager manager) {
		// He needs a Connection to acess the database
		this.manager = manager;
	}
	
	public void insert(Posts post) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		manager.persist(post);
		manager.getTransaction().commit();
	}
	
	public void update(Posts post) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();;
		post = manager.merge(post);
		manager.getTransaction().commit();
	}
	
	public void deleteById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		manager.getTransaction().begin();
		Posts post = manager.find(Posts.class, id);
		manager.refresh(post);
		post.setExcluded("S");
		post = manager.merge(post);
		manager.getTransaction().commit();
	}
	
	public Posts findById(Integer id) throws DbException {
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		Posts p = manager.find(Posts.class, id);
		if(p != null && p.getExcluded() == null) {
			return p;
		} else {
			return null;
		}
	}
	
	public List<Posts> findAll() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<Posts> query = manager.createQuery("SELECT c FROM Cargos c where excluido is null", Posts.class);
		return query.getResultList();
	}
	
	public List<String> findAllPosts() throws DbException{
		if(manager == null) {
			throw new DbException("DB Connection not instantiated");
		}
		TypedQuery<String> query = manager.createQuery("SELECT post FROM Cargos c where excluido is null", String.class);
		return query.getResultList();
	}
	
}