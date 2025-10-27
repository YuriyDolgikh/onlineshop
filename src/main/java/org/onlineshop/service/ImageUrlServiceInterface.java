package org.onlineshop.service;

public interface ImageUrlServiceInterface {
    String convertToDirect(String imageUrl);
    String processAndValidateImageUrl(String imageUrl);
    String validateAndNormalize(String imageUrl);
}