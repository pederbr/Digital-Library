package com.itp.DigLib.api.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenresTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private Genres genres;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetGenresList() throws IOException {
        List<String> mockGenres = Arrays.asList("Fiction", "Non-Fiction", "Science Fiction");
        when(objectMapper.readValue(
            org.mockito.ArgumentMatchers.any(InputStream.class), 
            org.mockito.ArgumentMatchers.<TypeReference<List<String>>>any()
        )).thenReturn(mockGenres);

        List<String> genresList = genres.getGenresList();
        assertEquals(mockGenres, genresList);
    }

    @Test
    public void testGetGenresListIOException() throws IOException {
        when(objectMapper.readValue(
            org.mockito.ArgumentMatchers.any(InputStream.class), 
            org.mockito.ArgumentMatchers.<TypeReference<List<String>>>any()
        )).thenThrow(new IOException("Test exception"));

        IOException exception = assertThrows(IOException.class, () -> {
            genres.getGenresList();
        });

        assertEquals("Test exception", exception.getMessage());
    }
}
