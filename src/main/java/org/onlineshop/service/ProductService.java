package org.onlineshop.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;
    Sort.Direction sortDesc = Sort.Direction.DESC;
    Sort.Direction sortAsc = Sort.Direction.ASC;

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

//    public List<Product> getByNameOrderByName(String name, Sort.Direction sort) {
//        return productRepository.findByNameOrderByName(name, sort);
//    }

    List<Product> getProductsByPartOfNameIgnoreCase(String partOfName, Sort sort) {
        return productRepository.findByNameContainingIgnoreCase(partOfName, sort);
    }

    List<Product> sortTest() {
        Sort sort = Sort.by(Sort.Direction.DESC, "partOfName");
        return productRepository.findByNameContainingIgnoreCase("test", sort);
    }


    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id: " + productId + " does not exist"));
    }


}
