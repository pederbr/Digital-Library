package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A utility class for handling genres.
 */
public class GenreReader {
    
    private static final String BASE_URL = "https://api.diglib.no/genres";
    private final HttpClient httpClient;

    /**
     * Constructor that allows injection of HttpClient.
     *
     * @param httpClient the HttpClient to be used for making requests
     */
    public GenreReader(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    /**
     * Default constructor that uses a real HttpClient.
     */
    public GenreReader() {
        this(HttpClient.newHttpClient());
    } 

    /**
     * Reads genres from the API.
     *
     * @return a list of genres
     * @throws IOException if an I/O error occurs when sending or receiving
     * @throws InterruptedException if the operation is interrupted
     */
    public List<String> readGenres() throws IOException, InterruptedException {
        List<String> genres;
        var request = HttpRequest.newBuilder(
            URI.create(BASE_URL))
            .header("accept", "application/json")
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        genres = objectMapper.readValue(response.body(), new TypeReference<List<String>>() {});
        return genres;        
    }
}
