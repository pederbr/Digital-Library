package model;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;


/**
 * The {@code JsonToPagedContent} class provides methods to convert JSON strings to {@link PagedContent} objects.
 */
public class JsonToPagedContent {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .addMixIn(PagedContent.class, PagedContentMixin.class);

    /**
     * Converts a JSON string to a {@link PagedContent} object.
     *
     * @param json the JSON string representing a paged content
     * @return a {@link PagedContent} object
     * @throws IOException if an I/O error occurs while processing the JSON string
     */
    public static PagedContent toPagedContent(String json) throws IOException {
        return OBJECT_MAPPER.readValue(json, PagedContent.class);
    }

    /**
     * Mixin class for {@link PagedContent} to handle JSON serialization and deserialization.
     */
    public abstract static class PagedContentMixin {
        /**
         * Constructor for {@code PagedContentMixin}.
         *
         * @param content the content of the page
         * @param pageNumber the current page number
         * @param totalPages the total number of pages
         * @param pageSize the size of the page
         * @param totalSize the total size of the content
         */
        @JsonCreator
        public PagedContentMixin(
            @JsonProperty("content") String content,
            @JsonProperty("pageNumber") int pageNumber,
            @JsonProperty("totalPages") int totalPages,
            @JsonProperty("pageSize") int pageSize,
            @JsonProperty("totalSize") int totalSize) {}
    }
}