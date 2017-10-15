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

package rwatsh.demo.db.api.mongodb;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.mongodb.MongoClient;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import rwatsh.demo.db.api.DBClient;

import java.text.MessageFormat;

/**
 * @author rwatsh on 3/26/17.
 */
public abstract class MongoDBClientBase implements DBClient {
    protected final String server;
    protected final int port;
    protected final String dbName;
    protected Morphia morphia = null;
    protected MongoClient mongoClient;
    protected Datastore morphiaDatastore;

    /**
     * Constructs a MongoDB client instance.
     *
     * This is private so it can only be instantiated via DI (using Guice).
     *
     * @param server    server hostname or ip
     * @param port      port number for mongodb service
     * @param dbName    name of db to use
     */
    @Inject
    public MongoDBClientBase(@Assisted("server") String server, @Assisted("port") int port, @Assisted("dbName") String dbName) {
        this.server = server;
        this.port = port;
        this.dbName = dbName;
        mongoClient = new MongoClient(server, port);

        morphia = new Morphia();
        //morphia.mapPackageFromClass(Service.class);
        morphiaDatastore = morphia.createDatastore(mongoClient, dbName);
        morphiaDatastore.ensureIndexes();
    }

    @Override
    public void dropDB(String dbName) {
        morphiaDatastore.getDB().dropDatabase();
    }

    @Override
    public void useDB(String dbName) {
        morphiaDatastore = morphia.createDatastore(mongoClient, dbName);
        morphiaDatastore.ensureIndexes();
    }

    @Override
    public boolean checkHealth() {
        try {
            mongoClient.listDatabaseNames();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    @Override
    public String getConnectString() {
        return MessageFormat.format("DB Connect Info: server [{0}], port [{1}], dbName [{2}]", server, port, dbName);
    }


    @Override
    public Morphia getMorphia() {
        return morphia;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws Exception {
        mongoClient.close();
    }

    @Override
    public String toString() {
        return "MongoDBClient{" +
                "server='" + server + '\'' +
                ", port=" + port +
                ", dbName='" + dbName + '\'' +
                '}';
    }
}
