package com.example.jax_ws.resource;

import com.example.jax_ws.entity.Product;
import com.example.jax_ws.model.ProductModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;

@Path("/products")
public class ProductResource {

    private ProductModel productModel;

    public ProductResource() {
        this.productModel = new ProductModel();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findAll() {
        try {
            return Response.status(Response.Status.OK).entity(productModel.findAll()).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.OK).entity(new ArrayList<>()).build();
        }
    }

    @GET()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") int id) {
        try {
            return Response.status(Response.Status.OK).entity(productModel.findById(id)).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @POST()
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response save(Product product) {
        try {
            Product savedProduct = productModel.save(product);
            return Response.status(Response.Status.CREATED).entity(savedProduct).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @PUT()
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("id") int id, Product product) {
        try {
            Product foundProduct = productModel.findById(id);
            if (foundProduct == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            Product updatedProduct = productModel.update(id, product);
            return Response.status(Response.Status.OK).entity(updatedProduct).build();
        } catch (SQLException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
    }

    @DELETE()
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("id") int id) {
        try {
            Product foundProduct = productModel.findById(id);
            if (foundProduct == null) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }
            boolean isDeleted = productModel.delete(id);
            if (isDeleted) {
                return Response.status(Response.Status.OK).entity(isDeleted).build();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }
}

