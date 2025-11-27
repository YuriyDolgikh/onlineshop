package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = "classpath:application-test.yml")
class ProductServiceGetProductByCriteriaTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void dropDatabase() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
    }

    @Test
    void testGetProductsByPriceCriteriaAscIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        Page<ProductResponseDto> result = productService.getProductsByCriteria("price", "100-300",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "price")));

        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getContent().get(0).getProductPrice().compareTo(new BigDecimal("100")));
        assertEquals(0, result.getContent().get(1).getProductPrice().compareTo(new BigDecimal("200")));
        assertEquals(0, result.getContent().get(2).getProductPrice().compareTo(new BigDecimal("250")));

    }

    @Test
    void testGetProductsByPriceCriteriaDescIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);

        Page<ProductResponseDto> result = productService.getProductsByCriteria("price", "100-300",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "price")));

        assertEquals(3, result.getTotalElements());
        assertEquals(0, result.getContent().get(0).getProductPrice().compareTo(new BigDecimal("250")));
        assertEquals(0, result.getContent().get(1).getProductPrice().compareTo(new BigDecimal("200")));
        assertEquals(0, result.getContent().get(2).getProductPrice().compareTo(new BigDecimal("100")));

    }

    @Test
    void testGetProductsByDiscountPriceCriteriaAscIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(5))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);

        Page<ProductResponseDto> result = productService.getProductsByCriteria("discount", " ",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "discountPrice")));

        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getContent().get(0).getProductDiscountPrice().compareTo(new BigDecimal("5")));
        assertEquals(0, result.getContent().get(1).getProductDiscountPrice().compareTo(new BigDecimal("10")));
        assertEquals(0, result.getContent().get(2).getProductDiscountPrice().compareTo(new BigDecimal("15")));
        assertEquals(0, result.getContent().get(3).getProductDiscountPrice().compareTo(new BigDecimal("20")));
    }

    @Test
    void testGetProductsByDiscountPriceCriteriaDescIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(5))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("testProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(15))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("testProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(20))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);

        Page<ProductResponseDto> result = productService.getProductsByCriteria("discount", " ",
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "discountPrice")));

        assertEquals(4, result.getTotalElements());
        assertEquals(0, result.getContent().get(0).getProductDiscountPrice().compareTo(new BigDecimal("20")));
        assertEquals(0, result.getContent().get(1).getProductDiscountPrice().compareTo(new BigDecimal("15")));
        assertEquals(0, result.getContent().get(2).getProductDiscountPrice().compareTo(new BigDecimal("10")));
        assertEquals(0, result.getContent().get(3).getProductDiscountPrice().compareTo(new BigDecimal("5")));
    }

    @Test
    void testGetProductsByNameCriteriaAscIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("ProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        Page<ProductResponseDto> result = productService.getProductsByCriteria("name", "te",
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name")));

        assertEquals(2, result.getTotalElements());
        assertEquals("testProductOne", result.getContent().get(0).getProductName());
        assertEquals("testProductTwo", result.getContent().get(1).getProductName());

    }

    @Test
    void testGetProductsByNameCriteriaDescIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("ProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        Page<ProductResponseDto> result = productService.getProductsByCriteria("name", "te",
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "name")));

        assertEquals(2, result.getTotalElements());
        assertEquals("testProductTwo", result.getContent().get(0).getProductName());
        assertEquals("testProductOne", result.getContent().get(1).getProductName());

    }

    @Test
    void testGetProductsByCategoryCriteriaIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Category categorySecond = Category.builder()
                .categoryName("testCategorySecond")
                .image("https://drive.google.com/file/second")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categorySecond);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categorySecond)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("ProductFour")
                .category(categorySecond)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        Page<ProductResponseDto> result = productService.getProductsByCriteria("category", "testCategorySecond",
                PageRequest.of(0, 5, Sort.by(Sort.Direction.ASC, "name")));

        assertEquals(2, result.getTotalElements());
        assertEquals("ProductThree", result.getContent().get(0).getProductName());
        assertEquals("ProductFour", result.getContent().get(1).getProductName());

    }

    @Test
    void testGetProductsByCreateDateCriteriaIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        LocalDateTime now = LocalDateTime.now();
        String dateNow = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);

        Page<ProductResponseDto> result = productService.getProductsByCriteria("createDate", dateNow,
                PageRequest.of(0, 5, Sort.Direction.valueOf("asc")));

        assertEquals(2, result.getTotalElements());

    }

    @Test
    void testGetProductsByCriteriaGetAllProductAscIfOk() {
        Category categoryFirst = Category.builder()
                .categoryName("testCategoryFirst")
                .image("https://drive.google.com/file/first")
                .products(new ArrayList<>())
                .build();

        categoryRepository.save(categoryFirst);

        Product productTestOne = Product.builder()
                .name("testProductOne")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(100))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/first")
                .build();

        productRepository.save(productTestOne);

        Product productTestTwo = Product.builder()
                .name("testProductTwo")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(200))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/one")
                .build();

        productRepository.save(productTestTwo);

        Product productTestThree = Product.builder()
                .name("ProductThree")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(250))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/three")
                .build();

        productRepository.save(productTestThree);

        Product productTestFour = Product.builder()
                .name("ProductFour")
                .category(categoryFirst)
                .description("testDescription")
                .price(BigDecimal.valueOf(400))
                .discountPrice(BigDecimal.valueOf(10))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .image("https://drive.google.com/file/four")
                .build();

        productRepository.save(productTestFour);


        Page<ProductResponseDto> result = productService.getProductsByCriteria(" ", " ",
                PageRequest.of(0, 5, Sort.Direction.valueOf("asc")));

        assertEquals(4, result.getTotalElements());
    }

}