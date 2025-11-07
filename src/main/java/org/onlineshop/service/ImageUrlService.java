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

    @Override
    public String validateAndNormalizeUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return null;
        }

        validateUrlConstraints(imageUrl);
        String normalizedUrl = linkNormalizer.normalizeGoogleDriveUrl(imageUrl);
        validateUrlReachability(normalizedUrl);

        return normalizedUrl;
    }

    @Override
    public String convertToDirectUrl(String url) {
        return linkNormalizer.normalizeGoogleDriveUrl(url);
    }

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

    private void validateUrlReachability(String url) {
        if (!urlValidator.isReachable(url)) {
            throw new UrlValidationException(UrlValidationError.UNREACHABLE);
        }
    }

}