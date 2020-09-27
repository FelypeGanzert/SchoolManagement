package application;

import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.application.Application;
import javafx.stage.Stage;
import sharedData.Context;
import sharedData.Globe;
import sharedData.State;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// I'm using Singleton pattern, because this way a can easily acess datas beetwen controllers
		// Create main Context, and main State to put some data to share beetwen controllers
		Context mainContext = new Context();
		State mainState = new State();
		// Put State in Context and then in Globe.
		mainContext.putState("main", mainState);
		Globe.getGlobe().putContext("main", mainContext);
		
		// 1: user entry the url of database to try to connect
		Utils.loadView(this, false, FXMLPath.DB_CONNECTION_URL, primaryStage,"Conexão com o Banco de Dados", false,	x -> {});
	}

}