package org.onlineshop.service;

/**
 * Service interface for handling image URL operations.
 */

public interface ImageUrlServiceInterface {
    /**
     * Converts an image URL to a direct access URL.
     *
     * @param imageUrl the original image URL
     * @return the direct access URL
     */
    String convertToDirectUrl(String imageUrl);

    /**
     * Validates and normalizes an image URL.
     *
     * @param imageUrl the image URL to validate and normalize
     * @return the processed and validated image URL
     * @throws IllegalArgumentException if the URL is invalid
     */
    String validateAndNormalizeUrl(String imageUrl);
}