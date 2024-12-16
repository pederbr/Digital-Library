package com.itp.DigLib.api.controller;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import com.itp.DigLib.api.model.Book;
import com.itp.DigLib.api.service.BookContentService;
import com.itp.DigLib.db.BookRepository;

public class SetControllerTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private BookContentService bookContentService;

    @InjectMocks
    private SetController setController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddNewBook() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepo.save(any(Book.class))).thenReturn(book);

        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        ResponseEntity<String> response = setController.addNewBook(
            "Test Book",
            "Test Author",
            "Fiction",
            "1234567890123",
            2020,
            content
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("ID:" + book.getId(), response.getBody());
        verify(bookRepo).save(any(Book.class));
        verify(bookContentService).storeFile(any(), anyString());
    }

    @Test
    void testDeleteBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        ResponseEntity<String> response = setController.deleteBook(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book deleted successfully", response.getBody());
        verify(bookRepo).deleteById(1);
        verify(bookContentService).deleteBookContent(anyString());
    }

    @Test
    void testAddNewBookWithIOException() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepo.save(any(Book.class))).thenReturn(book);
        when(bookContentService.storeFile(any(), anyString())).thenThrow(new IOException("IO Error"));

        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        ResponseEntity<String> response = setController.addNewBook(
            "Test Book",
            "Test Author",
            "Fiction",
            "1234567890123",
            2020,
            content
        );

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to store book content: IO Error", response.getBody());
    }

    @Test
    void testAddNewBookWithIllegalArgumentException() throws Exception {
        when(bookRepo.save(any(Book.class))).thenThrow(new IllegalArgumentException("Illegal Argument"));

        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        ResponseEntity<String> response = setController.addNewBook(
            "Test Book",
            "Test Author",
            "Fiction",
            "1234567890123",
            2020,
            content
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Illegal values Illegal Argument", response.getBody());
    }

    @Test
    void testDeleteBookNotFound() {
        when(bookRepo.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<String> response = setController.deleteBook(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testAddNewBookWithMissingTitle() throws Exception {
        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        ResponseEntity<String> response = setController.addNewBook(
            "",
            "Test Author",
            "Fiction",
            "1234567890123",
            2020,
            content
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddNewBookWithInvalidISBN() throws Exception {
        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        ResponseEntity<String> response = setController.addNewBook(
            "Test Book",
            "Test Author",
            "Fiction",
            "invalid_isbn",
            2020,
            content
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}