package org.onlineshop.service.mail;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.onlineshop.dto.ProductResponseDto;
import org.onlineshop.entity.Product;
import org.onlineshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product with id: " + productId + " does not exist"));
    }


}
