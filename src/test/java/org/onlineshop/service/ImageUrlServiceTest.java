package org.onlineshop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.onlineshop.exception.UrlValidationError;
import org.onlineshop.exception.UrlValidationException;
import org.onlineshop.service.util.UrlDriveLinkNormalizer;
import org.onlineshop.validation.ValidationUrlService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageUrlServiceTest {

    @Mock
    private ValidationUrlService urlValidator;

    @Mock
    private UrlDriveLinkNormalizer linkNormalizer;

    @InjectMocks
    private ImageUrlService imageUrlService;

    private final String RAW_URL = "https://drive.google.com/file/d/ABC123/view?usp=drive_link";
    private final String NORMALIZED_URL = "https://drive.google.com/uc?export=view&id=ABC123";

    @BeforeEach
    void setUp() {
    }

    @Test
    void validateAndNormalizeUrl_shouldReturnNull_whenUrlIsNull() {
        String result = imageUrlService.validateAndNormalizeUrl(null);

        assertNull(result);
        verifyNoInteractions(urlValidator, linkNormalizer);
    }

    @Test
    void validateAndNormalizeUrl_shouldReturnNull_whenUrlIsBlank() {
        String result = imageUrlService.validateAndNormalizeUrl("   ");

        assertNull(result);
        verifyNoInteractions(urlValidator, linkNormalizer);
    }

    @Test
    void validateAndNormalizeUrl_shouldReturnNormalizedUrl_whenAllChecksPass() {
        when(urlValidator.isLengthOk(RAW_URL)).thenReturn(true);
        when(urlValidator.isWellFormedHttpUrl(RAW_URL)).thenReturn(true);
        when(urlValidator.isAllowedByDomainOrExtension(RAW_URL)).thenReturn(true);
        when(linkNormalizer.normalizeGoogleDriveUrl(RAW_URL)).thenReturn(NORMALIZED_URL);
        when(urlValidator.isReachable(NORMALIZED_URL)).thenReturn(true);

        String result = imageUrlService.validateAndNormalizeUrl(RAW_URL);

        assertEquals(NORMALIZED_URL, result);

        InOrder inOrder = inOrder(urlValidator, linkNormalizer);
        inOrder.verify(urlValidator).isLengthOk(RAW_URL);
        inOrder.verify(urlValidator).isWellFormedHttpUrl(RAW_URL);
        inOrder.verify(urlValidator).isAllowedByDomainOrExtension(RAW_URL);
        inOrder.verify(linkNormalizer).normalizeGoogleDriveUrl(RAW_URL);
        inOrder.verify(urlValidator).isReachable(NORMALIZED_URL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void validateAndNormalizeUrl_shouldThrowINVALID_LENGTH_whenLengthNotOk() {
        when(urlValidator.isLengthOk(RAW_URL)).thenReturn(false);

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> imageUrlService.validateAndNormalizeUrl(RAW_URL)
        );

        assertEquals(UrlValidationError.INVALID_LENGTH, ex.getError());

        verify(urlValidator).isLengthOk(RAW_URL);
        verifyNoMoreInteractions(urlValidator, linkNormalizer);
    }

    @Test
    void validateAndNormalizeUrl_shouldThrowINVALID_DOMAIN_whenUrlNotWellFormed() {
        when(urlValidator.isLengthOk(RAW_URL)).thenReturn(true);
        when(urlValidator.isWellFormedHttpUrl(RAW_URL)).thenReturn(false);

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> imageUrlService.validateAndNormalizeUrl(RAW_URL)
        );

        assertEquals(UrlValidationError.INVALID_DOMAIN, ex.getError());

        InOrder inOrder = inOrder(urlValidator);
        inOrder.verify(urlValidator).isLengthOk(RAW_URL);
        inOrder.verify(urlValidator).isWellFormedHttpUrl(RAW_URL);
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(linkNormalizer);
    }

    @Test
    void validateAndNormalizeUrl_shouldThrowINVALID_EXTENSION_whenDomainOrExtensionNotAllowed() {
        when(urlValidator.isLengthOk(RAW_URL)).thenReturn(true);
        when(urlValidator.isWellFormedHttpUrl(RAW_URL)).thenReturn(true);
        when(urlValidator.isAllowedByDomainOrExtension(RAW_URL)).thenReturn(false);

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> imageUrlService.validateAndNormalizeUrl(RAW_URL)
        );

        assertEquals(UrlValidationError.INVALID_EXTENSION, ex.getError());

        InOrder inOrder = inOrder(urlValidator);
        inOrder.verify(urlValidator).isLengthOk(RAW_URL);
        inOrder.verify(urlValidator).isWellFormedHttpUrl(RAW_URL);
        inOrder.verify(urlValidator).isAllowedByDomainOrExtension(RAW_URL);
        inOrder.verifyNoMoreInteractions();
        verifyNoInteractions(linkNormalizer);
    }

    @Test
    void validateAndNormalizeUrl_shouldThrowUNREACHABLE_whenHeadRequestFails() {
        when(urlValidator.isLengthOk(RAW_URL)).thenReturn(true);
        when(urlValidator.isWellFormedHttpUrl(RAW_URL)).thenReturn(true);
        when(urlValidator.isAllowedByDomainOrExtension(RAW_URL)).thenReturn(true);
        when(linkNormalizer.normalizeGoogleDriveUrl(RAW_URL)).thenReturn(NORMALIZED_URL);
        when(urlValidator.isReachable(NORMALIZED_URL)).thenReturn(false);

        UrlValidationException ex = assertThrows(
                UrlValidationException.class,
                () -> imageUrlService.validateAndNormalizeUrl(RAW_URL)
        );

        assertEquals(UrlValidationError.UNREACHABLE, ex.getError());

        InOrder inOrder = inOrder(urlValidator, linkNormalizer);
        inOrder.verify(urlValidator).isLengthOk(RAW_URL);
        inOrder.verify(urlValidator).isWellFormedHttpUrl(RAW_URL);
        inOrder.verify(urlValidator).isAllowedByDomainOrExtension(RAW_URL);
        inOrder.verify(linkNormalizer).normalizeGoogleDriveUrl(RAW_URL);
        inOrder.verify(urlValidator).isReachable(NORMALIZED_URL);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void convertToDirectUrl_shouldDelegateToNormalizer_andReturnResult() {
        String input = RAW_URL;
        when(linkNormalizer.normalizeGoogleDriveUrl(input)).thenReturn(NORMALIZED_URL);

        String result = imageUrlService.convertToDirectUrl(input);

        assertEquals(NORMALIZED_URL, result);
        verify(linkNormalizer).normalizeGoogleDriveUrl(input);
        verifyNoInteractions(urlValidator);
    }

    @Test
    void convertToDirectUrl_shouldReturnNull_whenNormalizerReturnsNull() {
        when(linkNormalizer.normalizeGoogleDriveUrl(null)).thenReturn(null);

        String result = imageUrlService.convertToDirectUrl(null);

        assertNull(result);
        verify(linkNormalizer).normalizeGoogleDriveUrl(null);
        verifyNoInteractions(urlValidator);
    }

}