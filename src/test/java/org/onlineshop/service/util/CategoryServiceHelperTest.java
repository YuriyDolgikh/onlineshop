package org.onlineshop.service.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.onlineshop.config.ImageServiceConfig;
import org.onlineshop.exception.ValidationException;
import org.onlineshop.service.interfaces.ImageUrlServiceInterface;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceHelperTest {

    @Mock
    private ImageUrlServiceInterface imageUrlService;

    private ImageServiceConfig config;
    private CategoryServiceHelper helper;

    @BeforeEach
    void setUp() {
        config = new ImageServiceConfig();
        config.getCategoryDefault().setImage("https://drive.google.com/file/d/CAT_DEFAULT/view");
        helper = new CategoryServiceHelper(config, imageUrlService);
    }

    @Test
    void resolveImageUrl_usesGivenUrl_trimAndValidate() {
        String input = "  https://drive.google.com/file/d/CAT_X/view  ";
        String normalized = "https://drive.google.com/uc?export=view&id=CAT_X";

        when(imageUrlService.validateAndNormalizeUrl("https://drive.google.com/file/d/CAT_X/view"))
                .thenReturn(normalized);

        String result = helper.resolveImageUrl(input);

        assertEquals(normalized, result);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(imageUrlService).validateAndNormalizeUrl(captor.capture());
        assertEquals("https://drive.google.com/file/d/CAT_X/view", captor.getValue());
    }

    @Test
    void resolveImageUrl_usesDefaultFromConfig_whenNull() {
        String def = config.getCategoryDefault().getImage();
        String normalized = "https://drive.google.com/uc?export=view&id=CAT_DEFAULT";
        when(imageUrlService.validateAndNormalizeUrl(def)).thenReturn(normalized);

        String result = helper.resolveImageUrl(null);

        assertEquals(normalized, result);
        verify(imageUrlService).validateAndNormalizeUrl(def);
    }

    @Test
    void resolveImageUrl_usesDefaultFromConfig_whenBlank() {
        String def = config.getCategoryDefault().getImage();
        String normalized = "https://drive.google.com/uc?export=view&id=CAT_DEFAULT";
        when(imageUrlService.validateAndNormalizeUrl(def)).thenReturn(normalized);

        String result = helper.resolveImageUrl("    ");

        assertEquals(normalized, result);
        verify(imageUrlService).validateAndNormalizeUrl(def);
    }

    @Test
    void resolveImageUrl_throws_whenDefaultMissing() {
        config.getCategoryDefault().setImage(null);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> helper.resolveImageUrl(null)
        );

        assertTrue(ex.getMessage().contains("image.service.category-default.image"));
        verifyNoInteractions(imageUrlService);
    }

    @Test
    void resolveImageUrl_propagatesValidationException_fromService() {
        when(imageUrlService.validateAndNormalizeUrl(anyString()))
                .thenThrow(new ValidationException("Invalid or unreachable URL"));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> helper.resolveImageUrl("https://drive.google.com/file/d/BAD_CAT/view")
        );

        assertEquals("Invalid or unreachable URL", ex.getMessage());
    }
}
