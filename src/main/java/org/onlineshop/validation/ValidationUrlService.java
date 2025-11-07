package org.onlineshop.validation;

import org.onlineshop.config.ImageServiceConfig;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;

@Service
public final class ValidationUrlService {

    private interface HttpConstants {
        String HEAD_METHOD = "HEAD";
        int HTTP_OK = 200;
        int HTTP_BAD_REQUEST = 400;
        String USER_AGENT = "OnlineShopImageValidator/1.0";
        String USER_AGENT_HEADER = "User-Agent";
    }

    private enum SupportedProtocol {
        HTTP, HTTPS;

        static boolean isSupported(String scheme) {
            if (scheme == null) return false;
            try {
                valueOf(scheme.toUpperCase(Locale.ROOT));
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    private final ImageServiceConfig config;
    private final HttpClient httpClient;

    public ValidationUrlService(ImageServiceConfig config) {
        this.config = config;
        this.httpClient = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofMillis(config.getValidation().getTimeoutMillis()))
                .build();
    }

    public boolean isLengthOk(final String url) {
        return Optional.ofNullable(url)
                .map(String::trim)
                .filter(string -> !string.isEmpty())
                .map(string -> string.length() <= config.getUrl().getMaxLength())
                .orElse(false);
    }

    // in ValidationUrlService
    public boolean isWellFormedHttpUrl(final String url) {
        return toUri(url)
                .filter(uri -> SupportedProtocol.isSupported(uri.getScheme()))
                .map(URI::getHost)
                .filter(host -> host != null && !host.isBlank())
                .isPresent();
    }

    public boolean isAllowedByDomainOrExtension(final String url) {
        return toUri(url)
                .filter(uri -> SupportedProtocol.isSupported(uri.getScheme()))
                .map(uri -> isHostAllowed(uri) || isExtensionAllowed(url.trim()))
                .orElse(false);
    }

    public boolean isReachable(final String url) {
        if (!config.getValidation().isHeadRequestEnabled()) {
            return true;
        }
        return toUri(url)
                .map(uri -> {
                    if (shouldSkipHeadForHost(uri.getHost())) {
                        return true;
                    }
                    return sendHeadRequest(uri)
                            .map(this::isSuccessStatusCode)
                            .orElse(false);
                })
                .orElse(false);
    }

    private Optional<URI> toUri(final String url) {
        if (url == null) return Optional.empty();
        try {
            return Optional.of(new URI(url.trim()));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    private boolean isHostAllowed(final URI uri) {
        String host = uri.getHost();
        if (host == null) return false;
        String normalizedHost = host.toLowerCase(Locale.ROOT);
        return config.getAllowedDomains().stream()
                .map(string -> string.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedHost::endsWith);
    }

    private boolean isExtensionAllowed(final String url) {
        if (config.getAllowedExtensions().isEmpty()) {
            return true;
        }
        final String normalizedUrl = url.toLowerCase(Locale.ROOT);
        return config.getAllowedExtensions().stream()
                .map(string -> string.toLowerCase(Locale.ROOT))
                .anyMatch(normalizedUrl::endsWith);
    }

    private boolean shouldSkipHeadForHost(String host) {
        if (host == null) return false;
        String formattedHost = host.toLowerCase(Locale.ROOT);
        return config.getValidation().getSkipHeadDomains().stream()
                .map(string -> string.toLowerCase(Locale.ROOT))
                .anyMatch(formattedHost::endsWith);
    }

    private Optional<HttpResponse<Void>> sendHeadRequest(final URI uri) {
        try {
            final HttpRequest request = createHeadRequest(uri);
            return Optional.of(httpClient.send(request, HttpResponse.BodyHandlers.discarding()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private HttpRequest createHeadRequest(final URI uri) {
        return HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.ofMillis(config.getValidation().getTimeoutMillis()))
                .header(HttpConstants.USER_AGENT_HEADER, HttpConstants.USER_AGENT)
                .method(HttpConstants.HEAD_METHOD, HttpRequest.BodyPublishers.noBody())
                .build();
    }

    private boolean isSuccessStatusCode(final HttpResponse<Void> response) {
        int code = response.statusCode();
        return code >= HttpConstants.HTTP_OK && code < HttpConstants.HTTP_BAD_REQUEST;
    }
}
