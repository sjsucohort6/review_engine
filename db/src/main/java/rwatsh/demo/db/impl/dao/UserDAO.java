package rwatsh.demo.db.impl.dao;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;
import rwatsh.demo.db.api.DBException;
import rwatsh.demo.db.api.mongodb.BaseDAOImpl;
import rwatsh.demo.db.impl.model.User;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * @author rwatsh on 10/9/17.
 */
public class UserDAO extends BaseDAOImpl<User> {
    public UserDAO(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
    }

    @Override
    public void update(List<User> entityList) throws DBException {
        throw new NotImplementedException();
    }
}
