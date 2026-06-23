package org.yearup.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

class ProductServiceTest
{
    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp()
    {
        productRepository = mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }


    // Bug 1 - Search should return ALL products, not just featured ones
    @Test
    void search_shouldReturnAllProducts_notJustFeatured()
    {
        // Arrange
        Product featured = new Product(1, "Elden Ring", 59.99, 1,
                "Dark fantasy RPG", "RPG", 40, true, "elden-ring.jpg");

        Product notFeatured = new Product(2, "Gears 5", 29.99, 1,
                "Third-person shooter", "Shooter", 25, false, "gears-5.jpg");

        when(productRepository.findAll()).thenReturn(List.of(featured, notFeatured));

        // Act
        List<Product> results = productService.search(null, null, null, null);

        // Assert
        assertEquals(2, results.size(), "Search with no filters should return ALL products, including non-featured");
        assertTrue(results.stream().anyMatch(p -> p.getProductId() == 2),
                "Non-featured product 'Gears 5' should be included in results");
    }


    // Bug 2 - Update should persist the stock field
    @Test
    void update_shouldPersistStockValue()
    {
        // Arrange
        Product existing = new Product(1, "Elden Ring", 59.99, 1,
                "Dark fantasy RPG", "RPG", 40, true, "elden-ring.jpg");

        Product updatedData = new Product(1, "Elden Ring", 59.99, 1,
                "Dark fantasy RPG", "RPG", 100, true, "elden-ring.jpg");

        when(productRepository.findById(1)).thenReturn(Optional.of(existing));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Product result = productService.update(1, updatedData);

        // Assert
        assertEquals(100, result.getStock(), "Stock should be updated to 100 after update");
    }
}