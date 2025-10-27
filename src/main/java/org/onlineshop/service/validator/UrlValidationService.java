package org.onlineshop.service.validator;

import lombok.RequiredArgsConstructor;
import org.onlineshop.config.ImageServiceConfig;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UrlValidationService {
    private final ImageServiceConfig config;

    public boolean isLengthOk(String url) {
        return url != null && url.length() <= config.getUrl().getMaxLength();
    }

    public URI toUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public boolean isAllowedByDomainOrExtension(String url) {
        URI uri = toUri(url);
        if (uri == null) return false;
        String host = uri.getHost();

        boolean hostAllowed = host != null &&
                config.getAllowedDomains().stream().anyMatch(host::endsWith);

        boolean extAllowed = config.getAllowedExtensions().isEmpty() ||
                config.getAllowedExtensions().stream()
                        .anyMatch(ext -> url.toLowerCase().endsWith(ext.toLowerCase()));

        return hostAllowed || extAllowed;
    }

    public boolean isReachable(String url) {
        if (!config.getValidate().isHeadRequestEnabled()) return true;
        try {
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(config.getValidate().getTimeoutMillis()))
                    .build();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofMillis(config.getValidate().getTimeoutMillis()))
                    .method("HEAD", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<Void> statusResponse = client.send(httpRequest, HttpResponse.BodyHandlers.discarding());
            int httpStatusCode = statusResponse.statusCode();
            return httpStatusCode >= 200 && httpStatusCode < 400;
        } catch (Exception e) {
            return false;
        }
    }

}