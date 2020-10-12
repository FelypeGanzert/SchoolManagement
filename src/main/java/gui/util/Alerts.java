package gui.util;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
public class Alerts {
	public static void showAlert(String title, String header, String content, AlertType type, Stage owner) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.initOwner(owner); // This sets the owner of this Dialog
		alert.showAndWait();
	}
	
	public static Alert showProcessingScreen(Stage owner) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Processando...");
		alert.setHeaderText("Aguarde");
		alert.setContentText("Trabalhando nisso");
		alert.initOwner(owner);
		alert.show();
		return alert;
	}
}
