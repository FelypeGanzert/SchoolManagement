package application;

import gui.DBConnectionURLController;
import gui.LoginController;
import gui.MainViewController;
import gui.util.FxmlPath;
import gui.util.Utils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.entites.Collaborator;

public class Main extends Application {

	
	private static Scene mainScene;
	private static Collaborator currentUser;

	public static Scene getMainScene() {
		return Main.mainScene;
	}
	
	public static Collaborator getCurrentUser() {
		return Main.currentUser;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// First we load the screen to user entry the url of database to connect with the database
		Utils.loadView(this, false, FxmlPath.DB_CONNECTION_URL, primaryStage, "Conexão com o Banco de Dados", false,
				(DBConnectionURLController controller) -> {
					// Set this Main to allow he to call in future to show Login
					controller.setMain(this);
				});
	}

	// This method is called by DBConnectionURLController after established connection with database,
	// and when we need to change current user
	public void showLoginForm() {
		currentUser = null;
		Utils.loadView(this, false, FxmlPath.LOGIN, new Stage(), "Login", false, (LoginController controller) -> {
			// Set this Main to allow he to call in future to show Main View
			controller.setMain(this);
		});
	}
	
	// This is method is called by Login
	public void showMainView(Collaborator collaborator) {
		Main.currentUser = collaborator;
		Utils.loadView(this, false, FxmlPath.MAIN_VIEW, new Stage(), "Gerenciamento Escolar", true,
				(MainViewController controller) -> {
					controller.setMain(this);
				});
	}

}
