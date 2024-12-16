package model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class FileCreator {

    /**
     * Creates a file for the specified {@link Book} in the given directory with the provided content.
     *
     * @param selectedBook the {@link Book} object for which the file is to be created
     * @param path the directory in which the file is to be created
     * @param contentList the list of {@link PagedContent} objects representing the content to write to the file
     * @throws IOException if an I/O error occurs while creating the file or writing the content
     */
    public void createBookFile(Book selectedBook, Path path, List<PagedContent> contentList) throws IOException {
        // Ensure the directory exists
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }

        // Creates a new file with the name of the book
        Path newFile = path.resolve(selectedBook.getFileName());

        StringBuilder content = new StringBuilder();
        for (PagedContent contentPage : contentList) {
            content.append(contentPage.getContent());
        }
        
        // Writes the content of the book to the file
        Files.writeString(newFile, content.toString());
    }
}