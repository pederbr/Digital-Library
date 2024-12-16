package model;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


/**
 * The {@code ContentHandler} class provides methods to read and write content
 * from and to a {@link Book} object.
 */
public class ContentHandler {
    private static final String BASE_URL = "https://api.diglib.no/books";
    private final HttpClient httpClient;

    // Constructor that allows injection of HttpClient
    public ContentHandler(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    // Default constructor that uses real HttpClient
    public ContentHandler() {
        this(HttpClient.newHttpClient());
    }
    /**
     * Reads a specific page of content from a book.
     *
     * @param bookID the ID of the book
     * @param page the page number to read
     * @param pageSize the size of the page
     * @return a {@link PagedContent} object containing the content of the specified page
     * @throws InterruptedException if the operation is interrupted
     * @throws IOException if an I/O error occurs
     */
    public PagedContent readPage(int bookID, int page, int pageSize) throws InterruptedException, IOException {        
        String queryParams = "?page=" + page + "&pageSize=" + pageSize;
        
        var request = HttpRequest.newBuilder(
            URI.create(BASE_URL + "/" + bookID + "/content" + queryParams))
            .header("accept", "application/json")
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonToPagedContent.toPagedContent(response.body());
    }

    /**
     * Reads all pages of content from a book.
     *
     * @param book the {@link Book} object
     * @param pageSize the size of each page
     * @return a list of {@link PagedContent} objects containing all pages of the book
     * @throws InterruptedException if the operation is interrupted
     * @throws IOException if an I/O error occurs
     */
    public List<PagedContent> readAllPages(Book book, int pageSize) throws InterruptedException, IOException {
        List<PagedContent> pages = new ArrayList<>();
        PagedContent page = readPage(book.getId(), 0, pageSize);
        pages.add(page);
        for (int i = 0; i < page.getTotalPages(); i++) {
            pages.add(readPage(book.getId(), i, pageSize));
        }
        return pages;
    }

    /**
     * Writes content to a book from a specified file path.
     *
     * @param book the {@link Book} object
     * @param contentPath the path to the file containing the content to write
     * @throws IOException if an I/O error occurs
     * @throws InterruptedException if the operation is interrupted
     */
    public void writeContentToBook(Book book, Path contentPath) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(
            URI.create(BASE_URL + "/" + book.getId() + "/content"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofFile(contentPath))
            .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }

}