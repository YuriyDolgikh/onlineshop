package org.onlineshop.service.validator;

import org.onlineshop.config.ImageServiceConfig;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Optional;

/**
 * Service for validating URLs based on length, domain, file extension, and reachability.
 * This service provides functionalities to ensure URLs meet predefined criteria
 * before being processed further.
 */
@Service
public class UrlValidationService {
    private static final String HEAD_METHOD = "HEAD";
    private static final int HTTP_OK = 200;
    private static final int HTTP_BAD_REQUEST = 400;

    private final ImageServiceConfig config;
    private final HttpClient httpClient;

    public UrlValidationService(ImageServiceConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getValidation().getTimeoutMillis()))
                .build();
    }

    public boolean isLengthOk(final String url) {
        return Optional.ofNullable(url)
                .map(u -> u.length() <= config.getUrl().getMaxLength())
                .orElse(false);
    }

    private Optional<URI> toUri(final String url) {
        try {
            return Optional.of(new URI(url));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    public boolean isAllowedByDomainOrExtension(final String url) {
        return toUri(url)
                .map(uri -> isHostAllowed(uri) || isExtensionAllowed(url))
                .orElse(false);
    }

    private boolean isHostAllowed(final URI uri) {
        return Optional.ofNullable(uri.getHost())
                .map(host -> config.getAllowedDomains().stream()
                        .anyMatch(host::endsWith))
                .orElse(false);
    }

    private boolean isExtensionAllowed(final String url) {
        if (config.getAllowedExtensions().isEmpty()) {
            return true;
        }
        final String lowercaseUrl = url.toLowerCase();
        return config.getAllowedExtensions().stream()
                .map(String::toLowerCase)
                .anyMatch(lowercaseUrl::endsWith);
    }

    public boolean isReachable(final String url) {
        if (!config.getValidation().isHeadRequestEnabled()) {
            return true;
        }
        return sendHeadRequest(url)
                .map(this::isSuccessStatusCode)
                .orElse(false);
    }

    private Optional<HttpResponse<Void>> sendHeadRequest(final String url) {
        try {
            final HttpRequest request = createHeadRequest(url);
            return Optional.of(httpClient.send(request, HttpResponse.BodyHandlers.discarding()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private HttpRequest createHeadRequest(final String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .timeout(Duration.ofMillis(config.getValidation().getTimeoutMillis()))
                .method(HEAD_METHOD, HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private boolean isSuccessStatusCode(final HttpResponse<Void> response) {
        return response.statusCode() >= HTTP_OK && response.statusCode() < HTTP_BAD_REQUEST;
    }

}