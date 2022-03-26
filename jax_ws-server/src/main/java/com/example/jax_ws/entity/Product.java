package com.example.jax_ws.entity;

public class Product {
    private int id;
    private String name;
    private double price;
    private int status;

    public Product() {
    }

    public Product(int id, String name, double price, int status) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public Product(String name, double price, int status) {
        this.name = name;
        this.price = price;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
