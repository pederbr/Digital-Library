package com.itp.DigLib.integration;

import com.itp.DigLib.api.controller.GetController;
import com.itp.DigLib.api.model.Book;
import com.itp.DigLib.api.model.Genres;
import com.itp.DigLib.api.model.PagedContent;
import com.itp.DigLib.api.service.BookContentService;
import com.itp.DigLib.db.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(GetController.class)
public class GetControllerIT {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookRepository bookRepo;
    
    @MockBean
    private BookContentService bookContentService;

    @MockBean
    private Genres genres;

    @Test
    public void testGetAllBooks() throws Exception {
        // Arrange
        Book book1 = new Book();
        book1.setId(1);
        book1.setTitle("Test Book 1");
        book1.setAuthor("Author 1");
        book1.setYear(2020);
        book1.setGenre("Fiction");
        book1.setIsbn("1234567890123");

        List<Book> books = Arrays.asList(book1);
        Page<Book> bookPage = new PageImpl<>(books);
        
        when(bookRepo.findAll(any(PageRequest.class))).thenReturn(bookPage);

        // Act & Assert
        mockMvc.perform(get("/books"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title").value("Test Book 1"))
            .andExpect(jsonPath("$.content[0].author").value("Author 1"));
    }

    @Test
    public void testGetBookContent() throws Exception {
        // Arrange
        Book book = new Book();
        book.setId(1);
        book.setTitle("Test Book");
        when(bookRepo.findById(1)).thenReturn(java.util.Optional.of(book));

        PagedContent content = new PagedContent("Test content", 0, 5, 100, 500);
        when(bookContentService.readBookContent(any(), any(Integer.class), any()))
            .thenReturn(content);

        // Act & Assert
        mockMvc.perform(get("/books/1/content"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    public void testGetGenres() throws Exception {
        // Arrange
        List<String> genresList = Arrays.asList("Fiction", "Non-Fiction");
        when(genres.getGenresList()).thenReturn(genresList);

        // Act & Assert
        mockMvc.perform(get("/genres"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0]").value("Fiction"))
            .andExpect(jsonPath("$[1]").value("Non-Fiction"));
    }
}