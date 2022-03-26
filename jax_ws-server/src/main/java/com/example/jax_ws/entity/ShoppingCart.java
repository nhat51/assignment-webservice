package com.example.jax_ws.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShoppingCart {
    private int id; // khoá chính
    private int userId; // của ai, để default hard code = 1, bởi chưa có bảng user.
    private String shipName;
    private String shipAddress;
    private String shipPhone;
    private double totalPrice; // đây là một biến tạm để show thông tin còn bản chất là tính tổng từ cartItems.
    private HashMap<Integer, CartItem> mapCartItems;
    private List<CartItem> cartItems;

    public ShoppingCart() {
        this.mapCartItems = new HashMap<>();
        this.cartItems = new ArrayList<>();
        this.shipName = "";
        this.shipAddress = "";
        this.shipPhone = "";
    }

    public ShoppingCart(int userId) {
        this.userId = userId;
    }

    public void add(Product product, int quantity) {
        if (mapCartItems == null) {
            mapCartItems = new HashMap<>();
        }
        CartItem item = mapCartItems.get(product.getId());
        if (item == null) {
            item = new CartItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(quantity);
        } else {
            item.setQuantity(item.getQuantity() + quantity);
        }
        mapCartItems.put(product.getId(), item);
    }

    public void update(Product product, int quantity) {
        if (mapCartItems == null) {
            mapCartItems = new HashMap<>();
        }
        CartItem item = mapCartItems.get(product.getId());
        if (item == null) {
            item = new CartItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(quantity);
        } else {
            item.setQuantity(quantity);
        }
        mapCartItems.put(product.getId(), item);
    }

    public void deduct(Product product, int quantity) {
        if (mapCartItems == null) {
            mapCartItems = new HashMap<>();
        }

        CartItem item = mapCartItems.get(product.getId());
        if (item == null) {
            item = new CartItem();
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setQuantity(quantity);
        } else if (item.getQuantity() <= quantity) {
            item.setQuantity(-1);
        } else {
            item.setQuantity(item.getQuantity() - quantity);
        }
        mapCartItems.put(product.getId(), item);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }

    public String getShipPhone() {
        return shipPhone;
    }

    public void setShipPhone(String shipPhone) {
        this.shipPhone = shipPhone;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<CartItem> getCartItems() {
        if (mapCartItems == null) {
            return new ArrayList<>();
        }
        cartItems = new ArrayList<CartItem>(mapCartItems.values());
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
        this.mapCartItems = new HashMap<>();
        for (CartItem item :
                cartItems) {
            this.mapCartItems.put(item.getProductId(), item);
        }
    }

    public void calculateTotalPrice() {
        if (cartItems != null && cartItems.size() > 0) {
            for (CartItem item :
                    cartItems) {
                totalPrice += item.getQuantity() * item.getUnitPrice();
            }
        }
    }

    @Override
    public String toString() {
        return "ShoppingCart{" +
                "id=" + id +
                ", userId=" + userId +
                ", shipName='" + shipName + '\'' +
                ", shipAddress='" + shipAddress + '\'' +
                ", shipPhone='" + shipPhone + '\'' +
                ", totalPrice=" + totalPrice +
                ", cartItems=" + cartItems +
                '}';
    }
}
