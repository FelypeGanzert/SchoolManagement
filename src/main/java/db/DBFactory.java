package db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DBFactory {
	
	private static EntityManagerFactory entityManagerFactory = null;
	private static Map<String, String> persistenceMap = new HashMap<String, String>();
	private static EntityManager entityManager = null;
	private static String username = "student";
	private static String password = "student";
	private static String driver = "com.mysql.cj.jdbc.Driver";
	
	public static void setUnits(String url) {
		url = "jdbc:mysql://"+url+"/cadastroprojetos?createDatabaseIfNotExist=true&autoReconnect=true&useTimezone=true&serverTimezone=UTC";
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", username);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", driver);
	}
	
	public static EntityManagerFactory getFactory() {
		if(entityManagerFactory == null) {
			if(persistenceMap.size() == 0) { throw new DbMissingUnitsException("Units not initialized"); }
			entityManagerFactory = Persistence.createEntityManagerFactory("school", persistenceMap);
		}
		return entityManagerFactory;
	}
	
	public static void closeFactory() {
		if(entityManager != null) {
			entityManager.close();
		}
		if(entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}
	
	public static EntityManager getConnection() {
		if(entityManagerFactory == null) {
			DBFactory.getFactory();
		}
		if(entityManager == null) {
			entityManager = entityManagerFactory.createEntityManager();
		}
		return entityManager;
	}

}
