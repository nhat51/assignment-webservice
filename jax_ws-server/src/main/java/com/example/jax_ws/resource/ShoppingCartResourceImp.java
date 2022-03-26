package com.example.jax_ws.resource;

import com.example.jax_ws.entity.Product;
import com.example.jax_ws.entity.ShoppingCart;
import com.example.jax_ws.model.ProductModel;
import com.example.jax_ws.model.ShoppingCartModel;
import com.example.jax_ws.model.ShoppingCartModelImp;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

@Path("/carts")
public class ShoppingCartResourceImp implements ShoppingCartResource {

    private ShoppingCartModel shoppingCartModel;
    private ProductModel productModel;

    public ShoppingCartResourceImp() {
        this.shoppingCartModel = new ShoppingCartModelImp();
        this.productModel = new ProductModel();
    }

    @GET
    @Path("/create")
    @Produces(MediaType.APPLICATION_JSON)
    public Response create(@HeaderParam("Authorization") int userId) {
        try {
            ShoppingCart shoppingCart = this.shoppingCartModel.create(userId);
            return Response.status(Response.Status.OK).entity(shoppingCart).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@HeaderParam("Authorization") int userId) {
        try {
            ShoppingCart shoppingCart = this.shoppingCartModel.get(userId);
            return Response.status(Response.Status.OK).entity(shoppingCart).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    public Response add(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ShoppingCart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.shoppingCartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.add(product, quantity);
        try {
            shoppingCart = this.shoppingCartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }

    @GET
    @Path("/deduct")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deduct(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ShoppingCart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.shoppingCartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.deduct(product, quantity);
        try {
            shoppingCart = this.shoppingCartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }


    @GET
    @Path("/update")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response update(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId, @QueryParam("quantity") int quantity) {
        // kiểm tra số lượng
        if (quantity <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        ShoppingCart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.shoppingCartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }
        if (shoppingCart == null) {
            shoppingCart = new ShoppingCart();
            shoppingCart.setUserId(userId);
        }
        // do something.
        shoppingCart.update(product, quantity);
        try {
            shoppingCart = this.shoppingCartModel.save(shoppingCart);
        } catch (SQLException e) {
            e.printStackTrace();
            shoppingCart = null;
        }
        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
        return Response.status(Response.Status.CREATED).entity(shoppingCart).build();
    }

    @GET
    @Path("/remove")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response remove(@HeaderParam("Authorization") int userId, @QueryParam("productId") int productId) {
        // kiểm tra sản phẩm.
        Product product = null;
        try {
            product = this.productModel.findById(productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        if (product == null) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        ShoppingCart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.shoppingCartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }

        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }

        boolean isDeletedSuccess = false;

        try {
            isDeletedSuccess = this.shoppingCartModel.remove(productId);
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(isDeletedSuccess).build();
        }

        return Response.status(Response.Status.CREATED).entity(isDeletedSuccess).build();
    }

    @GET
    @Path("/clear")
    @Produces(MediaType.APPLICATION_JSON)
    @Override
    public Response clear(@HeaderParam("Authorization") int userId) {
        ShoppingCart shoppingCart = null;
        try {
            // check shopping cart trong db theo id người dùng.
            shoppingCart = this.shoppingCartModel.get(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            // trường hợp không có thì tạo mới.
        }

        if (shoppingCart == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }

        boolean isDeletedSuccess = false;

        try {
            isDeletedSuccess = this.shoppingCartModel.clear(shoppingCart.getId());
        } catch (SQLException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(isDeletedSuccess).build();
        }

        return Response.status(Response.Status.CREATED).entity(isDeletedSuccess).build();
    }
}

