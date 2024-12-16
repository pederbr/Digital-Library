package model;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;



class ContentHandlerTest {
    @Mock
    private HttpClient mockHttpClient;
    
    @Mock
    private HttpResponse<String> mockResponse;
    
    private ContentHandler contentHandler;
    private Book testBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        contentHandler = new ContentHandler(mockHttpClient);
        testBook = new Book();
        testBook.setID(1);
    }

    @Test
    void readPage_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "{\"content\":\"Test content\",\"pageNumber\":0,\"totalPages\":2,\"pageSize\":100,\"totalSize\":150}";
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act
        PagedContent result = contentHandler.readPage(1, 0, 100);

        // Assert
        assertNotNull(result);
        assertEquals("Test content", result.getContent());
        assertEquals(0, result.getPageNumber());
        assertEquals(2, result.getTotalPages());
    }

    @Test
    void readPage_Failure() throws IOException, InterruptedException {
        // Arrange
        when(mockResponse.statusCode()).thenReturn(404);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> contentHandler.readPage(1, 0, 100));
    }

    @Test
    void readAllPages_Success() throws IOException, InterruptedException {
        // Arrange
        String firstPageJson = "{\"content\":\"Page 1\",\"pageNumber\":0,\"totalPages\":1,\"pageSize\":100,\"totalSize\":150}";
        String secondPageJson = "{\"content\":\"Page 2\",\"pageNumber\":1,\"totalPages\":1,\"pageSize\":50,\"totalSize\":150}";
        
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);
        when(mockResponse.body())
            .thenReturn(firstPageJson)
            .thenReturn(secondPageJson);
        // Act
        List<PagedContent> pages = contentHandler.readAllPages(testBook, 100);


        // Assert
        assertEquals(2, pages.size());
        assertEquals("Page 1", pages.get(0).getContent());
        assertEquals("Page 2", pages.get(1).getContent());
    }

    @Test
    void writeContentToBook_Success() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "{\"content\":\"Test content\",\"pageNumber\":0,\"totalPages\":2,\"pageSize\":100,\"totalSize\":150}";
        when(mockResponse.body()).thenReturn(jsonResponse);
        when(mockResponse.statusCode()).thenReturn(200);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
            .thenReturn(mockResponse);

        Path testPath = Path.of("test.txt");
        Files.deleteIfExists(testPath);
        Files.createFile(testPath);
        try {
            // Act & Assert
            assertDoesNotThrow(() -> contentHandler.writeContentToBook(testBook, testPath));
        } finally {
            Files.deleteIfExists(testPath);
        }
    }
}