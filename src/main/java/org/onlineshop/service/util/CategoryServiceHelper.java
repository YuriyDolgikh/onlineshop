package org.onlineshop.service.util;

import lombok.RequiredArgsConstructor;
import org.onlineshop.config.ImageServiceConfig;
import org.onlineshop.exception.ValidationException;
import org.onlineshop.service.interfaces.ImageUrlServiceInterface;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CategoryServiceHelper {

    private final ImageServiceConfig imageConfig;
    private final ImageUrlServiceInterface imageUrlService;

    public String resolveImageUrl(String requestedUrl) {
        String candidate = StringUtils.hasText(requestedUrl)
                ? requestedUrl.trim()
                : requiredDefaultCategoryImageUrl();
        return imageUrlService.validateAndNormalizeUrl(candidate);
    }

    private String requiredDefaultCategoryImageUrl() {
        String url = imageConfig.getCategoryDefault() != null
                ? imageConfig.getCategoryDefault().getImage()
                : null;
        if (!StringUtils.hasText(url)) {
            throw new ValidationException(
                    "Missing default category image URL. Set 'image.service.category-default.image' in application.properties");
        }
        return url.trim();
    }

}