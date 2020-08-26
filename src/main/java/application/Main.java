package application;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

	public static void main(String[] args) {

		EntityManagerFactory emf= Persistence.createEntityManagerFactory("school");
		EntityManager manager = emf.createEntityManager();
		
		manager.close();
		emf.close();
	}

}
