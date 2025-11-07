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
    private static final int DEFAULT_TIMEOUT_MILLIS = 3000;
    private static final int DEFAULT_MAX_URL_LENGTH = 256;
    private static final boolean DEFAULT_HEAD_REQUEST_ENABLED = true;

    private final List<String> allowedDomains = new ArrayList<>();
    private final List<String> allowedExtensions = new ArrayList<>();
    private final UrlSourceConfig urlConfig = new UrlSourceConfig();
    private final ValidationConfig validation = new ValidationConfig();
    private final ProductDefaultConfig productDefault = new ProductDefaultConfig();
    private final CategoryDefaultConfig categoryDefault = new CategoryDefaultConfig();
    private final UrlConfig url = new UrlConfig();

    @Getter
    @Setter
    public static class UrlSourceConfig {
        private String baseUrl;
        private String viewUrl;
        private String fileIdRegex;
    }

    @Getter
    @Setter
    public static class ValidationConfig {
        private boolean headRequestEnabled = DEFAULT_HEAD_REQUEST_ENABLED;
        private int timeoutMillis = DEFAULT_TIMEOUT_MILLIS;

        private List<String> skipHeadDomains = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class UrlConfig {
        private int maxLength = DEFAULT_MAX_URL_LENGTH;
    }

    @Getter
    @Setter
    public static class ProductDefaultConfig {
        private String image;
    }

    @Getter
    @Setter
    public static class CategoryDefaultConfig {
        private String image;
    }
}