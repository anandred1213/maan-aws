package com.product_service.product_microservice.service;

import com.product_service.product_microservice.entity.Product;
import com.product_service.product_microservice.exception.ProductNotFoundException;
import com.product_service.product_microservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(Product product) {
        log.info("Adding new product: {}", product.getName());
        if (product.getName() == null || product.getPrice() == null || product.getQuantity() == null) {
            log.error("Product validation failed: missing required fields");
            throw new IllegalArgumentException("Product name, price, and quantity are required");
        }
        if (product.getPrice() < 0) {
            log.error("Product validation failed: negative price");
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (product.getQuantity() < 0) {
            log.error("Product validation failed: negative quantity");
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product saved = productRepository.save(product);
        log.info("Product added successfully with ID: {}", saved.getId());
        return saved;
    }

    public List<Product> getAllProducts() {
        log.debug("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.info("Retrieved {} products", products.size());
        return products;
    }

    public Product getProductById(Long id) {
        log.debug("Fetching product with ID: {}", id);
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", id);
                    return new ProductNotFoundException(id);
                });
    }

    public Product updateProduct(Long id, Product product) {
        log.info("Updating product with ID: {}", id);
        if (product.getPrice() != null && product.getPrice() < 0) {
            log.error("Update validation failed: negative price");
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (product.getQuantity() != null && product.getQuantity() < 0) {
            log.error("Update validation failed: negative quantity");
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Product not found for update with ID: {}", id);
                    return new ProductNotFoundException(id);
                });
        if (product.getName() != null) existing.setName(product.getName());
        if (product.getPrice() != null) existing.setPrice(product.getPrice());
        if (product.getQuantity() != null) existing.setQuantity(product.getQuantity());
        Product updated = productRepository.save(existing);
        log.info("Product updated successfully with ID: {}", id);
        return updated;
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with ID: {}", id);
        productRepository.deleteById(id);
        log.info("Product deleted successfully with ID: {}", id);
    }
}
