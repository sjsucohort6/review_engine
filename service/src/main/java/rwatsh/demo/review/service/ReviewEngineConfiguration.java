package rwatsh.demo.review.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author rwatsh on 10/9/17.
 */
public class ReviewEngineConfiguration extends Configuration {
    @Valid
    @NotNull
    private DBConfig dbConfig = new DBConfig();

    @JsonProperty("swagger")
    public SwaggerBundleConfiguration swaggerBundleConfiguration;

    @JsonProperty("database")
    public DBConfig getDbConfig() {
        return dbConfig;
    }

    @JsonProperty("database")
    public void setDbConfig(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
    }
}
