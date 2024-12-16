package com.itp.DigLib.api.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.itp.DigLib.api.model.Book;
import com.itp.DigLib.api.model.Genres;
import com.itp.DigLib.api.service.BookContentService;
import com.itp.DigLib.db.BookRepository;
import com.itp.DigLib.api.model.PagedContent;

/**
 * REST controller for handling book-related requests.
 */
@RestController
@RequestMapping("/")
public class GetController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GetController.class);

    @Autowired
    private BookRepository bookRepo;

    @Autowired
    private BookContentService bookContentService;

    @Autowired
    private Genres genres;

    /**
     * Fetches a paginated list of books with optional sorting and filtering.
     *
     * @param page the page number to retrieve (default is 0)
     * @param size the number of items per page (default is 10)
     * @param sortBy the field to sort by (default is "title")
     * @param sortDir the direction to sort (asc or desc, default is "asc")
     * @param title optional filter by book title
     * @param author optional filter by book author
     * @param genre optional filter by book genre
     * @return a paginated list of books
     */
    @GetMapping("/books")
    public @ResponseBody Page<Book> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String genre
    ) {
        LOGGER.info("Fetching all books with page: {}, size: {}, sortBy: {}, sortDir: {}, title: {}, author: {}, genre: {}",
        page, size, sortBy, sortDir, title, author, genre);

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy);
        PageRequest pageRequest = PageRequest.of(page, size, sort);

        Page<Book> result;
        if (title != null) {
            result = bookRepo.findByTitleContainingIgnoreCase(title, pageRequest);
        } else if (author != null) {
            result = bookRepo.findByAuthorContainingIgnoreCase(author, pageRequest);
        } else if (genre != null) {
            result = bookRepo.findByGenreContainingIgnoreCase(genre, pageRequest);
        } else {
            result = bookRepo.findAll(pageRequest);
        } 
        LOGGER.info("Fetched {} books", result.getTotalElements());
        return result;
    }

    /**
     * Fetches a book by its ID.
     *
     * @param id the ID of the book to retrieve
     * @return the book with the specified ID, or a 404 Not Found status if the book does not exist
     */
    @GetMapping("/books/{id}")
    public @ResponseBody ResponseEntity<Book> getBook(@PathVariable int id) {
        LOGGER.info("Fetching book with ID: {}", id);
        return bookRepo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Fetches the content of a book by its ID.
     *
     * @param id the ID of the book
     * @param page the page number of the content to retrieve (default is 0)
     * @param pageSize the size of the content page (optional)
     * @return the content of the book, or a 404 Not Found status if the book does not exist,
     *         or a 500 Internal Server Error status if an error occurs while reading the content
     */
    @GetMapping("/books/{id}/content")
    public @ResponseBody ResponseEntity<PagedContent> getBookContent(
            @PathVariable int id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Integer pageSize
    ) {
        LOGGER.info("Fetching content for book with ID: {}, page: {}, pageSize: {}", id, page, pageSize);
        try {
            Optional<Book> bookOpt = bookRepo.findById(id);
            if (bookOpt.isPresent()) {
                Book book = bookOpt.get();
                PagedContent content = bookContentService.readBookContent(book.getFileName(), page, pageSize);
                return ResponseEntity.ok(content);
            } else {
                LOGGER.error("Book with ID: {} not found", id);
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read book content for book with ID: {}. Error: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Fetches a list of available genres.
     *
     * @return a list of genres, or a 500 Internal Server Error status if an error occurs while fetching the genres
     */
    @GetMapping("/genres")
    public @ResponseBody ResponseEntity<List<String>> getGenres() {
        LOGGER.info("Fetching genres");
        try {
            List<String> genresList = genres.getGenresList();
            return ResponseEntity.ok(genresList);
        } catch (IOException e) {
            LOGGER.error("Failed to fetch genres: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}
