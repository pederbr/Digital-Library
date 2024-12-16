package model;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;


class JsonToPagedContentTest {
    @Test
    void testToPagedContent_validJson() throws IOException {
        String json = """
            {
                "content": "Some paginated content",
                "pageNumber": 1,
                "totalPages": 10,
                "pageSize": 20,
                "totalSize": 200
            }
            """;

        PagedContent pagedContent = JsonToPagedContent.toPagedContent(json);

        PagedContent expected = new PagedContent("Some paginated content", 1, 10, 20, 200);

        assertEquals(expected.getContent(), pagedContent.getContent());
        assertEquals(expected.getMetaData(), pagedContent.getMetaData());
    }

    @Test
    void testToPagedContent_missingFields() throws IOException {
        String json = """
            {
                "content": "Some content"
            }
            """;

        PagedContent pagedContent = JsonToPagedContent.toPagedContent(json);

        assertEquals("Some content", pagedContent.getContent());
        assertEquals(0, pagedContent.getPageNumber());  
        assertEquals(0, pagedContent.getTotalPages());
        assertEquals(0, pagedContent.getPageSize());
        assertEquals(0, pagedContent.getTotalSize());
    }

    @Test
    void testToPagedContent_emptyJson() throws IOException {
        String json = "{}";

        PagedContent pagedContent = JsonToPagedContent.toPagedContent(json);

        assertNull(pagedContent.getContent());
        assertEquals(0, pagedContent.getPageNumber());
        assertEquals(0, pagedContent.getTotalPages());
        assertEquals(0, pagedContent.getPageSize());
        assertEquals(0, pagedContent.getTotalSize());
    }

    @Test
    void testToPagedContent_invalidJson() {
        String json = "invalid json string";

        assertThrows(JsonProcessingException.class, () -> {
            JsonToPagedContent.toPagedContent(json);
        });
    }
}
