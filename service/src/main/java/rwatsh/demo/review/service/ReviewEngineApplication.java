package rwatsh.demo.review.service;

import io.dropwizard.Application;
import io.dropwizard.setup.Environment;
import lombok.extern.java.Log;
import rwatsh.demo.db.api.DBClient;
import rwatsh.demo.review.service.health.DBHealthCheck;
import rwatsh.demo.review.service.rest.ProductResource;
import rwatsh.demo.review.service.rest.UserResource;
import rwatsh.demo.utils.EndpointUtils;

/**
 * @author rwatsh on 10/9/17.
 */
@Log
public class ReviewEngineApplication extends Application<ReviewEngineConfiguration> {
    private DBClient dbClient;

    public static void main(String[] args) throws Exception {
        new ReviewEngineApplication().run(args);
    }


    @Override
    public void run(ReviewEngineConfiguration reviewEngineConfiguration, Environment environment) throws Exception {
        log.info("Initializing db client");
        dbClient = reviewEngineConfiguration.getDbConfig().build(environment);
        log.info("db connect string: " + dbClient.getConnectString());
        log.info("Connected to db: " + dbClient.getConnectString());

        environment.healthChecks().register("database", new DBHealthCheck(dbClient));
        /*
         * Register resources with jersey.
         */
        final UserResource userResource = new UserResource(dbClient);
        final ProductResource productResource = new ProductResource(dbClient);

        /*
         * Setup jersey environment.
         */
        environment.jersey().setUrlPattern(EndpointUtils.ENDPOINT_ROOT + "/*");
        environment.jersey().register(userResource);
        environment.jersey().register(productResource);
        log.info("Done with all initializations for review engine service");
    }
}
