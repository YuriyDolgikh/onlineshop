package org.onlineshop.service.util;

import lombok.RequiredArgsConstructor;
import org.onlineshop.config.ImageServiceConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Component responsible for normalizing Google Drive links to their viewable format.
 * Transforms various Google Drive URL formats into a standardized viewing URL.
 */
@Component
@RequiredArgsConstructor
public class GoogleDriveLinkNormalizer {
    private static final String ERROR_MSG_INVALID_CONFIG =
            "Google Drive configuration is incomplete. Both fileIdRegex and viewUrl must be set.";

    private final ImageServiceConfig imageServiceConfig;

    /**
     * Transforms a Google Drive URL into its normalized viewing format.
     *
     * @param url the original Google Drive URL
     * @return normalized URL for viewing, or original URL if not matching a Google Drive pattern
     * @throws IllegalStateException if Google Drive configuration is incomplete
     */
    public String normalizeGoogleDriveUrl(String url) {
        if (url == null) {
            return null;
        }
        validateConfiguration();

        return extractAndTransformUrl(url);
    }

    private void validateConfiguration() {
        ImageServiceConfig.GoogleDriveConfig config = imageServiceConfig.getGoogleDrive();
        if (config.getFileIdRegex() == null || config.getViewUrl() == null) {
            throw new IllegalStateException(ERROR_MSG_INVALID_CONFIG);
        }
    }

    private String extractAndTransformUrl(String url) {
        ImageServiceConfig.GoogleDriveConfig config = imageServiceConfig.getGoogleDrive();
        Matcher urlMatcher = Pattern.compile(config.getFileIdRegex()).matcher(url);

        return urlMatcher.find()
                ? config.getViewUrl() + urlMatcher.group()
                : url;
    }

}