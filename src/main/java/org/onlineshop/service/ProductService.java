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

    @Transactional
    @Override
    public ProductResponseDto addProduct(ProductRequestDto productRequestDto) {
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

    @Transactional
    @Override
    public ProductResponseDto updateProduct(Integer productId, ProductUpdateDto productUpdateDto) {
        Product productToUpdate = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id = " + productId + " not found"));
        Category category = productToUpdate.getCategory();
        if (productUpdateDto.getProductCategory() != null && !productUpdateDto.getProductCategory().isBlank()) {
            Category categoryAfterUpdate = categoryService.getCategoryByName(productUpdateDto.getProductCategory());
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
            productToUpdate.setName(productUpdateDto.getProductName());
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

    @Transactional
    public ProductResponseDto setDiscountPrice(Integer productId, BigDecimal newDiscountPrice) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id = " + productId + " not found"));
        product.setDiscountPrice(newDiscountPrice);

        productRepository.save(product);

        return productConverter.toDto(product);
    }

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

    @Override
    public List<ProductResponseDto> getProductsByPartOfNameIgnoreCase(String partOfName, String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "name");
        List<Product> products = productRepository.findByNameContainingIgnoreCase(partOfName, sort);
        return productConverter.toDtos(products);
    }

    @Override
    public List<ProductResponseDto> getProductsByCategory(String categoryName, String sortDirection) {
        Category category = categoryService.getCategoryByName(categoryName);
        Sort sort = Sort.by(getSortDirection(sortDirection), "category.categoryName");
        List<Product> products = productRepository.findByCategory(category, sort);
        return productConverter.toDtos(products);
    }

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

    @Override
    public List<ProductResponseDto> getProductsByDiscount(String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "discountPrice");
        List<Product> products = productRepository.findByDiscountPriceGreaterThan(BigDecimal.valueOf(0), sort);
        return productConverter.toDtos(products);
    }

    @Override
    public List<ProductResponseDto> getProductsByCreateDate(String sortDirection) {
        Sort sort = Sort.by(getSortDirection(sortDirection), "createdAt");
        List<Product> products = productRepository.findAll(sort);
        return productConverter.toDtos(products);
    }

    @Override
    public List<ProductResponseDto> getAllProducts() {
        return productConverter.toDtos(productRepository.findAll());
    }

    public List<ProductResponseForUserDto> getAllProductsForUser() {
        return productConverter.toUserDtos(getAllProducts());
    }

    public List<ProductResponseForUserDto> getTopFiveDiscountedProductsOfTheDay() {
        List<ProductResponseForUserDto> result = productConverter.toUserDtos(getProductsByDiscount("desc").stream().limit(5).toList());
        if (result.size() == 0) {
            throw new NotFoundException("No discounted products found");
        }
        return result;
    }

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

    @Override
    public Optional<Product> getProductById(Integer productId) {
        return productRepository.findById(productId);
    }
}
