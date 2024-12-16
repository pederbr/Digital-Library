package fxui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import model.Book;
import model.BookHandler;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
public class MainControllerTest {

    private MainController mainController;
    
    @Mock(lenient = true) 
    private BookHandler mockBookHandler;
    @Mock
    private ListController mockListController;
    @Mock
    private ViewController mockViewController;
    @Mock
    private UploadController mockUploadController;
    
    private TabPane tabPane;
    private Tab listTab;
    private Tab viewTab;
    private Tab uploadTab;

    private List<Book> testBooks;
    private static final String VALID_ISBN = "9780123456789"; // 13-digit ISBN

    @Start
    private void start(Stage stage) {
        tabPane = new TabPane();
        listTab = new Tab("List");
        viewTab = new Tab("View");
        uploadTab = new Tab("Upload");
        
        tabPane.getTabs().addAll(listTab, viewTab, uploadTab);
        
        Scene scene = new Scene(tabPane);
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
        testBook.setIsbn(VALID_ISBN);
        testBook.setYear(2024);
        testBooks.add(testBook);

        // Initialize controller
        mainController = new MainController();
        
        // Inject dependencies
        TestUtils.setPrivateField(mainController, "bookHandler", mockBookHandler);
        TestUtils.setPrivateField(mainController, "listTabController", mockListController);
        TestUtils.setPrivateField(mainController, "viewTabController", mockViewController);
        TestUtils.setPrivateField(mainController, "uploadTabController", mockUploadController);
        TestUtils.setPrivateField(mainController, "tabPane", tabPane);
        TestUtils.setPrivateField(mainController, "listTab", listTab);
        TestUtils.setPrivateField(mainController, "viewTab", viewTab);
        TestUtils.setPrivateField(mainController, "uploadTab", uploadTab);
        TestUtils.setPrivateField(mainController, "bookList", new ArrayList<>(testBooks));
    }

    @Test
    void testSetActiveBook() {
        Book book = testBooks.get(0);
        mainController.setActiveBook(book);

        verify(mockViewController).setActiveBook(book);
        assertEquals(viewTab, tabPane.getSelectionModel().getSelectedItem());
    }

    @Test
    void testDeleteBook_Successful() throws Exception {
        Book book = testBooks.get(0);
        when(mockBookHandler.deleteBook(book.getId())).thenReturn(true);

        mainController.deleteBook(book);

        verify(mockBookHandler).deleteBook(book.getId());
        verify(mockListController).updateBookList(anyList());
        assertEquals(listTab, tabPane.getSelectionModel().getSelectedItem());
        assertFalse(mainController.getBookList().contains(book));
    }

    @Test
    void testDeleteBook_Failed() throws Exception {
        Book book = testBooks.get(0);
        when(mockBookHandler.deleteBook(book.getId())).thenReturn(false);

        mainController.deleteBook(book);

        verify(mockBookHandler).deleteBook(book.getId());
        verify(mockListController, never()).updateBookList(anyList());
        assertTrue(mainController.getBookList().contains(book));
    }

    @Test
    void testUploadBook_Successful() throws Exception {
        File mockFile = mock(File.class);
        when(mockFile.getName()).thenReturn("test.pdf");
        when(mockBookHandler.postBook(anyString(), anyString(), anyString(), eq(VALID_ISBN), anyInt(), any(File.class)))
            .thenReturn(2);

        mainController.uploadBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockFile);

        verify(mockBookHandler).postBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockFile);
        verify(mockListController).updateBookList(anyList());
        assertEquals(listTab, tabPane.getSelectionModel().getSelectedItem());
        
        List<Book> updatedBooks = mainController.getBookList();
        assertEquals(2, updatedBooks.size());
        Book newBook = updatedBooks.get(1);
        assertEquals("New Book", newBook.getTitle());
        assertEquals("New Author", newBook.getAuthor());
        assertEquals(2, newBook.getId());
    }

    @Test
    void testUploadBook_TempFile() throws Exception {
        File mockTempFile = mock(File.class);
        when(mockTempFile.getName()).thenReturn("temp_12345.pdf");
        when(mockBookHandler.postBook(anyString(), anyString(), anyString(), eq(VALID_ISBN), anyInt(), any(File.class)))
            .thenReturn(2);

        mainController.uploadBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockTempFile);

        verify(mockTempFile).delete();
    }

    @Test
    void testUploadBook_Failed() throws Exception {
        File mockFile = mock(File.class);
        
        // Set up expectation for postBook to throw exception
        doThrow(new IOException("Upload failed"))
            .when(mockBookHandler)
            .postBook(anyString(), anyString(), anyString(), eq(VALID_ISBN), anyInt(), any(File.class));

        assertThrows(IOException.class, () -> {
            mainController.uploadBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockFile);
        });

        verify(mockListController, never()).updateBookList(anyList());
        assertEquals(1, mainController.getBookList().size());
    }

    @Test
    void testLoadBooks_Successful() throws Exception {
        when(mockBookHandler.getAllBooks()).thenReturn(testBooks);
        
        TestUtils.invokePrivateMethod(mainController, "loadBooks");
        
        verify(mockListController).updateBookList(testBooks);
        assertEquals(testBooks, mainController.getBookList());
    }

    @Test
    void testLoadBooks_Failed() throws Exception {
        when(mockBookHandler.getAllBooks()).thenThrow(new IOException("Test exception"));
        
        TestUtils.invokePrivateMethod(mainController, "loadBooks");
        
        verify(mockListController, never()).updateBookList(anyList());
    }

    @Test
    void testConstructor_WithDefaultHttpClient() {
        MainController controller = new MainController();
        assertFalse(TestUtils.getPrivateField(controller, "client") == null);
        assertFalse(TestUtils.getPrivateField(controller, "bookHandler") == null);
    }

    @Test
    void testConstructor_WithCustomHttpClient() {
        HttpClient mockClient = mock(HttpClient.class);
        MainController controller = new MainController(mockClient);
        assertEquals(mockClient, TestUtils.getPrivateField(controller, "client"));
    }

    @Test
    void testInitialize_LoaderException() {
        MainController controller = new MainController();
        // Set invalid FXML locations to trigger exception
        assertThrows(RuntimeException.class, () -> {
            controller.initialize();
        });
    }

    @Test
    void testSetConfirmationText() {
        String testText = "Test confirmation";
        mainController.setConfirmationText(testText);
        verify(mockViewController).setConfirmationText(testText);
    }

    @Test
    void testSetBookList() {
        List<Book> newBookList = new ArrayList<>();
        Book newBook = new Book();
        newBook.setTitle("New Test Book");
        newBookList.add(newBook);

        mainController.setBookList(newBookList);
        assertEquals(newBookList, mainController.getBookList());
    }

    @Test
    void testDeleteBook_ThrowsException() throws Exception {
        Book book = testBooks.get(0);
        when(mockBookHandler.deleteBook(book.getId())).thenThrow(new IOException("Delete failed"));

        mainController.deleteBook(book);

        verify(mockBookHandler).deleteBook(book.getId());
        verify(mockListController, never()).updateBookList(anyList());
        assertTrue(mainController.getBookList().contains(book));
    }

    @Test
    void testUploadBook_InterruptedException() throws Exception {
        File mockFile = mock(File.class);
        when(mockBookHandler.postBook(anyString(), anyString(), anyString(), eq(VALID_ISBN), anyInt(), any(File.class)))
            .thenThrow(new InterruptedException("Upload interrupted"));

        assertThrows(IOException.class, () -> {
            mainController.uploadBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockFile);
        });

        verify(mockListController, never()).updateBookList(anyList());
        assertEquals(1, mainController.getBookList().size());
    }

    @Test
    void testUploadBook_InvalidIdResponse() throws Exception {
        File mockFile = mock(File.class);
        when(mockBookHandler.postBook(anyString(), anyString(), anyString(), eq(VALID_ISBN), anyInt(), any(File.class)))
            .thenReturn(-1);

        mainController.uploadBook("New Book", "New Author", "New Genre", VALID_ISBN, 2024, mockFile);

        verify(mockListController, never()).updateBookList(anyList());
        assertEquals(1, mainController.getBookList().size());
    }

    @Test
    void testLoadBooks_NullListController() throws Exception {
        when(mockBookHandler.getAllBooks()).thenReturn(testBooks);
        TestUtils.setPrivateField(mainController, "listTabController", null);
        
        TestUtils.invokePrivateMethod(mainController, "loadBooks");
        
        assertEquals(testBooks, mainController.getBookList());
    }

    @Test
    void testSetActiveBook_NullViewController() throws Exception {
        TestUtils.setPrivateField(mainController, "viewTabController", null);
        Book book = testBooks.get(0);
        
        mainController.setActiveBook(book);
        
        assertEquals(viewTab, tabPane.getSelectionModel().getSelectedItem());
    }
}

class TestUtils {
    public static void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
    
    public static void invokePrivateMethod(Object object, String methodName, Object... args) throws Exception {
        Class<?>[] argTypes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            argTypes[i] = args[i].getClass();
        }
        java.lang.reflect.Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
        method.setAccessible(true);
        method.invoke(object, args);
    }
    
    public static Object getPrivateField(Object object, String fieldName) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
            return null;
        }
    }
}