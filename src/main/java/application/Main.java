package application;

import java.util.Locale;
import java.util.TimeZone;

import gui.util.FXMLPath;
import gui.util.Utils;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		Locale.setDefault(new Locale("pt", "BR"));
		// Was seeted Fortaleza to solve summer hour problem
		TimeZone.setDefault(TimeZone.getTimeZone("America/Fortaleza"));
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		// 1: user entry the url of database to try to connect
		Utils.loadView(this, false, FXMLPath.DB_CONNECTION_URL, primaryStage,"Conexão com o Banco de Dados", false,	x -> {});
	}

}