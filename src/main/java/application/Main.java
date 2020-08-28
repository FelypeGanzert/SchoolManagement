package application;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import model.entites.Student;

public class Main extends Application {
	
	private static Scene mainScene;

	public static void main(String[] args) {
		launch(args);
	}
		
	@Override
	public void start(Stage primaryStage) {
		
		EntityManagerFactory emf= Persistence.createEntityManagerFactory("school");
		EntityManager entityManager = emf.createEntityManager();
		
		CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();

        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);

        criteriaQuery.select(root);

        TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
        List<Student> lista = typedQuery.getResultList();
        System.out.println("=========== ALUNOS ============");
        lista.forEach(a -> System.out.println(a.getId() + ", " + a.getNome() + ", " + a.getSituacao()));
        System.out.println("===============================");

        entityManager.close();
		emf.close();
		
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = loader.load();
			
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);
			
			mainScene = new Scene(scrollPane);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Escola");
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return Main.mainScene;
	}


}
