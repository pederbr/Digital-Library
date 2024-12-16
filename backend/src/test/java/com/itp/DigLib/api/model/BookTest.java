package com.itp.DigLib.api.model;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BookTest {

    private Book book;

    @BeforeEach
    void setUp() {
        book = new Book();
    }

    @Test
    void testSetAndGetTitle() {
        book.setTitle("The Great Gatsby");
        assertEquals("The Great Gatsby", book.getTitle());
    }

    @Test
    void testSetTitleThrowsExceptionOnEmptyTitle() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setTitle("");
        });
        assertEquals("Title cannot be empty", exception.getMessage());
    }

    @Test
    void testSetTitleThrowsExceptionOnLongTitle() {
        String longTitle = "a".repeat(101);  // Exceeds 100 characters
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setTitle(longTitle);
        });
        assertEquals("Title must be less than 100 characters", exception.getMessage());
    }

    @Test
    void testSetAndGetAuthor() {
        book.setAuthor("F. Scott Fitzgerald");
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
    }

    @Test
    void testSetAuthorThrowsExceptionOnEmptyAuthor() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setAuthor("");
        });
        assertEquals("Author cannot be empty", exception.getMessage());
    }

    @Test
    void testSetAuthorThrowsExceptionOnLongAuthorName() {
        String longAuthor = "a".repeat(101);  // Exceeds 100 characters
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setAuthor(longAuthor);
        });
        assertEquals("Author must be less than 100 characters", exception.getMessage());
    }

    @Test
    void testSetAndGetYear() {
        book.setYear(1925);
        assertEquals(1925, book.getYear());
    }

    @Test
    void testSetYearThrowsExceptionOnInvalidYear() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setYear(3000);  // Invalid year
        });
        assertEquals("Year must be between 0 and 2025", exception.getMessage());

        Exception exception2 = assertThrows(IllegalArgumentException.class, () -> {
            book.setYear(-1);  // Invalid year
        });
        assertEquals("Year must be between 0 and 2025", exception2.getMessage());
    }

    @Test
    void testSetAndGetGenre() {
        book.setGenre("Fiction");
        assertEquals("Fiction", book.getGenre());
    }

    @Test
    void testSetAndGetIsbn() {
        book.setIsbn("9781234567890");
        assertEquals("9781234567890", book.getIsbn());
    }

    @Test
    void testSetIsbnThrowsExceptionOnInvalidIsbn() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            book.setIsbn("123");  // Invalid ISBN
        });
        assertEquals("ISBN must be 13 digits", exception.getMessage());
    }

    @Test
    void testSetAndGetFileName() {
        book.setTitle("Test Title");
        String fileName = book.getFileName();
        assertTrue(fileName.endsWith(".txt")); 
    }

    @Test
    void testGetFilePath() {
        book.setTitle("Test Title");
        String bookDir = "books";
        String expectedFilePath = Paths.get(bookDir, book.getFileName()).toString();
        assertEquals(expectedFilePath, book.getFilePath(bookDir));
    }

    @Test 
    void testSetAndGetId() {
        book.setId(1);
        assertEquals(1, book.getId());
    }

    @Test
    void testGetMetadata() {
        book.setTitle("The Great Gatsby");
        book.setAuthor("F. Scott Fitzgerald");
        book.setGenre("Fiction");
        book.setIsbn("9781234567890");
        book.setYear(1925);

        String expectedMetadata = "Title: The Great Gatsby\n" +
                                  "Author: F. Scott Fitzgerald\n" +
                                  "Genre: Fiction\n" +
                                  "ISBN: 9781234567890\n" +
                                  "Year: 1925";
        assertEquals(expectedMetadata, book.getMetadata());
    }
}
