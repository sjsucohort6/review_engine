package rwatsh.demo.db.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import rwatsh.demo.db.api.model.IModel;
import rwatsh.demo.db.api.model.Validable;
import rwatsh.demo.db.api.model.ValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwatsh on 10/9/17.
 */
@Entity(value = "products" , noClassnameStored = true, concern = "SAFE")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Product extends Validable implements IModel {
    @Id
    @JsonProperty
    private String id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private Double aggregatedScore;

    @JsonProperty
    private Integer totalScore;

    @JsonProperty
    private Integer reviewsCount;

    @Embedded
    private List<Review> reviews = new ArrayList<>();

    public Product() {
        totalScore = 0;
        reviewsCount = 0;
        aggregatedScore = 0.0;
    }

    @Override
    public boolean isValid() throws ValidationException {
        return isReqd(id) &&
                isReqd(name) &&
                isReqd(description);
    }
}
