package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        validateProductRequestDto(productRequestDto);
        Category category = categoryService.getCategoryByName(productRequestDto.getProductCategory());
        List<Product> productListFromCategory = category.getProducts();
        productListFromCategory.stream().map(Product::getName)
                .filter(productName -> productName.equalsIgnoreCase(productRequestDto.getProductName()))
                .findFirst()
                .ifPresent(productName -> {
                    throw new IllegalArgumentException("Product with name: " + productName
                            + " already exist in category: " + category.getCategoryName());
                });
        LocalDateTime now = LocalDateTime.now();

        final String finalImage = helper.resolveImageUrl(productRequestDto.getImage());
        Product productToSave = Product.builder()
                .name(productRequestDto.getProductName().trim())
                .description(productRequestDto.getProductDescription())
                .price(productRequestDto.getProductPrice())
                .discountPrice(productRequestDto.getProductDiscountPrice())
                .image(finalImage)
                .category(category)
                .createdAt(now)
                .updatedAt(now)
                .build();
        Product savedProduct = productRepository.save(productToSave);
        return productConverter.toDto(savedProduct);
    }

    /**
     * Updates an existing product with provided details. The method validates the input data,
     * ensures the product ID exists, checks for unique constraints in the specified category,
     * and updates the product's properties accordingly. If the update is successful, the updated
     * product details are returned.
     *
     * @param productId the ID of the product to be updated
     * @param productUpdateDto the details to update the product, including name, price, category,
     *                         description, discount price, and image
     * @return a {@code ProductResponseDto} containing the updated product details
     * @throws IllegalArgumentException if the product ID does not exist, if the product name already
     *                                  exists in the specified category, if the product name length
     *                                  violates constraints, or if the product price is non-positive
     */
    @Transactional
    @Override
    public ProductResponseDto updateProduct(Integer productId, ProductUpdateDto productUpdateDto) {
        Product productToUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id = " + productId + " not found"));
        Category category = productToUpdate.getCategory();
        if (productUpdateDto.getProductCategory() != null && !productUpdateDto.getProductCategory().isBlank()) {
            Category categoryAfterUpdate = categoryService.getCategoryByName(productUpdateDto.getProductCategory().trim());
            category = category.equals(categoryAfterUpdate) ? category : categoryAfterUpdate;
            productToUpdate.setCategory(category);
        }
        List<Product> productListFromCategory = category.getProducts();
        productListFromCategory.stream().map(Product::getName)
                .filter(productName -> productName.equalsIgnoreCase(productUpdateDto.getProductName()))
                .findFirst()
                .ifPresent(productName -> {
                    throw new IllegalArgumentException("Product with name: " + productName
                            + " already exist in category.");
                });
        if (productUpdateDto.getProductName() != null && !productUpdateDto.getProductName().isBlank()) {
            if (productUpdateDto.getProductName().length() < 3 || productUpdateDto.getProductName().length() > 20) {
                throw new IllegalArgumentException("Product title must be between 3 and 20 characters");
            }
            productToUpdate.setName(productUpdateDto.getProductName().trim());
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
            productToUpdate.setDiscountPrice(productUpdateDto.getProductDiscountPrice());
        }
        if (productUpdateDto.getImage() != null && !productUpdateDto.getImage().isBlank()) {
            String newImage = helper.resolveImageUrl(productUpdateDto.getImage());
            productToUpdate.setImage(newImage);
        }
        LocalDateTime now = LocalDateTime.now();
        productToUpdate.setUpdatedAt(now);
        productRepository.save(productToUpdate);
        return productConverter.toDto(productToUpdate);
    }

    /**
     * Sets the discount price for a product with the specified ID.
     *
     * @param productId the ID of the product to set the discount price for
     * @param newDiscountPrice the new discount price to set
     * @return a {@code ProductResponseDto} containing the updated product details
     * @throws IllegalArgumentException if the product ID does not exist
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
                .orElseThrow(() -> new IllegalArgumentException("Product with id = " + productId + " not found"));
        product.setDiscountPrice(newDiscountPrice);
        productRepository.save(product);
        return productConverter.toDto(product);
    }

    /**
     * Deletes a product with the specified ID.
     *
     * @param productId the ID of the product to be deleted
     * @return a {@code ProductResponseDto} containing the deleted product details
     * @throws IllegalArgumentException if the product ID does not exist
     */
    @Transactional
    @Override
    public ProductResponseDto deleteProduct(Integer productId) {
        Product productToDelete = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id = " + productId + " not found"));
        productRepository.delete(productToDelete);
        return productConverter.toDto(productToDelete);
    }

    // localhost:8080/v1/products/getProductsByCriteria?paramName=price&paramValue=100-300&sortDirection=asc
    // localhost:8080/v1/products/getProductsByCriteria?paramName=price&paramValue=100-300&sort=asc
    // localhost:8080/v1/products/getProductsByCriteria?paramName=discount&paramValue=35&sortDirection=desc
    // localhost:8080/v1/products/getProductsByCriteria?paramName=category&paramValue=categoryName&sortDirection=desc
    // localhost:8080/v1/products/getProductsByCriteria?paramName=name&paramValue=partName&sortDirection=desc
    // localhost:8080/v1/products/getProductsByCriteria?paramName=createDate&sortDirection=desc
    // localhost:8080/v1/products

    /**
     * Retrieves a list of products based on the specified criteria.
     *
     * @param paramName the name of the parameter to filter the products by (e.g., "price", "discount", "category", "name", "createDate")
     * @param paramValue the value of the parameter used for filtering. For "price", this should be a range in the format "min-max". For other parameters,
     *                   it should be a specific value or partial value as applicable.
     * @param sortDirection the sorting direction for the results ("asc" for ascending or "desc" for descending)
     * @return a list of products that match the given criteria
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByCriteria(String paramName, String paramValue, String sortDirection) {
        switch (paramName) {
            case "price":
                String[] priceRange = paramValue.split("-");
                BigDecimal minPrice = new BigDecimal(priceRange[0]);
                BigDecimal maxPrice = new BigDecimal(priceRange[1]);
                return getProductsByPriceRange(minPrice, maxPrice, sortDirection);
            case "discount":
                return getProductsByDiscount(sortDirection);
            case "category":
                return getProductsByCategory(paramValue, sortDirection);
            case "name":
                return getProductsByPartOfNameIgnoreCase(paramValue, sortDirection);
            case "createDate":
                return getProductsByCreateDate(sortDirection);
            default:
                return getAllProducts();
        }
    }

    /**
     * Retrieves a list of product response DTOs where the product name contains the specified
     * part of the name, ignoring case. The resulting list is sorted based on the provided sort
     * direction.
     *
     * @param partOfName the part of the product name to search for, case-insensitively
     * @param sortDirection the direction to sort the results, either "ASC" for ascending
     *                      or "DESC" for descending
     * @return a list of {@code ProductResponseDto} objects matching the search criteria
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "name");
        List<Product> products = productRepository.findByNameContainingIgnoreCase(partOfName, sort);
        return productConverter.toDtos(products);
    }

    /**
     * Retrieves a list of products belonging to a specific category and sorts them
     * in the specified direction.
     *
     * @param categoryName the name of the category for which products are to be retrieved
     * @param sortDirection the sorting direction, either "ASC" for ascending or "DESC" for descending
     * @return a list of product response DTOs corresponding to the products in the specified category,
     *         sorted accordingly
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByCategory(String categoryName, String sortDirection) {
        Category category = categoryService.getCategoryByName(categoryName);
        Sort sort = Sort.by(getSortDirection(sortDirection), "category.categoryName");
        List<Product> products = productRepository.findByCategory(category, sort);
        return productConverter.toDtos(products);
    }

    /**
     * Retrieves a list of products within the specified price range and sorts the results
     * based on the provided sort direction.
     *
     * @param minPrice the minimum price for the price range, must not be null
     * @param maxPrice the maximum price for the price range, must not be null
     * @param sortDirection in the direction to sort the results, can be "asc" or "desc"
     * @return a list of products matching the price range criteria, sorted by the specified direction
     * @throws IllegalArgumentException if minPrice or maxPrice is null
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, String sortDirection) {
        if (minPrice == null || maxPrice == null) {
            throw new IllegalArgumentException("Min price and max price must be provided");
        }
        if (minPrice.compareTo(maxPrice) > 0) {
            BigDecimal temp = minPrice;
            minPrice = maxPrice;
            maxPrice = temp;
        }
        Sort sort = Sort.by(getSortDirection(sortDirection), "price");
        List<Product> products = productRepository.findByPriceBetween(minPrice, maxPrice, sort);
        return productConverter.toDtos(products);
    }

    /**
     * Retrieves a list of products that have a discount.
     * The products are sorted by their discount price based on the specified sort direction.
     *
     * @param sortDirection the direction to sort the products by discount price.
     *                       It can be "asc" for ascending order or "desc" for descending order.
     * @return a list of ProductResponseDto containing the details of the products with a discount.
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByDiscount(String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "discountPrice");
        List<Product> products = productRepository.findByDiscountPriceGreaterThan(BigDecimal.valueOf(0), sort);
        return productConverter.toDtos(products);
    }

    /**
     * Retrieves a list of products sorted by their creation date.
     *
     * @param sortDirection the direction to sort the products by creation date,
     *                      either "asc" for ascending or "desc" for descending
     * @return a list of ProductResponseDto objects sorted by creation date
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getProductsByCreateDate(String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "createdAt");
        List<Product> products = productRepository.findAll(sort);
        return productConverter.toDtos(products);
    }

    /**
     * Retrieves a list of all products in the database.
     *
     * @return a list of ProductResponseDto objects containing the details of all products in the database
     */
    @Transactional
    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productConverter.toDtos(productRepository.findAll());
    }

    /**
     * Retrieves a list of products for the current user.
     * @return a list of ProductResponseForUserDto objects containing the details of the products for the current user
     */
    @Transactional
    public List<ProductResponseForUserDto> getAllProductsForUser() {
        return productConverter.toUserDtos(getAllProducts());
    }

    /**
     * Retrieves the top five discounted products of the day, sorted in descending order of discount.
     * If no discounted products are found, a NotFoundException is thrown.
     *
     * @return a list of ProductResponseForUserDto containing information about the top five discounted products.
     * @throws NotFoundException if no discounted products are found.
     */
    @Transactional
    public List<ProductResponseForUserDto> getTopFiveDiscountedProductsOfTheDay() {
        List<ProductResponseForUserDto> result = productConverter.toUserDtos(getProductsByDiscount("desc").stream().limit(5).toList());
        if (result.isEmpty()) {
            throw new NotFoundException("No discounted products found");
        }
        return result;
    }

    /**
     * Determines the sort direction based on the provided string representation.
     *
     * @param sortDirection the string representation of the sort direction;
     *                      can be "asc" for ascending or "desc" for descending.
     *                      If null or blank, defaults to ascending.
     * @return the corresponding {@code Sort.Direction} value;
     *         returns {@code Sort.Direction.ASC} for ascending or {@code Sort.Direction.DESC} for descending.
     * @throws IllegalArgumentException if the provided sort direction is invalid or unsupported.
     */
    private Sort.Direction getSortDirection(String sortDirection) {
        if (sortDirection == null || sortDirection.isBlank()) {
            return Sort.Direction.ASC;
        }
        if (sortDirection.equalsIgnoreCase("asc")) {
            return Sort.Direction.ASC;
        }
        if (sortDirection.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the ID of the product to retrieve from the database
     * @return an Optional containing the Product if found; otherwise, an empty Optional
     */
    @Transactional
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
        if (productPrice == null){
            throw new IllegalArgumentException("Product price cannot be null");
        }
        if (productPrice.compareTo(BigDecimal.valueOf(0.01)) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0. Minimum price is 0.01");
        }
        if (productDiscountPrice == null){
            throw new IllegalArgumentException("Product discount price cannot be null");
        }
        if (productDiscountPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Product discount price must be greater than 0");
        }
    }
}
