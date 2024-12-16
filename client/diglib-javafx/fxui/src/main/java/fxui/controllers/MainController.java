package fxui.controllers;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import model.Book;
import model.BookHandler;


/**
 * Main controller that coordinates between different tabs and handles
 * communication with the backend server.
 */
public class MainController {
    @FXML private TabPane tabPane;
    @FXML private Tab listTab;
    @FXML private Tab viewTab;
    @FXML private Tab uploadTab;
    
    @FXML private ListController listTabController;
    @FXML private ViewController viewTabController;
    @FXML private UploadController uploadTabController;

    private final HttpClient client;
    
    private final BookHandler bookHandler;
    private List<Book> bookList;

    /**
     * Default constructor that creates a new HTTP client.
     */
    public MainController() {
        this(HttpClient.newHttpClient());
    }

    /**
     * Constructor with custom HTTP client.
     * @param httpClient the HTTP client to use
     */
    public MainController(HttpClient httpClient) {
        this.client = httpClient;
        this.bookHandler = new BookHandler(httpClient);
    }

    /**
     * Returns the HTTP client used by this controller.
     * @return the HTTP client instance
     */
    public HttpClient getHttpClient() {
        return client;
    }

    /**
     * Initializes the controller by loading FXML files and setting up relationships.
     */
    @FXML
    public void initialize() {

        FXMLLoader listLoader = new FXMLLoader(getClass().getResource("/fxui/ListTab.fxml"));
        FXMLLoader viewLoader = new FXMLLoader(getClass().getResource("/fxui/ViewTab.fxml"));
        FXMLLoader uploadLoader = new FXMLLoader(getClass().getResource("/fxui/UploadTab.fxml"));

        try {
            // Load the FXML files
            listTab.setContent(listLoader.load());
            viewTab.setContent(viewLoader.load());
            uploadTab.setContent(uploadLoader.load());

            // Get the controllers
            listTabController = listLoader.getController();
            viewTabController = viewLoader.getController();
            uploadTabController = uploadLoader.getController();

            // Set up the controller relationships
            listTabController.setMainController(this);
            viewTabController.setMainController(this, client);
            uploadTabController.setMainController(this, client);

            loadBooks();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to load FXML files", e);
        }
    }

    /**
     * Loads books from the server and updates the book list display.
     */
    private void loadBooks() {
        try {
            List<Book> books = bookHandler.getAllBooks();
            bookList = books.stream()
                .map(book -> (Book) book)
                .collect(Collectors.toList());
            
            if (listTabController != null) {
                listTabController.updateBookList(bookList);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Couldn't load bookList from server: " + e.getMessage());
        }
    }

    /**
     * Sets the currently active book for viewing and switches to the view tab.
     * 
     * @param book the book to display
     */
    public void setActiveBook(Book book) {
        if (viewTabController != null) {
            viewTabController.setActiveBook(book);
        }
        tabPane.getSelectionModel().select(viewTab);
    }

    /**
     * Deletes a book from the library.
     * @param book the book to delete
     */
    public void deleteBook(Book book) {
        try {
            if (bookHandler.deleteBook(book.getId())) {
                bookList.remove(book);
                listTabController.updateBookList(bookList);
                tabPane.getSelectionModel().select(listTab);
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error deleting book: " + e.getMessage());
        }
    }

    /**
     * Sets confirmation text in the view tab.
     * @param text the text to display
     */
    public void setConfirmationText(String text) {
        viewTabController.setConfirmationText(text);
    }

    /**
     * Uploads a new book with the given metadata and content.
     * 
     * @param title the book title
     * @param author the book author
     * @param genre the book genre
     * @param ISBN the book ISBN
     * @param year the publication year
     * @param content the book content file
     * @throws IOException if the upload fails
     */
    public void uploadBook(String title, String author, String genre, String ISBN, int year, File content) throws IOException {
        try {
            int id = bookHandler.postBook(title, author, genre, ISBN, year, content);
            if (id != -1) {
                Book newBook = new Book();
                newBook.setTitle(title);
                newBook.setAuthor(author);
                newBook.setGenre(genre);
                newBook.setIsbn(ISBN);
                newBook.setYear(year);
                newBook.setID(id);
                
                bookList.add(newBook);
                listTabController.updateBookList(bookList);
                
                if (content.getName().startsWith("temp") && !content.delete()) {
                    System.err.println("Warning: Failed to delete temporary file: " + content.getAbsolutePath());
                }
                
                tabPane.getSelectionModel().select(listTab);
            }
        } catch (InterruptedException e) {
            throw new IOException("Failed to upload book", e);
        }
    }

    /**
     * Returns the current list of books.
     * @return list of books in the library
     */
    public List<Book> getBookList() {
        return bookList != null ? new ArrayList<>(bookList) : new ArrayList<>();
    }

    /**
     * Sets the current list of books.
     * @param bookList the new list of books
     */
    public void setBookList(final List<Book> bookList) {
        this.bookList = bookList != null ? new ArrayList<>(bookList) : new ArrayList<>();
    }
    
}
