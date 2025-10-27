package org.onlineshop.service.util;

import lombok.RequiredArgsConstructor;
import org.onlineshop.config.ImageServiceConfig;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class GoogleDriveLinkNormalizer {
    private final ImageServiceConfig imageServiceConfig;

    public String normalize(String url) {
        if (url == null) return null;
        String regex = imageServiceConfig.getGoogleDrive().getFileIdRegex();
        String view = imageServiceConfig.getGoogleDrive().getViewUrl();
        if (regex == null || view == null) return url;

        Matcher urlMatcher = Pattern.compile(regex).matcher(url);
        if (urlMatcher.find()) {
            return view + urlMatcher.group();
        }
        return url;
    }

}