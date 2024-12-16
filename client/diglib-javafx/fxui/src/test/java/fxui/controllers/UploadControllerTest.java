package fxui.controllers;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class UploadControllerTest {
    
    private UploadController uploadController;
    
    @Mock
    private MainController mockMainController;
    
    private TextField titleField;
    private TextField authorField;
    private ComboBox<String> genreListField;
    private TextField yearField;
    private TextField ISBNField;
    private Button uploadBook;
    private Button uploadContent;
    private Text titleError;
    private Text authorError;
    private Text genreEmptyError;
    private Text genreInvalidError;
    private Text yearError;
    private Text ISBNError;
    private Text contentError;
    private Text fileError;
    
    private static final List<String> SAMPLE_GENRES = Arrays.asList(
        "Fiction", "Non-Fiction", "Science Fiction", "Romance", "Mystery"
    );
    
    @TempDir
    File tempDir;
    
    @Start
    private void start(Stage stage) {
        // Initialize JavaFX components
        titleField = new TextField();
        authorField = new TextField();
        genreListField = new ComboBox<>();
        genreListField.getItems().addAll(SAMPLE_GENRES);
        yearField = new TextField();
        ISBNField = new TextField();
        uploadBook = new Button("Upload Book");
        uploadContent = new Button("Upload Content");
        titleError = new Text("Title is required");
        authorError = new Text("Author is required");
        genreEmptyError = new Text("Genre is required");
        genreInvalidError = new Text("Invalid genre");
        yearError = new Text("Invalid year");
        ISBNError = new Text("ISBN must be 13 digits");
        contentError = new Text("Content required");
        fileError = new Text("File error");
        
        VBox root = new VBox(
            titleField, authorField, genreListField, yearField, ISBNField,
            uploadBook, uploadContent, titleError, authorError, genreEmptyError,
            genreInvalidError, yearError, ISBNError, contentError, fileError
            
        );
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @BeforeEach
    void setUp() throws Exception {
        // Set up a test file
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        uploadController = new UploadController(SAMPLE_GENRES);
        
        // Inject JavaFX components and dependencies
        TestUtils.setPrivateField(uploadController, "titleField", titleField);
        TestUtils.setPrivateField(uploadController, "authorField", authorField);
        TestUtils.setPrivateField(uploadController, "genreListField", genreListField);
        TestUtils.setPrivateField(uploadController, "yearField", yearField);
        TestUtils.setPrivateField(uploadController, "ISBNField", ISBNField);
        TestUtils.setPrivateField(uploadController, "uploadBook", uploadBook);
        TestUtils.setPrivateField(uploadController, "uploadContent", uploadContent);
        TestUtils.setPrivateField(uploadController, "titleError", titleError);
        TestUtils.setPrivateField(uploadController, "authorError", authorError);
        TestUtils.setPrivateField(uploadController, "genreEmptyError", genreEmptyError);
        TestUtils.setPrivateField(uploadController, "genreInvalidError", genreInvalidError);
        TestUtils.setPrivateField(uploadController, "yearError", yearError);
        TestUtils.setPrivateField(uploadController, "ISBNError", ISBNError);
        TestUtils.setPrivateField(uploadController, "contentError", contentError);
        TestUtils.setPrivateField(uploadController, "fileError", fileError);
        TestUtils.setPrivateField(uploadController, "mainController", mockMainController);
        TestUtils.setPrivateField(uploadController, "genres", SAMPLE_GENRES);
        
        // Initialize on JavaFX thread
        Platform.runLater(() -> uploadController.initialize());
        WaitForAsyncUtils.waitForFxEvents();
    }
    
    @Test
    void testValidUpload() throws Exception {
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            titleField.setText("Test Book");
            authorField.setText("Test Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).uploadBook(
            eq("Test Book"),
            eq("Test Author"),
            eq("Fiction"),
            eq("1234567890123"),
            eq(2024),
            any(File.class)
        );
    }
    
@Test
void testGenreFiltering() {
    Platform.runLater(() -> {
        // Set up initial genres
        genreListField.getItems().clear();
        genreListField.getItems().addAll(SAMPLE_GENRES);
        genreListField.setEditable(true);
        
        // Trigger the filtering by setting editor text
        genreListField.show();
        genreListField.getEditor().setText("Fic");
    });
    
    // Wait for the PauseTransition to complete (200ms + buffer)
    WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();
    
    // Verify filtered items
    List<String> items = genreListField.getItems();
    assertEquals(1, items.size(), "Should only show matching items");
    assertTrue(items.contains("Fiction"), "Should contain Fiction");
    assertFalse(items.contains("Romance"), "Should not contain Romance");
    
    // Test clearing filter
    Platform.runLater(() -> {
        genreListField.getEditor().setText("");
    });
    
    // Wait for the PauseTransition to complete
    WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();
    
    assertEquals(SAMPLE_GENRES.size(), genreListField.getItems().size(), 
        "Should show all genres when filter is empty");
}

@Test
void testGenreFilteringPartialMatch() {
    Platform.runLater(() -> {
        genreListField.getItems().clear();
        genreListField.getItems().addAll(SAMPLE_GENRES);
        genreListField.setEditable(true);
        
        // Test partial match
        genreListField.show();
        genreListField.getEditor().setText("Sci");
    });
    
    // Wait for the PauseTransition to complete
    WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();
    
    List<String> items = genreListField.getItems();
    assertTrue(items.contains("Science Fiction"), 
        "Should contain Science Fiction for 'Sci' input");
    assertEquals(1, items.size(), 
        "Should only show items starting with 'Sci'");
}

@Test
void testGenreFilteringNoMatch() {
    Platform.runLater(() -> {
        genreListField.getItems().clear();
        genreListField.getItems().addAll(SAMPLE_GENRES);
        genreListField.setEditable(true);
        
        // Test no match
        genreListField.show();
        genreListField.getEditor().setText("xyz");
    });
    
    // Wait for the PauseTransition to complete
    WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
    WaitForAsyncUtils.waitForFxEvents();
    
    assertTrue(genreListField.getItems().isEmpty(), 
        "Should show no items for non-matching filter");
}
    
    @Test
    void testClearFields() throws Exception {
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            // Set fields
            titleField.setText("Test");
            authorField.setText("Test");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Handle successful upload
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify fields are cleared
        assertEquals("", titleField.getText(), "Title field should be empty");
        assertEquals("", authorField.getText(), "Author field should be empty");
        assertNull(genreListField.getValue(), "Genre should be cleared");
        assertEquals("", yearField.getText(), "Year field should be empty");
        assertEquals("", ISBNField.getText(), "ISBN field should be empty");
        assertEquals("Upload file from local computer", uploadContent.getText());
    }
    
    @Test
    void testUploadError() throws Exception {
        doThrow(new IOException("Upload failed"))
            .when(mockMainController)
            .uploadBook(anyString(), anyString(), anyString(), anyString(), anyInt(), any(File.class));
            
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            titleField.setText("Test Book");
            authorField.setText("Test Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(fileError.isVisible(), "File error should be visible");
        assertTrue(fileError.isManaged(), "File error should be managed");
    }

    @Test
    void testGenreFilteringCaseSensitivity() {
        Platform.runLater(() -> {
            genreListField.getItems().clear();
            genreListField.getItems().addAll(SAMPLE_GENRES);
            genreListField.setEditable(true);
            genreListField.show();
            genreListField.getEditor().setText("fiction");
        });
        
        WaitForAsyncUtils.sleep(300, TimeUnit.MILLISECONDS);
        WaitForAsyncUtils.waitForFxEvents();
        
        List<String> items = genreListField.getItems();
        assertEquals(1, items.size(), "Should match regardless of case");
        assertTrue(items.contains("Fiction"), "Should find 'Fiction' when searching for 'fiction'");
    }

    @Test
    void testValidationBoundaryValues() throws Exception {
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            // Test boundary year values
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2025"); // Max allowed year
            ISBNField.setText("1234567890123");
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).uploadBook(
            anyString(), anyString(), anyString(), anyString(), eq(2025), any(File.class)
        );
    }

    @Test
    void testConcurrentUploads() throws Exception {
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            // Set up valid data
            titleField.setText("Test Book");
            authorField.setText("Test Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Trigger multiple uploads rapidly
            uploadController.handleUpload();
            uploadController.handleUpload();
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify only one upload succeeded
        verify(mockMainController).uploadBook(
            eq("Test Book"),
            eq("Test Author"),
            eq("Fiction"),
            eq("1234567890123"),
            eq(2024),
            any(File.class)
        );
    }

    @Test
    void testSpecialCharactersInFields() throws Exception {
        File testFile = new File(tempDir, "test.txt");
        testFile.createNewFile();
        
        Platform.runLater(() -> {
            titleField.setText("Test#$%^&*");
            authorField.setText("Author@!~");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            try {
                TestUtils.setPrivateField(uploadController, "content", testFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).uploadBook(
            eq("Test#$%^&*"),
            eq("Author@!~"),
            eq("Fiction"),
            eq("1234567890123"),
            eq(2024),
            any(File.class)
        );
    }

    @Test
    void testInvalidYearValues() {
        Platform.runLater(() -> {
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("-1");
            ISBNField.setText("1234567890123");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(yearError.isVisible(), "Year error should be visible for negative year");
        
        Platform.runLater(() -> {
            yearField.setText("2026");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(yearError.isVisible(), "Year error should be visible for future year");
    }
    
    @Test
    void testEmptyFileUpload() throws IOException {
        Platform.runLater(() -> {
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Should create a temporary file with default content
        verify(mockMainController).uploadBook(
            anyString(), anyString(), anyString(), anyString(), anyInt(), any(File.class)
        );
    }
    
    @Test
    void testGenreLoadingFailure() throws Exception {
        // Create new controller with empty genre list
        UploadController controller = new UploadController(new ArrayList<>());
        
        // Set up mock client that throws exception
        HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
        doThrow(new IOException("Network error"))
            .when(mockClient)
            .send(any(), any());
        
        // Set up ComboBox before calling setMainController
        ComboBox<String> testGenreList = new ComboBox<>();
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                controller.setMainController(mockMainController, mockClient);
            } catch (Exception e) {
                // Expected exception
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify both the genres list and ComboBox items are empty
        List<String> genres = (List<String>) TestUtils.getPrivateField(controller, "genres");
        assertTrue(genres.isEmpty(), "Genres list should be empty after load failure");
        assertTrue(testGenreList.getItems().isEmpty(), "Genre ComboBox should be empty after load failure");
    }

    @Test
    void testNonNumericISBN() {
        Platform.runLater(() -> {
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("123abc4567890");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(ISBNError.isVisible(), "ISBN error should be visible for non-numeric input");
    }

    @Test
    void testExtremelyLongInputs() throws IOException {
        String longString = "a".repeat(1000);
        
        Platform.runLater(() -> {
            titleField.setText(longString);
            authorField.setText(longString);
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).uploadBook(
            eq(longString),
            eq(longString),
            anyString(),
            anyString(),
            anyInt(),
            any(File.class)
        );
    }

    @Test
    void testHandleFileUploadWithNull() {
        Platform.runLater(() -> {
            try {
                // Add contentError to errorTexts
                TestUtils.setPrivateField(uploadController, "contentError", contentError);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            uploadController.handleFileUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(contentError.isVisible(), "Content error should be visible when no file selected");
        assertTrue(contentError.isManaged(), "Content error should be managed when no file selected");
    }

    @Test
    void testEmptyFieldValidation() {
        Platform.runLater(() -> {
            titleField.setText("");
            authorField.setText("");
            genreListField.setValue(null);
            yearField.setText("");
            ISBNField.setText("");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(titleError.isVisible(), "Title error should be visible");
        assertTrue(authorError.isVisible(), "Author error should be visible");
        assertTrue(genreEmptyError.isVisible(), "Genre error should be visible");
        assertTrue(yearError.isVisible(), "Year error should be visible");
        assertTrue(ISBNError.isVisible(), "ISBN error should be visible");
    }

    @Test
    void testGenreComboBoxEditorBehavior() {
        Platform.runLater(() -> {
            // Reset error states
            genreInvalidError.setVisible(false);
            genreInvalidError.setManaged(false);
            
            // Set invalid genre
            genreListField.getEditor().setText("InvalidGenre");
            genreListField.setValue("InvalidGenre");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertTrue(genreInvalidError.isVisible(), "Genre error should be visible for invalid input");
    }

    @Test
    void testErrorTextVisibilityAfterClear() {
        Platform.runLater(() -> {
            // Trigger errors first
            uploadController.handleUpload();
            // Then perform valid upload
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        assertFalse(titleError.isVisible(), "Title error should be hidden after valid upload");
        assertFalse(authorError.isVisible(), "Author error should be hidden after valid upload");
        assertFalse(genreEmptyError.isVisible(), "Genre error should be hidden after valid upload");
        assertFalse(yearError.isVisible(), "Year error should be hidden after valid upload");
        assertFalse(ISBNError.isVisible(), "ISBN error should be hidden after valid upload");
    }

    @Test
    void testMainControllerNull() {
        UploadController controller = new UploadController(new ArrayList<>());
        
        Platform.runLater(() -> {
            try {
                // Initialize required error texts
                Text fileError = new Text("File error");
                fileError.setVisible(false);
                fileError.setManaged(false);
                
                TestUtils.setPrivateField(controller, "fileError", fileError);
                TestUtils.setPrivateField(controller, "errorTexts", Arrays.asList(fileError));
                TestUtils.setPrivateField(controller, "activeErrors", new ArrayList<>());
                
                controller.handleUpload();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        Text actualFileError = (Text) TestUtils.getPrivateField(controller, "fileError");
        assertTrue(actualFileError.isVisible(), "File error should be visible when mainController is null");
    }

    @Test
    void testWhitespaceInFields() throws IOException {
        Platform.runLater(() -> {
            titleField.setText("  Test Book  ");
            authorField.setText("  Test Author  ");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        verify(mockMainController).uploadBook(
            eq("  Test Book  "),
            eq("  Test Author  "),
            anyString(),
            anyString(),
            anyInt(),
            any(File.class)
        );
    }

    @Test
    void testHandleFileUploadSuccess(@TempDir Path tempDir) throws Exception {
        File testFile = tempDir.resolve("testUpload.txt").toFile();
        testFile.createNewFile();

        Platform.runLater(() -> {
            try {
                // Mock FileChooser behavior using reflection
                FileChooser mockChooser = org.mockito.Mockito.mock(FileChooser.class);
                org.mockito.Mockito.when(mockChooser.showOpenDialog(any())).thenReturn(testFile);
                
                // Create a new instance to avoid interfering with other tests
                UploadController testController = new UploadController();
                TestUtils.setPrivateField(testController, "uploadContent", uploadContent);
                TestUtils.setPrivateField(testController, "contentError", contentError);
                TestUtils.setPrivateField(testController, "activeErrors", new ArrayList<>());
                
                // Inject mock FileChooser
                java.lang.reflect.Field fileChooserField = UploadController.class.getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(testController, mockChooser);
                
                testController.handleFileUpload();
                
                assertEquals(testFile.getName(), uploadContent.getText(), 
                    "Upload button should display filename");
                assertFalse(contentError.isVisible(), 
                    "Content error should not be visible after successful upload");
                
                File actualContent = (File) TestUtils.getPrivateField(testController, "content");
                assertEquals(testFile, actualContent, 
                    "Selected file should be stored in content field");
                
            } catch (Exception e) {
                fail("Test failed with exception: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleFileUploadNullSelection() throws Exception {
        Platform.runLater(() -> {
            try {
                // Mock FileChooser that returns null (simulating cancel)
                FileChooser mockChooser = org.mockito.Mockito.mock(FileChooser.class);
                org.mockito.Mockito.when(mockChooser.showOpenDialog(any())).thenReturn(null);
                
                // Create a new instance with controlled error texts
                UploadController testController = new UploadController();
                TestUtils.setPrivateField(testController, "contentError", contentError);
                TestUtils.setPrivateField(testController, "activeErrors", new ArrayList<>());
                
                // Inject mock FileChooser
                java.lang.reflect.Field fileChooserField = UploadController.class.getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(testController, mockChooser);
                
                testController.handleFileUpload();
                
                assertTrue(contentError.isVisible(), 
                    "Content error should be visible when no file is selected");
                assertTrue(contentError.isManaged(), 
                    "Content error should be managed when no file is selected");
                
            } catch (Exception e) {
                fail("Test failed with exception: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testHandleFileUploadException() throws Exception {
        Platform.runLater(() -> {
            try {
                // Mock FileChooser that throws exception
                FileChooser mockChooser = org.mockito.Mockito.mock(FileChooser.class);
                org.mockito.Mockito.when(mockChooser.showOpenDialog(any()))
                    .thenThrow(new RuntimeException("Test exception"));
                
                // Create a new instance with controlled error texts
                UploadController testController = new UploadController();
                TestUtils.setPrivateField(testController, "contentError", contentError);
                TestUtils.setPrivateField(testController, "activeErrors", new ArrayList<>());
                
                // Inject mock FileChooser
                java.lang.reflect.Field fileChooserField = UploadController.class.getDeclaredField("fileChooser");
                fileChooserField.setAccessible(true);
                fileChooserField.set(testController, mockChooser);
                
                testController.handleFileUpload();
                
                assertTrue(contentError.isVisible(), 
                    "Content error should be visible when exception occurs");
                assertTrue(contentError.isManaged(), 
                    "Content error should be managed when exception occurs");
                
                List<Text> activeErrors = (List<Text>) TestUtils.getPrivateField(testController, "activeErrors");
                assertEquals(1, activeErrors.size(), 
                    "Should have exactly one active error");
                assertEquals(contentError, activeErrors.get(0), 
                    "Active error should be contentError");
                
            } catch (Exception e) {
                fail("Test failed with exception: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testLoadGenresSuccess() throws Exception {
        // Setup test data
        List<String> expectedGenres = Arrays.asList("Mystery", "Romance", "Fiction");
        HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
        
        // Mock HTTP response to return genre data
        java.net.http.HttpResponse mockResponse = org.mockito.Mockito.mock(java.net.http.HttpResponse.class);
        org.mockito.Mockito.when(mockResponse.body()).thenReturn("[\"Mystery\",\"Romance\",\"Fiction\"]");
        org.mockito.Mockito.when(mockClient.send(any(), any())).thenReturn(mockResponse);
        
        // Create new controller instance
        UploadController controller = new UploadController(new ArrayList<>());
        ComboBox<String> testGenreList = new ComboBox<>();
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                controller.setMainController(mockMainController, mockClient);
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        List<String> actualGenres = (List<String>) TestUtils.getPrivateField(controller, "genres");
        assertEquals(expectedGenres, actualGenres, "Genres should match expected list");
        assertEquals(expectedGenres, testGenreList.getItems(), "ComboBox items should match expected genres");
    }

    @Test
    void testLoadGenresNetworkFailure() throws Exception {
        HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
        
        // Mock network failure
        org.mockito.Mockito.when(mockClient.send(any(), any()))
            .thenThrow(new IOException("Network timeout"));
        
        UploadController controller = new UploadController(new ArrayList<>());
        ComboBox<String> testGenreList = new ComboBox<>();
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                controller.setMainController(mockMainController, mockClient);
            } catch (Exception e) {
                // Expected exception
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        List<String> genres = (List<String>) TestUtils.getPrivateField(controller, "genres");
        assertTrue(genres.isEmpty(), "Genres should be empty after network failure");
        assertTrue(testGenreList.getItems().isEmpty(), "ComboBox should be empty after network failure");
    }

    @Test
    void testLoadGenresNullClient() throws Exception {
        UploadController controller = new UploadController(new ArrayList<>());
        ComboBox<String> testGenreList = new ComboBox<>();
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                controller.setMainController(mockMainController, null);
                fail("Should throw NullPointerException");
            } catch (NullPointerException e) {
                // Expected exception
            } catch (Exception e) {
                fail("Wrong exception type: " + e.getClass().getName());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void testLoadGenresWithPreExistingGenres() throws Exception {
        // Setup initial genres
        List<String> initialGenres = Arrays.asList("Initial Genre");
        UploadController controller = new UploadController(initialGenres);
        ComboBox<String> testGenreList = new ComboBox<>();
        testGenreList.setItems(FXCollections.observableArrayList(initialGenres));
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                // Should not attempt to load genres since list is not empty
                controller.setMainController(mockMainController, org.mockito.Mockito.mock(HttpClient.class));
            } catch (Exception e) {
                fail("Should not throw exception: " + e.getMessage());
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        List<String> genres = (List<String>) TestUtils.getPrivateField(controller, "genres");
        assertEquals(initialGenres, genres, "Should keep initial genres");
        assertEquals(initialGenres, testGenreList.getItems(), "ComboBox should keep initial genres");
    }

    @Test
    void testLoadGenresInterruptedException() throws Exception {
        HttpClient mockClient = org.mockito.Mockito.mock(HttpClient.class);
        
        // Mock interrupted exception
        org.mockito.Mockito.when(mockClient.send(any(), any()))
            .thenThrow(new InterruptedException("Operation interrupted"));
        
        UploadController controller = new UploadController(new ArrayList<>());
        ComboBox<String> testGenreList = new ComboBox<>();
        TestUtils.setPrivateField(controller, "genreListField", testGenreList);
        
        Platform.runLater(() -> {
            try {
                controller.setMainController(mockMainController, mockClient);
            } catch (Exception e) {
                // Expected exception
            }
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        List<String> genres = (List<String>) TestUtils.getPrivateField(controller, "genres");
        assertTrue(genres.isEmpty(), "Genres should be empty after interruption");
        assertTrue(testGenreList.getItems().isEmpty(), "ComboBox should be empty after interruption");
    }

    @Test
    void testUpdateErrorsWithNullErrorText() throws Exception {
        Platform.runLater(() -> {
            // Deliberately set one error text to null
            try {
                TestUtils.setPrivateField(uploadController, "titleError", null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Trigger validation that would normally show title error
            titleField.setText("");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Other errors should still work normally
        assertTrue(authorError.isVisible(), "Author error should be visible");
        assertTrue(genreEmptyError.isVisible(), "Genre error should be visible");
    }

    @Test
    void testUpdateErrorsVisibilityToggle() throws Exception {
        Platform.runLater(() -> {
            // First make all errors visible
            titleField.setText("");
            authorField.setText("");
            genreListField.setValue(null);
            uploadController.handleUpload();
            
            // Then make a valid submission
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify all errors are hidden
        assertFalse(titleError.isVisible(), "Title error should be hidden");
        assertFalse(titleError.isManaged(), "Title error should not be managed");
        assertFalse(authorError.isVisible(), "Author error should be hidden");
        assertFalse(authorError.isManaged(), "Author error should not be managed");
        assertFalse(genreEmptyError.isVisible(), "Genre error should be hidden");
        assertFalse(genreEmptyError.isManaged(), "Genre error should not be managed");
    }

    @Test
    void testMultipleErrorsSimultaneously() throws Exception {
        Platform.runLater(() -> {
            // Trigger multiple validation errors at once
            titleField.setText("");            // title error
            authorField.setText("");           // author error
            genreListField.setValue(null);     // genre error
            yearField.setText("invalid");      // year error
            ISBNField.setText("invalid");      // ISBN error
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Verify all errors are shown
        assertTrue(titleError.isVisible() && titleError.isManaged(), 
            "Title error should be visible and managed");
        assertTrue(authorError.isVisible() && authorError.isManaged(), 
            "Author error should be visible and managed");
        assertTrue(genreEmptyError.isVisible() && genreEmptyError.isManaged(), 
            "Genre error should be visible and managed");
        assertTrue(yearError.isVisible() && yearError.isManaged(), 
            "Year error should be visible and managed");
        assertTrue(ISBNError.isVisible() && ISBNError.isManaged(), 
            "ISBN error should be visible and managed");
    }

    @Test
    void testErrorClearingBehavior() throws Exception {
        Platform.runLater(() -> {
            // Clear any existing errors first
            uploadController.handleUpload();

            // Show some errors
            titleField.setText("");
            authorField.setText("");
            uploadController.handleUpload();
            
            // Verify errors are visible
            assertTrue(titleError.isVisible(), "Title error should be visible initially");
            assertTrue(authorError.isVisible(), "Author error should be visible initially");
            
            // Fix one field and add all required fields
            titleField.setText("Valid Title");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // Only author error should remain visible
        assertFalse(titleError.isVisible(), "Title error should be hidden after fixing");
        assertTrue(authorError.isVisible(), "Author error should still be visible");
    }

    @Test
    void testEmptyActiveErrorsList() throws Exception {
        Platform.runLater(() -> {
            // Initialize with valid data to ensure clean state
            titleField.setText("Valid Title");
            authorField.setText("Valid Author");
            genreListField.setValue("Fiction");
            yearField.setText("2024");
            ISBNField.setText("1234567890123");
            
            // Clear active errors and trigger update
            List<Text> activeErrors = new ArrayList<>();
            try {
                TestUtils.setPrivateField(uploadController, "activeErrors", activeErrors);
            } catch (Exception e) {
                e.printStackTrace();
            }
            uploadController.handleUpload();
        });
        WaitForAsyncUtils.waitForFxEvents();
        
        // All errors should be hidden
        assertFalse(titleError.isVisible(), "Title error should be hidden");
        assertFalse(authorError.isVisible(), "Author error should be hidden");
        assertFalse(genreEmptyError.isVisible(), "Genre error should be hidden");
        assertFalse(yearError.isVisible(), "Year error should be hidden");
        assertFalse(ISBNError.isVisible(), "ISBN error should be hidden");
    }

    @Test
    void testGetMainController() throws IOException, InterruptedException {
        // Create a controller with pre-loaded genres to avoid HTTP client usage
        List<String> preloadedGenres = Arrays.asList("Fiction", "Non-fiction");
        UploadController controller = new UploadController(preloadedGenres);
        assertNull(controller.getMainController());
        
        MainController mainController = new MainController(null);
        // Pass a mock HttpClient to avoid actual HTTP calls
        HttpClient mockClient = mock(HttpClient.class);
        controller.setMainController(mainController, mockClient);
        assertEquals(mainController, controller.getMainController());
    }
}