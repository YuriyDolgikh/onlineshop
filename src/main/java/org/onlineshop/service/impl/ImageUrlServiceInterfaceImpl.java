package org.onlineshop.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.exception.ValidationException;
import org.onlineshop.service.ImageUrlServiceInterface;
import org.onlineshop.service.util.GoogleDriveLinkNormalizer;
import org.onlineshop.service.validator.UrlValidationService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor

public class ImageUrlServiceInterfaceImpl implements ImageUrlServiceInterface {
    private final UrlValidationService urlValidator;
    private final GoogleDriveLinkNormalizer linkNormalizer;

    public String processAndValidateImageUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return null;

        if (!urlValidator.isLengthOk(imageUrl)) {
            throw new ValidationException("Image URL too long");
        }
        if (!urlValidator.isAllowedByDomainOrExtension(imageUrl)) {
            throw new ValidationException("Image URL domain/extension not allowed");
        }

        String finalUrl = linkNormalizer.normalize(imageUrl);

        if (!urlValidator.isReachable(finalUrl)) {
            throw new ValidationException("Image URL not reachable");
        }
        return finalUrl;
    }

    public String convertToDirect(String url) {
        return linkNormalizer.normalize(url);
    }

    public String validateAndNormalize(String url) {
        return processAndValidateImageUrl(url);
    }

}