package com.itp.DigLib.api.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.itp.DigLib.api.model.PagedContent;

@ExtendWith(MockitoExtension.class)
public class BookContentServiceTest {
    
    @Mock
    private Storage storage;
    
    @Mock
    private Blob mockBlob;
    
    private BookContentService bookContentService;
    private static final String BUCKET_NAME = "test-bucket";
    private static final String TEST_CONTENT = """
            First page content.
            Second page content.
            Third page content.
            Fourth page content.
            """;

    @BeforeEach
    void setUp() {
        bookContentService = new BookContentService(storage, BUCKET_NAME, 20); // Set page size to 20 chars
        
        // Setup default blob behavior
        lenient().when(mockBlob.getContent()).thenReturn(TEST_CONTENT.getBytes(StandardCharsets.UTF_8));
        lenient().when(storage.get(any(BlobId.class))).thenReturn(mockBlob);
    }

    @Test
    void testStoreFile() throws IOException {
        // Arrange
        String newContent = "This is new test content";
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "newtest.txt",
            "text/plain", 
            newContent.getBytes()
        );
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mockBlob);

        // Act
        String storedPath = bookContentService.storeFile(file, "newtest.txt");
        
        // Assert
        verify(storage).create(any(BlobInfo.class), any(byte[].class));
        assertEquals("gs://" + BUCKET_NAME + "/newtest.txt", storedPath);
    }

    @Test
    void testReadBookContentFirstPage() throws IOException {
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("test.txt", 0, 20);
        
        // Assert
        assertEquals("First page content.\n", pagedContent.getContent());
        assertEquals(0, pagedContent.getPageNumber());
        assertEquals(20, pagedContent.getPageSize());
        assertEquals(TEST_CONTENT.length(), pagedContent.getTotalSize());
        assertTrue(pagedContent.getTotalPages() > 1);
    }

    @Test
    void testReadBookContentMiddlePage() throws IOException {
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("test.txt", 1, 20);
        
        // Assert
        assertEquals("Second page content.", pagedContent.getContent());
        assertEquals(1, pagedContent.getPageNumber());
        assertEquals(20, pagedContent.getPageSize());
    }

    @Test
    void testReadBookContentLastPage() throws IOException {
        // Arrange
        int lastPage = (TEST_CONTENT.length() - 1) / 20;
        
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("test.txt", lastPage, 20);
        
        // Assert
        assertTrue(pagedContent.getContent().length() <= 20);
        assertEquals(lastPage, pagedContent.getPageNumber());
        assertEquals(TEST_CONTENT.length(), pagedContent.getTotalSize());
    }

    @Test
    void testReadBookContentCustomPageSize() throws IOException {
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("test.txt", 0, 10);
        
        // Assert
        assertEquals(10, pagedContent.getContent().length());
        assertEquals(0, pagedContent.getPageNumber());
        assertEquals(10, pagedContent.getPageSize());
        assertEquals(TEST_CONTENT.length(), pagedContent.getTotalSize());
    }

    @Test
    void testReadBookContentInvalidPageNumber() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookContentService.readBookContent("test.txt", -1, 20));

        assertThrows(IllegalArgumentException.class, () -> 
            bookContentService.readBookContent("test.txt", 1000, 20));
    }

    @Test
    void testDeleteExistingBookContent() {
        // Arrange
        when(storage.delete(any(BlobId.class))).thenReturn(true);
        
        // Act
        boolean result = bookContentService.deleteBookContent("test.txt");
        
        // Assert
        assertTrue(result);
        verify(storage).delete(any(BlobId.class));
    }

    @Test
    void testDeleteNonExistentBookContent() {
        // Arrange
        when(storage.delete(any(BlobId.class))).thenReturn(false);
        
        // Act
        boolean result = bookContentService.deleteBookContent("nonexistent.txt");
        
        // Assert
        assertFalse(result);
        verify(storage).delete(any(BlobId.class));
    }

    @Test
    void testReadNonExistentFile() {
        // Arrange
        when(storage.get(any(BlobId.class))).thenReturn(null);
        
        // Act & Assert
        assertThrows(IOException.class, () -> 
            bookContentService.readBookContent("nonexistent.txt", 0, 20));
    }

    @Test
    void testReadBookContentDefaultPageSize() throws IOException {
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("test.txt", 0, null);
        
        // Assert
        assertEquals(20, pagedContent.getContent().length()); // defaultPageSize is set to 20 in setUp()
        assertEquals(0, pagedContent.getPageNumber());
        assertEquals(20, pagedContent.getPageSize());
        assertEquals(TEST_CONTENT.length(), pagedContent.getTotalSize());
    }

    @Test
    void testStoreEmptyFile() throws IOException {
        // Arrange
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "empty.txt",
            "text/plain", 
            new byte[0]
        );
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mockBlob);

        // Act
        String storedPath = bookContentService.storeFile(file, "empty.txt");
        
        // Assert
        verify(storage).create(any(BlobInfo.class), any(byte[].class));
        assertEquals("gs://" + BUCKET_NAME + "/empty.txt", storedPath);
    }

    @Test
    void testStoreLargeFile() throws IOException {
        // Arrange
        byte[] largeContent = new byte[10_000_000]; // 10 MB
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "largefile.txt",
            "text/plain", 
            largeContent
        );
        when(storage.create(any(BlobInfo.class), any(byte[].class))).thenReturn(mockBlob);

        // Act
        String storedPath = bookContentService.storeFile(file, "largefile.txt");
        
        // Assert
        verify(storage).create(any(BlobInfo.class), any(byte[].class));
        assertEquals("gs://" + BUCKET_NAME + "/largefile.txt", storedPath);
    }

    @Test
    void testReadBookContentEdgeCaseLastPage() throws IOException {
        // Arrange
        String edgeCaseContent = "12345678901234567890"; // 20 characters
        lenient().when(mockBlob.getContent()).thenReturn(edgeCaseContent.getBytes(StandardCharsets.UTF_8));
        
        // Act
        PagedContent pagedContent = bookContentService.readBookContent("edgecase.txt", 0, 20);
        
        // Assert
        assertEquals(edgeCaseContent, pagedContent.getContent());
        assertEquals(0, pagedContent.getPageNumber());
        assertEquals(20, pagedContent.getPageSize());
        assertEquals(edgeCaseContent.length(), pagedContent.getTotalSize());
    }

    @Test
    void testReadBookContentNullFilename() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookContentService.readBookContent(null, 0, 20));
    }

    @Test
    void testDeleteBookContentNullFilename() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            bookContentService.deleteBookContent(null));
    }
}