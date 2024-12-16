package com.itp.DigLib.deployment;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DeploymentIT {

    private final String BASE_URL = System.getProperty("deployment.url", "https://api.diglib.no");
    private final RestTemplate restTemplate = new RestTemplate();
    private static Integer createdBookId;
    private static final String BOOK_GENRE = "Action";

    @Test
    @Order(1)
    public void shouldAddNewBook() {
        // Arrange
        String endpoint = BASE_URL + "/books";
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("title", "Test Book For Deployment");
        body.add("author", "Test Author");
        body.add("genre", BOOK_GENRE); // Using the correct genre
        body.add("isbn", "1234567890123");
        body.add("year", "2024");
        
        String contentString = "This is test content for the book";
        ByteArrayResource contentResource = new ByteArrayResource(contentString.getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        };
        body.add("content", contentResource);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
            endpoint,
            HttpMethod.POST,
            requestEntity,
            String.class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).startsWith("ID:");
        
        createdBookId = Integer.parseInt(response.getBody().substring(3));
    }

    @Test
    @Order(2)
    public void shouldGetAllBooks() {
        // Arrange
        String endpoint = BASE_URL + "/books";

        // Act
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("content")).isNotNull();
        
        List<Map<String, Object>> books = (List<Map<String, Object>>) response.getBody().get("content");
        assertThat(books).isNotEmpty();
        
        boolean foundCreatedBook = books.stream()
            .anyMatch(book -> book.get("id").equals(createdBookId));
        assertThat(foundCreatedBook).isTrue();
    }

    @Test
    @Order(3)
    public void shouldGetGenres() {
        // Arrange
        String endpoint = BASE_URL + "/genres";

        // Act
        ResponseEntity<String[]> response = restTemplate.getForEntity(
            endpoint, 
            String[].class
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isGreaterThan(0);
        
        // Check for some specific genres that we know exist in the system
        assertThat(response.getBody()).contains(
            "Science Fiction (Sci-Fi)",
            "Fantasy",
            "Mystery"
        );
        
        // Verify the genre we used for our test book exists
        assertThat(response.getBody()).contains(BOOK_GENRE);
    }

    @Test
    @Order(4)
    public void shouldGetBookContent() {
        // Arrange
        String endpoint = String.format("%s/books/%d/content", BASE_URL, createdBookId);

        // Act
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
            endpoint,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("content")).isNotNull();
        assertThat(response.getBody().get("pageNumber")).isNotNull();
        assertThat(response.getBody().get("pageSize")).isNotNull();
        assertThat(response.getBody().get("totalPages")).isNotNull();
    }

    @Test
    @Order(5)
    public void shouldDeleteBook() {
        // Arrange
        String endpoint = String.format("%s/books/%d", BASE_URL, createdBookId);

        // Act
        ResponseEntity<String> deleteResponse = restTemplate.exchange(
            endpoint,
            HttpMethod.DELETE,
            null,
            String.class
        );

        // Assert deletion was successful
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(deleteResponse.getBody()).isEqualTo("Book deleted successfully");

        // Verify book no longer exists
        try {
            restTemplate.getForEntity(endpoint, Map.class);
            assertThat(false).isTrue(); // Should not reach this line
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("404");
        }
    }
    
    @Test
    @Order(6)
    public void shouldReturnNotFoundForNonExistentBook() {
        // Arrange
        String endpoint = BASE_URL + "/books/99999";

        // Act & Assert
        try {
            restTemplate.exchange(
                endpoint,
                HttpMethod.DELETE,
                null,
                String.class
            );
            assertThat(false).isTrue(); // Should not reach this line
        } catch (Exception e) {
            assertThat(e.getMessage()).contains("404");
        }
    }
}