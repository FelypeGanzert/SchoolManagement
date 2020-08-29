package db;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DB {
	
	private static EntityManagerFactory entityManagerFactory = null;
	private static Map<String, String> persistenceMap = new HashMap<String, String>();
	private static String url;
	private static String username = "student";
	private static String password = "student";
	private static String driver = "com.mysql.cj.jdbc.Driver";
	
	public static void setUnits(String url) {
		url = "jdbc:mysql://"+url+"/cadastroprojetos?createDatabaseIfNotExist=true&useTimezone=true&serverTimezone=UTC";
		persistenceMap.put("javax.persistence.jdbc.url", url);
		persistenceMap.put("javax.persistence.jdbc.user", username);
		persistenceMap.put("javax.persistence.jdbc.password", password);
		persistenceMap.put("javax.persistence.jdbc.driver", driver);
	}
	
	public static EntityManagerFactory getFactory() {
		if(entityManagerFactory == null) {
			if(persistenceMap.size() == 0) { throw new DbMissingUnitsException("Units not initialized"); }
			System.out.println(2);
			entityManagerFactory = Persistence.createEntityManagerFactory("school", persistenceMap);
		}
		return entityManagerFactory;
	}
	
	public static void closeFactory() {
		if(entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

}
