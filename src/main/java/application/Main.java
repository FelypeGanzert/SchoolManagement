package application;

import java.io.IOException;
import java.util.function.Consumer;

import gui.DBConnectionURLController;
import gui.LoginController;
import gui.MainViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
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
		// First we load the screen to user entry the url of database
		// that will try to connect with the database
		loadMainScreen("/gui/DBConnectionURL.fxml", primaryStage, "Conexão com o Banco de Dados", false,
				(DBConnectionURLController controller) -> {
					// Set this Main to allow he to call in future to show Login
					controller.setMain(this);
				});
	}

	// This method is called by DBConnectionURLController after established connection with database,
	// and when we need to change current user
	public void showLoginForm() {
		currentUser = null;
		loadMainScreen("/gui/Login.fxml", new Stage(), "Login", false, (LoginController controller) -> {
			// Set this Main to allow he to call in future to show Main View
			controller.setMain(this);
		});

	}
	
	// This is method is called by Login
	public void showMainView(Collaborator collaborator) {
		Main.currentUser = collaborator;
		loadMainScreen("/gui/MainView.fxml", new Stage(), "Gerenciamento Escolar", true,
				(MainViewController controller) -> {
					controller.setMain(this);
					controller.setCurrentUser(collaborator);
				});
	}

	// Auxiliar method to show: DBConnectionURL, Login, MainView
	private <T> void loadMainScreen(String FXMLPath, Stage stage, String stageTitle,
			boolean resizable, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(FXMLPath));
			Parent parent = loader.load();
			if (parent instanceof ScrollPane) {
				((ScrollPane) parent).setFitToHeight(true);
				((ScrollPane) parent).setFitToWidth(true);
			}
			mainScene = new Scene(parent);
			mainScene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			stage.setScene(mainScene);
			stage.setTitle(stageTitle);
			stage.setResizable(resizable);
			stage.show();

			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
