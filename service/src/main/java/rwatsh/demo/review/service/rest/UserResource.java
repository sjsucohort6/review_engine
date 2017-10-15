/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package rwatsh.demo.review.service.rest;

import io.dropwizard.servlets.assets.ResourceNotFoundException;
import io.swagger.annotations.*;
import lombok.extern.java.Log;
import rwatsh.demo.db.api.DBClient;
import rwatsh.demo.db.impl.model.User;
import rwatsh.demo.utils.EndpointUtils;
import rwatsh.demo.utils.InternalErrorException;
import rwatsh.demo.utils.JsonUtils;

import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author rwatsh
 */
@Path(EndpointUtils.ENDPOINT_ROOT + "/users")
@Produces(MediaType.APPLICATION_JSON)
@Api(value = EndpointUtils.ENDPOINT_ROOT + "/users", description = "Operations about users")
@Log
public class UserResource extends BaseResource<User>{
    public UserResource(DBClient dbClient) {
        super(dbClient);
    }

    /**
     * Create the resource.
     *
     * @param userJson
     * @return
     */
    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add a new user")
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Bad request") })
    public Response create(@Valid String userJson) {
        try {
            User user = JsonUtils.convertJsonToObject(userJson, User.class);
            List<User> userList = new ArrayList<>();
            userList.add(user);
            userDAO.add(userList);
            URI uri = UriBuilder.fromResource(UserResource.class).build(user.getEmail());
            return Response.created(uri)
                    .entity(Entity.json(user))
                    .build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in adding user", e);
            throw new BadRequestException(e);
        }
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Finds all users by filter",
            notes = "users can be filtered by any attribute",
            response = User.class,
            responseContainer = "List")
    public List<User> list(@ApiParam(value = "A filter query string", defaultValue = "empty string") @QueryParam("filter") String filter) throws InternalErrorException {
        try {
            List<String> userIds = new ArrayList<>();
            List<User> userList = userDAO.fetchById(userIds);
            return userList;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    @GET
    @Path("/{emailId}")
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(
            value = "Find product by ID",
            notes = "Returns a product for specified ID in the path",
            response = User.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "User not found") })
    public User retrieve(@PathParam("emailId") String emailId) throws ResourceNotFoundException, InternalErrorException {
        try {
            List<User> users = userDAO.fetchById(new ArrayList<String>() {{
                add(emailId);
            }});
            if (users != null && !users.isEmpty()) {
                return users.get(0);
            } else {
                throw new NotFoundException("User not found: " + emailId); // 404
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting user by email ID [" + emailId +  "]", e);
            throw new BadRequestException(e); // 400
        }
    }

    @Override
    public User update(String id, String entity) throws ResourceNotFoundException, InternalErrorException, IOException {
        return null;
    }


    @Override
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{emailId}")
    @ApiOperation(
            value = "Remove User by ID",
            notes = "Returns deleted user for specified ID in the path",
            response = Response.class)
    @ApiResponses(value = { @ApiResponse(code = 404, message = "User not found") })
    public Response delete(@ApiParam(value = "ID of user that needs to be deleted", required = true)
                               @PathParam("emailId") String id) throws ResourceNotFoundException, InternalErrorException {
        try {
            userDAO.deleteById(id);
            return Response.ok().build();
        } catch(Exception e) {
            throw new InternalErrorException();
        }
    }
}
