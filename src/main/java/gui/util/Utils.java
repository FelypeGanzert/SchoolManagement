package gui.util;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class Utils {
	
	// To patterns
	public static String formatCPF(String value) {
		try {
			String formatedValue;
			formatedValue = value.replaceAll("[^0-9]", "");
			formatedValue = formatedValue.replaceFirst("(\\d{3})(\\d)", "$1.$2");
			formatedValue = formatedValue.replaceFirst("(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3");
			formatedValue = formatedValue.replaceFirst("(\\d{3})\\.(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3-$4");
			formatedValue = formatedValue.replaceFirst("(\\d{3})\\.(\\d{3})\\.(\\d{3})\\-(\\d{2})(\\d)", "$1.$2.$3.$4");
			return formatedValue;
		} catch(Exception e) {
			return value;
		}
	}
	
	public static String formatRG(String value) {
		try {
			String formatedValue;
			formatedValue = value.replaceAll("[^0-9]", "");
			formatedValue = formatedValue.replaceFirst("(\\d{2})(\\d)", "$1.$2");
			formatedValue = formatedValue.replaceFirst("(\\d{2})\\.(\\d{3})(\\d)", "$1.$2.$3");
			formatedValue = formatedValue.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})(\\d)", "$1.$2.$3-$4");
			formatedValue = formatedValue.replaceFirst("(\\d{2})\\.(\\d{3})\\.(\\d{3})\\-(\\d{1})(\\d)", "$1.$2.$3.$4");
			return formatedValue;
		} catch(Exception e) {
			return value;
		}
	}

	// To numbers
	private static final DecimalFormatSymbols DOLAR = new DecimalFormatSymbols(Locale.US);
	public static final DecimalFormat DINHEIRO_DOLAR = new DecimalFormat("¤ ###,###,##0.00", DOLAR);
	private static final Locale BRAZIL = new Locale("pt", "BR");
	private static final DecimalFormatSymbols REAL = new DecimalFormatSymbols(BRAZIL);
	public static final DecimalFormat DINHEIRO_REAL = new DecimalFormat("¤ ###,###,##0.00", REAL);

	public static String pointSeparator(Integer value) {
		DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator('.');
		formatter.setDecimalFormatSymbols(symbols);
		return formatter.format(value);
	}

	public static String formatCurrentMoney(Double value, DecimalFormat coin) {
		return coin.format(value);
	}

	public static Integer tryParseToInt(String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double tryParseToDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	// To Screens
	public static Stage currentStage(ActionEvent event) {
		return (Stage) ((Node) event.getSource()).getScene().getWindow();
	}

	public static synchronized <Tclass, T> void loadView(Tclass currentClass, boolean showAndWait, String FXMLPath,
			Stage parentStage, String windowTitle, boolean resizable, Consumer<T> initializingAction) {
		try {
			FXMLLoader loader = new FXMLLoader(currentClass.getClass().getResource(FXMLPath));
			Parent parent = loader.load();
			if (parent instanceof ScrollPane) {
				((ScrollPane) parent).setFitToHeight(true);
				((ScrollPane) parent).setFitToWidth(true);
			}
			Stage dialogStage = new Stage();
			dialogStage.setTitle(windowTitle);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(parentStage);
			dialogStage.setResizable(resizable);

			Scene scene = new Scene(parent);
			scene.getStylesheets()
					.add(currentClass.getClass().getResource("/application/application.css").toExternalForm());
			dialogStage.setScene(scene);

			T controller = loader.getController();
			initializingAction.accept(controller);
			if(showAndWait) {
				dialogStage.showAndWait();
			} else {
				dialogStage.show();
			}
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Alerts.showAlert("IllegalStateException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR, null);
		}
	}
	
	public static <T, Tclass> void addTab(Tclass currentClass, String contentPath, String tabTitle, TabPane tabPane, Consumer<T> initializingAction) {
		Tab tab = new Tab();
		try {
			FXMLLoader loader = new FXMLLoader(currentClass.getClass().getResource(contentPath));
			VBox newContent = loader.load();
			tab.setContent(newContent);
			tab.setText(tabTitle);
			//tab.setStyle(newContent.getStyle());
			T controller = loader.getController();
			initializingAction.accept(controller);
		} catch (IOException e) {
			e.printStackTrace();
			Alerts.showAlert("IOException", "Erro ao exibir tela", e.getMessage(),AlertType.ERROR, null);
		} catch (IllegalStateException e) {
			e.printStackTrace();
			Alerts.showAlert("IllegalStateException", "Erro ao exibir tela", e.getMessage(), AlertType.ERROR, null);
		}
		tabPane.getTabs().add(tab);
	}

	// To Tables
	public static <T, T2> void setCellValueFactory(TableColumn<T, T2> tableColumn, String property) {
		tableColumn.setCellValueFactory(new PropertyValueFactory<>(property));
	}

	public static <T> void formatTableColumnDate(TableColumn<T, Date> tableColumn, String format) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Date> cell = new TableCell<T, Date>() {
				private SimpleDateFormat sdf = new SimpleDateFormat(format);

				@Override
				protected void updateItem(Date item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
					} else {
						setText(sdf.format(item));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void formatTableColumnDoubleCurrency(TableColumn<T, Double> tableColumn) {
		tableColumn.setCellFactory(column -> {
			TableCell<T, Double> cell = new TableCell<T, Double>() {
				@Override
				protected void updateItem(Double item, boolean empty) {
					super.updateItem(item, empty);
					if (empty || item == null) {
						setText(null);
					} else {
						setText(formatCurrentMoney(item, DINHEIRO_REAL));
					}
				}
			};
			return cell;
		});
	}

	public static <T> void initButtons(TableColumn<T, T> tableColumn, int size, String svgIcon, String className,
			BiConsumer<T, ActionEvent> buttonAction) {
		final int COLUMN_ICON_SPACE = 20;
		tableColumn.setMinWidth(size + COLUMN_ICON_SPACE);

		Callback<TableColumn<T, T>, TableCell<T, T>> cellFactory = new Callback<TableColumn<T, T>, TableCell<T, T>>() {
			@Override
			public TableCell<T, T> call(final TableColumn<T, T> param) {
				final TableCell<T, T> cell = new TableCell<T, T>() {
					private final Button btn = Utils.createIconButton(svgIcon, size, className);
					{
						btn.setOnAction((ActionEvent event) -> {
							T data = getTableView().getItems().get(getIndex());
							buttonAction.accept(data, event);
						});
					}

					@Override
					public void updateItem(T item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(btn);
						}
					}
				};
				return cell;
			}
		};
		tableColumn.setCellFactory(cellFactory);
	}

	// To Buttons
	public static Button createIconButton(String svgAbsolutePath, int size, String iconClass) {
		SVGPath path = new SVGPath();
		path.setContent(svgAbsolutePath);
		Bounds bounds = path.getBoundsInLocal();

		// scale to size size x size (max)
		double scaleFactor = size / Math.max(bounds.getWidth(), bounds.getHeight());
		path.setScaleX(scaleFactor);
		path.setScaleY(scaleFactor);
		path.getStyleClass().add("button-icon");

		Button button = new Button();
		button.setPickOnBounds(true); // make sure transparent parts of the button register clicks too
		button.setGraphic(path);
		button.setAlignment(Pos.CENTER);
		button.getStyleClass().add("icon-button");
		button.getStyleClass().add(iconClass);
		return button;
	}

	// To UI fields
	public static void formatDatePicker(DatePicker datePicker, String format) {
		datePicker.setConverter(new StringConverter<LocalDate>() {

			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(format);
			{
				datePicker.setPromptText(format.toLowerCase());
			}

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});
	}

}