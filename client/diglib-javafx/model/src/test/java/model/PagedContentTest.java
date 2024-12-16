package model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PagedContentTest {

    @Test
    void testConstructor() {
        String content = "This is the content for page 1.";
        int pageNumber = 0;
        int totalPages = 5;
        int pageSize = 32;
        int totalSize = 160;

        PagedContent pagedContent = new PagedContent(content, pageNumber, totalPages, pageSize, totalSize);

        Assertions.assertEquals(content, pagedContent.getContent());
        Assertions.assertEquals(pageNumber, pagedContent.getPageNumber());
        Assertions.assertEquals(totalPages, pagedContent.getTotalPages());
        Assertions.assertEquals(pageSize, pagedContent.getPageSize());
        Assertions.assertEquals(totalSize, pagedContent.getTotalSize());
        Assertions.assertNotNull(pagedContent.getReadingTime());
    }

    @Test
    void testCalculateReadingTime() {
        String content = "This is the content for page 1. This is the content for page 1. This is the content for page 1. This is the content for page 1.";
        int pageNumber = 0;
        int totalPages = 5;
        int pageSize = 100;
        int totalSize = 500;

        PagedContent pagedContent = new PagedContent(content, pageNumber, totalPages, pageSize, totalSize);

        String expectedReadingTime = "0 h, 0 min";
        Assertions.assertEquals(expectedReadingTime, pagedContent.getReadingTime());
    }

    @Test
    void testGetMetaData() {
        String content = "This is the content for page 1.";
        int pageNumber = 0;
        int totalPages = 5;
        int pageSize = 32;
        int totalSize = 160;

        PagedContent pagedContent = new PagedContent(content, pageNumber, totalPages, pageSize, totalSize);

        String readingTime = pagedContent.getReadingTime();
        String expectedMetadata = String.format("%nPage %d of %d%nReading time: %s", pageNumber + 1, totalPages, readingTime);
        Assertions.assertEquals(expectedMetadata, pagedContent.getMetaData());
    }
}