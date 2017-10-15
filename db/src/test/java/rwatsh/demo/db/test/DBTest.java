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

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.testng.Assert;
import org.testng.annotations.*;
import rwatsh.demo.db.api.BaseDAO;
import rwatsh.demo.db.api.DBClient;
import rwatsh.demo.db.api.DBException;
import rwatsh.demo.db.api.DBFactory;
import rwatsh.demo.db.impl.DatabaseModule;
import rwatsh.demo.db.impl.dao.UserDAO;
import rwatsh.demo.db.impl.model.User;

import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public abstract class DBTest<T extends BaseDAO, S> {
    @Inject
    private DBFactory dbFactory;
    protected T dao;
    private Class<T> tClass;

    public String dbName;
    private static final Logger log = Logger.getLogger(DBTest.class.getName());
    protected static DBClient client;
    private long startTime;

    public DBTest() {
        Module module = new DatabaseModule();
        Guice.createInjector(module).injectMembers(this);

    }

    @BeforeClass
    @Parameters({"server", "port", "dbName"})
    public void setUp(@Optional("localhost") String server,
                      @Optional("27017") String port,
                      @Optional("review_engine_db") String dbName) throws Exception {
        client = dbFactory.create(server, Integer.parseInt(port), dbName);

        this.dbName = dbName;
        /*
         * Use reflection to infer the class for T type.
         */
        this.tClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        dao = (T) client.getDAO(tClass);
        client.dropDB(this.dbName);
    }

    @AfterClass
    public void tearDown() throws Exception {
        client.close();
    }

    @BeforeMethod
    public void createDB() {
        client.useDB(dbName);
        log.info("********************");
        startTime = System.currentTimeMillis();
    }

    @AfterMethod
    public void dropDB() {
        //client.dropDB(dbName);
        long endTime = System.currentTimeMillis();
        long diff = endTime - startTime;
        log.info(MessageFormat.format("********* Time taken: {0} ms", diff));
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

    /**
     * Common test methods shared across test sub classes.
     *
     * Add below...
     *
     */

    protected List<String> testCreateUser() throws DBException {
        User user = createUser();

        UserDAO userDAO = (UserDAO) client.getDAO(UserDAO.class);
        List<String> insertedIds = userDAO.add(new ArrayList<User>() {{
            add(user);
        }});
        List<User> providers = userDAO.fetchById(insertedIds);
        Assert.assertNotNull(providers);
        log.info("User created: " + providers);
        return insertedIds;
    }

    private User createUser() {
        User user = new User();
        user.setEmail("me@myself.com");
        user.setName("John Doe");
        user.setPassword("pass");
        return user;
    }
}
