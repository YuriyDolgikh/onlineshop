package org.onlineshop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "image.service")
public class ImageServiceConfig {
    private static final int DEFAULT_TIMEOUT_MILLIS = 1500;
    private static final int DEFAULT_MAX_URL_LENGTH = 256;
    private static final boolean DEFAULT_HEAD_REQUEST_ENABLED = true;

    private final List<String> allowedDomains = new ArrayList<>();
    private final List<String> allowedExtensions = new ArrayList<>();
    private final GoogleDriveConfig googleDrive = new GoogleDriveConfig();
    private final ValidationConfig validation = new ValidationConfig();
    private final UrlConfig url = new UrlConfig();

    @Getter
    @Setter
    public static class GoogleDriveConfig {
        private String baseUrl;
        private String viewUrl;
        private String fileIdRegex;
    }

    @Getter
    @Setter
    public static class ValidationConfig {
        private boolean headRequestEnabled = DEFAULT_HEAD_REQUEST_ENABLED;
        private int timeoutMillis = DEFAULT_TIMEOUT_MILLIS;
    }

    @Getter
    @Setter
    public static class UrlConfig {
        private int maxLength = DEFAULT_MAX_URL_LENGTH;
    }

}