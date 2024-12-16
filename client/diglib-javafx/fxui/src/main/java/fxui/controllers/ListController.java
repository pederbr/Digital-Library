package fxui.controllers;

import java.util.List;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import model.Book;

/**
 * Controller for the list tab that displays all available books.
 * Handles book selection and display updates.
 */
public class ListController {
    @FXML
    private FlowPane bookDisplay;
    
    @FXML
    private Text displayError;
    
    private MainController mainController;
    private List<Book> bookList;

    /**
     * Default constructor.
     */
    public ListController() {}

    /**
     * Initializes the controller by hiding the error display.
     */
    @FXML
    public void initialize() {
        displayError.setVisible(false);
        displayError.setManaged(false);
    }

    /**
     * Sets the main controller for this list controller.
     * @param mainController the main controller instance
     */
    public void setMainController(final MainController mainController) {
        // Since MainController cannot be defensively copied, we store it directly
        // but make the parameter final to prevent modification
        this.mainController = mainController;
    }

    /**
     * Returns the associated main controller.
     * @return the main controller instance
     */
    public MainController getMainController() {
        return mainController != null ? mainController : null;
    }

    /**
     * Updates the book list and refreshes the view.
     * 
     * @param books list of books to display, null clears the display
     */
    public void updateBookList(List<Book> books) {
        this.bookList = books == null ? List.of() : books;
        updateBookView();
    }

    /**
     * Updates the book view with provided books list.
     */
    private void updateBookView() {
        bookDisplay.getChildren().clear();

        if (bookList.isEmpty()) {
            displayError.setVisible(true);
            displayError.setManaged(true);
            return;
        }

        displayError.setVisible(false);
        displayError.setManaged(false);

        for (Book book : bookList) {
            StringBuilder bookSb = new StringBuilder();
            bookSb.append(book.getTitle() != null ? book.getTitle() : "null")
                 .append("\n")
                 .append("by: ")
                 .append(book.getAuthor() != null ? book.getAuthor() : "null")
                 .append("\n")
                 .append(book.getYear());

            Text bookText = new Text(bookSb.toString());
            ImageView bookImage = new ImageView(new Image(getClass().getResource("/fxui/static/standard.png").toString()));
            bookImage.setFitHeight(100);
            bookImage.setFitWidth(100);

            VBox bookBox = new VBox(bookImage, bookText);
            String safeTitle = book.getTitle() != null ? 
                book.getTitle().replaceAll("[^a-zA-Z0-9]", "") : 
                "nullTitle";
            bookBox.setId(safeTitle + "Button");
            bookBox.setOnMouseClicked(e -> {
                if (mainController != null) {
                    mainController.setActiveBook(book);
                }
            });

            bookDisplay.getChildren().add(bookBox);
        }
    }
}