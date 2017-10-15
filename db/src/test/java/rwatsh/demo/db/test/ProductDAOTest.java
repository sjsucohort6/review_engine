/*
 * Copyright (c) 2017 San Jose State University.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 */

package rwatsh.demo.db.test;

import org.testng.Assert;
import rwatsh.demo.db.api.DBException;
import rwatsh.demo.db.impl.dao.ProductDAO;
import rwatsh.demo.db.impl.dao.UserDAO;
import rwatsh.demo.db.impl.model.Product;
import rwatsh.demo.db.impl.model.Review;
import rwatsh.demo.db.impl.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author rwatsh on 4/16/17.
 */
public class ProductDAOTest extends DBTest<ProductDAO, Product> {
    private static final Logger log = Logger.getLogger(ProductDAOTest.class.getName());

    @Override
    public void testAdd() throws Exception {
        testCreateProduct();
    }

    private List<String> testCreateProduct() throws DBException {
        Product product = createProduct();

        ProductDAO productDAO = (ProductDAO) client.getDAO(ProductDAO.class);
        List<String> insertedIds = productDAO.add(new ArrayList<Product>() {{
            add(product);
        }});
        List<Product> products = productDAO.fetchById(insertedIds);
        Assert.assertNotNull(products);
        log.info("Product created: " + products);
        return insertedIds;
    }

    private Product createProduct() {
        Product product = new Product();
        product.setId("P1");
        product.setName("Potatoes");
        product.setDescription("Golden Gate Potatoes");
        return product;
    }

    @Override
    public void testRemove() throws Exception {
        List<String> insertedIds = testCreateProduct();
        Assert.assertNotNull(insertedIds);
        long countRemovedEntries = dao.remove(insertedIds);
        Assert.assertTrue(countRemovedEntries > 0, "Failed to delete any service");
    }

    @Override
    public void testUpdate() throws Exception {
        List<String> insertedIds = testCreateProduct();
        Assert.assertNotNull(insertedIds);
        List<Product> products = dao.fetchById(insertedIds);
        Assert.assertNotNull(products);

        Product product = products.get(0);
        List<Review> reviews = new ArrayList<>();
        Review review = testCreateReview();
        reviews.add(review);

        product.setReviews(reviews);
        dao.update(new ArrayList<Product>() {{
            add(product);
        }});
    }

    private Review testCreateReview() throws DBException {
        List<String> userIds = testCreateUser();
        UserDAO userDAO = (UserDAO) client.getDAO(UserDAO.class);
        List<User> users = userDAO.fetchById(userIds);
        Assert.assertNotNull(users);
        User user = users.get(0);

        Review review = new Review();
        review.setComments("test comment");
        review.setScore(3);
        review.setUser(user);
        return review;
    }

    @Override
    public void testFetch() throws Exception {
        List<String> insertedIds = testCreateProduct();
        Assert.assertNotNull(insertedIds);
        List<Product> products = dao.fetchById(insertedIds);
        Assert.assertNotNull(products);
    }
}
