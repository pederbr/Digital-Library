package model;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * The {@code BookHandler} class provides methods to interact with a book API.
 * It allows fetching all books, fetching a specific book by ID, posting a new book, and deleting a book by ID.
 */
public class BookHandler {
    private static final String BASE_URL = "https://api.diglib.no/books";
    private final HttpClient httpClient;

    // Constructor that allows injection of HttpClient
    public BookHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // Default constructor that uses real HttpClient
    public BookHandler() {
        this(HttpClient.newHttpClient());
    }
    /**
     * Fetches all books from the API.
     *
     * @return a list of {@link Book} objects.
     * @throws IOException if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     */
    public List<Book> getAllBooks() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(
            URI.create(BASE_URL))
            .header("accept", "application/json")
            .build();

        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonToBook.toBookList(response.body());
    }

    /**
     * Fetches a specific book by its ID from the API.
     *
     * @param ID the ID of the book to fetch.
     * @return the {@link Book} object.
     * @throws IOException if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     */
    public Book getBook(int ID) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(
            URI.create(BASE_URL + "/" + ID))
            .header("accept", "application/json")
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonToBook.toBook(response.body());
    }

    /**
     * Posts a new book to the API.
     *
     * @param title the title of the book.
     * @param author the author of the book.
     * @param genre the genre of the book.
     * @param isbn the ISBN of the book.
     * @param year the publication year of the book.
     * @param content the content file of the book.
     * @return the ID of the newly created book, or -1 if the operation failed.
     * @throws IOException if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     */
    public int postBook(String title, String author, String genre, String isbn, Integer year, File content) throws IOException, InterruptedException {        
        HTTPRequestMultipartBody body = new HTTPRequestMultipartBody.Builder()
            .addPart("title", title)
            .addPart("author", author)
            .addPart("genre", genre)
            .addPart("isbn", isbn)
            .addPart("year", year.toString())
            .addPart("content", content, null, content.getName())
            .build();
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", body.getContentType())
                .POST(HttpRequest.BodyPublishers.ofByteArray(body.getBody()))
                .build();
                
        // Send the request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    
        // Print the response
        if(response.statusCode() == 200){
            return Integer.parseInt(response.body().split(":")[1]);
        } else {
            return -1;
        }
    }

    /**
     * Deletes a specific book by its ID from the API.
     *
     * @param ID the ID of the book to delete.
     * @return {@code true} if the book was successfully deleted, {@code false} otherwise.
     * @throws IOException if an I/O error occurs when sending or receiving.
     * @throws InterruptedException if the operation is interrupted.
     */
    public Boolean deleteBook(int ID) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/" + ID))
            .DELETE()
            .build();
        
        // Send the request and receive the response
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Print the response
        return response.statusCode() == 200;
    }
}