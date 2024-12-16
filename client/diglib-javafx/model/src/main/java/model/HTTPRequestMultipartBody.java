package model;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a multipart body for an HTTP request.
 */
public class HTTPRequestMultipartBody {

    private final byte[] bytes;
    private String boundary;

    /**
     * Gets the boundary string used in the multipart body.
     *
     * @return the boundary string
     */
    public String getBoundary() {
        return boundary;
    }

    /**
     * Sets the boundary string used in the multipart body.
     *
     * @param boundary the boundary string
     */
    public void setBoundary(String boundary) {
        this.boundary = boundary;
    }

    private HTTPRequestMultipartBody(byte[] bytes, String boundary) {
        this.bytes = bytes;
        this.boundary = boundary;
    }

    /**
     * Gets the content type for the multipart body.
     *
     * @return the content type string
     */
    public String getContentType() {
        return "multipart/form-data; boundary=" + this.getBoundary();
    }

    /**
     * Gets the byte array representing the multipart body.
     *
     * @return the byte array of the multipart body
     */
    public byte[] getBody() {
        return bytes == null ? null : bytes.clone();
    }

    /**
     * Builder class for constructing an HTTPRequestMultipartBody.
     */
    public static class Builder {
        private static final String DEFAULT_MIMETYPE = "text/plain";

        /**
         * Represents a single part in the multipart body.
         */
        public static class MultiPartRecord {
            private String fieldName;
            private String filename;
            private String contentType;
            private Object content;

            /**
             * Gets the field name of the part.
             *
             * @return the field name
             */
            public String getFieldName() {
                return fieldName;
            }

            /**
             * Sets the field name of the part.
             *
             * @param fieldName the field name
             */
            public void setFieldName(String fieldName) {
                this.fieldName = fieldName;
            }

            /**
             * Gets the filename of the part.
             *
             * @return the filename
             */
            public String getFilename() {
                return filename;
            }

            /**
             * Sets the filename of the part.
             *
             * @param filename the filename
             */
            public void setFilename(String filename) {
                this.filename = filename;
            }

            /**
             * Gets the content type of the part.
             *
             * @return the content type
             */
            public String getContentType() {
                return contentType;
            }

            /**
             * Sets the content type of the part.
             *
             * @param contentType the content type
             */
            public void setContentType(String contentType) {
                this.contentType = contentType;
            }

            /**
             * Gets the content of the part.
             *
             * @return the content
             */
            public Object getContent() {
                return content;
            }

            /**
             * Sets the content of the part.
             *
             * @param content the content
             */
            public void setContent(Object content) {
                this.content = content;
            }
        }

        List<MultiPartRecord> parts;

        /**
         * Constructs a new Builder.
         */
        public Builder() {
            this.parts = new ArrayList<>();
        }

        /**
         * Adds a part with a field name and field value.
         *
         * @param fieldName the field name
         * @param fieldValue the field value
         * @return the Builder instance
         */
        public Builder addPart(String fieldName, String fieldValue) {
            MultiPartRecord part = new MultiPartRecord();
            part.setFieldName(fieldName);
            part.setContent(fieldValue);
            part.setContentType(DEFAULT_MIMETYPE);
            this.parts.add(part);
            return this;
        }

        /**
         * Adds a part with a field name, field value, and content type.
         *
         * @param fieldName the field name
         * @param fieldValue the field value
         * @param contentType the content type
         * @return the Builder instance
         */
        public Builder addPart(String fieldName, String fieldValue, String contentType) {
            MultiPartRecord part = new MultiPartRecord();
            part.setFieldName(fieldName);
            part.setContent(fieldValue);
            part.setContentType(contentType);
            this.parts.add(part);
            return this;
        }

        /**
         * Adds a part with a field name, field value, content type, and filename.
         *
         * @param fieldName the field name
         * @param fieldValue the field value
         * @param contentType the content type
         * @param fileName the filename
         * @return the Builder instance
         */
        public Builder addPart(String fieldName, Object fieldValue, String contentType, String fileName) {
            MultiPartRecord part = new MultiPartRecord();
            part.setFieldName(fieldName);
            part.setContent(fieldValue);
            part.setContentType(contentType);
            part.setFilename(fileName);
            this.parts.add(part);
            return this;
        }

        /**
         * Builds the HTTPRequestMultipartBody from the added parts.
         *
         * @return the constructed HTTPRequestMultipartBody
         * @throws IOException if an I/O error occurs
         */
        public HTTPRequestMultipartBody build() throws IOException {
            String boundary = new BigInteger(256, new SecureRandom()).toString();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            for (MultiPartRecord record : parts) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("--").append(boundary).append("\r\n");
                stringBuilder.append("Content-Disposition: form-data; name=\"").append(record.getFieldName()).append("\"");
                if (record.getFilename() != null) {
                    stringBuilder.append("; filename=\"").append(record.getFilename()).append("\"");
                }
                stringBuilder.append("\r\n");
                if (record.getContentType() != null) {
                    stringBuilder.append("Content-Type: ").append(record.getContentType()).append("\r\n");
                }
                stringBuilder.append("\r\n");
                out.write(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));

                Object content = record.getContent();
                if (content instanceof String) {
                    out.write(((String) content).getBytes(StandardCharsets.UTF_8));
                } else if (content instanceof byte[]) {
                    out.write((byte[]) content);
                } else if (content instanceof File) {
                    Files.copy(((File) content).toPath(), out);
                } else {
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
                    objectOutputStream.writeObject(content);
                    objectOutputStream.flush();
                }
                out.write("\r\n".getBytes(StandardCharsets.UTF_8));
            }
            out.write(("--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));

            return new HTTPRequestMultipartBody(out.toByteArray(), boundary);
        }
    }
}