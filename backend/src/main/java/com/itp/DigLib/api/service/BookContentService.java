package com.itp.DigLib.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;

import com.itp.DigLib.api.model.PagedContent;

/**
 * Service for managing book content stored in Google Cloud Storage.
 */
@Service
public class BookContentService {    
    private final Storage storage;
    private final String bucketName;
    private final int defaultPageSize;

    /**
     * Constructs a new BookContentService.
     *
     * @param storage the Google Cloud Storage instance
     * @param bucketName the name of the bucket where book contents are stored
     * @param pageSize the default page size for reading book content
     */
    public BookContentService(
            Storage storage,
            @Value("${spring.cloud.gcp.storage.bucket-name}") String bucketName,
            @Value("${book.page.size:1000}") int pageSize
    ) {
        this.storage = storage;
        this.bucketName = bucketName;
        this.defaultPageSize = pageSize;
    }

    /**
     * Stores a file in Google Cloud Storage.
     *
     * @param file the file to store
     * @param filename the name of the file to store
     * @return the path to the stored file
     * @throws IOException if an I/O error occurs
     */
    public String storeFile(MultipartFile file, String filename) throws IOException {
        BlobId blobId = BlobId.of(bucketName, "bookcontents/" + filename);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("text/plain").build();
        
        storage.create(blobInfo, file.getBytes());
        
        return String.format("gs://%s/%s", bucketName, filename);
    }

    /**
     * Reads the content of a book from Google Cloud Storage.
     *
     * @param filename the name of the file to read
     * @param pageNumber the page number to read
     * @param pageSize the size of the page to read, or null to use the default page size
     * @return the paged content
     * @throws IOException if an I/O error occurs or the file is not found
     * @throws IllegalArgumentException if the filename is null or the page number is invalid
     */
    public PagedContent readBookContent(String filename, int pageNumber, Integer pageSize) throws IOException {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        BlobId blobId = BlobId.of(bucketName, "bookcontents/" + filename);
        Blob blob = storage.get(blobId);
        
        if (blob == null) {
            throw new IOException("File not found: " + filename);
        }

        byte[] content = blob.getContent();
        String fullContent = new String(content, StandardCharsets.UTF_8);
        
        int charactersPerPage = pageSize != null ? pageSize : defaultPageSize;
        int totalPages = (fullContent.length() + charactersPerPage - 1) / charactersPerPage;
        
        if (pageNumber < 0 || pageNumber >= totalPages) {
            throw new IllegalArgumentException("Invalid page number");
        }
        
        int startPosition = pageNumber * charactersPerPage;
        int endPosition = Math.min(startPosition + charactersPerPage, fullContent.length());
        String pageContent = fullContent.substring(startPosition, endPosition);
        
        return new PagedContent(
            pageContent,
            pageNumber,
            totalPages,
            endPosition - startPosition,
            fullContent.length()
        );
    }

    /**
     * Deletes a book content file from Google Cloud Storage.
     *
     * @param filename the name of the file to delete
     * @return true if the file was deleted, false otherwise
     * @throws IllegalArgumentException if the filename is null
     */
    public boolean deleteBookContent(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }
        BlobId blobId = BlobId.of(bucketName, "bookcontents/" + filename);
        return storage.delete(blobId);
    }
}