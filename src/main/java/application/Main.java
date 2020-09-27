package application;

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
		// Put State in Context and then in Globe.
		// I'm using Singleton pattern, because this way a can easily acess datas beetwen controllers
		mainContext.putState("main", mainState);
		Globe.getGlobe().putContext("main", mainContext);
		// 1: user entry the url of database to try to connect
		Utils.loadView(this, false, FXMLPath.DB_CONNECTION_URL, primaryStage,"Conexão com o Banco de Dados", false,	x -> {});
	}

}