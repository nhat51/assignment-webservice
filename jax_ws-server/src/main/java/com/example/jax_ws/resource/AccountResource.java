package com.example.jax_ws.resource;

import com.example.jax_ws.dto.AccountDto;
import com.example.jax_ws.entity.Account;
import com.example.jax_ws.entity.ShoppingCart;
import com.example.jax_ws.model.AccountModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;

public class AccountResource {
    private AccountModel accountModel;

    public AccountResource() {
        this.accountModel = new AccountModel();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@HeaderParam("Authorization") int userId) {
        try {
            return Response.status(Response.Status.OK).entity(this.accountModel.findById(userId)).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ShoppingCart()).build();
        }
    }

    @GET
    @Path("/add")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response add(AccountDto accountDto) {
        Account account = null;
        try {
            account = this.accountModel.findByEmail(accountDto.getEmail());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (account == null) {
            account = new Account();
            account.setEmail(accountDto.getEmail());
            account.setPassword(accountDto.getPassword());
            account.setFirstName(accountDto.getFirstName());
            account.setLastName(accountDto.getLastName());
            try {
                account = this.accountModel.save(account);
            } catch (SQLException e) {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new Account()).build();
            }
        }
        return Response.status(Response.Status.CREATED).entity(account).build();
    }

}
