package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GenreReaderTest {

    private HttpClient httpClient;
    private HttpResponse<String> httpResponse;
    private GenreReader genreReader;

    @BeforeEach
    public void setUp() {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);
        genreReader = new GenreReader(httpClient);
    }

    @Test
    public void testReadGenres() throws IOException, InterruptedException {
        // Arrange
        String jsonResponse = "[\"Fiction\", \"Non-Fiction\"]";
        when(httpResponse.body()).thenReturn(jsonResponse);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // Act
        List<String> genres = genreReader.readGenres();

        // Assert
        assertNotNull(genres);
        assertEquals(2, genres.size());
        assertEquals("Fiction", genres.get(0));
        assertEquals("Non-Fiction", genres.get(1));

        // Verify the request
        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(URI.create("https://api.diglib.no/genres"), capturedRequest.uri());
        assertEquals("application/json", capturedRequest.headers().firstValue("accept").orElse(""));
    }

    @Test
    public void testReadGenresIOException() throws IOException, InterruptedException {
        // Arrange
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(IOException.class);

        // Act & Assert
        assertThrows(IOException.class, () -> genreReader.readGenres());
    }

    @Test
    public void testReadGenresInterruptedException() throws IOException, InterruptedException {
        // Arrange
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(InterruptedException.class);

        // Act & Assert
        assertThrows(InterruptedException.class, () -> genreReader.readGenres());
    }
}