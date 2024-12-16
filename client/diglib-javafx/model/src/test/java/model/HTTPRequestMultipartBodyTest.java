package model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HTTPRequestMultipartBodyTest {

    private HTTPRequestMultipartBody.Builder builder;

    @BeforeEach
    void setUp() {
        builder = new HTTPRequestMultipartBody.Builder();
    }

    @Test
    void testAddPart_withSimpleField() throws IOException {
        builder.addPart("field1", "value1");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"field1\""));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testAddPart_withContentType() throws IOException {
        builder.addPart("field1", "value1", "text/plain");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"field1\""));
        assertTrue(body.contains("Content-Type: text/plain"));
        assertTrue(body.contains("value1"));
    }

    @Test
    void testAddPart_withFilename() throws IOException {
        builder.addPart("fileField", "fileContent", "text/plain", "example.txt");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"fileField\"; filename=\"example.txt\""));
        assertTrue(body.contains("Content-Type: text/plain"));
        assertTrue(body.contains("fileContent"));
    }

    @Test
    void testBuild_withFilePart() throws IOException {
        // Create a temporary file with some content
        File tempFile = Files.createTempFile("testfile", ".txt").toFile();
        Files.writeString(tempFile.toPath(), "This is a test file.");

        builder.addPart("fileField", tempFile, "application/octet-stream", tempFile.getName());
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"fileField\"; filename=\"" + tempFile.getName() + "\""));
        assertTrue(body.contains("Content-Type: application/octet-stream"));
        assertTrue(body.contains("This is a test file."));

        // Cleanup
        tempFile.delete();
    }

    @Test
    void testBoundaryGeneration() throws IOException {
        builder.addPart("field1", "value1");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String boundary = multipartBody.getBoundary();
        assertNotNull(boundary);
        assertTrue(boundary.length() > 0);

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.startsWith("--" + boundary));
        assertTrue(body.endsWith("--" + boundary + "--\r\n"));
    }

    @Test
    void testGetContentType() throws IOException {
        builder.addPart("field1", "value1");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String contentType = multipartBody.getContentType();
        assertNotNull(contentType);
        assertTrue(contentType.startsWith("multipart/form-data; boundary="));
        assertTrue(contentType.contains(multipartBody.getBoundary()));
    }

    @Test
    void testAddPart_withByteArrayContent() throws IOException {
        byte[] content = "Binary content".getBytes(StandardCharsets.UTF_8);
        builder.addPart("binaryField", content, "application/octet-stream", "data.bin");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"binaryField\"; filename=\"data.bin\""));
        assertTrue(body.contains("Content-Type: application/octet-stream"));
        assertTrue(body.contains("Binary content"));
    }

    @Test
    void testAddPart_withSerializableObject() throws IOException {
        ArrayList<String> testList = new ArrayList<>();
        testList.add("test item");
        builder.addPart("objectField", testList, "application/java-object", "object.bin");
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"objectField\"; filename=\"object.bin\""));
        assertTrue(body.contains("Content-Type: application/java-object"));
    }

    @Test
    void testBuild_withEmptyParts() throws IOException {
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        String boundary = multipartBody.getBoundary();
        assertTrue(body.startsWith("--" + boundary + "--\r\n"));
    }

    @Test
    void testBuild_withMultipleParts() throws IOException {
        builder.addPart("field1", "value1")
               .addPart("field2", "value2", "text/plain")
               .addPart("field3", "value3", "text/html", "test.html");
        
        HTTPRequestMultipartBody multipartBody = builder.build();
        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        
        assertTrue(body.contains("name=\"field1\""));
        assertTrue(body.contains("name=\"field2\""));
        assertTrue(body.contains("name=\"field3\""));
        assertTrue(body.contains("filename=\"test.html\""));
    }

    @Test
    void testAddPart_withNullValues() throws IOException {
        builder.addPart("nullField", "", null, null);
        HTTPRequestMultipartBody multipartBody = builder.build();

        String body = new String(multipartBody.getBody(), StandardCharsets.UTF_8);
        assertTrue(body.contains("Content-Disposition: form-data; name=\"nullField\""));
        assertFalse(body.contains("filename"));
        assertFalse(body.contains("Content-Type:"));
    }
}
