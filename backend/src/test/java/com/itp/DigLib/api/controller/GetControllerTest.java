package com.itp.DigLib.api.controller;

import java.util.Arrays;
import java.util.Optional;
import java.io.IOException;
import java.util.List;
import static org.mockito.Mockito.doThrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.itp.DigLib.api.model.Book;
import com.itp.DigLib.api.service.BookContentService;
import com.itp.DigLib.db.BookRepository;
import com.itp.DigLib.api.model.Genres;
import com.itp.DigLib.api.model.PagedContent;

public class GetControllerTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private BookContentService bookContentService;

    @InjectMocks
    private GetController getController;

    @Mock
    private Genres genres;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setTitle("Book 2");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "title", "asc", null, null, null);

        assertEquals(2, result.getContent().size());
        verify(bookRepo).findAll(any(PageRequest.class));
    }

    @Test
    void testGetBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        ResponseEntity<Book> response = getController.getBook(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Test Book", response.getBody().getTitle());
    }

    @Test
    void testGetBookContent() throws Exception {
        Book book = new Book();
        book.setTitle("Test Book");
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        PagedContent pagedContent = new PagedContent("test content", 0, 1, 12, 12);
        when(bookContentService.readBookContent(anyString(), anyInt(), any())).thenReturn(pagedContent);

        ResponseEntity<PagedContent> response = getController.getBookContent(1, 0, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("test content", response.getBody().getContent());

        // Test case where book is not found
        when(bookRepo.findById(2)).thenReturn(Optional.empty());
        ResponseEntity<PagedContent> notFoundResponse = getController.getBookContent(2, 0, null);
        assertEquals(HttpStatus.NOT_FOUND, notFoundResponse.getStatusCode());

        // Test case where IOException is thrown
        when(bookRepo.findById(3)).thenReturn(Optional.of(book));
        when(bookContentService.readBookContent(anyString(), anyInt(), any())).thenThrow(new IOException("Test IOException"));
        ResponseEntity<PagedContent> errorResponse = getController.getBookContent(3, 0, null);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
    }

    @Test
    void testGetAllBooksByTitle() {
        Book book1 = new Book();
        book1.setTitle("Spring in Action");
        Book book2 = new Book();
        book2.setTitle("Spring Boot in Practice");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findByTitleContainingIgnoreCase(anyString(), any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "title", "asc", "Spring", null, null);

        assertEquals(2, result.getContent().size());
        verify(bookRepo).findByTitleContainingIgnoreCase(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetAllBooksByAuthor() {
        Book book1 = new Book();
        book1.setAuthor("Craig Walls");
        Book book2 = new Book();
        book2.setAuthor("Mark Heckler");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findByAuthorContainingIgnoreCase(anyString(), any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "author", "asc", null, "Craig", null);

        assertEquals(2, result.getContent().size());
        verify(bookRepo).findByAuthorContainingIgnoreCase(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetAllBooksByGenre() {
        Book book1 = new Book();
        book1.setGenre("Technology");
        Book book2 = new Book();
        book2.setGenre("Technology");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findByGenreContainingIgnoreCase(anyString(), any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "genre", "asc", null, null, "Technology");

        assertEquals(2, result.getContent().size());
        verify(bookRepo).findByGenreContainingIgnoreCase(anyString(), any(PageRequest.class));
    }

    @Test
    void testGetAllBooksNoFilters() {
        Book book1 = new Book();
        book1.setTitle("Book 1");
        Book book2 = new Book();
        book2.setTitle("Book 2");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "title", "asc", null, null, null);

        assertEquals(2, result.getContent().size());
        verify(bookRepo).findAll(any(PageRequest.class));
    }

    @Test
    void testGetGenres() throws Exception {
        List<String> genresList = Arrays.asList("Technology", "Science Fiction", "Fantasy");
        when(genres.getGenresList()).thenReturn(genresList);

        ResponseEntity<List<String>> response = getController.getGenres();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(genresList, response.getBody());

        // Test case where IOException is thrown
        doThrow(new IOException("Test IOException")).when(genres).getGenresList();
        ResponseEntity<List<String>> errorResponse = getController.getGenres();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
    }

    @Test
    void testGetAllBooksSortedByTitleAsc() {
        Book book1 = new Book();
        book1.setTitle("A Book");
        Book book2 = new Book();
        book2.setTitle("Z Book");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "title", "asc", null, null, null);

        assertEquals(2, result.getContent().size());
        assertEquals("A Book", result.getContent().get(0).getTitle());
        assertEquals("Z Book", result.getContent().get(1).getTitle());
        verify(bookRepo).findAll(any(PageRequest.class));
    }

    @Test
    void testGetAllBooksSortedByTitleDesc() {
        Book book1 = new Book();
        book1.setTitle("Z Book");
        Book book2 = new Book();
        book2.setTitle("A Book");

        Page<Book> page = new PageImpl<>(Arrays.asList(book1, book2));
        when(bookRepo.findAll(any(PageRequest.class))).thenReturn(page);

        Page<Book> result = getController.getAllBooks(0, 10, "title", "desc", null, null, null);

        assertEquals(2, result.getContent().size());
        assertEquals("Z Book", result.getContent().get(0).getTitle());
        assertEquals("A Book", result.getContent().get(1).getTitle());
        verify(bookRepo).findAll(any(PageRequest.class));
    }
}

