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
import rwatsh.demo.db.impl.model.User;
import rwatsh.demo.utils.EndpointUtils;
import rwatsh.demo.utils.JsonUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author rwatsh
 */
@Log
public class UserResourceTest extends BaseResourceTest {

    public static final String RESOURCE_URI = EndpointUtils.ENDPOINT_ROOT + "/users";

    @Override
    public void testAdd() throws Exception {
        User user = createUser(getTestUser("rwatsh-add@test.com"));
        Assert.assertNotNull(user);
    }

    @Override
    public void testRemove() throws Exception {
        User user = createUser(getTestUser("rwatsh-del@test.com"));
        log.info("Created user: " + user);
        Response response = webTarget.path(RESOURCE_URI)
                .path("/" + user.getEmail())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("User deleted: " + user);
    }

    @Override
    public void testUpdate() throws Exception {
        /*User user = createUser(getTestUser("rwatsh-update@test.com"));
        log.info("Created user: " + user);
        user.setName("test_modified_name");
        String jsonStr = JsonUtils.convertObjectToJson(user);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .path("/" + user.getEmail())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .put(Entity.json(jsonStr));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("User updated: " + user);
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        User updatedUser = JsonUtils.convertJsonToObject(respStr, User.class);
        log.info("Updated user: " + updatedUser);*/
        // Not implemented on DB DAO
    }

    private List<User> getUsers() throws Exception {
        Response response = webTarget.path(RESOURCE_URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        List<User> userList = JsonUtils.convertJsonArrayToList(respStr, User.class);
        log.info(userList.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        return userList;
    }

    @Override
    public void testFetch() throws Exception {
        List<User> userList = getUsers();
        if (userList != null && !userList.isEmpty()) {
            String id = userList.get(0).getEmail();

            Response response = webTarget.path(RESOURCE_URI)
                    .path(id)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            log.info(response.toString());
            String respStr = response.readEntity(String.class);
            /*userList = JsonUtils.convertJsonArrayToList(respStr, User.class);
            log.info(userList.toString());*/
            User user = JsonUtils.convertJsonToObject(respStr, User.class);
            log.info(user.toString());
            Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        }
    }

    @Override
    public void testFetchAll() throws Exception {
        List<User> userList  = getUsers();
        Assert.assertNotNull(userList);
    }
}
