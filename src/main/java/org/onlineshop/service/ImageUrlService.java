package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.exception.UrlValidationError;
import org.onlineshop.exception.UrlValidationException;
import org.onlineshop.service.interfaces.ImageUrlServiceInterface;
import org.onlineshop.service.util.UrlDriveLinkNormalizer;
import org.onlineshop.validation.ValidationUrlService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ImageUrlService implements ImageUrlServiceInterface {

    private final ValidationUrlService urlValidator;
    private final UrlDriveLinkNormalizer linkNormalizer;

    /**
     * Validates and normalizes the provided image URL.
     *
     * @param imageUrl the image URL to validate and normalize
     * @return the processed and validated image URL, or null if the provided URL is null or blank
     */
    @Override
    public String validateAndNormalizeUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            log.warn("Invalid image URL: {}", imageUrl);
            return null;
        }
        validateUrlConstraints(imageUrl);
        String normalizedUrl = linkNormalizer.normalizeGoogleDriveUrl(imageUrl);
        validateUrlReachability(normalizedUrl);
        return normalizedUrl;
    }

    /**
     * Converts an image URL to a direct access URL.
     *
     * @param url the original image URL to convert
     * @return the direct access URL, or null if the provided URL is null or blank
     */
    @Override
    public String convertToDirectUrl(String url) {
        return linkNormalizer.normalizeGoogleDriveUrl(url);
    }

    /**
     * Validates the provided URL against the configured constraints.
     *
     * @param url the URL to validate against constraints
     */
    private void validateUrlConstraints(String url) {
        if (!urlValidator.isLengthOk(url)) {
            throw new UrlValidationException(UrlValidationError.INVALID_LENGTH);
        }
        if (!urlValidator.isWellFormedHttpUrl(url)) {
            throw new UrlValidationException(UrlValidationError.INVALID_DOMAIN);
        }
        if (!urlValidator.isAllowedByDomainOrExtension(url)) {
            throw new UrlValidationException(UrlValidationError.INVALID_EXTENSION);
        }
    }

    /**
     * Validates the provided URL for reachability.
     *
     * @param url the URL to validate for reachability
     */
    private void validateUrlReachability(String url) {
        if (!urlValidator.isReachable(url)) {
            throw new UrlValidationException(UrlValidationError.UNREACHABLE);
        }
    }
}