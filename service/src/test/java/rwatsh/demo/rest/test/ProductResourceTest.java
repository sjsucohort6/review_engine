package rwatsh.demo.rest.test;

import lombok.extern.java.Log;
import org.testng.Assert;
import org.testng.annotations.Test;
import rwatsh.demo.db.impl.model.Product;
import rwatsh.demo.db.impl.model.Review;
import rwatsh.demo.db.impl.model.User;
import rwatsh.demo.review.service.rest.ReviewHelper;
import rwatsh.demo.utils.EndpointUtils;
import rwatsh.demo.utils.JsonUtils;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * @author rwatsh on 10/9/17.
 */
@Log
public class ProductResourceTest extends BaseResourceTest  {
    public static final String RESOURCE_URI = EndpointUtils.ENDPOINT_ROOT + "/products";

    private Product createProduct(Product product) throws Exception {
        // Convert to string
        String jsonStr = JsonUtils.convertObjectToJson(product);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonStr));
        //String us = response.readEntity(String.class);
        Product p = JsonUtils.convertJsonToObject(response, Product.class);
        log.info(p.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.CREATED.getStatusCode());
        return p;
    }

    private Product getTestProduct(String productId) {
        Product product = new Product();
        product.setId(productId);
        product.setName("Test product");
        product.setDescription("Very testy product");
        return product;
    }

    @Override
    public void testAdd() throws Exception {
        Product product = createProduct(getTestProduct("TestProduct-Add"));
        Assert.assertNotNull(product);
    }

    @Override
    public void testRemove() throws Exception {
        Product product = createProduct(getTestProduct("TestProduct-Del"));
        log.info("Created product: " + product);
        Response response = webTarget.path(RESOURCE_URI)
                .path("/" + product.getId())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .delete();
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("Product deleted: " + product);
    }

    @Override
    public void testUpdate() throws Exception {
        /*Product product = createProduct(getTestProduct("TestProduct-Update"));
        log.info("Created product: " + product);
        product.setName("test_modified_name");
        String jsonStr = JsonUtils.convertObjectToJson(product);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .path(product.getId())
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .put(Entity.json(jsonStr));
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        log.info("Product updated: " + product);
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        Product updatedProduct = JsonUtils.convertJsonToObject(respStr, Product.class);
        log.info("Updated product: " + updatedProduct);*/
        // Not implemented in ProductResources
    }

    @Test
    public List<Product> testGetProducts() throws Exception {
        Response response = webTarget.path(RESOURCE_URI)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        log.info(response.toString());
        String respStr = response.readEntity(String.class);

        List<Product> productList = JsonUtils.convertJsonArrayToList(respStr, Product.class);
        log.info(productList.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        return productList;
    }

    @Override
    public void testFetch() throws Exception {
        List<Product> productList = testGetProducts();
        if (productList != null && !productList.isEmpty()) {
            String id = productList.get(0).getId();

            Response response = webTarget.path(RESOURCE_URI)
                    .path(id)
                    .request(MediaType.APPLICATION_JSON_TYPE)
                    .get();
            log.info(response.toString());
            String respStr = response.readEntity(String.class);
            /*productList = JsonUtils.convertJsonArrayToList(respStr, User.class);
            log.info(productList.toString());*/
            Product product = JsonUtils.convertJsonToObject(respStr, Product.class);
            log.info(product.toString());
            Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        }
    }

    @Override
    public void testFetchAll() throws Exception {
        List<Product> productList  = testGetProducts();
        Assert.assertNotNull(productList);
    }

    private Product createReview(Product product, ReviewHelper review) throws Exception {
        // Convert to string
        String jsonStr = JsonUtils.convertObjectToJson(review);
        log.info(jsonStr);
        Response response = webTarget.path(RESOURCE_URI)
                .path(product.getId())
                .path("reviews")
                .request()
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .header("content-type", MediaType.APPLICATION_JSON)
                .post(Entity.json(jsonStr));
        //String us = response.readEntity(String.class);
        Product p = JsonUtils.convertJsonToObject(response, Product.class);
        log.info(p.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
        return p;
    }

    private ReviewHelper getTestReview(String email) {
        ReviewHelper review = new ReviewHelper();
        review.setComments("test comment");
        review.setScore(3);
        review.setUser(email);
        return review;
    }

    @Test
    public void testAddReview() throws Exception {
        Product product = createProduct(getTestProduct("TestProduct-AddReview"));
        User user = createUser(getTestUser("rwatsh-addReview@test.com"));
        ReviewHelper review = getTestReview(user.getEmail());
        Product newProduct = createReview(product, review);
        Assert.assertNotNull(newProduct);
    }

    @Test
    public void testFetchReviewsForProduct() throws Exception {
        Product product = createProduct(getTestProduct("TestProduct-FetchReview"));
        User user = createUser(getTestUser("rwatsh-fetchReview@test.com"));
        ReviewHelper review = getTestReview(user.getEmail());
        Product newProduct = createReview(product, review);
        Response response = webTarget.path(RESOURCE_URI)
                .path(product.getId())
                .path("reviews")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get();
        log.info(response.toString());
        String respStr = response.readEntity(String.class);
        List<Review> reviewList = JsonUtils.convertJsonArrayToList(respStr, Review.class);
        log.info(reviewList.toString());
        Assert.assertTrue(response.getStatus() == Response.Status.OK.getStatusCode());
    }
}
