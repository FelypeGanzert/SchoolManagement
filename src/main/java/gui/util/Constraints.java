package gui.util;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;

public class Constraints {

	public static void cpf(TextField txt) {
		maxField(txt, 14);
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

	public static void rg(TextField txt) {
		maxField(txt, 12);
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
	
	public static void date(TextField txt) {
		maxField(txt, 12);
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
	
	public static String onlyDigitsValue(TextField field) {
		String result = field.getText();
		if (result == null) {
			return null;
		}
		return result.replaceAll("[^0-9]", "");
	}

	public static void maxField(TextField txt, Integer length) {
		txt.textProperty().addListener((observableValue, oldValue, newValue) -> {
			if (newValue == null || newValue.length() > length) {
				txt.setText(oldValue);
			}
		});
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
	
	public static void noWhiteSpace(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.contains(" ")) {
				txt.setText(oldValue);
			}
		});
	}
	
	public static void alwaysUpperCase(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null) {
				txt.setText(newValue.toUpperCase());
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

	public static void setTextFieldDayPeriod(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && !newValue.matches("\\d{0,2}|(?:\\d{1,2}-){0,1}\\d{0,2}")) {
				txt.setText(oldValue);
			}
		});
	}

	public static void setTextFieldDbLimitation(TextField txt) {
		txt.textProperty().addListener((obs, oldValue, newValue) -> {
			if (newValue != null && newValue.contains("#")) {
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