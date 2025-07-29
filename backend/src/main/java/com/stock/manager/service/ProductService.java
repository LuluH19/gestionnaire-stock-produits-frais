package com.stock.manager.service;

import com.stock.manager.dto.ProductCreateDTO;
import com.stock.manager.dto.ProductUpdateDTO;
import com.stock.manager.dto.QuantityUpdateDTO;
import com.stock.manager.entity.Product;
import com.stock.manager.entity.ProductCategory;
import com.stock.manager.exception.ResourceNotFoundException;
import com.stock.manager.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts(ProductCategory category) {
        if (category != null) {
            return productRepository.findByCategory(category);
        }
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'ID " + id));
    }

    public Product createProduct(ProductCreateDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setQuantity(dto.getQuantity());
        product.setExpirationDate(dto.getExpirationDate());
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, ProductUpdateDTO dto) {
        Product product = getProductById(id);
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setQuantity(dto.getQuantity());
        product.setExpirationDate(dto.getExpirationDate());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        productRepository.delete(product);
    }

    public List<Product> getExpiringProducts() {
        LocalDate now = LocalDate.now();
        LocalDate inThreeDays = now.plusDays(3);
        return productRepository.findByExpirationDateBetween(now, inThreeDays);
    }

    @Transactional
    public Product updateQuantity(Long id, QuantityUpdateDTO dto) {
        Product product = getProductById(id);
        product.setQuantity(dto.getQuantity());
        return product;
    }
}