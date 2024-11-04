
// IMPORTS
// These are some classes that may be useful for completing the project.
// You may have to add others.
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebEvent;
import javafx.scene.web.WebView;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;
import javafx.stage.Stage;
import javafx.scene.*;
import javafx.concurrent.Worker.State;
import javafx.concurrent.Worker;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

/**
 * The main class for BrowserProgram. BrowserProgram constructs the JavaFX
 * window and
 * handles interactions with the dynamic components contained therein.
 */
public class BrowserProgram extends Application {
	// INSTANCE VARIABLES
	// These variables are included to get you started.
	private Stage stage = null;
	private BorderPane borderPane = null;
	private WebView view = null;
	private WebEngine webEngine = null;
	private TextField statusbar = null;
	private TextField urlBar = null;
	private Button backButton = null;
	private Button forwardButton = null;
	private Button helpButton = null;

	// HELPER METHODS
	/**
	 * Retrieves the value of a command line argument specified by the index.
	 *
	 * @param index - position of the argument in the args list.
	 * @return The value of the command line argument.
	 */
	private String getParameter(int index) {
		Parameters params = getParameters();
		List<String> parameters = params.getRaw();
		return !parameters.isEmpty() ? parameters.get(index) : "";
	}

	/**
	 * Creates a WebView which handles mouse and some keyboard events, and
	 * manages scrolling automatically, so there's no need to put it into a
	 * ScrollPane.
	 * The associated WebEngine is created automatically at construction time.
	 *
	 * @return browser - a WebView container for the WebEngine.
	 */
	private WebView makeHtmlView() {
		view = new WebView();
		webEngine = view.getEngine();
		return view;
	}

	/**
	 * Generates the status bar layout and text field.
	 *
	 * @return statusbarPane - the HBox layout that contains the statusbar.
	 */
	private HBox makeStatusBar() {
		HBox statusbarPane = new HBox();
		statusbarPane.setPadding(new Insets(5, 4, 5, 4));
		statusbarPane.setSpacing(10);
		statusbarPane.setStyle("-fx-background-color: #336699;");
		statusbar = new TextField();
		HBox.setHgrow(statusbar, Priority.ALWAYS);
		statusbarPane.getChildren().addAll(statusbar);
		return statusbarPane;
	}

	/**
	 * Generates a box for the url and arrows
	 * 
	 * @return urlPane - The Borderpan that contains the url box and back and
	 *         forward arrows
	 */

	public BorderPane makePageHeader() {
		BorderPane pageHeader = new BorderPane();

		BorderPane buttonHolder = new BorderPane();

		// create back and forward arrows
		backButton = new Button("Back");
		forwardButton = new Button("Forward");
		buttonHolder.setLeft(backButton);
		buttonHolder.setRight(forwardButton);

		helpButton = new Button("Help");

		// create url box and styling
		HBox urlBox = new HBox();
		urlBox.setPadding(new Insets(5, 4, 5, 4));
		urlBox.setSpacing(10);
		urlBox.setStyle("-fx-background-color: #336699;");
		urlBar = new TextField(webEngine.getLocation()); // gets url location
		HBox.setHgrow(urlBar, Priority.ALWAYS);
		urlBox.getChildren().addAll(urlBar);

		pageHeader.setLeft(buttonHolder);
		pageHeader.setCenter(urlBox); // set right of pageHeader to urlbox
		pageHeader.setRight(helpButton);
		return pageHeader;
	}

	/*
	 * Load help menu that displays information for the user to help understand
	 * the web browser more effectively.
	 * 
	 * @return root - return root borderpane that displays help information
	 */
	public static BorderPane helpMenu() {
		BorderPane root = new BorderPane();
		BorderPane top = new BorderPane();

		Text text = new Text("HELP MENU:\nRobby Johnson\nCS1131 Lab 3" +
				"\n The Forward and Back buttons" +
				" when clicked \n will changed the page either to" +
				"the last page you \n were on or the first page you were on.");

		root.setTop(top);
		root.setCenter(text);

		return root;
	}

	// REQUIRED METHODS
	/**
	 * The main entry point for all JavaFX applications. The start method is
	 * called after the init method has returned, and after the system is ready
	 * for the application to begin running.
	 *
	 * NOTE: This method is called on the JavaFX Application Thread.
	 *
	 * @param primaryStage - the primary stage for this application, onto which
	 *                     the application scene can be set.
	 */
	@Override
	public void start(Stage primaryStage) {
		stage = primaryStage;

		borderPane = new BorderPane();

		WebView htmlView = makeHtmlView(); // create webpage view
		webEngine.load(getParameter(0)); // load up google

		BorderPane pageHeader = makePageHeader(); // create url bar
		HBox statusBar = makeStatusBar(); // create statusBar

		// Set title of window if no title use url
		stage.setTitle((webEngine.getTitle()==null) ? 
		webEngine.getLocation() : webEngine.getTitle());
		
		// Add components to specific regions of the BorderPane
		borderPane.setTop(pageHeader);
		borderPane.setCenter(helpMenu()); // Open on help menu
		borderPane.setBottom(statusBar); // Add the status bar at the bottom

		// when helpbutton is hovered over displays help text
		Tooltip help = new Tooltip("HELP MENU:\n The Forward and Back buttons" +
				" when clicked \n will changed the page either to" +
				"the last page you \n were on or the first page you were on.");
		Tooltip.install(helpButton, help);

		// Check for url change in textFeild after enter key is pressed
		urlBar.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				// Set the text in the center
				borderPane.setCenter(htmlView);
				// Change webpage
				webEngine.load(urlBar.textProperty().getValue());
				// Set proper url to bar
				urlBar.setText(webEngine.getLocation());
				// Set title of window if no title use url
				stage.setTitle((webEngine.getTitle() == null) 
				? webEngine.getLocation() : webEngine.getTitle());
			}
		});

		// Go to previous webpage and change url location text
		backButton.setOnAction(event -> {
			webEngine.executeScript("history.back()");
			urlBar.setText(webEngine.getLocation());
			// Set title of window if no title use url
			stage.setTitle((webEngine.getTitle() == null) 
			? webEngine.getLocation() : webEngine.getTitle());
		});

		// Go to forward webpage and change url location text
		forwardButton.setOnAction(event -> {
			webEngine.executeScript("history.forward()");
			urlBar.setText(webEngine.getLocation());
			// Set title of window if no title use url
			stage.setTitle((webEngine.getTitle() == null) 
			? webEngine.getLocation() : webEngine.getTitle());
		});

		// Set help menu to center or borderpane
		helpButton.setOnAction(event -> {
			borderPane.setCenter(helpMenu());
		});

		// Trying to detect when mouse is over a link --------------------------
		webEngine.setOnStatusChanged( event -> {
			String data = event.getData( ); // Get event data 
			statusbar.setText(data); // Give data to statusBar
		});

		// create window
		Scene scene = new Scene(borderPane, 500, 500);

		// Set the scene to the stage
		stage.setScene(scene);

		// Show the window
		stage.show();
	}

	/**
	 * The main( ) method is ignored in JavaFX applications.
	 * main( ) serves only as fallback in case the application is launched
	 * as a regular Java application, e.g., in IDEs with limited FX
	 * support.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
