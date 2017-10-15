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

package rwatsh.demo.db.impl.mongodb;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import rwatsh.demo.db.api.BaseDAO;
import rwatsh.demo.db.api.mongodb.MongoDBClientBase;
import rwatsh.demo.db.impl.dao.ProductDAO;
import rwatsh.demo.db.impl.dao.UserDAO;
import rwatsh.demo.db.impl.model.Product;


/**
 * @author rwatsh
 */
public class MongoDBClient extends MongoDBClientBase {
    private ProductDAO productDAO;
    private UserDAO userDAO;

    /**
     * Constructs a MongoDB client instance.
     * <p>
     * This is private so it can only be instantiated via DI (using Guice).
     *
     * @param server server hostname or ip
     * @param port   port number for mongodb service
     * @param dbName name of db to use
     */
    @Inject
    private MongoDBClient(@Assisted("server") String server, @Assisted("port") int port, @Assisted("dbName") String dbName) {
        super(server, port, dbName);
        morphia.mapPackageFromClass(Product.class);
        productDAO = new ProductDAO(mongoClient, morphia, dbName);
        userDAO = new UserDAO(mongoClient, morphia, dbName);
    }

    @Override
    public Object getDAO(Class<? extends BaseDAO> clazz) {
        if (clazz != null) {
            if (clazz.getTypeName().equalsIgnoreCase(ProductDAO.class.getTypeName())) {
                return productDAO;
            }
            if (clazz.getTypeName().equalsIgnoreCase(UserDAO.class.getTypeName())) {
                return userDAO;
            }
        }
        return null;
    }
}
