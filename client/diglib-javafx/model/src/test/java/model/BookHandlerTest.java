package model;

import java.io.File;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;



class BookHandlerTest {
    @Mock
    private HttpClient mockHttpClient;
    
    @Mock
    private HttpResponse<String> mockResponse;
    
    private BookHandler bookHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        bookHandler = new BookHandler(mockHttpClient);
    }

    @Test
    void getAllBooks_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "{\"content\":[{\"id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\",\"year\":2024,\"genre\":\"Fiction\",\"isbn\":\"1234567890123\"}]}";
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act
        List<Book> books = bookHandler.getAllBooks();

        // Assert
        assertNotNull(books);
        assertFalse(books.isEmpty());
        assertEquals(1, books.size());
        assertEquals("Test Book", books.get(0).getTitle());
    }

    @Test
    void getBook_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "{\"id\":1,\"title\":\"Test Book\",\"author\":\"Test Author\",\"year\":2024,\"genre\":\"Fiction\",\"isbn\":\"1234567890123\"}";
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act
        Book book = bookHandler.getBook(1);

        // Assert
        assertNotNull(book);
        assertEquals("Test Book", book.getTitle());
        assertEquals("Test Author", book.getAuthor());
    }

    @Test
    void postBook_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "id:1";
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        
        Path testPath = Path.of("test.txt");
        Files.deleteIfExists(testPath);
        Files.createFile(testPath);
        File testFile = testPath.toFile();
        
        try {
            // Act
            int result = bookHandler.postBook(
                "Test Book",
                "Test Author",
                "Fiction",
                "1234567890123",
                2024,
                testFile
            );
    
            // Assert
            assertEquals(1, result);
    
            } finally {
                Files.deleteIfExists(testPath);

            }


    }

    @Test
    void postBook_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(400);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        Path testPath = Path.of("test.txt");
        Files.deleteIfExists(testPath);
        Files.createFile(testPath);
        File testFile = testPath.toFile();
        try {
        // Act
        int result = bookHandler.postBook(
            "Test Book",
            "Test Author",
            "Fiction",
            "1234567890123",
            2024,
            testFile
        );

        // Assert
        assertEquals(-1, result);

        } finally {
            Files.deleteIfExists(testPath);

        }
    }

    @Test
    void deleteBook_Success() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act
        boolean result = bookHandler.deleteBook(1);

        // Assert
        assertTrue(result);
    }

    @Test
    void deleteBook_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act
        boolean result = bookHandler.deleteBook(1);

        // Assert
        assertFalse(result);
    }

    @Test
    void getAllBooks_ThrowsIOException() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new IOException("Network error"));

        // Act & Assert
        assertThrows(IOException.class, () -> bookHandler.getAllBooks());
    }

    @Test
    void getBook_ThrowsInterruptedException() throws IOException, InterruptedException {
        // Arrange
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenThrow(new InterruptedException("Operation interrupted"));

        // Act & Assert
        assertThrows(InterruptedException.class, () -> bookHandler.getBook(1));
    }
}