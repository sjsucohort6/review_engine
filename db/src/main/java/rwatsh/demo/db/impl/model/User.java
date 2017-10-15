package rwatsh.demo.db.impl.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import rwatsh.demo.db.api.model.IModel;
import rwatsh.demo.db.api.model.Validable;
import rwatsh.demo.db.api.model.ValidationException;

@Entity(value = "users" , noClassnameStored = true, concern = "SAFE")
@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class User extends Validable implements IModel {
    /*
     * User email ID
     */
    @Id
    @JsonProperty
    private String email;
    /*
     * User name.
     */
    @JsonProperty
    private String name;
    /*
     * User password.
     */
    @JsonProperty
    private String password;

    @Override
    public boolean isValid() throws ValidationException {
        return isReqd(email) &&
                isReqd(name) &&
                isReqd(password);
    }
}
