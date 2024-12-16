package model;

import java.nio.file.Paths;
import java.util.Random;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a book with a title, author, year, genre, ISBN, and file path.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book {
    private int ID;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final Random RANDOM = new Random();

    private  String title;
    private String author;
    private int year;
    private String genre;
    private String isbn;
    private String filename;

    /**
     * Sets the ID of the book.
     *
     * @param ID the unique identifier to set for the book
     */
    public void setID(int ID) {
        this.ID = ID;
    }
    
    /**
     * Returns the ID of the book.
     *
     * @return the ID of the book
     */

     public int getId() {
        return ID;
    }

    /**
     * Sets the publication year of the book.
     *
     * @param year the publication year to set
     * @throws IllegalArgumentException if the year is not between 0 and 2025
     */
    public void setYear(int year) {
        if(year < 0 || year > 2025) {
            throw new IllegalArgumentException("Year must be between 0 and 2025");
        } 
        this.year = year;
    }

    /**
     * Sets the genre of the book.
     *
     * @param genre the genre to set
     */
    public void setGenre(String genre) {
        this.genre = genre;
    }

    /**
     * Sets the ISBN of the book.
     *
     * @param isbn the ISBN to set
     * @throws IllegalArgumentException if the ISBN is not exactly 13 digits
     */
    public void setIsbn(String isbn) {
        if(!isbn.matches("[0-9]{13}")) {
            throw new IllegalArgumentException("ISBN must be 13 digits");
        }
        this.isbn = isbn;
    }

    /**
     * Sets the author of the book.
     *
     * @param author the author to set
     * @throws IllegalArgumentException if the author is empty or longer than 100 characters
     */
    public void setAuthor(String author) {
        if(author.isEmpty()) {
            throw new IllegalArgumentException("Author cannot be empty");
        }
        if(author.length() > 100) {
            throw new IllegalArgumentException("Author must be less than 100 characters");
        }
        this.author = author;
    }

    /**
     * Sets the title of the book and generates a random filename based on the title.
     *
     * @param title the title to set
     * @throws IllegalArgumentException if the title is empty or longer than 100 characters
     */
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

    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */

     public String getTitle() {
        return title;
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