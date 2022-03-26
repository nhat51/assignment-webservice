package com.example.jax_ws.model;

import com.example.jax_ws.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;

class ProductModelTest {

    private ProductModel productModel;

    @BeforeEach
    void setUp() {
        productModel = new ProductModel();
    }

    @Test
    void save() throws SQLException {
        Product product = new Product("Product 1", 10000, 1);
        Product insertedProduct = productModel.save(product);
        assertThat(insertedProduct.getId()).isNotEqualTo(0);
    }

    @Test
    void findAll() throws SQLException {
        Product product = new Product("Product 1", 10000, 1);
        productModel.save(product);
        List<Product> productList = productModel.findAll();
        assertThat(productList.size()).isGreaterThan(0);
    }

    @Test
    void findById() throws SQLException {
        Product product = new Product("Product 1", 10000, 1);
        Product insertedProduct = productModel.save(product);
        Product foundProduct = productModel.findById(insertedProduct.getId());
        assertThat(foundProduct).isNotNull();
    }

    @Test
    void update() throws SQLException {
        Product product = new Product("Product 1", 10000, 1);
        Product insertedProduct = productModel.save(product);
        Product updateProduct = new Product("Product 2", 10000, 0);
        Product updatedProduct = productModel.update(insertedProduct.getId(), updateProduct);
        assertThat(updatedProduct).isEqualToComparingFieldByFieldRecursively(updateProduct);
//        assertThat(updatedProduct.getName()).isEqualTo(updateProduct.getName());
//        assertThat(updatedProduct.getPrice()).isEqualTo(updateProduct.getPrice());
//        assertThat(updatedProduct.getStatus()).isEqualTo(updateProduct.getStatus());
    }

    @Test
    void delete() throws SQLException {
        Product product = new Product("Product 1", 10000, 1);
        Product insertedProduct = productModel.save(product);
        boolean result = productModel.delete(insertedProduct.getId());
        assertThat(result).isEqualTo(true);
    }
}