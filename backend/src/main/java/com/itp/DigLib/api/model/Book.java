package com.itp.DigLib.api.model;

import java.nio.file.Paths;
import java.util.Random;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

/**
 * Represents a book with a title, author, year, genre, ISBN, and file path.
 */
@Entity // This tells Hibernate to make a table out of this class
public class Book {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String title;
    private String author;
    private int year;
    private String genre;
    private String isbn;
    private String filename;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    public Book(){}

    public void setYear(int year) {
        if(year < 0 || year > 2025) {
            throw new IllegalArgumentException("Year must be between 0 and 2025");
        } 
        this.year = year;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setIsbn(String isbn) {
        if(!isbn.matches("[0-9]{13}")) {
            throw new IllegalArgumentException("ISBN must be 13 digits");
        }
        this.isbn = isbn;
    }

    public void setAuthor(String author) {
        if(author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if(author.length() > 100) {
            throw new IllegalArgumentException("Author must be less than 100 characters");
        }
        this.author = author;
    }

    public void setTitle(String title) {
        if(title.length() > 100) {
            throw new IllegalArgumentException("Title must be less than 100 characters");
        }
        if(title.isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        this.title = title;
        
        // Generate a random file name for the book
        int length = title.length() + 5;
        
        StringBuilder sb = new StringBuilder(length);
        
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        this.filename = sb.toString() + ".txt";
    }

    public void setId(int id) {
        this.id = id; 
    }


    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public int getId() {
        return id;
    }


    /**
     * Returns the file name of the book.
     *
     * @return the file name of the book
     */
    public String getFileName() {
        return filename;
    }

    /**
     * Returns the author of the book.
     *
     * @return the author of the book
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Returns the year of publication of the book.
     *
     * @return the year of publication of the book
     */
    public int getYear() {
        return year;
    }

    /**
     * Returns the genre of the book.
     *
     * @return the genre of the book
     */
    public String getGenre() {
        return genre;
    }

    /**
     * Returns the ISBN of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }

    /**
     * Returns the file path of the book.
     *
     * @param getBookDir the directory where the book is stored
     * 
     * @return the file path of the book
     */
    @JsonIgnore
    public String getFilePath(String getBookDir) {
        return Paths.get(getBookDir, filename).toString();
    }
    /**
     * Returns the metadata of the book as a formatted string.
     * The metadata includes the title, author, genre, ISBN, and year of the book.
     * 
     * <p>The format of the returned string is:</p>
     * <pre>
     * Title: [title]
     * Author: [author]
     * Genre: [genre]
     * ISBN: [isbn]
     * Year: [year]
     * </pre>
     * 
     * @return a string containing the metadata of the book.
     */
    
    @JsonIgnore
    public String getMetadata() {
        return "Title: " + title + "\nAuthor: " + author + "\nGenre: " + genre + "\nISBN: " + isbn + "\nYear: " + year;
    }

}