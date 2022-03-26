package com.example.jax_ws.model;

import com.example.jax_ws.entity.CartItem;
import com.example.jax_ws.entity.ShoppingCart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ShoppingCartModelTest {

    private ShoppingCartModelImp model;

    @BeforeEach
    void setUp() {
        model = new ShoppingCartModelImp();
    }

    @Test
    void save() throws SQLException {
        ShoppingCart shoppingCart = new ShoppingCart();
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(0, 1, "Product 01", 10000, 1));
        items.add(new CartItem(0, 2, "Product 02", 20000, 2));
        shoppingCart.setCartItems(items);
        shoppingCart.setShipPhone("012345678");
        shoppingCart.setShipAddress("Hanoi");
        shoppingCart.setShipName("Hung");
        shoppingCart.setUserId(1); // default user
        shoppingCart.calculateTotalPrice();
        System.out.println(shoppingCart.toString());
        assertThat(model.save(shoppingCart).getId()).isNotEqualTo(0);
    }

    @Test
    void update() throws SQLException {
        ShoppingCart shoppingCart = new ShoppingCart();
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(0, 5, "Product 05", 30000, 5));
        shoppingCart.setCartItems(items);
        shoppingCart.setShipPhone("00000000");
        shoppingCart.setShipAddress("Updated");
        shoppingCart.setShipName("Hung Updated");
        shoppingCart.setUserId(1); // default user
        shoppingCart.calculateTotalPrice();
        System.out.println(shoppingCart.toString());

    }

    @Test
    void delete() throws SQLException {

    }
}