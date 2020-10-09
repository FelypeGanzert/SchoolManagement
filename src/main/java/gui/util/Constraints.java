package gui.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class Constraints {

	public static void cpfAutoComplete(TextField txt) {
		setTextFieldMaxLength(txt, 14);
		txt.lengthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (newValue.intValue() <= 14) {
							String value = txt.getText();
							value = value.replaceAll("[^0-9]", "");
							value = value.replaceFirst("(\\d{3})(\\d)", "$1.$2");
							value = value.replaceFirst("(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3");
							value = value.replaceFirst("(\\d{3})\\.(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3-$4");
							value = value.replaceFirst("(\\d{3})\\.(\\d{3})\\.(\\d{3})\\-(\\d{2})(\\d)", "$1.$2.$3.$4");
							txt.setText(value);
							positionCaret(txt);
						}

					}
				});
			}
		});
	}

	public static void rgAutoComplete(TextField txt) {
		setTextFieldMaxLength(txt, 12);
		txt.lengthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (newValue.intValue() <= 14) {
							String value = txt.getText();
							value = value.replaceAll("[^0-9]", "");
							value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3-$4");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})\\-(\\d{1})(\\d)", "$1.$2.$3.$4");
							txt.setText(value);
							positionCaret(txt);
						}

					}
				});
			}
		});
	}
	
	public static void dateAutoComplete(TextField txt) {
		setTextFieldMaxLength(txt, 12);
		txt.lengthProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						if (newValue.intValue() <= 14) {
							String value = txt.getText();
							value = value.replaceAll("[^0-9]", "");
							value = value.replaceFirst("(\\d{2})(\\d)", "$1.$2");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3-$4");
							value = value.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})\\-(\\d{1})(\\d)", "$1.$2.$3.$4");
							txt.setText(value);
							positionCaret(txt);
						}

					}
				});
			}
		});
	}
	
	public static String getOnlyDigitsValue(TextField field) {
		String result = field.getText();
		if (result == null) {
			return null;
		}
		return result.replaceAll("[^0-9]", "");
	}
	
	// Contacts
	public static String formatNumberContact(String number) {
		if (number.length() == 8) { // 1234-5678
			return number.replaceFirst("(\\d{4})(\\d)", "$1-$2");
		}
		if (number.length() == 9) { // 91234-5678
			return number.replaceFirst("(\\d{5})(\\d)", "$1-$2");
		}
		if (number.length() == 10) { // (41)1234-5678
			return number.replaceFirst("(\\d{2})(\\d{4})(\\d)", "($1) $2-$3");
		}
		if (number.length() == 11) { // (41)91234-5678
			return number.replaceFirst("(\\d{2})(\\d{5})(\\d)", "($1) $2-$3");
		}
		return number;
	}

	// set the position of cursor to the end
	public static void positionCaret(TextField txt) {
		Platform.runLater(() -> {
			try {
				if (txt.getText().length() != 0) {
					txt.positionCaret(txt.getText().length());
				}
			} catch (Exception e) {

			}
		});
	}
	
	public static void setTextFieldNoWhiteSpace(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.contains(" ")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldAlwaysUpperCase(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(newValue.toUpperCase());
			}
		});
	}
	
	public static void setTextFieldAlwaysLowerCase(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(newValue.toLowerCase());
			}
		});
	}

	public static void setTextFieldInteger(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldIntegerYear(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d{0,4}")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void setTextFieldMaxLength(TextField txt, int max) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.length() > max) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDouble(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d*([\\.]\\d*)?")) {
				txt.setText(oldValue);
			}
		});
	}
}