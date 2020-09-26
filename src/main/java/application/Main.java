package application;

import gui.DBConnectionURLController;
import gui.LoginController;
import gui.MainViewController;
import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.entites.Collaborator;
import sharedData.Context;
import sharedData.Globe;
import sharedData.State;

public class Main extends Application {

	// private static Scene mainScene;
	// private static Collaborator currentUser;

	private Scene mainScene;
	private Collaborator currentUser;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// Create main Context
		Context mainContext = new Context();
		// Creat main State and put some data in
		State mainState = new State();
		mainState.putItem("mainClass", this);
		mainState.putItem("mainScene", this.mainScene);
		mainState.putItem("currentUser", this.currentUser);
		// Put State in Context and then in Globe
		// I'm using Singleton pattern, and this way a can easily acess datas beetwen
		// controllers
		mainContext.putState("main", mainState);
		Globe.getGlobe().putContext("main", mainContext);

		// 1: we load the screen to user entry the url of database to connect with the
		// database
		Utils.loadView(this, false, FXMLPath.DB_CONNECTION_URL, primaryStage, "Conexão com o Banco de Dados", false,
				x -> {});
	}

	// This method is called by DBConnectionURLController after established
	// connection with database,
	// and when we need to change current user
	public void showLoginForm() {
		currentUser = null;
		Utils.loadView(this, false, FXMLPath.LOGIN, new Stage(), "Login", false, (LoginController controller) -> {
			// Set this Main to allow he to call in future to show Main View
			controller.setMain(this);
		});
	}

	// This is method is called by Login
	public void showMainView(Collaborator collaborator) {
		Main.currentUser = collaborator;
		Utils.loadView(this, false, FXMLPath.MAIN_VIEW, new Stage(), "Gerenciamento Escolar", true,
				(MainViewController controller) -> {
					controller.setMain(this);
				});
	}

}
