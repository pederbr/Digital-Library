package model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a paginated portion of book content along with metadata about the pagination.
 * This class encapsulates both the actual content for a specific page and information
 * about the overall pagination state.
 */
public class PagedContent {
    private final String content;      // The actual text content for this page
    private final int pageNumber;      // Current page number (0-based)
    private final int totalPages;      // Total number of pages available
    private final int pageSize;        // Number of characters in this page
    private final int totalSize;       // Total number of characters in the entire book
    private final String readingTime;       // Average reading time for the entire book
    
    public PagedContent(
        @JsonProperty("content") String content,
        @JsonProperty("pageNumber") int pageNumber,
        @JsonProperty("totalPages") int totalPages,
        @JsonProperty("pageSize") int pageSize,
        @JsonProperty("totalSize") int totalSize) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
        this.readingTime = calculateReadingTime();
    }

    private String calculateReadingTime() {
        int words = this.totalSize / 5; // Average word length is 5 characters
        int avgReadingTime = 183; // Average reading speed in words per minute
        int totaltMinutes = words / avgReadingTime;
        int hours = totaltMinutes / 60;
        int minutes = totaltMinutes % 60;
        return String.format("%d h, %d min", hours, minutes);
    }
    
    // Getters
    /**
     * Gets the content of the current page.
     *
     * @return the text content of the page
     */
    public String getContent() { 
        return content; 
    }
    
    /**
     * Gets the current page number (0-based).
     *
     * @return the current page number
     */
    public int getPageNumber() { 
        return pageNumber; 
    }
    
    /**
     * Gets the total number of pages in the book.
     *
     * @return the total number of pages
     */
    public int getTotalPages() { 
        return totalPages; 
    }
    
    /**
     * Gets the size (number of characters) of the current page.
     *
     * @return the size of the current page
     */
    public int getPageSize() { 
        return pageSize; 
    }
    
    /**
     * Gets the total size (number of characters) of the entire book.
     *
     * @return the total size of the book
     */
    public int getTotalSize() { 
        return totalSize; 
    }
    
    /**
     * Gets the estimated reading time for the entire book.
     *
     * @return a formatted string representing the reading time in hours and minutes
     */
    @JsonIgnore
    public String getReadingTime() {
        return readingTime;
    }

    /**
     * Gets the metadata about the current page and book.
     *
     * @return formatted string containing metadata
     */
    public String getMetaData() {
        return String.format("%nPage %d of %d%nReading time: %s",
            pageNumber + 1, totalPages, readingTime);
    }
}