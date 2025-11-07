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
class ProductServiceHelperTest {

    @Mock
    private ImageUrlServiceInterface imageUrlService;

    private ImageServiceConfig config;
    private ProductServiceHelper helper;

    @BeforeEach
    void setUp() {
        config = new ImageServiceConfig();
        config.getProductDefault().setImage("https://drive.google.com/file/d/PROD_DEFAULT/view");
        helper = new ProductServiceHelper(config, imageUrlService);
    }

    @Test
    void resolveImageUrl_usesGivenUrl_trimAndValidate() {
        String input = "  https://drive.google.com/file/d/IMG/view  ";
        String normalized = "https://drive.google.com/uc?export=view&id=IMG";

        when(imageUrlService.validateAndNormalizeUrl("https://drive.google.com/file/d/IMG/view"))
                .thenReturn(normalized);

        String result = helper.resolveImageUrl(input);

        assertEquals(normalized, result);

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(imageUrlService).validateAndNormalizeUrl(captor.capture());
        assertEquals("https://drive.google.com/file/d/IMG/view", captor.getValue());
    }

    @Test
    void resolveImageUrl_usesDefaultFromConfig_whenNull() {
        String def = config.getProductDefault().getImage();
        String normalized = "https://drive.google.com/uc?export=view&id=PROD_DEFAULT";
        when(imageUrlService.validateAndNormalizeUrl(def)).thenReturn(normalized);

        String result = helper.resolveImageUrl(null);

        assertEquals(normalized, result);
        verify(imageUrlService).validateAndNormalizeUrl(def);
    }

    @Test
    void resolveImageUrl_usesDefaultFromConfig_whenBlank() {
        String def = config.getProductDefault().getImage();
        String normalized = "https://drive.google.com/uc?export=view&id=PROD_DEFAULT";
        when(imageUrlService.validateAndNormalizeUrl(def)).thenReturn(normalized);

        String result = helper.resolveImageUrl("   ");

        assertEquals(normalized, result);
        verify(imageUrlService).validateAndNormalizeUrl(def);
    }

    @Test
    void resolveImageUrl_throws_whenDefaultMissing() {
        config.getProductDefault().setImage(null);

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> helper.resolveImageUrl(null)
        );

        assertTrue(ex.getMessage().contains("image.service.product-default.image"));
        verifyNoInteractions(imageUrlService);
    }

    @Test
    void resolveImageUrl_propagatesValidationException_fromService() {
        when(imageUrlService.validateAndNormalizeUrl(anyString()))
                .thenThrow(new ValidationException("Image URL not reachable"));

        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> helper.resolveImageUrl("https://drive.google.com/file/d/BAD/view")
        );

        assertEquals("Image URL not reachable", ex.getMessage());
    }
}
