package org.onlineshop.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "image.service")
public class ImageServiceConfig {
    private List<String> allowedDomains = new ArrayList<>();
    private List<String> allowedExtensions = new ArrayList<>();
    private GoogleDrive googleDrive = new GoogleDrive();
    private Validate validate = new Validate();
    private Url url = new Url();

    @Data
    public static class GoogleDrive {
        private String baseUrl;
        private String viewUrl;
        private String fileIdRegex;
    }

    @Data
    public static class Validate {
        private boolean headRequestEnabled = true;
        private int timeoutMillis = 1500;
    }

    @Data
    public static class Url {
        private int maxLength = 256;
    }

}