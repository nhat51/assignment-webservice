package com.example.jax_ws.model;


import com.example.jax_ws.entity.CartItem;
import com.example.jax_ws.entity.ShoppingCart;
import com.example.jax_ws.utils.ConnectionHelper;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShoppingCartModelImp implements ShoppingCartModel {

    private Connection conn;

    private CartItemModel cartItemModel;

    public ShoppingCartModelImp() {
        conn = ConnectionHelper.getConnection();
        cartItemModel = new CartItemModel();
    }

    public ShoppingCart create(int userId) throws SQLException {
        PreparedStatement stmtShoppingCart = conn.prepareStatement("insert into shopping_carts (userId, shipName, shipAddress, shipPhone, totalPrice) values (?, '', '', '', 0)", Statement.RETURN_GENERATED_KEYS);
        stmtShoppingCart.setInt(1, userId);
        int affectedRows = stmtShoppingCart.executeUpdate();
        ShoppingCart shoppingCart = new ShoppingCart();
        if (affectedRows > 0) {
            ResultSet resultSetGeneratedKeys = stmtShoppingCart.getGeneratedKeys();
            if (resultSetGeneratedKeys.next()) {
                int id = resultSetGeneratedKeys.getInt(1);
                shoppingCart.setId(id);
                shoppingCart.setUserId(userId);
            }
        }

        return shoppingCart;
    }

    @Override
    public ShoppingCart get(int userId) throws SQLException {
        conn.setAutoCommit(false);
        try {
            PreparedStatement stmtShoppingCart = conn.prepareStatement("select * from shopping_carts where userId = ?", Statement.RETURN_GENERATED_KEYS);
            stmtShoppingCart.setInt(1, userId);
            ResultSet resultSet = stmtShoppingCart.executeQuery();
            if (resultSet.next()) {
                ShoppingCart shoppingCart = new ShoppingCart();
                int shoppingCartId = resultSet.getInt("id");
                shoppingCart.setId(shoppingCartId);
                shoppingCart.setUserId(userId);
                shoppingCart.setShipName(resultSet.getString("shipName"));
                shoppingCart.setShipAddress(resultSet.getString("shipAddress"));
                shoppingCart.setShipPhone(resultSet.getString("shipPhone"));
                shoppingCart.setTotalPrice(resultSet.getDouble("totalPrice"));
                List<CartItem> cartItems = new ArrayList<>();
                try {
                    PreparedStatement stmtCartItems = conn.prepareStatement("select * from cart_items where shoppingCartId = ?", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItems.setInt(1, shoppingCartId);
                    ResultSet resultSetCartItems = stmtCartItems.executeQuery();
                    while (resultSetCartItems.next()) {
                        CartItem cartItem = new CartItem();
                        cartItem.setShoppingCartId(shoppingCartId);
                        cartItem.setProductId(resultSetCartItems.getInt("productId"));
                        cartItem.setProductName(resultSetCartItems.getString("productName"));
                        cartItem.setUnitPrice(resultSetCartItems.getInt("unitPrice"));
                        cartItem.setQuantity(resultSetCartItems.getInt("quantity"));
                        cartItems.add(cartItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                shoppingCart.setCartItems(cartItems);
                return shoppingCart;
            }

            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true);
        }
        return null;
    }

    public ShoppingCart save(ShoppingCart shoppingCart) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            // trường hợp shopping cart null hoặc không có sản phẩm.
            if (shoppingCart == null || shoppingCart.getCartItems().size() == 0) {
                throw new Error("Shopping's null or empty.");
            }
            //Kiểm tra xem cart đã tồn tại hay chưa
            if (shoppingCart.getId() == 0) {
                PreparedStatement stmtShoppingCart = conn.prepareStatement("insert into shopping_carts (userId, shipName, shipAddress, shipPhone, totalPrice) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                stmtShoppingCart.setInt(1, shoppingCart.getUserId());
                stmtShoppingCart.setString(2, shoppingCart.getShipName());
                stmtShoppingCart.setString(3, shoppingCart.getShipAddress());
                stmtShoppingCart.setString(4, shoppingCart.getShipPhone());
                stmtShoppingCart.setDouble(5, shoppingCart.getTotalPrice());
                int affectedRows = stmtShoppingCart.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet resultSetGeneratedKeys = stmtShoppingCart.getGeneratedKeys();
                    if (resultSetGeneratedKeys.next()) {
                        int id = resultSetGeneratedKeys.getInt(1);
                        shoppingCart.setId(id);
                    }
                }
                if (shoppingCart.getId() == 0) {
                    throw new Error("Can't insert shopping cart.");
                }
            }

            double totalPrice = 0;
            boolean isDeleted = false;
            List<CartItem> cartItemsAfterDelete = new ArrayList<>();
            CartItem itemToDelete = new CartItem();
            // update, insert and deduct cart items;
            for (CartItem item : shoppingCart.getCartItems()) {
                item.setShoppingCartId(shoppingCart.getId());
                CartItem cartItem = cartItemModel.findByProductIdAndShoppingCartId(item.getProductId(), item.getShoppingCartId());
                if (cartItem == null) {
                    PreparedStatement stmtCartItem = conn.prepareStatement("insert into cart_items (shoppingCartId, productId, productName, unitPrice, quantity) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItem.setInt(1, item.getShoppingCartId());
                    stmtCartItem.setInt(2, item.getProductId());
                    stmtCartItem.setString(3, item.getProductName());
                    stmtCartItem.setDouble(4, item.getUnitPrice());
                    stmtCartItem.setInt(5, item.getQuantity());
                    int affectedCartItemRows = stmtCartItem.executeUpdate();
                    totalPrice += item.getQuantity() * item.getUnitPrice();
                    if (affectedCartItemRows == 0) { // lỗi
                        throw new Error("Insert cart item fails.");
                    }
                } else if (item.getQuantity() == -1) {
                    remove(item.getProductId());
                    cartItemsAfterDelete = shoppingCart.getCartItems();
                    itemToDelete = item;
                    isDeleted = true;
                    conn.setAutoCommit(false);
                    break;
                } else {
                    PreparedStatement stmtCartItem = conn.prepareStatement("update cart_items set quantity = ? where productId =  ?", Statement.RETURN_GENERATED_KEYS);
                    stmtCartItem.setInt(1, item.getQuantity());
                    stmtCartItem.setInt(2, item.getProductId());
                    int affectedCartItemRows = stmtCartItem.executeUpdate();
                    if (affectedCartItemRows == 0) { // lỗi
                        throw new Error("Insert cart item fails.");
                    }
                    totalPrice += item.getQuantity() * item.getUnitPrice();
                }
            }

            if (isDeleted) {
                cartItemsAfterDelete.remove(itemToDelete);
                shoppingCart.setCartItems(cartItemsAfterDelete);
            }
            PreparedStatement stmtUpdateTotalPrice = conn.prepareStatement("update shopping_carts set totalPrice = ? where id = ?", Statement.RETURN_GENERATED_KEYS);
            stmtUpdateTotalPrice.setDouble(1, totalPrice);
            stmtUpdateTotalPrice.setInt(2, shoppingCart.getId());
            stmtUpdateTotalPrice.executeUpdate();
            shoppingCart.setTotalPrice(totalPrice);
            conn.commit(); // lưu tất cả vào db.
        } catch (Exception ex) {
            ex.printStackTrace();
            shoppingCart = null;
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return shoppingCart;
    }

    public boolean remove(int productId) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            PreparedStatement stmtDeleteCartItem = conn.prepareStatement("delete from cart_items where productId = ?");
            stmtDeleteCartItem.setInt(1, productId);
            int affectedCartItemRows = stmtDeleteCartItem.executeUpdate();
            if (affectedCartItemRows <= 0) {
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return false;
    }

    public boolean clear(int shoppingCartId) throws SQLException {
        conn.setAutoCommit(false);// begin transaction
        try {
            PreparedStatement stmtDeleteCartItem = conn.prepareStatement("delete from cart_items where shoppingCartId = ?");
            stmtDeleteCartItem.setInt(1, shoppingCartId);
            int affectedCartItemRows = stmtDeleteCartItem.executeUpdate();
            if (affectedCartItemRows <= 0) {
                return false;
            }
            PreparedStatement stmtDelete = conn.prepareStatement("delete from shopping_carts where id = ?");
            stmtDelete.setInt(1, shoppingCartId);
            int affectedRows = stmtDelete.executeUpdate();
            if (affectedRows <= 0) {
                return false;
            }
            conn.commit();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            conn.rollback();
        } finally {
            conn.setAutoCommit(true); // trả trạng thái auto commit default.
        }
        return false;
    }

    public boolean checkShoppingCartExisting(ShoppingCart shoppingCart) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("select * from shopping_carts where id = ?", Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, shoppingCart.getId());
        ResultSet resultSet = stmt.executeQuery();
        if (resultSet.next()) {
            return true;
        }
        return false;
    }
}
