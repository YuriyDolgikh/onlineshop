package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Parsed;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.CategoryRepository;
import org.onlineshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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


        List<ProductResponseDto> result = productService.getProductsByCriteria("price","100-300","asc");

        assertEquals(3, result.size());
        assertTrue(result.get(0).getProductPrice().compareTo(new BigDecimal("100")) == 0);
        assertTrue(result.get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.get(2).getProductPrice().compareTo(new BigDecimal("250")) == 0);

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

        List<ProductResponseDto> result = productService.getProductsByCriteria("price","100-300","desc");

        assertEquals(3, result.size());
        assertTrue(result.get(0).getProductPrice().compareTo(new BigDecimal("250")) == 0);
        assertTrue(result.get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.get(2).getProductPrice().compareTo(new BigDecimal("100")) == 0);

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

        List<ProductResponseDto> result = productService.getProductsByCriteria("discount"," ","asc");

        assertEquals(4, result.size());
        assertTrue(result.get(0).getProductDiscountPrice().compareTo(new BigDecimal("5")) == 0);
        assertTrue(result.get(1).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);
        assertTrue(result.get(2).getProductDiscountPrice().compareTo(new BigDecimal("15")) == 0);
        assertTrue(result.get(3).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
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


        List<ProductResponseDto> result = productService.getProductsByCriteria("discount"," ","desc");

        assertEquals(4, result.size());
        assertTrue(result.get(0).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
        assertTrue(result.get(1).getProductDiscountPrice().compareTo(new BigDecimal("15")) == 0);
        assertTrue(result.get(2).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);
        assertTrue(result.get(3).getProductDiscountPrice().compareTo(new BigDecimal("5")) == 0);
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


        List<ProductResponseDto> result = productService.getProductsByCriteria("name","te","asc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"testProductOne");
        assertEquals(result.get(1).getProductName(),"testProductTwo");

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


        List<ProductResponseDto> result = productService.getProductsByCriteria("name","te","desc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"testProductTwo");
        assertEquals(result.get(1).getProductName(),"testProductOne");

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


        List<ProductResponseDto> result = productService.getProductsByCriteria("category","testCategorySecond","asc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"ProductThree");
        assertEquals(result.get(1).getProductName(),"ProductFour");

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

        List<ProductResponseDto> result = productService.getProductsByCriteria("createDate",dateNow,"asc");

        assertEquals(2, result.size());

    }

}