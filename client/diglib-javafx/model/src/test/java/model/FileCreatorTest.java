package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;


class FileCreatorTest {
    private FileCreator fileCreator;
    private Book testBook;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileCreator = new FileCreator();
        testBook = new Book();
        testBook.setTitle("Test Book");
        testBook.setAuthor("Test Author");
        testBook.setGenre("Fiction");
        testBook.setIsbn("1234567890123");
        testBook.setYear(2023);
        testBook.setID(1);
        }

    @Test
    void createBookFile_Success() throws IOException {
        // Arrange
        List<PagedContent> contentList = Arrays.asList(
            new PagedContent("First page content\n", 0, 2, 100, 200),
            new PagedContent("Second page content", 1, 2, 100, 200)
        );

        // Act
        fileCreator.createBookFile(testBook, tempDir, contentList);

        // Assert
        Path createdFile = tempDir.resolve(testBook.getFileName());
        assertTrue(Files.exists(createdFile));
        String fileContent = Files.readString(createdFile);
        assertEquals("First page content\nSecond page content", fileContent);
    }

    @Test
    void createBookFile_EmptyContent() throws IOException {
        // Arrange
        List<PagedContent> emptyContentList = Collections.emptyList();

        // Act
        fileCreator.createBookFile(testBook, tempDir, emptyContentList);

        // Assert
        Path createdFile = tempDir.resolve(testBook.getFileName());
        assertTrue(Files.exists(createdFile));
        String fileContent = Files.readString(createdFile);
        assertTrue(fileContent.isEmpty());
    }

    @Test
    void createBookFile_NonexistentDirectory() throws IOException {
        // Arrange
        Path nonExistentDir = tempDir.resolve("nonexistent");
        List<PagedContent> contentList = Collections.singletonList(
            new PagedContent("Test content", 0, 1, 100, 100)
        );

        // Act
        fileCreator.createBookFile(testBook, nonExistentDir, contentList);

        // Assert
        Path createdFile = nonExistentDir.resolve(testBook.getFileName());
        assertTrue(Files.exists(createdFile));
        assertTrue(Files.exists(nonExistentDir));
        String fileContent = Files.readString(createdFile);
        assertEquals("Test content", fileContent);
    }

    @Test
    void createBookFile_OverwriteExisting() throws IOException {
        // Arrange
        Path bookFile = tempDir.resolve(testBook.getFileName());
        Files.writeString(bookFile, "Original content");

        List<PagedContent> contentList = Collections.singletonList(
            new PagedContent("New content", 0, 1, 100, 200)
        );

        // Act
        fileCreator.createBookFile(testBook, tempDir, contentList);

        // Assert
        assertTrue(Files.exists(bookFile));
        String fileContent = Files.readString(bookFile);
        assertEquals("New content", fileContent);
    }

    @Test
    void createBookFile_LargeContent() throws IOException {
        // Arrange
        StringBuilder largeContent = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeContent.append("Page ").append(i).append(" content\n");
        }

        List<PagedContent> contentList = Collections.singletonList(
            new PagedContent(largeContent.toString(), 0, 1, 100000, 10000)
        );

        // Act
        fileCreator.createBookFile(testBook, tempDir, contentList);

        // Assert
        Path createdFile = tempDir.resolve(testBook.getFileName());
        assertTrue(Files.exists(createdFile));
        String fileContent = Files.readString(createdFile);
        assertEquals(largeContent.toString(), fileContent);
    }

    @Test
    void createBookFile_SpecialCharacters() throws IOException {
        // Arrange
        List<PagedContent> contentList = Collections.singletonList(
            new PagedContent("Special chars: áéíóú ñ 你好 \n\t\r", 0, 1, 100, 100)
        );

        // Act
        fileCreator.createBookFile(testBook, tempDir, contentList);

        // Assert
        Path createdFile = tempDir.resolve(testBook.getFileName());
        assertTrue(Files.exists(createdFile));
        String fileContent = Files.readString(createdFile);
        assertEquals("Special chars: áéíóú ñ 你好 \n\t\r", fileContent);
    }
}