package model;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

class JsonToBookTest {


    @Test
    void testToBookList_validJson() throws IOException {
        String json = """
            {
                "content": [
                    {
                        "title": "The Great Gatsby",
                        "author": "F. Scott Fitzgerald",
                        "isbn": "9780743273565"
                    },
                    {
                        "title": "1984",
                        "author": "George Orwell",
                        "isbn": "9780451524935"
                    }
                ]
            }
            """;

        List<Book> books = JsonToBook.toBookList(json);
        assertEquals(2, books.size());

        Book book1 = new Book();
        book1.setTitle("The Great Gatsby");
        book1.setAuthor("F. Scott Fitzgerald");
        book1.setIsbn("9780743273565");

        Book book2 = new Book();
        book2.setTitle("1984");
        book2.setAuthor("George Orwell");
        book2.setIsbn("9780451524935");

        assertEquals(book1.getTitle(), books.get(0).getTitle());
        assertEquals(book1.getAuthor(), books.get(0).getAuthor());
        assertEquals(book1.getIsbn(), books.get(0).getIsbn());

        assertEquals(book2.getTitle(), books.get(1).getTitle());
        assertEquals(book2.getAuthor(), books.get(1).getAuthor()); 
        assertEquals(book2.getIsbn(), books.get(1).getIsbn());
    }

    @Test
    void testToBookList_emptyJson() throws IOException {
        String json = "{\"content\": []}";
        List<model.Book> books = JsonToBook.toBookList(json);
        assertTrue(books.isEmpty());
    }

    @Test
    void testToBookList_missingContent() throws IOException {
        String json = "{}";

        List<Book> books = JsonToBook.toBookList(json);
        assertTrue(books.isEmpty());
    }

    @Test
    void testToBook_validJson() throws IOException {
        String json = """
            {
                "title": "The Great Gatsby",
                "author": "F. Scott Fitzgerald",
                "isbn": "9780743273565"
            }
            """;

        Book book = JsonToBook.toBook(json);

        Book expectedBook = new Book();
        expectedBook.setTitle("The Great Gatsby");
        expectedBook.setAuthor("F. Scott Fitzgerald");
        expectedBook.setIsbn("9780743273565");

        assertEquals(expectedBook.getTitle(), book.getTitle());
        assertEquals(expectedBook.getAuthor(), book.getAuthor());
        assertEquals(expectedBook.getIsbn(), book.getIsbn());

    }

    @Test
    void testToBook_invalidJson() {
        String json = "invalid json string";

        assertThrows(JsonProcessingException.class, () -> {
            JsonToBook.toBook(json);
        });
    }

    @Test
    void testToBookList_invalidJson() {
        String json = "invalid json string";

        assertThrows(JsonProcessingException.class, () -> {
            JsonToBook.toBookList(json);
        });
    }
}
