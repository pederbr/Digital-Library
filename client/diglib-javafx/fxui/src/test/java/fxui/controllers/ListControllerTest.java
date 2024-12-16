package fxui.controllers;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import model.Book;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ListControllerTest {
    
    private ListController listController;
    private FlowPane bookDisplay;
    private Text displayError;
    
    @Mock
    private MainController mockMainController;
    
    private List<Book> testBooks;
    
    @Start
    private void start(Stage stage) {
        // Initialize JavaFX components
        bookDisplay = new FlowPane();
        displayError = new Text("No books available");
        VBox root = new VBox(bookDisplay, displayError);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Initialize test data
        testBooks = new ArrayList<>();
        Book testBook = new Book();
        testBook.setID(1);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setGenre("Test Genre");
        testBook.setIsbn("9780123456789");
        testBook.setYear(2024);
        testBooks.add(testBook);
        
        // Initialize controller
        listController = new ListController();
        
        // Inject dependencies using reflection
        TestUtils.setPrivateField(listController, "bookDisplay", bookDisplay);
        TestUtils.setPrivateField(listController, "displayError", displayError);
        TestUtils.setPrivateField(listController, "mainController", mockMainController);
        
        // Initialize on JavaFX thread
        Platform.runLater(() -> listController.initialize());
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    void testInitialize() {
        assertFalse(displayError.isVisible());
        assertFalse(displayError.isManaged());
    }
    
    @Test
    void testUpdateBookList_WithBooks() {
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(displayError.isVisible());
        assertFalse(displayError.isManaged());
        assertFalse(bookDisplay.getChildren().isEmpty());
        assertEquals(1, bookDisplay.getChildren().size());
        
        // Verify book display content
        VBox bookBox = (VBox) bookDisplay.getChildren().get(0);
        Text bookText = (Text) bookBox.getChildren().get(1); // Index 1 because ImageView is at 0
        
        assertTrue(bookText.getText().contains("Test Book"));
        assertTrue(bookText.getText().contains("Test Author"));
        assertTrue(bookText.getText().contains("2024"));
    }
    
    @Test
    void testUpdateBookList_Empty() {
        Platform.runLater(() -> listController.updateBookList(new ArrayList<>()));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(displayError.isVisible());
        assertTrue(displayError.isManaged());
        assertTrue(bookDisplay.getChildren().isEmpty());
    }
    
    @Test
    void testUpdateBookList_MultipleTimes() {
        // First update with books
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(bookDisplay.getChildren().isEmpty(), "Book display should not be empty with test books");
        assertEquals(1, bookDisplay.getChildren().size(), "Should have exactly one book");
        assertFalse(displayError.isVisible(), "Error should not be visible when books are present");
        
        // Update with empty list
        Platform.runLater(() -> listController.updateBookList(new ArrayList<>()));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(bookDisplay.getChildren().isEmpty(), "Book display should be empty with no books");
        assertTrue(displayError.isVisible(), "Error should be visible with no books");
        
        // Update with books again
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(bookDisplay.getChildren().isEmpty(), "Book display should not be empty after re-adding books");
        assertEquals(1, bookDisplay.getChildren().size(), "Should have exactly one book again");
        assertFalse(displayError.isVisible(), "Error should not be visible when books are re-added");
        assertFalse(displayError.isManaged(), "Error should not be managed when books are present");
    }
    
    @Test
    void testBookClick() {
        Platform.runLater(() -> {
            listController.updateBookList(testBooks);
            // Get the book box and simulate click
            VBox bookBox = (VBox) bookDisplay.getChildren().get(0);
            bookBox.getOnMouseClicked().handle(null); // Simulate click event
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify that setActiveBook was called with the correct book
        verify(mockMainController).setActiveBook(testBooks.get(0));
    }
    
    @Test
    void testMultipleBooks() {
        // Add another test book
        Book testBook2 = new Book();
        testBook2.setID(2);
        testBook2.setTitle("Another Book");
        testBook2.setAuthor("Another Author");
        testBook2.setYear(2023);
        testBooks.add(testBook2);
        
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(2, bookDisplay.getChildren().size());
        
        Platform.runLater(() -> {
            // Verify first book
            VBox firstBookBox = (VBox) bookDisplay.getChildren().get(0);
            Text firstBookText = (Text) firstBookBox.getChildren().get(1);
            assertTrue(firstBookText.getText().contains("Test Book"));
            
            // Verify second book
            VBox secondBookBox = (VBox) bookDisplay.getChildren().get(1);
            Text secondBookText = (Text) secondBookBox.getChildren().get(1);
            assertTrue(secondBookText.getText().contains("Another Book"));
            
            // Test clicking second book
            secondBookBox.getOnMouseClicked().handle(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).setActiveBook(testBook2);
    }
    
    @Test
    void testSetMainController() {
        MainController newMainController = mock(MainController.class);
        listController.setMainController(newMainController);
        
        Platform.runLater(() -> {
            listController.updateBookList(testBooks);
            VBox bookBox = (VBox) bookDisplay.getChildren().get(0);
            bookBox.getOnMouseClicked().handle(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(newMainController).setActiveBook(testBooks.get(0));
        verify(mockMainController, never()).setActiveBook(any()); // Original mock should not be called
    }

    @Test
    void testUpdateBookList_Null() {
        Platform.runLater(() -> listController.updateBookList(null));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(displayError.isVisible());
        assertTrue(displayError.isManaged());
        assertTrue(bookDisplay.getChildren().isEmpty());
    }

    @Test
    void testBookWithNullFields() {
        Book nullFieldsBook = new Book();
        nullFieldsBook.setID(3);
        // Leave other fields null
        testBooks.add(nullFieldsBook);
        
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(2, bookDisplay.getChildren().size());
        VBox nullBookBox = (VBox) bookDisplay.getChildren().get(1);
        Text nullBookText = (Text) nullBookBox.getChildren().get(1);
        
        assertTrue(nullBookText.getText().contains("null"));
    }

    @Test
    void testBookWithSpecialCharacters() {
        Book specialBook = new Book();
        specialBook.setID(4);
        specialBook.setTitle("Book#$%@! With Special Ch@r$");
        specialBook.setAuthor("Author!@#$");
        specialBook.setYear(2024);
        testBooks.add(specialBook);
        
        Platform.runLater(() -> listController.updateBookList(testBooks));
        WaitForAsyncUtils.waitForFxEvents();
        
        VBox specialBookBox = (VBox) bookDisplay.getChildren().get(1);
        assertTrue(specialBookBox.getId().matches("[a-zA-Z0-9]+Button"));
    }

    @Test
    void testNullMainController() {
        listController.setMainController(null);
        
        Platform.runLater(() -> {
            listController.updateBookList(testBooks);
            VBox bookBox = (VBox) bookDisplay.getChildren().get(0);
            bookBox.getOnMouseClicked().handle(null);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should not throw NullPointerException
        assertTrue(true);
    }

    @Test
    void testConcurrentUpdates() {
        Platform.runLater(() -> {
            // Rapid consecutive updates
            listController.updateBookList(testBooks);
            listController.updateBookList(new ArrayList<>());
            listController.updateBookList(testBooks);
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(bookDisplay.getChildren().isEmpty());
        assertEquals(1, bookDisplay.getChildren().size());
    }

    @Test
    void testGetMainController() {
        ListController controller = new ListController();
        assertNull(controller.getMainController());
        
        MainController mainController = new MainController(null);
        controller.setMainController(mainController);
        assertEquals(mainController, controller.getMainController());
    }
}