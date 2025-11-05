package org.onlineshop.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.shadow.com.univocity.parsers.annotations.Parsed;
import org.onlineshop.dto.product.ProductRequestDto;
import org.onlineshop.dto.product.ProductResponseDto;
import org.onlineshop.entity.Category;
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

    @BeforeEach
    void setUp() {

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

    }

    @Test
    void testGetProductsByPriceCriteriaAscIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("TestProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("TestProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("price","100-300","asc");

        assertEquals(3, result.size());
        assertTrue(result.get(0).getProductPrice().compareTo(new BigDecimal("100")) == 0);
        assertTrue(result.get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.get(2).getProductPrice().compareTo(new BigDecimal("250")) == 0);

    }

    @Test
    void testGetProductsByPriceCriteriaDescIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("TestProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("TestProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("price","100-300","desc");

        assertEquals(3, result.size());
        assertTrue(result.get(0).getProductPrice().compareTo(new BigDecimal("250")) == 0);
        assertTrue(result.get(1).getProductPrice().compareTo(new BigDecimal("200")) == 0);
        assertTrue(result.get(2).getProductPrice().compareTo(new BigDecimal("100")) == 0);

    }

    @Test
    void testGetProductsByDiscountPriceCriteriaAscIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("TestProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("TestProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("discount"," ","asc");

        assertEquals(4, result.size());
        assertTrue(result.get(0).getProductDiscountPrice().compareTo(new BigDecimal("5")) == 0);
        assertTrue(result.get(1).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);
        assertTrue(result.get(2).getProductDiscountPrice().compareTo(new BigDecimal("15")) == 0);
        assertTrue(result.get(3).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
    }

    @Test
    void testGetProductsByDiscountPriceCriteriaDescIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("TestProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("TestProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("discount"," ","desc");

        assertEquals(4, result.size());
        assertTrue(result.get(0).getProductDiscountPrice().compareTo(new BigDecimal("20")) == 0);
        assertTrue(result.get(1).getProductDiscountPrice().compareTo(new BigDecimal("15")) == 0);
        assertTrue(result.get(2).getProductDiscountPrice().compareTo(new BigDecimal("10")) == 0);
        assertTrue(result.get(3).getProductDiscountPrice().compareTo(new BigDecimal("5")) == 0);
    }

    @Test
    void testGetProductsByNameCriteriaAscIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("ProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("ProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("name","te","asc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"TestProductFirst");
        assertEquals(result.get(1).getProductName(),"TestProductSecond");

    }

    @Test
    void testGetProductsByNameCriteriaDescIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("ProductThird")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("ProductFourth")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("name","te","desc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"TestProductSecond");
        assertEquals(result.get(1).getProductName(),"TestProductFirst");

    }

    @Test
    void testGetProductsByCategoryCriteriaIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("TestProductSecond")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(250))
                .productDiscountPrice(BigDecimal.valueOf(10))
                .build();

        ProductResponseDto savedProductSecond = productService.addProduct(productSecond);

        ProductRequestDto productThird = ProductRequestDto.builder()
                .productName("ProductThird")
                .productCategory("testCategorySecond")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productThird);

        ProductRequestDto productFourth = ProductRequestDto.builder()
                .productName("ProductFourth")
                .productCategory("testCategorySecond")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(400))
                .productDiscountPrice(BigDecimal.valueOf(20))
                .build();

        ProductResponseDto savedProductFourth  = productService.addProduct(productFourth);


        List<ProductResponseDto> result = productService.getProductsByCriteria("category","testCategorySecond","asc");

        assertEquals(2, result.size());
        assertEquals(result.get(0).getProductName(),"ProductThird");
        assertEquals(result.get(1).getProductName(),"ProductFourth");

    }

    @Test
    void testGetProductsByCreateDateCriteriaIfOk() {
        ProductRequestDto productFirst = ProductRequestDto.builder()
                .productName("TestProductFirst")
                .productCategory("testCategoryFirst")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(100))
                .productDiscountPrice(BigDecimal.valueOf(5))
                .build();

        ProductResponseDto savedProductFirst = productService.addProduct(productFirst);

        ProductRequestDto productSecond = ProductRequestDto.builder()
                .productName("ProductSecond")
                .productCategory("testCategorySecond")
                .image("https://drive.google.com/test")
                .productDescription("TestProductText")
                .productPrice(BigDecimal.valueOf(200))
                .productDiscountPrice(BigDecimal.valueOf(15))
                .build();

        ProductResponseDto savedProductThird = productService.addProduct(productSecond);



        LocalDateTime now = LocalDateTime.now();
        String dateNow = DateTimeFormatter.ofPattern("yyyy-MM-dd").format(now);


        List<ProductResponseDto> result = productService.getProductsByCriteria("createDate",dateNow,"asc");

        assertEquals(2, result.size());


    }


}