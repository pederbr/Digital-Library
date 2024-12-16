package com.itp.DigLib.api.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Component responsible for loading genres from a JSON file.
 */
@Component
public class Genres {
    private static final Logger LOGGER = LoggerFactory.getLogger(Genres.class);
    private static final String GENRE_FILE = "GenreList.json";
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Retrieves the list of genres from the JSON file.
     *
     * @return a list of genres
     * @throws IOException if an I/O error occurs while reading the JSON file
     */
    public List<String> getGenresList() throws IOException {
        LOGGER.info("Loading genres from {}", GENRE_FILE);
        try {
            ClassPathResource resource = new ClassPathResource(GENRE_FILE);
            InputStream inputStream = resource.getInputStream();
            return objectMapper.readValue(inputStream, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            LOGGER.error("Failed to load genres from {}: {}", GENRE_FILE, e.getMessage());
            throw e;
        }
    }
}