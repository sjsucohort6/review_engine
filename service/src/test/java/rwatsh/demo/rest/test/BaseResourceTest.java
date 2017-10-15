/*
 * Copyright (c) 2015 San Jose State University.
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

package rwatsh.demo.rest.test;

import lombok.extern.java.Log;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import rwatsh.demo.db.impl.model.User;
import rwatsh.demo.utils.EndpointUtils;
import rwatsh.demo.utils.JsonUtils;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author rwatsh on 10/1/15.
 */
@Log
public abstract class BaseResourceTest {

    protected static final String BASE_URI = "http://localhost:8080";
    protected Client client;
    protected WebTarget webTarget;

    @BeforeClass
    public void setUp() throws Exception {
        client = ClientBuilder.newClient();
        webTarget = client.target(BASE_URI);
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (client != null) {
            client.close();
        }
    }

    /*
     * Abstract test methods to be implemented by concrete test classes.
     */
    @Test
    abstract public void testAdd() throws Exception;

    @Test
    abstract public void testRemove() throws Exception;

    @Test
    abstract public void testUpdate() throws Exception;

    @Test
    abstract public void testFetch() throws Exception;

    @Test
    abstract public void testFetchAll() throws Exception;

    /**
     * Common methods across test classes...
     *
     */

    private static final String USER_RESOURCE_URI = EndpointUtils.ENDPOINT_ROOT + "/users";

    public User createUser(User user) throws Exception {
        // Convert to string
        String jsonStr = JsonUtils.convertObjectToJson(user);
        log.info(jsonStr);
        Response response = webTarget.path(USER_RESOURCE_URI)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonStr));
        //String us = response.readEntity(String.class);
        User u = JsonUtils.convertJsonToObject(response, User.class);
        log.info(u.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.CREATED.getStatusCode());
        return u;
    }

    public User getTestUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setName("Watsh Rajneesh");
        user.setPassword("pass");
        return user;
    }

}
