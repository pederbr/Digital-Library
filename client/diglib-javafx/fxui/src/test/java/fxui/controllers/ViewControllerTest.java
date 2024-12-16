package fxui.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.Book;
import model.ContentHandler;
import model.FileCreator;
import model.PagedContent;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class ViewControllerTest {
    
    private ViewController viewController;
    
    @Mock
    private MainController mockMainController;
    
    @Mock
    private ContentHandler mockContentHandler;
    
    @Mock
    private FileCreator mockFileCreator;
    
    private TextArea displayField;
    private TextArea metadataField;
    private Button downloadBook;
    private Button deleteBook;
    private Button prevPage;
    private Button nextPage;
    private Text errorText;
    private Text confirmationText;
    
    @TempDir
    Path tempDir;
    
    private Book testBook;
    private PagedContent firstPage;
    private PagedContent middlePage;
    private PagedContent lastPage;
    
    @Start
    private void start(Stage stage) {
        // Initialize JavaFX components
        displayField = new TextArea();
        metadataField = new TextArea();
        downloadBook = new Button("Download");
        deleteBook = new Button("Delete");
        prevPage = new Button("Previous");
        nextPage = new Button("Next");
        errorText = new Text();
        confirmationText = new Text();
        
        VBox root = new VBox(
            displayField, metadataField, downloadBook, deleteBook,
            prevPage, nextPage, errorText, confirmationText
        );
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Initialize test data
        testBook = new Book();
        testBook.setID(1);
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setGenre("Test Genre");
        testBook.setIsbn("1234567890123");
        testBook.setYear(2024);
        
        // Create test pages with realistic content and metadata
        firstPage = new PagedContent(
            "First page content",  // content
            0,                    // pageNumber
            3,                    // totalPages
            1000,                // pageSize
            3000                 // totalSize
        );
        
        middlePage = new PagedContent(
            "Middle page content",
            1,
            3,
            1000,
            3000
        );
        
        lastPage = new PagedContent(
            "Last page content",
            2,
            3,
            1000,
            3000
        );
        
        // Initialize controller
        viewController = new ViewController();
        
        // Inject mock dependencies
        TestUtils.setPrivateField(viewController, "contentHandler", mockContentHandler);
        TestUtils.setPrivateField(viewController, "fileCreator", mockFileCreator);
        TestUtils.setPrivateField(viewController, "mainController", mockMainController);
        
        // Inject JavaFX components
        TestUtils.setPrivateField(viewController, "displayField", displayField);
        TestUtils.setPrivateField(viewController, "metadataField", metadataField);
        TestUtils.setPrivateField(viewController, "downloadBook", downloadBook);
        TestUtils.setPrivateField(viewController, "deleteBook", deleteBook);
        TestUtils.setPrivateField(viewController, "prevPage", prevPage);
        TestUtils.setPrivateField(viewController, "nextPage", nextPage);
        TestUtils.setPrivateField(viewController, "errorText", errorText);
        TestUtils.setPrivateField(viewController, "confirmationText", confirmationText);
        
        Platform.runLater(() -> viewController.initialize());
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    void testInitialize() {
        assertEquals("no book selected", displayField.getText());
        assertEquals("no book selected", metadataField.getText());
        assertEquals("", errorText.getText());
        assertEquals("", confirmationText.getText());
    }
    
    @Test
    void testSetActiveBook() throws Exception {
        when(mockContentHandler.readPage(eq(1), eq(0), anyInt())).thenReturn(firstPage);
        
        Platform.runLater(() -> viewController.setActiveBook(testBook));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals(firstPage.getContent(), displayField.getText());
        assertTrue(metadataField.getText().contains(testBook.getMetadata()));
        assertTrue(metadataField.getText().contains("Page 1 of 3"));
        assertTrue(metadataField.getText().contains("Reading time:"));
        assertEquals("", errorText.getText());
    }
    
    @Test
    void testSetActiveBook_Error() throws Exception {
        when(mockContentHandler.readPage(anyInt(), anyInt(), anyInt()))
            .thenThrow(new IOException("Read error"));
        
        Platform.runLater(() -> viewController.setActiveBook(testBook));
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("Error reading file", displayField.getText());
        assertEquals("Error reading metadata", metadataField.getText());
    }
    

    @Test
    void testPagination_PreviousPage() throws Exception {
        // Setup starting at middle page
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        TestUtils.setPrivateField(viewController, "page", middlePage);
        
        // Stub only what we'll use
        when(mockContentHandler.readPage(eq(1), eq(0), anyInt())).thenReturn(firstPage);
        
        // Test backward navigation
        Platform.runLater(() -> viewController.prevPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify backward navigation
        verify(mockContentHandler).readPage(eq(1), eq(0), anyInt());
        assertEquals(firstPage.getContent(), displayField.getText());
        assertTrue(metadataField.getText().contains("Page 1 of 3"));
    }
    
    @Test
    void testPagination_NextPageAtEnd() throws Exception {
        // Initial setup - at the last page
        Platform.runLater(() -> {
            try {
                TestUtils.setPrivateField(viewController, "selectedBook", testBook);
                TestUtils.setPrivateField(viewController, "page", lastPage);
                // Update display field directly since we're bypassing setActiveBook
                displayField.setText(lastPage.getContent());
            } catch (Exception ex) {
                fail("Setup failed: " + ex.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Try to go forward from last page
        Platform.runLater(() -> viewController.nextPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify we stay on the last page
        verify(mockContentHandler, never()).readPage(anyInt(), anyInt(), anyInt());
        assertEquals(lastPage.getContent(), displayField.getText());
    }

    @Test
    void testPagination_PrevPageAtStart() throws Exception {
        // Initial setup - at the first page
        Platform.runLater(() -> {
            try {
                TestUtils.setPrivateField(viewController, "selectedBook", testBook);
                TestUtils.setPrivateField(viewController, "page", firstPage);
                // Update display field directly since we're bypassing setActiveBook
                displayField.setText(firstPage.getContent());
            } catch (Exception ex) {
                fail("Setup failed: " + ex.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Try to go backward from first page
        Platform.runLater(() -> viewController.prevPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify we stay on the first page
        verify(mockContentHandler, never()).readPage(anyInt(), anyInt(), anyInt());
        assertEquals(firstPage.getContent(), displayField.getText());
    } 
    
    @Test
    void testPagination_Boundaries() throws Exception {
        // Test at first page
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        TestUtils.setPrivateField(viewController, "page", firstPage);
        
        Platform.runLater(() -> viewController.prevPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockContentHandler, never()).readPage(anyInt(), eq(-1), anyInt());
        
        // Test at last page
        TestUtils.setPrivateField(viewController, "page", lastPage);
        
        Platform.runLater(() -> viewController.nextPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockContentHandler, never()).readPage(anyInt(), eq(3), anyInt());
    }
    
    @Test
    void testHandleDownload() throws Exception {
        // Setup
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        List<PagedContent> content = Arrays.asList(firstPage, middlePage, lastPage);
        when(mockContentHandler.readAllPages(any(Book.class), anyInt())).thenReturn(content);
        
        // Create a temporary directory for the test
        File testDir = tempDir.toFile();
        
        // Mock the DirectoryChooser
        DirectoryChooser mockChooser = mock(DirectoryChooser.class);
        when(mockChooser.showDialog(any())).thenReturn(testDir);
        
        // Create a spy of viewController to inject our mock DirectoryChooser
        ViewController spyController = spy(viewController);
        doReturn(mockChooser).when(spyController).createDirectoryChooser();
        
        // Execute the test
        Platform.runLater(() -> spyController.handleDownload());
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify
        verify(mockContentHandler).readAllPages(eq(testBook), anyInt());
        verify(mockFileCreator).createBookFile(eq(testBook), eq(testDir.toPath()), eq(content));
        assertEquals("file successfully downloaded", confirmationText.getText());
    }

    @Test
    void testHandleDownload_Error() throws Exception {
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        
        when(mockContentHandler.readAllPages(eq(testBook), anyInt()))
            .thenThrow(new IOException("Download error"));
        
        Platform.runLater(() -> viewController.handleDownload());
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("error while reading/creating file", errorText.getText());
    }
    
    @Test
    void testHandleDelete() {
        try {
            TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        } catch (Exception e) {
            fail("Error setting up test");
        }
        
        Platform.runLater(() -> viewController.handleDelete());
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).deleteBook(testBook);
    }

    @Test
    void testHandleDownload_NoDirectorySelected() throws Exception {
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        
        // Mock directory chooser to return null (user canceled selection)
        DirectoryChooser mockChooser = mock(DirectoryChooser.class);
        when(mockChooser.showDialog(any())).thenReturn(null);
        
        ViewController spyController = spy(viewController);
        doReturn(mockChooser).when(spyController).createDirectoryChooser();
        
        Platform.runLater(() -> spyController.handleDownload());
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockFileCreator, never()).createBookFile(any(), any(), any());
        assertEquals("error while selecting directory", errorText.getText());
    }

    @Test
    void testHandleDelete_Error() throws Exception {
        // Setup test book and initial display content
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        String testContent = "Test content";
        displayField.setText(testContent);
        
        // Setup mock to throw exception on delete
        doThrow(new RuntimeException("Delete failed")).when(mockMainController).deleteBook(any());
        
        Platform.runLater(() -> viewController.handleDelete());
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify display still shows original content
        assertEquals(testContent, displayField.getText());
        assertEquals("Failed to delete book", errorText.getText());
    }

    @Test
    void testSetActiveBook_NullBook() {
        Platform.runLater(() -> viewController.setActiveBook(null));
        WaitForAsyncUtils.waitForFxEvents();
        
        try {
            verify(mockContentHandler, never()).readPage(anyInt(), anyInt(), anyInt());
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        assertEquals("no book selected", displayField.getText());
        assertEquals("no book selected", metadataField.getText());
    }

    @Test
    void testPagination_CorruptedPageData() throws Exception {
        TestUtils.setPrivateField(viewController, "selectedBook", testBook);
        
        // Simulate corrupted page data
        PagedContent corruptedPage = new PagedContent(
            "Corrupted content",
            1,
            -1,  // Invalid total pages
            1000,
            3000
        );
        
        when(mockContentHandler.readPage(eq(1), eq(1), anyInt())).thenReturn(corruptedPage);
        
        TestUtils.setPrivateField(viewController, "page", firstPage);
        
        Platform.runLater(() -> viewController.nextPage());
        WaitForAsyncUtils.waitForFxEvents();
        
        assertEquals("Corrupted content", displayField.getText());
        assertTrue(metadataField.getText().contains("Page 2 of -1")); // This should trigger UI validation in production
    }

    @Test
    void testGetMainController() {
        ViewController controller = new ViewController();
        assertNull(controller.getMainController());
        
        MainController mainController = new MainController(null);
        controller.setMainController(mainController, null);
        assertEquals(mainController, controller.getMainController());
    }
}