package fxui.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import javax.net.ssl.SSLSession;

public class MockHttpResponse<T> implements HttpResponse<T> {
    private final int statusCode;
    private final String body;

    public MockHttpResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    /**
     * Returns the HTTP status code of this response.
     *
     * @return the HTTP status code
     */
    @Override
    public int statusCode() {
        return statusCode;
    }

    @Override
    public HttpRequest request() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<HttpResponse<T>> previousResponse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpHeaders headers() {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the body of this response.
     *
     * @return the response body as type T
     */
    @Override
    public T body() {
        return (T) body;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI uri() {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpClient.Version version() {
        throw new UnsupportedOperationException();
    }
}