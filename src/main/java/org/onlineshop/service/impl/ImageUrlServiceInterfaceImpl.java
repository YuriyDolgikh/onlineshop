package org.onlineshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.exception.ValidationException;
import org.onlineshop.service.ImageUrlServiceInterface;
import org.onlineshop.service.util.GoogleDriveLinkNormalizer;
import org.onlineshop.service.validator.UrlValidationService;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ImageUrlServiceInterface} for validating and normalizing
 * image URLs. Handles specific constraints and transformations for processing image URLs.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImageUrlServiceInterfaceImpl implements ImageUrlServiceInterface {
    private static final String ERROR_URL_TOO_LONG = "Image URL too long";
    private static final String ERROR_INVALID_DOMAIN = "Image URL domain/extension not allowed";
    private static final String ERROR_NOT_REACHABLE = "Image URL not reachable";

    private final UrlValidationService urlValidator;
    private final GoogleDriveLinkNormalizer linkNormalizer;

    /**
     * Validates and normalizes an image URL. It checks if the URL adheres to constraints,
     * transforms specific Google Drive URLs to their normalized format, and verifies
     * the URL's reachability.
     *
     * @param imageUrl the image URL to validate and normalize; may be null or blank
     * @return the normalized and validated URL if successful, or null if the input is null or blank
     * @throws ValidationException   if the URL does not meet constraints or is not reachable
     * @throws IllegalStateException if Google Drive configuration is incomplete during normalization
     */
    @Override
    public String validateAndNormalizeUrl
    (String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        validateUrlConstraints(imageUrl);
        String normalizedUrl = linkNormalizer.normalizeGoogleDriveUrl(imageUrl);
        validateUrlReachability(normalizedUrl);

        return normalizedUrl;
    }

    /**
     * Converts a given URL into its corresponding direct access URL format.
     *
     * @param url the original URL to be converted; may be null
     * @return the direct access URL if successfully converted, or null if the input is null
     * @throws IllegalStateException if the Google Drive configuration is incomplete
     */
    @Override
    public String convertToDirectUrl(String url) {
        return linkNormalizer.normalizeGoogleDriveUrl(url);
    }

    /**
     * Validates the specified URL against defined constraints. Ensures the URL length is within
     * allowed limits and the domain or extension is permitted. Throws a {@code ValidationException}
     * if any constraint is violated.
     *
     * @param url the URL to validate; must not be null
     * @throws ValidationException if the URL is too long or has an invalid domain/extension
     */
    private void validateUrlConstraints(String url) {
        if (!urlValidator.isLengthOk(url)) {
            throw new ValidationException(ERROR_URL_TOO_LONG);
        }
        if (!urlValidator.isAllowedByDomainOrExtension(url)) {
            throw new ValidationException(ERROR_INVALID_DOMAIN);
        }
    }

    /**
     * Validates the reachability of a given URL. If the URL is not reachable,
     * a {@code ValidationException} is thrown with a predefined error message.
     *
     * @param url the URL to be validated for reachability
     * @throws ValidationException if the URL is not reachable
     */
    private void validateUrlReachability(String url) {
        if (!urlValidator.isReachable(url)) {
            throw new ValidationException(ERROR_NOT_REACHABLE);
        }
    }

}