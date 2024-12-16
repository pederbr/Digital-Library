package fxui.util;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.PushPromiseHandler;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;

public class MockHttpClient extends HttpClient {
    private Map<String, String> responses = new HashMap<>();
    private Map<String, Integer> statusCodes = new HashMap<>();

    /**
     * Adds a mock response for a specific URL.
     *
     * @param url the URL to mock
     * @param response the response body to return
     * @param statusCode the HTTP status code to return
     */
    public void addResponse(String url, String response, int statusCode) {
        responses.put(url, response);
        statusCodes.put(url, statusCode);
    }

    /**
     * Sends a mock HTTP request and returns a mock response.
     *
     * @param request the HTTP request
     * @param responseBodyHandler the response body handler
     * @return a mock HTTP response
     * @param <T> the response body type
     */
    @Override
    public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseBodyHandler) {
        String url = request.uri().toString();
        String response = responses.getOrDefault(url, "{}");
        int statusCode = statusCodes.getOrDefault(url, 200);
        
        return (HttpResponse<T>) new MockHttpResponse<>(statusCode, response);
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request,
            HttpResponse.BodyHandler<T> responseBodyHandler) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<CookieHandler> cookieHandler() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Duration> connectTimeout() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Redirect followRedirects() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<ProxySelector> proxy() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLContext sslContext() {
        throw new UnsupportedOperationException();
    }

    @Override
    public SSLParameters sslParameters() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Authenticator> authenticator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Version version() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Executor> executor() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, BodyHandler<T> responseBodyHandler,
            PushPromiseHandler<T> pushPromiseHandler) {
        throw new UnsupportedOperationException("Unimplemented method 'sendAsync'");
    }
}