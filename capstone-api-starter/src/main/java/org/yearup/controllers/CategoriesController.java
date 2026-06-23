package org.yearup.controllers;

import jakarta.persistence.Id;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.models.Category;
import org.yearup.models.Product;
import org.yearup.service.CategoryService;
import org.yearup.service.ProductService;

import java.util.List;

// add the annotations to make this a REST controller
@RestController
// add the annotation to make this controller the endpoint for the following url
    // http://localhost:8080/categories
@RequestMapping("categories")
// add annotation to allow cross site origin requests
@CrossOrigin
public class CategoriesController
{
    private CategoryService categoryService;
    private ProductService productService;


    // create an Autowired constructor to inject the categoryService and productService
    public CategoriesController(CategoryService categoryService, ProductService productService)
    {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    // add the appropriate annotation for a get action
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public List<Category> getAllCategories() // change findAll() -> getAllCategories()
    {
        // find and return all categories
        return categoryService.getAllCategories();
    }

    // add the appropriate annotation for a get action
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id)
    {
        Category category = categoryService.getById(id);

        if (category == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found");

        // get the category by id
        return category;
    }

    // the url to return all products in category 1 would look like this
    // https://localhost:8080/categories/1/products
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId)
    {
        // get a list of product by categoryId
        return productService.listByCategoryId(categoryId);
    }

    // add annotation to call this method for a POST action
    @PostMapping("")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> addCategory(@RequestBody Category category)
    {
        Category saved = categoryService.create(category);
        // insert the category and return it with status 201 Created
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // add annotation to call this method for a PUT (update) action - the url path must include the categoryId
    @PutMapping("{id}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Category updateCategory(@PathVariable int id, @RequestBody Category category)
    {
        if (categoryService.getById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found");

        // update the category by id and return the updated category (200 OK)
        return categoryService.update(id, category);
    }


    // add annotation to call this method for a DELETE action - the url path must include the categoryId
    @DeleteMapping("{id}")
    // add annotation to ensure that only an ADMIN can call this function
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id)
    {
        if (categoryService.getById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category Not Found");

        // delete the category by id and return status 204 No Content
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
