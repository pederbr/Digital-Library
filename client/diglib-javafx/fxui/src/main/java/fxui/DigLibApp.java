package fxui;

import java.io.IOException;
import java.net.http.HttpClient;

import fxui.controllers.ListController;
import fxui.controllers.MainController;
import fxui.controllers.UploadController;
import fxui.controllers.ViewController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * The {@code DigLibApp} class is the main entry point for the Digital Library JavaFX application.
 * It extends the {@link javafx.application.Application} class and overrides the {@code start} method
 * to set up the primary stage with the specified FXML layout.
 */
public class DigLibApp extends Application {
    private HttpClient httpClient;

    public DigLibApp() {
        this(HttpClient.newHttpClient());
    }

    protected DigLibApp(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * The main entry point for all JavaFX applications. The {@code start} method is called after the
     * JavaFX runtime has been initialized.
     *
     * @param stage the primary stage for this application, onto which the application scene can be set.
     *              Applications may create other stages, if needed, but they will not be primary stages.
     * @throws IOException if the FXML file cannot be loaded.
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DigLib.fxml"));
        fxmlLoader.setControllerFactory(param -> createController(param));
        Parent parent = fxmlLoader.load();
        stage.setScene(new Scene(parent));
        stage.setTitle("Digital Library");
        stage.show();
    }

    protected Object createController(Class<?> type) {
        if (type == null) {
            throw new IllegalArgumentException("Controller type cannot be null");
        }
        if (type == MainController.class) {
            return new MainController(httpClient);
        }
        if (type == UploadController.class) {
            return new UploadController();
        }
        if (type == ViewController.class) {
            return new ViewController();
        }
        if (type == ListController.class) {
            return new ListController();
        }
        throw new IllegalArgumentException("Unknown controller type: " + type.getName());
    }

    /**
     * The main method is ignored in JavaFX applications. The JavaFX application lifecycle methods
     * {@code init}, {@code start}, and {@code stop} are called as appropriate. The {@code main} method
     * serves only as an entry point for launching the JavaFX application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}