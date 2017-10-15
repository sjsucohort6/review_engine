package rwatsh.demo.review.service.rest;

import io.dropwizard.servlets.assets.ResourceNotFoundException;
import lombok.extern.java.Log;
import rwatsh.demo.db.api.DBClient;
import rwatsh.demo.db.api.DBException;
import rwatsh.demo.db.impl.model.Product;
import rwatsh.demo.db.impl.model.Review;
import rwatsh.demo.db.impl.model.User;
import rwatsh.demo.utils.InternalErrorException;
import rwatsh.demo.utils.JsonUtils;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * @author rwatsh on 10/9/17.
 */
@Log
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource extends BaseResource<Product> {
    public ProductResource(DBClient dbClient) {
        super(dbClient);
    }

    /**
     * Create the resource.
     *
     * @param productJson
     * @return
     */
    @Override
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(String productJson) {
        try {
            Product product = JsonUtils.convertJsonToObject(productJson, Product.class);
            List<Product> productList = new ArrayList<>();
            productList.add(product);
            productDAO.add(productList);
            URI uri = UriBuilder.fromResource(UserResource.class).build(product.getId());
            return Response.created(uri)
                    .entity(Entity.json(product))
                    .build();
        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in adding product", e);
            throw new BadRequestException(e);
        }
    }

    /**
     * Add review to a product
     *
     * @param reviewJson
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}/reviews")
    public Response addReview(@PathParam("id") String id, String reviewJson) {
        String error = "";
        try {
            List<Product> products = productDAO.fetchById(new ArrayList<String>() {{
                add(id);
            }});
            if (products != null && !products.isEmpty()) {
                Product product = products.get(0);
                ReviewHelper reviewHelper = JsonUtils.convertJsonToObject(reviewJson, ReviewHelper.class);
                String userEmail = reviewHelper.getUser();
                List<User> users = userDAO.fetchById(new ArrayList<String>() {{
                    add(userEmail);
                }});
                if (users != null && !users.isEmpty()) {
                    // user is valid, add review to product
                    Review review = new Review();
                    review.setUser(users.get(0));
                    review.setComments(reviewHelper.getComments());
                    review.setScore(reviewHelper.getScore());
                    product.setReviews(new ArrayList<Review>() {{
                        add(review);
                    }});
                    productDAO.update(new ArrayList<Product>() {{
                        add(product);
                    }});

                    URI uri = UriBuilder.fromResource(UserResource.class).build(product.getId());
                    return Response.ok().entity(Entity.json(product))
                            .build();
                } else {
                    error = "User not found: " + userEmail;
                    log.severe(error);
                }

            } else {
                error = "No such product with id:" + id;
                log.severe(error);
            }

        } catch (Exception e) {
            error = "Error in adding review for product: " + id;
            log.log(Level.SEVERE, error, e);
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(Entity.text(error)).build();
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> list(String filter) throws InternalErrorException {
        try {
            List<String> productIds = new ArrayList<>();
            List<Product> productList = productDAO.fetchById(productIds);
            return productList;
        } catch (Exception e) {
            throw new InternalErrorException(e);
        }
    }

    @Override
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Product retrieve(@PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        try {
            List<Product> products = productDAO.fetchById(new ArrayList<String>() {{
                add(id);
            }});
            if (products != null && !products.isEmpty()) {
                return products.get(0);
            } else {
                return null;
            }

        } catch (Exception e) {
            log.log(Level.SEVERE, "Error in getting product by ID [" + id +  "]", e);
            throw new BadRequestException(e);
        }
    }

    /**
     * Get all reviews for this product by all users.
     *
     * @param id
     * @return
     * @throws ResourceNotFoundException
     * @throws InternalErrorException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/reviews")
    public List<Review> getAllReviews(@PathParam("id") String id) throws ResourceNotFoundException, InternalErrorException {
        Product product = retrieve(id);
        if (product != null) {
            return product.getReviews();
        }
        throw new BadRequestException("product not found: " + id);
    }

    /**
     * Get review for a product by a user.
     *
     * @param id
     * @param userEmail
     * @return
     * @throws ResourceNotFoundException
     * @throws InternalErrorException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/reviews/{userEmail}")
    public List<Review> getReviewsByUser(@PathParam("id") String id, @PathParam("userEmail") String userEmail) throws ResourceNotFoundException, InternalErrorException, DBException {
        Product product = retrieve(id);
        List<Review> resultList = new ArrayList<>();
        if (product != null) {
            List<Review> reviews = product.getReviews();
            for (Review review: reviews) {
                if(review.getUser().getEmail().equals(userEmail)) {
                    resultList.add(review);
                }
            }
        }
        return resultList;
    }

    /**
     * Get all reviews by a user across all products.
     *
     * @param userEmail
     * @return
     * @throws ResourceNotFoundException
     * @throws InternalErrorException
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/reviews/{userEmail}")
    public List<Review> getAllReviewsByUser(@PathParam("userEmail") String userEmail) throws ResourceNotFoundException, InternalErrorException, DBException {
        List<Product> products = list("");
        List<Review> resultList = new ArrayList<>();
        for (Product product: products) {
            List<Review> reviews = getReviewsByUser(product.getId(), userEmail);
            resultList.addAll(reviews);
        }
        return resultList;
    }



    @Override
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Product update(@PathParam("id") String id, String entity) throws ResourceNotFoundException, InternalErrorException, IOException {
        return null;
    }

    @Override
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response delete(String id) throws ResourceNotFoundException, InternalErrorException {
        try {
            productDAO.deleteById(id);
            return Response.ok().build();
        } catch(Exception e) {
            throw new InternalErrorException();
        }
    }
}
