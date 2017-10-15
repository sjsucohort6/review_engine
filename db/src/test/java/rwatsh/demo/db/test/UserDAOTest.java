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

package rwatsh.demo.db.test;

import org.testng.Assert;
import rwatsh.demo.db.impl.dao.UserDAO;
import rwatsh.demo.db.impl.model.User;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author rwatsh
 */
public class UserDAOTest extends DBTest<UserDAO, User> {
    private static final Logger log = Logger.getLogger(UserDAOTest.class.getName());

    @Override
    public void testAdd() throws Exception {
        testCreateUser();
    }

    @Override
    public void testRemove() throws Exception {
        List<String> insertedIds = testCreateUser();
        Assert.assertNotNull(insertedIds);
        long countRemovedEntries = dao.remove(insertedIds);
        Assert.assertTrue(countRemovedEntries > 0, "Failed to delete any service");
    }

    @Override
    public void testUpdate() throws Exception {

    }

    @Override
    public void testFetch() throws Exception {
        List<String> insertedIds = testCreateUser();
        Assert.assertNotNull(insertedIds);
        List<User> users = dao.fetchById(insertedIds);
        Assert.assertNotNull(users);
    }
}
