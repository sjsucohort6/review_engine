
package rwatsh.demo.review.service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import io.dropwizard.lifecycle.Managed;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import org.hibernate.validator.constraints.NotEmpty;
import rwatsh.demo.db.api.DBClient;
import rwatsh.demo.db.api.DBFactory;
import rwatsh.demo.db.impl.DatabaseModule;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

/**
 * @author rwatsh on 3/26/17.
 */
@Log
public class DBConfig {
    @Inject
    DBFactory dbFactory;

    @NotEmpty
    @JsonProperty
    private String server = "localhost";

    @Min(1)
    @Max(65535)
    @JsonProperty
    private int port = 27017;

    @NotEmpty
    @JsonProperty
    private String dbName = "review_engine_db";
    private DBClient dbClient;

    @JsonIgnore
    public DBClient getDbClient() {
        return dbClient;
    }

    @JsonProperty
    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @JsonProperty
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public DBClient build(Environment environment) {
        String dbServer = System.getenv("DB");
        if (dbServer != null) {
            server = dbServer;
        }
        dbClient = dbFactory.create(server, port, dbName);
        log.info("Connected to db");
        environment.lifecycle().manage(new Managed() {
            @Override
            public void start() throws Exception {
                dbClient.useDB(dbName);
            }

            @Override
            public void stop() throws Exception {
                dbClient.close();
            }
        });
        return dbClient;
    }

    public DBConfig() {
        try {
            Thread.sleep(20000);
        } catch(Exception e) {

        }
        Module module = new DatabaseModule();
        Guice.createInjector(module).injectMembers(this);

    }
}
