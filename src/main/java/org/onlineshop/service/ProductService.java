package org.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.dto.product.ProductResponseForUserDto;
import org.onlineshop.dto.product.ProductUpdateDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.exception.NotFoundException;
import org.onlineshop.repository.ProductRepository;
import org.onlineshop.service.converter.ProductConverter;
import org.onlineshop.service.interfaces.ProductServiceInterface;
import org.onlineshop.service.util.ProductServiceHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductService implements ProductServiceInterface {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductConverter productConverter;
    private final ProductServiceHelper helper;

    /**
     * Adds a new product to a specified category after validating its uniqueness within the category.
     * It saves the product details to the repository and returns the saved product as a DTO.
     *
     * @param productRequestDto the data transfer object containing the details of the product to be added
     * @return a ProductResponseDto containing the details of the newly added product
     * @throws IllegalArgumentException if a product with the same name already exists in the specified category
     */
    @Transactional
    @Override
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
        if (productRequestDto == null) {
            throw new IllegalArgumentException("ProductRequestDto cannot be null");
        }
        validateProductRequestDto(productRequestDto);
        Category category = categoryService.getCategoryByName(productRequestDto.getProductCategory());
        String normalizedName = productRequestDto.getProductName().trim();

        if (productRepository.existsByNameIgnoreCaseAndCategory(normalizedName, category)) {
            throw new IllegalArgumentException("Product name '" + normalizedName + "' already exists in category '" + category + "'");
        }
        LocalDateTime now = LocalDateTime.now();
        final String finalImage = helper.resolveImageUrl(productRequestDto.getImage());

        Product productToSave = Product.builder()
                .name(normalizedName)
                .description(productRequestDto.getProductDescription())
                .price(productRequestDto.getProductPrice())
                .discountPrice(productRequestDto.getProductDiscountPrice())
                .image(finalImage)
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Product savedProduct = productRepository.save(productToSave);
        log.info("Product {} successfully added", savedProduct.getName());
        return productConverter.toDto(savedProduct);
    }

    /**
     * Updates an existing product with the details provided in the given ProductUpdateDto.
     * Uses atomic checks to prevent race conditions and ensure product name uniqueness within category.
     *
     * @param productId        the ID of the product to be updated
     * @param productUpdateDto the details of the product to be updated
     * @return a ProductResponseDto representing the updated product
     * @throws IllegalArgumentException if there is a duplicate product name within the category,
     *                                  or if product price values are not valid.
     * @throws NotFoundException        if a product with the specified id is not found in the database
     */
    @Transactional
    @Override
    public ProductResponseDto updateProduct(Integer productId, ProductUpdateDto productUpdateDto) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        if (productUpdateDto == null) {
            throw new IllegalArgumentException("ProductUpdateDto cannot be null");
        }
        if (productUpdateDto.getProductPrice() != null && productUpdateDto.getProductPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0");
        }
        Product productToUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id = " + productId + " not found"));

        String targetName = (productUpdateDto.getProductName() != null && !productUpdateDto.getProductName().isBlank())
                ? productUpdateDto.getProductName().trim()
                : productToUpdate.getName();

        Category targetCategory = (productUpdateDto.getProductCategory() != null && !productUpdateDto.getProductCategory().isBlank())
                ? categoryService.getCategoryByName(productUpdateDto.getProductCategory().trim())
                : productToUpdate.getCategory();

        if (productUpdateDto.getProductName() != null && !productUpdateDto.getProductName().isBlank()) {
            if (targetName.length() < 3 || targetName.length() > 20) {
                throw new IllegalArgumentException("Product title must be between 3 and 20 characters");
            }
            productToUpdate.setName(targetName);
        }

        boolean nameChanged = !targetName.equals(productToUpdate.getName());
        boolean categoryChanged = !targetCategory.equals(productToUpdate.getCategory());

        if (nameChanged || categoryChanged) {
            if (productRepository.existsByNameIgnoreCaseAndCategoryAndIdNot(targetName, targetCategory, productToUpdate.getId())) {
                throw new IllegalArgumentException(
                        "Product with name '" + targetName +
                                "' already exists in category '" +
                                targetCategory.getCategoryName() + "'"
                );
            }
        }

        if (nameChanged) {
            productToUpdate.setName(targetName);
        }

        if (categoryChanged) {
            productToUpdate.setCategory(targetCategory);
        }

        if (productUpdateDto.getProductDescription() != null && !productUpdateDto.getProductDescription().isBlank()) {
            productToUpdate.setDescription(productUpdateDto.getProductDescription());
        }

        if (productUpdateDto.getProductPrice() != null) {
            if (productUpdateDto.getProductPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Product price must be greater than 0");
            }
            productToUpdate.setPrice(productUpdateDto.getProductPrice());
        }

        if (productUpdateDto.getProductDiscountPrice() != null) {
            if (productUpdateDto.getProductDiscountPrice().compareTo(BigDecimal.ZERO) < 0 ||
                productUpdateDto.getProductDiscountPrice().compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Discount price must be between 0 and 100 percents");
            }
            productToUpdate.setDiscountPrice(productUpdateDto.getProductDiscountPrice());
        }

        if (productUpdateDto.getImage() != null && !productUpdateDto.getImage().isBlank()) {
            String newImage = helper.resolveImageUrl(productUpdateDto.getImage());
            productToUpdate.setImage(newImage);
        }
        productToUpdate.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(productToUpdate);
        log.info("Product {} successfully updated", updatedProduct.getName());
        return productConverter.toDto(updatedProduct);
    }

    /**
     * Sets the discount price for a product with the specified ID.
     *
     * @param productId        the ID of the product to set the discount price for
     * @param newDiscountPrice the new discount price to set
     * @return a {@code ProductResponseDto} containing the updated product details
     * @throws IllegalArgumentException if product update arguments are invalid
     * @throws NotFoundException        if a product with the specified id is not found in the database
     */
    @Transactional
    public ProductResponseDto setDiscountPrice(Integer productId, BigDecimal newDiscountPrice) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        if (newDiscountPrice == null) {
            throw new IllegalArgumentException("New discount price cannot be null");
        }
        if (newDiscountPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("New discount price cannot be less than 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id = " + productId + " not found"));

        product.setDiscountPrice(newDiscountPrice);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Discount price for product {} successfully updated", updatedProduct.getName());
        return productConverter.toDto(updatedProduct);
    }

    /**
     * Deletes a product with the specified ID.
     *
     * @param productId the ID of the product to be deleted
     * @return a {@code ProductResponseDto} containing the deleted product details
     * @throws NotFoundException if a product with the specified id is not found in the database
     */
    @Transactional
    @Override
    public ProductResponseDto deleteProduct(Integer productId) {
        if (productId == null) {
            throw new IllegalArgumentException("Product id cannot be null");
        }
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product with id = " + productId + " not found"));
        productRepository.delete(productToDelete);
        log.info("Product {} successfully deleted", productToDelete.getName());
        return productConverter.toDto(productToDelete);
    }

//    GET /v1/products?page=0&size=10&sort=price&direction=desc
//    GET /v1/products/search?name=phone&page=0&size=5
//    GET /v1/products/category/electronics?page=1&size=20
//    GET /v1/products/price-range?minPrice=100&maxPrice=500&page=0&size=15
//    GET /v1/products/discounted?page=0&size=10&sort=discountPrice&direction=desc
//    GET /v1/products/user?page=0&size=12
//    GET /v1/products/top-discounted?count=5

    /**
     * Retrieves a paginated list of products based on the specified search criteria.
     *
     * @param paramName  the name of the search parameter to filter products, such as "price", "discount", "category", "name", or "createDate"
     * @param paramValue the value of the search parameter to apply, such as a price range for "price" or a category name for "category"
     * @param pageable   the pagination information, including page number and size
     * @return a paginated list of products that match the specified search criteria
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByCriteria(String paramName, String paramValue, Pageable pageable) {
        switch (paramName) {
            case "price":
                String[] priceRange = paramValue.split("-");
                if (priceRange.length != 2) {
                    throw new IllegalArgumentException("Price range must be specified as a minimum and maximum price separated by a dash");
                }
                if (priceRange[0].isBlank() || priceRange[1].isBlank()) {
                    throw new IllegalArgumentException("Price range must contain both minimum and maximum prices");
                }
                BigDecimal minPrice;
                BigDecimal maxPrice;
                try {
                    minPrice = new BigDecimal(priceRange[0].trim());
                    maxPrice = new BigDecimal(priceRange[1].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Both prices must be valid numbers. Error: " + e.getMessage());
                }
                if (minPrice.compareTo(BigDecimal.ZERO) < 0 || maxPrice.compareTo(BigDecimal.ZERO) < 0) {
                    throw new IllegalArgumentException("Prices cannot be negative");
                }
                return getProductsByPriceRange(minPrice, maxPrice, pageable);
            case "discount":
                return getProductsByDiscount(pageable);
            case "category":
                return getProductsByCategory(paramValue, pageable);
            case "name":
                return getProductsByPartOfNameIgnoreCase(paramValue, pageable);
            case "createDate":
                return getProductsByCreateDate(pageable);
            default:
                return getAllProducts(pageable);
        }
    }

    /**
     * Retrieves a pageable list of products whose names contain the specified part
     * of a name, ignoring a case.
     *
     * @param partOfName the partial name used to search products
     * @param pageable   the pagination information including page size and page number
     * @return a paginated list of ProductResponseDto containing the matching products
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, Pageable pageable) {
        return productRepository.findByNameContainingIgnoreCase(partOfName, pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of products that belong to the specified category.
     *
     * @param categoryName the name of the category whose products are to be retrieved
     * @param pageable     the pagination and sorting information
     * @return a paginated list of product response DTOs belonging to the specified category
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByCategory(String categoryName, Pageable pageable) {
        Category category = categoryService.getCategoryByName(categoryName);
        return productRepository.findByCategory(category, pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of products within the specified price range.
     * If the minimum price is greater than the maximum price, the values are swapped
     * to ensure a valid range.
     *
     * @param minPrice the minimum price used to filter products; must not be null
     * @param maxPrice the maximum price used to filter products; must not be null
     * @param pageable the pagination information, including page number, size, and sorting, must not be null
     * @return a paginated list of ProductResponseDto representing products within the specified price range
     * @throws IllegalArgumentException if either minPrice or maxPrice is null
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Min price and max price must be provided");
        }
        final BigDecimal actualMinPrice;
        final BigDecimal actualMaxPrice;
        if (minPrice.compareTo(maxPrice) > 0) {
            actualMinPrice = maxPrice;
            actualMaxPrice = minPrice;
        } else {
            actualMinPrice = minPrice;
            actualMaxPrice = maxPrice;
        }
        return productRepository.findByPriceBetween(actualMinPrice, actualMaxPrice, pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of products that have a discount price greater than zero.
     *
     * @param pageable the pagination and sorting information
     * @return a Page containing ProductResponseDto objects representing products with discounts
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByDiscount(Pageable pageable) {
        return productRepository.findByDiscountPriceGreaterThan(BigDecimal.ZERO, pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of products sorted by their creation date.
     * The sorting should be specified in the provided pageable parameter.
     *
     * @param pageable the pagination and sorting information
     * @return a page of ProductResponseDto containing the product details
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getProductsByCreateDate(Pageable pageable) {
        // Используем стандартный findAll с переданным pageable
        // Сортировка должна быть указана в pageable
        return productRepository.findAll(pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of all products.
     *
     * @param pageable the pagination and sorting information
     * @return a page containing a list of product response DTOs
     */
    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponseDto> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productConverter::toDto);
    }

    /**
     * Retrieves a paginated list of products specifically formatted for user consumption.
     *
     * @param pageable the pagination information including page number, size, and sorting options
     * @return a paginated list of ProductResponseForUserDto objects representing the products available for the user
     */
    @Transactional(readOnly = true)
    public Page<ProductResponseForUserDto> getProductsForUser(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(product -> productConverter.toUserDto(productConverter.toDto(product)));
    }

    /**
     * Retrieves the top five discounted products of the day, sorted in descending order of discount.
     * If no discounted products are found, a NotFoundException is thrown.
     *
     * @return a list of ProductResponseForUserDto containing information about the top five discounted products.
     * @throws NotFoundException if no discounted products are found.
     */
    @Transactional(readOnly = true)
    public List<ProductResponseForUserDto> getTopFiveDiscountedProductsOfTheDay() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "discountPrice"));
        Page<Product> products = productRepository.findByDiscountPriceGreaterThan(BigDecimal.ZERO, pageable);
        List<ProductResponseDto> productDtos = products.map(productConverter::toDto).getContent();
        List<ProductResponseForUserDto> result = productConverter.toUserDtos(productDtos);

        if (result.isEmpty()) {
            throw new NotFoundException("No discounted products found");
        }
        return result;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the ID of the product to retrieve from the database
     * @return an Optional containing the Product if found; otherwise, an empty Optional
     */
    @Transactional(readOnly = true)
    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }

    /**
     * Validates the given ProductRequestDto object to ensure it adheres to required business rules.
     * This includes checks for non-null, non-empty, and properly formatted fields such as product name,
     * category, price, and discount price.
     *
     * @param productRequestDto the ProductRequestDto object containing details about the product to be validated
     *                          including product name, category, price, and discount price
     * @throws IllegalArgumentException if any of the following conditions are violated:
     *                                  - Product name is null, empty, or does not have a length between 3 and 20 characters
     *                                  - Product category is null or empty
     *                                  - Product price is null or less than or equal to 0.01
     *                                  - Product discount price is null or less than 0
     */
    private void validateProductRequestDto(ProductRequestDto productRequestDto) {
        String productName = productRequestDto.getProductName();
        String productCategory = productRequestDto.getProductCategory();
        BigDecimal productPrice = productRequestDto.getProductPrice();
        BigDecimal productDiscountPrice = productRequestDto.getProductDiscountPrice();
        if (productName == null || productName.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (productName.length() < 3 || productName.length() > 20) {
            throw new IllegalArgumentException("Product title must be between 3 and 20 characters");
        }
        if (productCategory == null || productCategory.isBlank()) {
            throw new IllegalArgumentException("Product category cannot be null or empty");
        }
        if (productPrice == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (productPrice.compareTo(BigDecimal.valueOf(0.01)) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0. Minimum price is 0.01");
        }
        if (productDiscountPrice == null) {
            throw new IllegalArgumentException("Product discount price cannot be null");
        }
        if (productDiscountPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product discount price must be greater than 0");
        }
    }
}
