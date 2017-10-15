package rwatsh.demo.db.impl.dao;

import com.mongodb.MongoClient;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import rwatsh.demo.db.api.DBException;
import rwatsh.demo.db.api.mongodb.BaseDAOImpl;
import rwatsh.demo.db.impl.model.Product;
import rwatsh.demo.db.impl.model.Review;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rwatsh on 10/9/17.
 */
public class ProductDAO extends BaseDAOImpl<Product> {
    public ProductDAO(MongoClient mongoClient, Morphia morphia, String dbName) {
        super(mongoClient, morphia, dbName);
    }

    @Override
    public void update(List<Product> entityList) throws DBException {
        for (Product product: entityList) {
            List<Product> products = fetchById(new ArrayList<String>() {{add(product.getId());}});
            Product existingProduct = null;
            if (products != null && !products.isEmpty()) {
                existingProduct = products.get(0);
            }
            if (existingProduct != null) {
                UpdateOperations<Product> ops = this.createUpdateOperations();
                if (product.getName() != null) {
                    ops.set("name", product.getName());
                }
                if (product.getDescription() != null) {
                    ops.set("description", product.getDescription());
                }

                if (product.getReviews() != null) {
                    List<Review> existingProductReviews = existingProduct.getReviews();
                    Review curReview = product.getReviews().get(0);
                    existingProductReviews.add(curReview);
                    ops.set("reviews", existingProductReviews);
                    int newTotalScore = curReview.getScore() + existingProduct.getTotalScore();
                    int newReviewsCount = existingProduct.getReviewsCount() + 1;
                    double newAggregatedScore = newTotalScore/newReviewsCount;
                    ops.set("aggregatedScore", newAggregatedScore);
                    ops.set("totalScore", newTotalScore);
                    ops.set("reviewsCount", newReviewsCount);
                }

                Query<Product> updateQuery = this.createQuery().field(Mapper.ID_KEY).equal(product.getId());
                this.update(updateQuery, ops);
            }
        }
    }
}
