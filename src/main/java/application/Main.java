package application;

import java.io.IOException;

import gui.DBConnectionURLController;
import gui.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import model.entites.Collaborator;

public class Main extends Application {

	private static Scene mainScene;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

//		EntityManagerFactory emf= Persistence.createEntityManagerFactory("school");
//		EntityManager entityManager = emf.createEntityManager();
//		 CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
//
//        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
//        Root<Student> root = criteriaQuery.from(Student.class);
//        criteriaQuery.select(root);
//
//        TypedQuery<Student> typedQuery = entityManager.createQuery(criteriaQuery);
//        List<Student> lista = typedQuery.getResultList();
//        System.out.println("=========== ALUNOS ============");
//        lista.forEach(a -> System.out.println(a.getId() + ", " + a.getNome() + ", " + a.getSituacao()));
//        System.out.println("===============================");
//        entityManager.close();
//		emf.close();

		try {
			// First we load the screen to user entry the url of database
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/DBConnectionURL.fxml"));
			Parent scrollPane = loader.load();
			// dbController will need to call this to show Login Form
			DBConnectionURLController dbController = loader.getController();
			dbController.setMain(this);
			mainScene = new Scene(scrollPane);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Conexão com o Banco de Dados");
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Scene getMainScene() {
		return Main.mainScene;
	}

	public void showLoginForm() {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/Login.fxml"));
			Parent scrollPane = null;
			scrollPane = loader.load();
			// Se this Main to allow he to call in future to show Main View
			LoginController controller = loader.getController();
			controller.setMain(this);

			mainScene = new Scene(scrollPane);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primaryStage = new Stage();
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Login");
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showMainView(Collaborator collaborator) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainView.fxml"));
			ScrollPane scrollPane = null;
			scrollPane = loader.load();
			scrollPane.setFitToHeight(true);
			scrollPane.setFitToWidth(true);

			mainScene = new Scene(scrollPane);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			Stage primaryStage = new Stage();
			primaryStage.setScene(mainScene);
			primaryStage.setTitle("Login");
			primaryStage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
