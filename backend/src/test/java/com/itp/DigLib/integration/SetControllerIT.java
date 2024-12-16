package com.itp.DigLib.integration;

import com.itp.DigLib.api.controller.SetController;
import com.itp.DigLib.api.model.Book;
import com.itp.DigLib.api.service.BookContentService;
import com.itp.DigLib.db.BookRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(SetController.class)
public class SetControllerIT {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookRepository bookRepo;
    
    @MockBean
    private BookContentService bookContentService;

    @Test
    public void testAddNewBook() throws Exception {
        // Arrange
        when(bookRepo.save(any(Book.class))).thenAnswer(invocation -> {
            Book book = invocation.getArgument(0);
            book.setId(1);  
            book.setTitle("Test Book");
            return book;
        });
        
        MockMultipartFile content = new MockMultipartFile(
            "content",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        // Act & Assert
        mockMvc.perform(multipart("/books")
                .file(content)
                .param("title", "Test Book")
                .param("author", "Test Author")
                .param("genre", "Fiction")
                .param("isbn", "1234567890123")
                .param("year", "2020"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("ID:1"));
    }

    @Test
    public void testDeleteBook() throws Exception {
        // Arrange
        Book book = new Book();
        book.setId(1);  // Set ID first
        book.setTitle("Test Book");
        
        when(bookRepo.findById(1)).thenReturn(Optional.of(book));

        // Act & Assert
        mockMvc.perform(delete("/books/1"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(content().string("Book deleted successfully"));
    }

    @Test
    public void testDeleteBook_NotFound() throws Exception {
        // Arrange
        when(bookRepo.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(delete("/books/99"))
            .andDo(print())
            .andExpect(status().isNotFound());
    }
}