package db;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class DB {
	
	private static EntityManagerFactory entityManagerFactory = null;
	
	public static EntityManagerFactory getFactory() {
		if(entityManagerFactory == null) {
			entityManagerFactory = Persistence.createEntityManagerFactory("school");
		}
		return entityManagerFactory;
	}
	
	public static void closeFactory() {
		if(entityManagerFactory != null) {
			entityManagerFactory.close();
		}
	}

}
