package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * The {@code JsonToBook} class provides methods to convert JSON strings to {@link Book} objects.
 */
public class JsonToBook {

    /**
     * Converts a JSON string to a list of {@link Book} objects.
     *
     * @param json the JSON string representing a list of books
     * @return a list of {@link Book} objects
     * @throws IOException if an I/O error occurs while processing the JSON string
     */
    public static List<Book> toBookList(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        JsonNode contentNode = rootNode.get("content");

        List<Book> books = new ArrayList<>();
        if (contentNode != null && contentNode.isArray()) {
            for (JsonNode bookNode : (ArrayNode) contentNode) {
                Book book = mapper.treeToValue(bookNode, Book.class);
                books.add(book);
            }
        }
        return books;
    }

    /**
     * Converts a JSON string to a single {@link Book} object.
     *
     * @param json the JSON string representing a book
     * @return a {@link Book} object
     * @throws IOException if an I/O error occurs while processing the JSON string
     */
    public static Book toBook(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(json);
        return mapper.treeToValue(rootNode, Book.class);
    }
}