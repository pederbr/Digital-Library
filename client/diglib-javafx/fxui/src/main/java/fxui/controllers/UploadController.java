package fxui.controllers;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.GenreReader;

/**
 * Controller for the upload tab that handles book submission.
 * Validates and processes book metadata and content uploads.
 */
public class UploadController {
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private ComboBox<String> genreListField;
    @FXML private TextField yearField;
    @FXML private TextField ISBNField;
    @FXML private Button uploadBook;
    @FXML private Button uploadContent;
    @FXML private Text titleError;
    @FXML private Text authorError;
    @FXML private Text genreEmptyError;
    @FXML private Text genreInvalidError;
    @FXML private Text yearError;
    @FXML private Text ISBNError;
    @FXML private Text contentError;
    @FXML private Text fileError;


    private final List<Text> errorTexts;
    private final List<Text> activeErrors;
    private List<String> genres;
    private File content;
    private FileChooser fileChooser;

    private MainController mainController;

    /**
     * Default constructor that initializes empty lists and file chooser.
     */
    public UploadController() {
        this.errorTexts = new ArrayList<>();
        this.activeErrors = new ArrayList<>();
        this.genres = new ArrayList<>();
        this.content = null;
        this.fileChooser = new FileChooser(); 
    }

    /**
     * Constructor with predefined genres list.
     * @param genres list of available genres
     */
    public UploadController(final List<String> genres) {
        this.errorTexts = new ArrayList<>();
        this.activeErrors = new ArrayList<>();
        this.genres = new ArrayList<>(genres);
        this.content = null;
    }
    
    /**
     * Initializes the controller by setting up error texts and genre combo box.
     */
    @FXML
    public void initialize() {
        setupErrorTexts();
        setupGenreComboBox();
    }

    /**
     * Sets the main controller and initializes genre list with the provided HTTP client.
     * Note: MainController instance should be immutable to prevent external modifications.
     * 
     * @param mainController the main controller instance (should be immutable)
     * @param client the HTTP client to use for genre loading
     * @throws IOException if genre loading fails
     * @throws InterruptedException if genre loading is interrupted
     */
    public void setMainController(final MainController mainController, final HttpClient client) throws IOException, InterruptedException {
        this.mainController = mainController;
        if(genres.isEmpty()) {
            loadGenres(client);
        }
    }

    /**
     * Loads genre list from server.
     * @param client HTTP client to use for loading
     */
    private void loadGenres(HttpClient client) throws IOException, InterruptedException {
        try {
            this.genres = new GenreReader(client).readGenres();
            if (genreListField != null) {
                genreListField.setItems(FXCollections.observableArrayList(genres));
            }
        } catch (IOException | InterruptedException e) {
            this.genres = new ArrayList<>();
            if (genreListField != null) {
                genreListField.setItems(FXCollections.observableArrayList());
            }
            System.err.println("Failed to load genres: " + e.getMessage());
        }    
    }

    /**
     * Sets up error text visibility management.
     */
    private void setupErrorTexts() {
        errorTexts.addAll(List.of(
            titleError, authorError, genreEmptyError, genreInvalidError,
            yearError, ISBNError, contentError, fileError
        ));

        errorTexts.forEach(error -> {
            error.setVisible(false);
            error.setManaged(false);
        });
    }

    /**
     * Configures the genre combo box with autocomplete functionality.
     */
    private void setupGenreComboBox() {
        genreListField.setItems(FXCollections.observableArrayList(genres));
        genreListField.setEditable(true);

        PauseTransition pause = new PauseTransition(Duration.millis(200));
        
        genreListField.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            pause.stop();
            
            pause.setOnFinished(event -> filterGenres(newText));
            pause.playFromStart();
        });
    }

    /**
     * Filters genre list based on user input.
     * @param typedText text to filter by
     */
    private void filterGenres(String typedText) {
        if (typedText == null || typedText.isEmpty()) {
            genreListField.setItems(FXCollections.observableArrayList(genres));
            return;
        }

        List<String> filteredList = genres.stream()
            .filter(genre -> genre.toLowerCase().startsWith(typedText.toLowerCase()))
            .collect(Collectors.toList());
        
        if (!genreListField.getItems().equals(filteredList)) {
            genreListField.setItems(FXCollections.observableArrayList(filteredList));
        }
    }

    /**
     * Handles file selection for book content.
     */
    @FXML
    public void handleFileUpload() { 
        try {
            fileChooser.setTitle("Select File to Upload");
            File selectedFile = fileChooser.showOpenDialog(new Stage());
            if (selectedFile == null) {
                activeErrors.clear();
                activeErrors.add(contentError);
                updateErrors(true);
                return;
            }
            uploadContent.setText(selectedFile.getName());
            content = selectedFile;
        } catch (Exception e) {
            activeErrors.clear();
            activeErrors.add(contentError);
            updateErrors(true);
        }
    }

    /**
     * Handles book upload action with metadata and content.
     */
    @FXML
    public void handleUpload() { 
        if (mainController == null) {
            activeErrors.clear();
            activeErrors.add(fileError);
            updateErrors(true);
            return;
        }

        String title = titleField.getText();
        String author = authorField.getText();
        String genre = genreListField.getValue() != null ? genreListField.getValue() : "";
        String ISBN = ISBNField.getText();
        String yearText = yearField.getText();

        updateErrors(false);
        activeErrors.clear();

        if (!validateInputs(title, author, genre, yearText, ISBN)) {
            updateErrors(true);
            clearFields();
            return;
        }

        try {
            ensureContent();
            int year = Integer.parseInt(yearText);
            mainController.uploadBook(title, author, genre, ISBN, year, content);
            clearFields();
        } catch (IOException e) {
            activeErrors.add(fileError);
            updateErrors(true);
        }
    }

    /**
     * Validates the input fields for a book upload.
     * 
     * @param title The book title to validate - must not be empty
     * @param author The book author to validate - must not be empty
     * @param genre The book genre to validate - must not be empty and must be in the predefined genres list
     * @param yearText The publication year as text - must be a valid integer between 0 and 2025
     * @param ISBN The book's ISBN to validate - must be exactly 13 digits
     * 
     * @return true if all inputs are valid, false if any validation fails
     * 
     * Validation rules:
     * - Title cannot be empty
     * - Author cannot be empty
     * - Genre must be non-empty and match a predefined genre
     * - Year must be a valid integer between 0-2025
     * - ISBN must be exactly 13 digits
     * 
     * When validation fails, corresponding error messages are added to activeErrors.
     */
    private boolean validateInputs(String title, String author, String genre, String yearText, String ISBN) {
        boolean isValid = true;

        if (title.isEmpty()) {
            activeErrors.add(titleError);
            isValid = false;
        }
        if (author.isEmpty()) {
            activeErrors.add(authorError);
            isValid = false;
        }
        if (genre.isEmpty()) {
            activeErrors.add(genreEmptyError);
            isValid = false;
        } else if (!genres.contains(genre)) {
            activeErrors.add(genreInvalidError);
            isValid = false;
        }
        try {
            int year = Integer.parseInt(yearText);
            if (year < 0 || year > 2025) {  
                activeErrors.add(yearError);
                isValid = false;
            }
        } catch (NumberFormatException e) {
            activeErrors.add(yearError);
            isValid = false;
        }
        if (!ISBN.matches("[0-9]{13}")) {
            activeErrors.add(ISBNError);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Ensures book content exists, creates empty content if none provided.
     */
    private void ensureContent() throws IOException {
        if (content == null) {
            content = File.createTempFile("temp", ".txt");
            try (FileWriter writer = new FileWriter(content, StandardCharsets.UTF_8)) {
                writer.write("this book is empty");
            }
            uploadContent.setText(content.getName());
        }
    }

    /**
     * Updates visibility of error messages.
     * @param visible whether errors should be visible
     */
    private void updateErrors(Boolean visible) {
        if (activeErrors.isEmpty()) {
            errorTexts.forEach(error -> {
                if (error != null) {
                    error.setVisible(false);
                    error.setManaged(false);
                }
            });
            return;
        }

        activeErrors.forEach(error -> {
            if (error != null) {
                error.setVisible(visible);
                error.setManaged(visible);
            }
        });

        errorTexts.stream()
            .filter(error -> error != null && !activeErrors.contains(error))
            .forEach(error -> {
                error.setVisible(false);
                error.setManaged(false);
            });
    }

    /**
     * Clears all input fields and resets form state.
     */
    private void clearFields() {
        titleField.clear();
        authorField.clear();
        genreListField.getSelectionModel().clearSelection();
        ISBNField.clear();
        yearField.clear();
        uploadContent.setText("Upload file from local computer");
        content = null;
        mainController.setConfirmationText("");
    }
    
    /**
     * Returns the associated main controller.
     * @return the main controller instance
     */
    public MainController getMainController() {
        return mainController != null ? mainController : null;
    }
}