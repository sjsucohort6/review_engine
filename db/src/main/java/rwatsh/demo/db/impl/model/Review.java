package rwatsh.demo.db.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Reference;
import rwatsh.demo.db.api.model.IModel;
import rwatsh.demo.db.api.model.Validable;
import rwatsh.demo.db.api.model.ValidationException;

/**
 * @author rwatsh on 10/9/17.
 */
@Embedded
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Review extends Validable implements IModel {
    @Reference
    private User user;

    @JsonProperty
    private String comments;

    @JsonProperty
    private Integer score;

    @Override
    public boolean isValid() throws ValidationException {
        return isReqd(comments) && isReqd(score);
    }
}
