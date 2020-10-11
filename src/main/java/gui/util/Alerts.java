package gui.util;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
public class Alerts {
	public static void showAlert(String title, String header, String content, AlertType type) {
		Alert alert = new Alert(type);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(content);
		alert.showAndWait();
	}
	
	public static Alert showProcessingScreen() {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Processando...");
		alert.setHeaderText("Aguarde");
		alert.setContentText("Trabalhando nisso");
		alert.show();
		return alert;
	}
}
