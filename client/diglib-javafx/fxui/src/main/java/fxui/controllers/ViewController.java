package fxui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Book;
import model.ContentHandler;
import model.FileCreator;
import model.PagedContent;

/**
 * Controller for the view tab that displays book content and metadata.
 * Handles book viewing, downloading and deletion operations.
 */
public class ViewController {
    private static final int PAGE_SIZE = 1000;
    
    @FXML private TextArea displayField;
    @FXML private TextArea metadataField;
    @FXML private Button downloadBook;
    @FXML private Button deleteBook;
    @FXML private Button prevPage;
    @FXML private Button nextPage;
    @FXML private Text errorText;
    @FXML private Text confirmationText;

    private Book selectedBook;
    private PagedContent page;
    private ContentHandler contentHandler;
    private FileCreator fileCreator;

    private MainController mainController;

    public ViewController() {
    }

    /**
     * Initializes the controller by clearing the display.
     */
    @FXML
    public void initialize() {
        clearDisplay();
    }

    /**
     * Sets the main controller and initializes handlers with the provided HTTP client.
     * Note: MainController instance should be immutable to prevent external modifications.
     * 
     * @param mainController the main controller instance (should be immutable)
     * @param client the HTTP client to use for content handling
     */
    public void setMainController(final MainController mainController, final HttpClient client) {
        this.mainController = mainController;
        this.contentHandler = new ContentHandler(client);
        this.fileCreator = new FileCreator();
    }

    /**
     * Clears all display fields and error messages.
     */
    private void clearDisplay() {
        displayField.setText("no book selected");
        metadataField.setText("no book selected");
        errorText.setText("");
        confirmationText.setText("");
    }

    /**
     * Creates and configures a directory chooser dialog.
     * @return configured DirectoryChooser
     */
    protected DirectoryChooser createDirectoryChooser() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Directory to Save the File");
        return directoryChooser;
    }

    /**
     * Handles the book download action, saving the book content to a selected directory.
     */
    @FXML
    public void handleDownload() {
        if (selectedBook == null) {
            clearDisplay();
            return;
        }

        try {
            List<PagedContent> bookContent = contentHandler.readAllPages(selectedBook, PAGE_SIZE);
            Stage directoryStage = new Stage();
            DirectoryChooser directoryChooser = createDirectoryChooser();

            File selectedDirectory = directoryChooser.showDialog(directoryStage);
            if (selectedDirectory != null) {
                fileCreator.createBookFile(selectedBook, selectedDirectory.toPath(), bookContent);
                confirmationText.setText("file successfully downloaded");
            } else {
                errorText.setText("error while selecting directory");
            }
        } catch (IOException | InterruptedException e) {
            errorText.setText("error while reading/creating file");
        }
    }
    
    /**
     * Handles the book deletion action.
     */
    @FXML
    public void handleDelete() {
        if (selectedBook != null) {
            try {
                mainController.deleteBook(selectedBook);
                clearDisplay();
            } catch (RuntimeException e) {
                errorText.setText("Failed to delete book");
            }
        }
    }

    /**
     * Navigates to the next page of the book content if available.
     */
    @FXML
    public void nextPage() {
        if (page != null && page.getPageNumber() < page.getTotalPages() - 1) {
            loadPage(page.getPageNumber() + 1);
        }
    }
    
    /**
     * Navigates to the previous page of the book content if available.
     */
    @FXML
    public void prevPage() {
        if (page != null && page.getPageNumber() > 0) {
            loadPage(page.getPageNumber() - 1);
        }
    }

    /**
     * Loads and displays the specified page of the book content.
     * @param pageNumber the page number to load
     */
    private void loadPage(int pageNumber) {
        try {
            page = contentHandler.readPage(selectedBook.getId(), pageNumber, PAGE_SIZE);
            displayField.setText(page.getContent());
            metadataField.setText(selectedBook.getMetadata() + "\n" + page.getMetaData());
        } catch (IOException | InterruptedException error) {
            displayField.setText("Error reading file");
            metadataField.setText("Error reading metadata");
        }
    }

    /**
     * Sets the confirmation message text.
     * @param text the message to display
     */
    public void setConfirmationText(String text) {
        confirmationText.setText(text);
    }
    
    /**
     * Sets the currently selected book and displays its content.
     * Creates a defensive copy of the book to prevent external modifications.
     * 
     * @param book the book to display, or null to clear the display
     */
    public void setActiveBook(final Book book) {
        if (book == null) {
            clearDisplay();
            selectedBook = null;
            return;
        }
        
        // Create a defensive copy of the book
        selectedBook = new Book();
        selectedBook.setID(book.getId());
        selectedBook.setTitle(book.getTitle());
        selectedBook.setAuthor(book.getAuthor());
        selectedBook.setGenre(book.getGenre());
        selectedBook.setIsbn(book.getIsbn());
        selectedBook.setYear(book.getYear());
        
        errorText.setText("");
        confirmationText.setText("");
        
        try {
            page = contentHandler.readPage(book.getId(), 0, PAGE_SIZE);
            displayField.setText(page.getContent());
            metadataField.setText(selectedBook.getMetadata() + "\n" + page.getMetaData());
        } catch (IOException | InterruptedException e) {
            displayField.setText("Error reading file");
            metadataField.setText("Error reading metadata");
        }
    }

    /**
     * Returns the associated main controller.
     * @return the main controller instance
     */
    public MainController getMainController() {
        return mainController != null ? mainController : null;
    }
}
